package com.sitirahma.rekomendasi_buku.controller;

import com.sitirahma.rekomendasi_buku.dto.BukuAdminDto;
import com.sitirahma.rekomendasi_buku.dto.BukuDto;
import com.sitirahma.rekomendasi_buku.model.Buku;
import com.sitirahma.rekomendasi_buku.service.BukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/buku")
@RequiredArgsConstructor
public class BukuController {

        private final BukuService bukuService;

        @GetMapping("/cari")
        public ResponseEntity<?> searchBooks(@RequestParam("q") String query) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String userRole = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .findFirst()
                                .orElse("ROLE_USER");

                List<BukuService.ScoredBook> searchResults = bukuService.search(query);

                if ("ROLE_ADMIN".equals(userRole)) {
                        List<BukuAdminDto> response = searchResults.stream()
                                        .map(item -> BukuAdminDto.builder()
                                                        .idBuku(item.book.getIdBuku())
                                                        .judul(item.book.getJudul())
                                                        .penulis(item.book.getPenulis())
                                                        .kategori(item.book.getKategori())
                                                        .penerbit(item.book.getPenerbit())
                                                        .tahunTerbit(item.book.getTahunTerbit())
                                                        .jumlahHalaman(item.book.getJumlahHalaman())
                                                        .coverUrl(item.book.getCoverUrl())
                                                        .sinopsis(item.book.getSinopsis())
                                                        .score(item.score)
                                                        .build())
                                        .collect(Collectors.toList());
                        return ResponseEntity.ok(response);
                } else {
                        List<BukuDto> response = searchResults.stream()
                                        .map(item -> BukuDto.builder()
                                                        .idBuku(item.book.getIdBuku())
                                                        .judul(item.book.getJudul())
                                                        .penulis(item.book.getPenulis())
                                                        .kategori(item.book.getKategori())
                                                        .penerbit(item.book.getPenerbit())
                                                        .tahunTerbit(item.book.getTahunTerbit())
                                                        .jumlahHalaman(item.book.getJumlahHalaman())
                                                        .coverUrl(item.book.getCoverUrl())
                                                        .sinopsis(item.book.getSinopsis())
                                                        .build())
                                        .collect(Collectors.toList());
                        return ResponseEntity.ok(response);
                }
        }

        @PostMapping("/batch")
        public ResponseEntity<List<BukuDto>> getBooksByIds(@RequestBody List<String> ids) {
                List<Buku> books = bukuService.getBooksByIds(ids);
                List<BukuDto> response = books.stream()
                                .map(book -> BukuDto.builder()
                                                // ... (mapping field dari book ke BukuDto) ...
                                                .idBuku(book.getIdBuku())
                                                .judul(book.getJudul())
                                                .penulis(book.getPenulis())
                                                .kategori(book.getKategori())
                                                .penerbit(book.getPenerbit())
                                                .tahunTerbit(book.getTahunTerbit())
                                                .jumlahHalaman(book.getJumlahHalaman())
                                                .coverUrl(book.getCoverUrl())
                                                .sinopsis(book.getSinopsis())
                                                .build())
                                .collect(Collectors.toList());
                return ResponseEntity.ok(response);
        }
}