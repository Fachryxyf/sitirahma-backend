package com.sitirahma.rekomendasi_buku.repository;

import com.sitirahma.rekomendasi_buku.model.Buku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BukuRepository extends JpaRepository<Buku, String> {
    // Dengan hanya membuat interface ini, kita sudah mendapatkan metode seperti:
    // - save(Buku buku) -> untuk menyimpan buku baru atau update
    // - findById(String idBuku) -> untuk mencari buku berdasarkan ID
    // - findAll() -> untuk mengambil semua buku
    // - deleteById(String idBuku) -> untuk menghapus buku
    // - count() -> untuk menghitung jumlah buku
    // Dan masih banyak lagi!
}