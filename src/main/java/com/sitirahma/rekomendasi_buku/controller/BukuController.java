package com.sitirahma.rekomendasi_buku.controller;

import com.sitirahma.rekomendasi_buku.dto.BukuAdminDto;
import com.sitirahma.rekomendasi_buku.dto.BukuDto;
import com.sitirahma.rekomendasi_buku.service.BukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/buku")
@RequiredArgsConstructor
public class BukuController {

        private final BukuService bukuService;

        // Di dalam file BukuController.java

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
                                                        .reportTerms(item.reportTerms)
                                                        .scoreDetails(item.scoreDetails) // Menyertakan rincian skor
                                                        .build())
                                        .collect(Collectors.toList());
                        return ResponseEntity.ok(response);
                } else {
                        // UNTUK USER BIASA, KEMBALIKAN HANYA DATA BUKUNYA (seperti sebelumnya)
                        List<BukuDto> response = searchResults.stream()
                                        .map(item -> item.book)
                                        .collect(Collectors.toList());
                        return ResponseEntity.ok(response);
                }
        }

        @PostMapping("/batch")
        public ResponseEntity<List<BukuDto>> getBooksByIds(@RequestBody List<String> ids) {
                // Service sudah mengembalikan List<BukuDto>, jadi tidak perlu mapping lagi
                List<BukuDto> response = bukuService.getBooksByIds(ids);
                return ResponseEntity.ok(response);
        }
}