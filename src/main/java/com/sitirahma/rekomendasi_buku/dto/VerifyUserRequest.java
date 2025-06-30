package com.sitirahma.rekomendasi_buku.dto;

import lombok.Data;

@Data
public class VerifyUserRequest {
    private String username;
    private String email;
}