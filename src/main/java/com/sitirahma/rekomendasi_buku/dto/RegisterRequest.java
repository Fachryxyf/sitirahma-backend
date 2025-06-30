package com.sitirahma.rekomendasi_buku.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String namaLengkap;
    private String username;
    private String email;
    private String password;
}