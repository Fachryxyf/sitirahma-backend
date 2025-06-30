package com.sitirahma.rekomendasi_buku.service;

import com.sitirahma.rekomendasi_buku.model.Buku;
import com.sitirahma.rekomendasi_buku.repository.BukuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BukuService {

    private final BukuRepository bukuRepository;
    // Kita akan integrasikan recommendationEngine di sini nanti

    // Metode untuk mengambil semua buku (untuk pengujian awal)
    public List<Buku> getAllBuku() {
        return bukuRepository.findAll();
    }

    // Metode untuk pencarian akan ditambahkan di sini
}