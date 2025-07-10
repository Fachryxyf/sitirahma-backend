package com.sitirahma.rekomendasi_buku.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class BukuAdminDto {
    private String idBuku;
    private String judul;
    private String penulis;
    private String kategori;
    private String penerbit;
    private Integer tahunTerbit;
    private Integer jumlahHalaman;
    private String coverUrl;
    private String sinopsis;
    private double score;
    private Map<String, List<String>> reportTerms;
    private Map<String, Double> scoreDetails;
}