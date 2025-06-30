package com.sitirahma.rekomendasi_buku.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buku")
public class Buku {

    @Id
    @JsonProperty("id_buku")
    @Column(name = "id_buku")
    private String idBuku;

    @JsonProperty("judul")
    @Column(name = "judul", nullable = false)
    private String judul;

    @JsonProperty("penulis")
    @Column(name = "penulis")
    private String penulis;

    @JsonProperty("kategori")
    @Column(name = "kategori")
    private String kategori;

    @JsonProperty("penerbit")
    @Column(name = "penerbit")
    private String penerbit;

    @JsonProperty("tahunTerbit")
    @Column(name = "tahun_terbit")
    private Integer tahunTerbit;

    @JsonProperty("jumlahHalaman")
    @Column(name = "jumlah_halaman")
    private Integer jumlahHalaman;

    @JsonProperty("coverUrl")
    @Column(name = "cover_url")
    private String coverUrl;

    @JsonProperty("sinopsis")
    @Column(name = "sinopsis", columnDefinition = "TEXT")
    private String sinopsis;

    // Field ini yang akan disimpan ke database sebagai String
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    // PERBAIKAN: Field sementara ini sekarang hanya akan membaca "keywords_list"
    // dari JSON
    @Transient
    @JsonProperty("keywords_list") // Nama ini harus berbeda dari field lain
    private List<String> keywordsFromJson;
}