package com.sitirahma.rekomendasi_buku.service;

import com.sitirahma.rekomendasi_buku.dto.BukuDto;
import com.sitirahma.rekomendasi_buku.dto.BukuRequest;
import com.sitirahma.rekomendasi_buku.model.Buku;
import com.sitirahma.rekomendasi_buku.repository.BukuRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jsastrawi.morphology.DefaultLemmatizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.Locale;

@Service
@RequiredArgsConstructor

public class BukuService {

    private final BukuRepository bukuRepository;
    private DefaultLemmatizer lemmatizer;

    @PostConstruct
    public void initLemmatizer() {
        Set<String> dictionary = new HashSet<>();

        try (InputStream in = getClass().getResourceAsStream("/root-words.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                dictionary.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal memuat root-words.txt untuk lemmatizer", e);
        }

        this.lemmatizer = new DefaultLemmatizer(dictionary);
    }

    // --- HELPER METHODS (KONVERSI DTO & ENTITY) ---

    private BukuDto convertToDto(Buku buku) {
        return BukuDto.builder()
                .idBuku(buku.getIdBuku())
                .judul(buku.getJudul())
                .penulis(buku.getPenulis())
                .kategori(buku.getKategori())
                .penerbit(buku.getPenerbit())
                .tahunTerbit(buku.getTahunTerbit())
                .jumlahHalaman(buku.getJumlahHalaman())
                .coverUrl(buku.getCoverUrl())
                .sinopsis(buku.getSinopsis())
                .keywords(buku.getKeywords()) // Memanggil getter kustom dari Buku.java
                .build();
    }

    private void mapRequestToEntity(BukuRequest request, Buku buku) {
        buku.setIdBuku(request.getIdBuku());
        buku.setJudul(request.getJudul());
        buku.setPenulis(request.getPenulis());
        buku.setKategori(request.getKategori());
        buku.setPenerbit(request.getPenerbit());
        buku.setTahunTerbit(request.getTahunTerbit());
        buku.setJumlahHalaman(request.getJumlahHalaman());
        buku.setCoverUrl(request.getCoverUrl());
        buku.setSinopsis(request.getSinopsis());
        buku.setKeywords(request.getKeywords()); // Memanggil setter kustom di Buku.java
    }

    // --- METODE CRUD (DIPERBAIKI) ---

    @Transactional(readOnly = true)
    public List<BukuDto> getAllBuku() {
        return bukuRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BukuDto getBukuById(String id) {
        Buku buku = bukuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan dengan ID: " + id));
        return convertToDto(buku);
    }

    @Transactional
    public BukuDto createBuku(BukuRequest request) {
        Buku buku = new Buku();
        mapRequestToEntity(request, buku);
        Buku savedBuku = bukuRepository.save(buku);
        return convertToDto(savedBuku);
    }

    @Transactional
    public BukuDto updateBuku(String id, BukuRequest request) {
        Buku buku = bukuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buku dengan ID " + id + " tidak ditemukan."));
        mapRequestToEntity(request, buku);
        Buku updatedBuku = bukuRepository.save(buku);
        return convertToDto(updatedBuku);
    }

    @Transactional
    public void deleteBuku(String id) {
        bukuRepository.deleteById(id);
    }

    @Transactional
    public void createBooksBatch(List<BukuRequest> requests) {
        List<String> existingIds = bukuRepository.findAllById(
                requests.stream().map(BukuRequest::getIdBuku).collect(Collectors.toList())).stream()
                .map(Buku::getIdBuku).collect(Collectors.toList());

        List<Buku> booksToSave = requests.stream()
                .filter(request -> !existingIds.contains(request.getIdBuku()))
                .map(request -> {
                    Buku buku = new Buku();
                    mapRequestToEntity(request, buku);
                    return buku;
                })
                .collect(Collectors.toList());

        if (!booksToSave.isEmpty()) {
            bukuRepository.saveAll(booksToSave);
        }
    }

    public List<BukuDto> getBooksByIds(List<String> ids) {
        return bukuRepository.findAllById(ids).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- LOGIKA PENCARIAN (DIPERBAIKI) ---
    // Di dalam file BukuService.java

    public static class ScoredBook {
        public BukuDto book;
        public double score;
        public Map<String, List<String>> reportTerms;
        public Map<String, Double> scoreDetails; // PASTIKAN FIELD INI SUDAH ADA

        // PASTIKAN CONSTRUCTOR INI MENERIMA EMPAT ARGUMEN
        public ScoredBook(BukuDto book, double score, Map<String, List<String>> reportTerms,
                Map<String, Double> scoreDetails) {
            this.book = book;
            this.score = score;
            this.reportTerms = reportTerms;
            this.scoreDetails = scoreDetails;
        }
    }

    @Transactional(readOnly = true)
    public List<ScoredBook> search(String query) {
        // PERINGATAN: Pendekatan ini memuat semua buku ke memori dan tidak efisien
        // untuk data besar.
        List<Buku> allBooks = bukuRepository.findAll();
        Set<String> queryTokens = preprocess(query);
        int currentYear = java.time.Year.now().getValue();
        int baseYear = 2010;

        if (queryTokens.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Double> idfMap = calculateIDF(allBooks, queryTokens);

        List<ScoredBook> scoredBooks = allBooks.stream()
                .map(book -> {
                    double totalScore;

                    Map<String, List<String>> matchedIn = new HashMap<>();
                    Map<String, Double> scoreDetails = new HashMap<>();

                    Set<String> titleTokens = preprocess(book.getJudul());
                    Set<String> authorTokens = preprocess(book.getPenulis());
                    Set<String> publisherTokens = preprocess(book.getPenerbit());
                    List<String> synopsisTokens = new ArrayList<>(preprocess(book.getSinopsis()));
                    List<String> keywordTokens = book.getKeywords();

                    Map<String, Double> synopsisTfMap = calculateTF(synopsisTokens);
                    Map<String, Double> keywordTfMap = calculateTF(keywordTokens);

                    for (String token : queryTokens) {
                        if (titleTokens.contains(token)) {
                            scoreDetails.merge("title", 3.0, Double::sum);
                            matchedIn.computeIfAbsent("title", k -> new ArrayList<>()).add(token);
                        }
                        if (authorTokens.contains(token)) {
                            scoreDetails.merge("author", 2.5, Double::sum);
                            matchedIn.computeIfAbsent("author", k -> new ArrayList<>()).add(token);
                        }
                        if (publisherTokens.contains(token)) {
                            scoreDetails.merge("publisher", 1.5, Double::sum);
                            matchedIn.computeIfAbsent("publisher", k -> new ArrayList<>()).add(token);
                        }
                        if (synopsisTfMap.containsKey(token) && idfMap.containsKey(token)) {
                            double tfIdfSynopsis = synopsisTfMap.get(token) * idfMap.get(token) * 2.0;
                            scoreDetails.merge("synopsis", tfIdfSynopsis, Double::sum);
                            matchedIn.computeIfAbsent("synopsis", k -> new ArrayList<>()).add(token);
                        }
                        if (keywordTfMap.containsKey(token) && idfMap.containsKey(token)) {
                            double tfIdfKeywords = keywordTfMap.get(token) * idfMap.get(token) * 1.8;
                            scoreDetails.merge("keywords", tfIdfKeywords, Double::sum);
                            matchedIn.computeIfAbsent("keywords", k -> new ArrayList<>()).add(token);
                        }
                    }

                    totalScore = scoreDetails.values().stream().mapToDouble(Double::doubleValue).sum();

                    if (totalScore > 0 && book.getTahunTerbit() != null && book.getTahunTerbit() >= baseYear) {
                        double yearBonus = ((double) (book.getTahunTerbit() - baseYear) / (currentYear - baseYear)
                                * 0.1);
                        scoreDetails.put("yearBonus", yearBonus);
                        totalScore += yearBonus;
                    }

                    return new ScoredBook(convertToDto(book), totalScore, matchedIn, scoreDetails);
                })
                .filter(sb -> sb.score > 0.01)
                .collect(Collectors.toList());

        if (scoredBooks.isEmpty())
            return new ArrayList<>();

        double maxScore = scoredBooks.stream().mapToDouble(sb -> sb.score).max().orElse(1.0);
        if (maxScore > 0.01) {
            scoredBooks.forEach(sb -> sb.score = sb.score / maxScore);
        }

        return scoredBooks.stream()
                .sorted(Comparator.comparingDouble(sb -> -sb.score))
                .collect(Collectors.toList());
    }

    private Map<String, Double> calculateTF(List<String> tokens) {
        if (tokens == null || tokens.isEmpty())
            return Collections.emptyMap();
        Map<String, Long> termCount = tokens.stream()
                .collect(Collectors.groupingBy(token -> token, Collectors.counting()));
        double totalTokens = tokens.size();
        return termCount.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / totalTokens));
    }

    private Map<String, Double> calculateIDF(List<Buku> allBooks, Set<String> queryTokens) {
        Map<String, Double> idfMap = new HashMap<>();
        int totalDocuments = allBooks.size();
        if (totalDocuments == 0)
            return idfMap;

        for (String token : queryTokens) {
            int documentsContaining = 0;
            for (Buku book : allBooks) {
                if (preprocess(book.getSinopsis()).contains(token) || book.getKeywords().contains(token)) {
                    documentsContaining++;
                }
            }

            double idf = (documentsContaining > 0)
                    ? Math.log((double) totalDocuments / (documentsContaining + 1))
                    : Math.log(totalDocuments + 1);
            idfMap.put(token, idf);
        }
        return idfMap;
    }

    private Set<String> preprocess(String text) {
        if (text == null || text.isBlank())
            return Collections.emptySet();

        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "dan", "di", "yang", "untuk", "ini", "itu", "dengan", "sebuah", "oleh", "pada", "dari",
                "adalah", "akan", "ada", "atau", "dalam", "ke", "sebagai", "satu", "tidak", "juga", "dapat",
                "telah", "sudah", "bahwa", "nya", "bisa", "seperti", "ia", "mereka", "kita", "kami", "dia",
                "kamu", "anda", "saya", "aku", "lebih", "sangat", "hanya", "pun", "lah", "kah", "tapi", "namun"));

        return Arrays.stream(text.toLowerCase(Locale.ROOT).replaceAll("[^a-z\\s]", " ").trim().split("\\s+"))
                .map(token -> lemmatizer.lemmatize(token)) // ⬅️ stemming di sini
                .filter(token -> !token.isEmpty() && token.length() > 2 && !stopWords.contains(token))
                .collect(Collectors.toSet());
    }
}