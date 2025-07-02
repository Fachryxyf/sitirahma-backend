package com.sitirahma.rekomendasi_buku.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email; // Diubah dari username menjadi email
    private String password;
}