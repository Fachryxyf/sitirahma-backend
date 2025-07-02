package com.sitirahma.rekomendasi_buku.service;

import com.sitirahma.rekomendasi_buku.dto.BukuRequest;
import com.sitirahma.rekomendasi_buku.model.Buku;
import com.sitirahma.rekomendasi_buku.repository.BukuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BukuService {

    private final BukuRepository bukuRepository;

    // --- Metode CRUD ---

    // PERBAIKAN: Tambahkan anotasi @Transactional(readOnly = true)
    @Transactional(readOnly = true)
    public List<Buku> getAllBuku() {
        return bukuRepository.findAll();
    }

    @Transactional
    public Buku createBuku(BukuRequest request) {
        Buku buku = new Buku();
        buku.setIdBuku(request.getIdBuku());
        buku.setJudul(request.getJudul());
        buku.setPenulis(request.getPenulis());
        buku.setKategori(request.getKategori());
        buku.setPenerbit(request.getPenerbit());
        buku.setTahunTerbit(request.getTahunTerbit());
        buku.setJumlahHalaman(request.getJumlahHalaman());
        buku.setCoverUrl(request.getCoverUrl());
        buku.setSinopsis(request.getSinopsis());
        if (request.getKeywords() != null && !request.getKeywords().isEmpty()) {
            buku.setKeywords(String.join(",", request.getKeywords()));
        }
        return bukuRepository.save(buku);
    }

    @Transactional
    public Buku updateBuku(String id, BukuRequest request) {
        Buku buku = bukuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buku dengan ID " + id + " tidak ditemukan."));

        buku.setJudul(request.getJudul());
        buku.setPenulis(request.getPenulis());
        buku.setKategori(request.getKategori());
        buku.setPenerbit(request.getPenerbit());
        buku.setTahunTerbit(request.getTahunTerbit());
        buku.setJumlahHalaman(request.getJumlahHalaman());
        buku.setCoverUrl(request.getCoverUrl());
        buku.setSinopsis(request.getSinopsis());
        if (request.getKeywords() != null && !request.getKeywords().isEmpty()) {
            buku.setKeywords(String.join(",", request.getKeywords()));
        } else {
            buku.setKeywords(null);
        }

        return bukuRepository.save(buku);
    }

    @Transactional
    public void deleteBuku(String id) {
        if (!bukuRepository.existsById(id)) {
            throw new RuntimeException("Buku dengan ID " + id + " tidak ditemukan.");
        }
        bukuRepository.deleteById(id);
    }

    // --- Logika Pencarian ---

    public static class ScoredBook {
        public Buku book;
        public double score;
        public Map<String, List<String>> reportTerms;

        public ScoredBook(Buku book, double score, Map<String, List<String>> reportTerms) {
            this.book = book;
            this.score = score;
            this.reportTerms = reportTerms;
        }
    }

    @Transactional(readOnly = true)
    // Fungsi utama untuk pencarian
    public List<ScoredBook> search(String query) {
        List<Buku> allBooks = bukuRepository.findAll();
        Set<String> queryTokens = preprocess(query);
        int currentYear = java.time.Year.now().getValue();
        int baseYear = 2010;

        if (queryTokens.isEmpty()) {
            return new ArrayList<>();
        }

        return allBooks.stream()
                .map(book -> {
                    double score = 0;
                    Map<String, List<String>> matchedIn = new HashMap<>();

                    Set<String> titleTokens = preprocess(book.getJudul());
                    Set<String> authorTokens = preprocess(book.getPenulis());
                    Set<String> publisherTokens = preprocess(book.getPenerbit());
                    List<String> synopsisTokens = Arrays.asList(preprocess(book.getSinopsis()).toArray(new String[0]));
                    Set<String> keywordTokens = book.getKeywords() != null
                            ? new HashSet<>(Arrays.asList(book.getKeywords().toLowerCase().split(",")))
                            : new HashSet<>();

                    for (String token : queryTokens) {
                        if (titleTokens.contains(token)) {
                            score += 3.0;
                            matchedIn.computeIfAbsent("title", k -> new ArrayList<>()).add(token);
                        }
                        if (authorTokens.contains(token)) {
                            score += 2.5;
                            matchedIn.computeIfAbsent("author", k -> new ArrayList<>()).add(token);
                        }
                        if (keywordTokens.contains(token)) {
                            score += 2.0;
                            matchedIn.computeIfAbsent("keywords", k -> new ArrayList<>()).add(token);
                        }
                        if (publisherTokens.contains(token)) {
                            score += 1.5;
                            matchedIn.computeIfAbsent("publisher", k -> new ArrayList<>()).add(token);
                        }

                        long occurrences = synopsisTokens.stream().filter(t -> t.equals(token)).count();
                        if (occurrences > 0) {
                            score += (occurrences * 1.0);
                            matchedIn.computeIfAbsent("synopsis", k -> new ArrayList<>()).add(token);
                        }
                    }

                    if (book.getTahunTerbit() != null && book.getTahunTerbit() >= baseYear) {
                        double yearBonus = (double) (book.getTahunTerbit() - baseYear) / (currentYear - baseYear);
                        score += yearBonus;
                    }

                    return new ScoredBook(book, score, matchedIn);
                })
                .filter(scoredBook -> scoredBook.score > 0)
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createBooksBatch(List<BukuRequest> requests) {
        List<Buku> booksToSave = requests.stream()
                .map(request -> {
                    Buku buku = new Buku();
                    // Pengecekan agar tidak error jika ada data yang sudah ada di database
                    if (bukuRepository.existsById(request.getIdBuku())) {
                        return null; // Lewati buku yang ID-nya sudah ada
                    }
                    buku.setIdBuku(request.getIdBuku());
                    buku.setJudul(request.getJudul());
                    buku.setPenulis(request.getPenulis());
                    buku.setKategori(request.getKategori());
                    buku.setPenerbit(request.getPenerbit());
                    buku.setTahunTerbit(request.getTahunTerbit());
                    buku.setJumlahHalaman(request.getJumlahHalaman());
                    buku.setCoverUrl(request.getCoverUrl());
                    buku.setSinopsis(request.getSinopsis());
                    if (request.getKeywords() != null && !request.getKeywords().isEmpty()) {
                        buku.setKeywords(String.join(",", request.getKeywords()));
                    }
                    return buku;
                })
                .filter(Objects::nonNull) // Hapus semua hasil null dari pemetaan
                .collect(Collectors.toList());

        if (!booksToSave.isEmpty()) {
            bukuRepository.saveAll(booksToSave);
        }
    }

    // Fungsi preprocessing teks
    private Set<String> preprocess(String text) {
        if (text == null || text.isBlank()) {
            return new HashSet<>();
        }
        // Daftar stop words bisa diperluas di sini jika perlu
        Set<String> stopWords = new HashSet<>(
                Arrays.asList("dan", "di", "yang", "untuk", "ini", "itu", "dengan", "sebuah", "oleh", "pada", "dari"));

        String[] tokens = text.toLowerCase()
                .replaceAll("[^a-z\\s]", " ")
                .trim()
                .split("\\s+");

        return Arrays.stream(tokens)
                .filter(token -> !token.isEmpty() && !stopWords.contains(token))
                .collect(Collectors.toSet());
    }

    public List<Buku> getBooksByIds(List<String> ids) {
        return bukuRepository.findAllById(ids);
    }
}