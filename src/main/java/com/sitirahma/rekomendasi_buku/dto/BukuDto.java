package com.sitirahma.rekomendasi_buku.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BukuDto {
    private String idBuku;
    private String judul;
    private String penulis;
    private String kategori;
    private String penerbit;
    private Integer tahunTerbit;
    private Integer jumlahHalaman;
    private String coverUrl;
    private String sinopsis;
    private List<String> keywords;
}