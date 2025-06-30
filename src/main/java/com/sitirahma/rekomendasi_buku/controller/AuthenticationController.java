package com.sitirahma.rekomendasi_buku.controller;

import com.sitirahma.rekomendasi_buku.dto.AuthenticationResponse;
import com.sitirahma.rekomendasi_buku.dto.LoginRequest;
import com.sitirahma.rekomendasi_buku.dto.RegisterRequest;
import com.sitirahma.rekomendasi_buku.dto.ResetPasswordRequest;
import com.sitirahma.rekomendasi_buku.dto.VerifyUserRequest;
import com.sitirahma.rekomendasi_buku.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    // PERUBAHAN: Tipe kembalian diubah menjadi AuthenticationResponse
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    // PERUBAHAN: Tipe kembalian diubah menjadi AuthenticationResponse
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/verify-user")
    public ResponseEntity<Boolean> verifyUser(@RequestBody VerifyUserRequest request) {
        boolean isValid = authenticationService.verifyUser(request);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        String message = authenticationService.resetPassword(request);
        return ResponseEntity.ok(message);
    }
}