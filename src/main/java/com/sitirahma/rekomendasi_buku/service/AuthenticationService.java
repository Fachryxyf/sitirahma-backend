package com.sitirahma.rekomendasi_buku.service;

import com.sitirahma.rekomendasi_buku.dto.AuthenticationResponse;
import com.sitirahma.rekomendasi_buku.dto.LoginRequest;
import com.sitirahma.rekomendasi_buku.dto.RegisterRequest;
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
        if (penggunaRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username sudah terdaftar.");
        }
        var pengguna = Pengguna.builder()
                .namaLengkap(request.getNamaLengkap())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Peran.ROLE_USER)
                .build();
        penggunaRepository.save(pengguna);

        // Buat token untuk pengguna yang baru mendaftar
        var jwtToken = jwtService.generateToken(pengguna);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        var pengguna = penggunaRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // Buat dan kembalikan token
        var jwtToken = jwtService.generateToken(pengguna);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}