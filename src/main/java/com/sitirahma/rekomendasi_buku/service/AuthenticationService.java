package com.sitirahma.rekomendasi_buku.service;

import com.sitirahma.rekomendasi_buku.dto.AuthenticationResponse;
import com.sitirahma.rekomendasi_buku.dto.LoginRequest;
import com.sitirahma.rekomendasi_buku.dto.RegisterRequest;
import com.sitirahma.rekomendasi_buku.dto.ResetPasswordRequest;
import com.sitirahma.rekomendasi_buku.dto.VerifyUserRequest;
import com.sitirahma.rekomendasi_buku.model.Pengguna;
import com.sitirahma.rekomendasi_buku.model.Peran;
import com.sitirahma.rekomendasi_buku.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (penggunaRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email sudah terdaftar.");
        }
        var pengguna = Pengguna.builder()
                .namaLengkap(request.getNamaLengkap())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Peran.ROLE_USER)
                .build();
        penggunaRepository.save(pengguna);
        var jwtToken = jwtService.generateToken(pengguna);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var pengguna = penggunaRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email atau password salah."));
        var jwtToken = jwtService.generateToken(pengguna);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public boolean verifyUser(VerifyUserRequest request) {
        // Menggunakan metode repository yang sudah benar
        return penggunaRepository.findByNamaLengkapAndEmail(request.getNamaLengkap(), request.getEmail()).isPresent();
    }

    public String resetPassword(ResetPasswordRequest request) {
        // Menggunakan metode repository yang sudah benar
        Pengguna pengguna = penggunaRepository.findByNamaLengkapAndEmail(request.getNamaLengkap(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan atau data tidak cocok."));

        pengguna.setPassword(passwordEncoder.encode(request.getNewPassword()));
        penggunaRepository.save(pengguna);

        return "Password berhasil diubah. Silakan login dengan password baru Anda.";
    }
}