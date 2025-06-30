package com.sitirahma.rekomendasi_buku.repository;

import com.sitirahma.rekomendasi_buku.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, Long> {

    // Ini adalah metode kustom. Spring Data JPA cukup pintar untuk
    // secara otomatis membuat query berdasarkan nama metode ini.
    // Metode ini akan mencari pengguna berdasarkan username-nya.
    // Kita gunakan Optional untuk menangani kasus jika username tidak ditemukan.
    Optional<Pengguna> findByUsername(String username);
}