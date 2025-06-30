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

    public boolean verifyUser(VerifyUserRequest request) {
        // Cari pengguna berdasarkan username, jika tidak ada, lempar error
        Pengguna pengguna = penggunaRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan."));

        // Cek apakah email yang dimasukkan cocok dengan email di database
        return pengguna.getEmail().equalsIgnoreCase(request.getEmail());
    }

    public String resetPassword(ResetPasswordRequest request) {
        // Cari pengguna berdasarkan username, jika tidak ada, lempar error
        Pengguna pengguna = penggunaRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan."));

        // Lakukan verifikasi email sekali lagi untuk keamanan
        if (!pengguna.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new IllegalStateException("Kombinasi username dan email tidak cocok.");
        }

        // Enkripsi dan set password baru
        pengguna.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Simpan perubahan ke database
        penggunaRepository.save(pengguna);

        return "Password berhasil diubah. Silakan login dengan password baru Anda.";
    }
}