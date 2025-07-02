package com.sitirahma.rekomendasi_buku.repository;

import com.sitirahma.rekomendasi_buku.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, Long> {

    // Metode untuk proses login
    Optional<Pengguna> findByEmail(String email);

    // Metode untuk proses verifikasi "Lupa Password"
    Optional<Pengguna> findByNamaLengkapAndEmail(String namaLengkap, String email);
}