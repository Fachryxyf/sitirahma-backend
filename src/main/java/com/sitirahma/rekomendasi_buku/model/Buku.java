package com.sitirahma.rekomendasi_buku.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buku")
public class Buku {

    @Id
    @Column(name = "id_buku")
    private String idBuku;

    @Column(name = "judul", nullable = false)
    private String judul;

    @Column(name = "penulis")
    private String penulis;

    @Column(name = "kategori")
    private String kategori;

    @Column(name = "penerbit")
    private String penerbit;

    @Column(name = "tahun_terbit")
    private Integer tahunTerbit;

    @Column(name = "jumlah_halaman")
    private Integer jumlahHalaman;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "sinopsis", columnDefinition = "TEXT")
    private String sinopsis;

    // Field ini yang disimpan di database sebagai string, misal:
    // "cinta,drama,remaja"
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    // GETTER KUSTOM: Saat aplikasi butuh keywords, metode ini dipanggil.
    // Ia akan memecah string dari database menjadi List.
    public List<String> getKeywords() {
        if (this.keywords == null || this.keywords.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(this.keywords.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    // SETTER KUSTOM: Saat aplikasi ingin menyimpan keywords (dari List), metode ini
    // dipanggil.
    // Ia akan menggabungkan List menjadi satu String untuk disimpan ke database.
    public void setKeywords(List<String> keywordsList) {
        if (keywordsList == null || keywordsList.isEmpty()) {
            this.keywords = null;
        } else {
            this.keywords = String.join(",", keywordsList);
        }
    }
}