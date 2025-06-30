package com.sitirahma.rekomendasi_buku.controller;

import com.sitirahma.rekomendasi_buku.model.Buku;
import com.sitirahma.rekomendasi_buku.service.BukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buku")
@RequiredArgsConstructor
public class BukuController {

    private final BukuService bukuService;

    // Endpoint untuk menguji pengambilan data buku
    // Nanti akan kita amankan agar tidak semua orang bisa akses
    @GetMapping
    public ResponseEntity<List<Buku>> getAllBuku() {
        return ResponseEntity.ok(bukuService.getAllBuku());
    }
}