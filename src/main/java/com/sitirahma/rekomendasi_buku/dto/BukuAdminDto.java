package com.sitirahma.rekomendasi_buku.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BukuAdminDto {
    // Mewarisi semua field dari BukuDto
    private String idBuku;
    private String judul;
    private String penulis;
    private String kategori;
    private String penerbit;
    private Integer tahunTerbit;
    private Integer jumlahHalaman;
    private String coverUrl;
    private String sinopsis;

    // Field tambahan khusus Admin
    private double score;
}