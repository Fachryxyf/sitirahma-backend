package com.sitirahma.rekomendasi_buku.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String namaLengkap;
    private String email;
    private String newPassword;
}