package com.sitirahma.rekomendasi_buku.service;

import com.sitirahma.rekomendasi_buku.dto.RegisterRequest;
import com.sitirahma.rekomendasi_buku.model.Pengguna;
import com.sitirahma.rekomendasi_buku.model.Peran;
import com.sitirahma.rekomendasi_buku.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PenggunaService {

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Pengguna> getAllUsers() {
        return penggunaRepository.findAll();
    }

    public Pengguna createUser(RegisterRequest request) {
        if (penggunaRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email sudah terdaftar.");
        }

        Peran peran = Peran.ROLE_USER;
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                peran = Peran.valueOf(request.getRole());
            } catch (IllegalArgumentException e) {
                System.err.println("Peringatan: Peran tidak valid, menggunakan peran default (USER).");
            }
        }

        Pengguna pengguna = Pengguna.builder()
                .namaLengkap(request.getNamaLengkap())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(peran)
                .build();

        return penggunaRepository.save(pengguna);
    }

    public Pengguna updateUser(Long id, RegisterRequest request) {
        Pengguna penggunaToUpdate = penggunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengguna dengan ID " + id + " tidak ditemukan."));

        // Proteksi agar hanya admin utama yang bisa mengedit dirinya sendiri
        if ("admin@sekolah.id".equalsIgnoreCase(penggunaToUpdate.getEmail())) {
            String currentlyLoggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!penggunaToUpdate.getEmail().equalsIgnoreCase(currentlyLoggedInUser)) {
                throw new IllegalStateException("Hanya admin utama yang bisa mengubah datanya sendiri.");
            }
        }

        // Proteksi agar peran admin utama tidak bisa diubah
        if ("admin@sekolah.id".equalsIgnoreCase(penggunaToUpdate.getEmail()) &&
                !Peran.ROLE_ADMIN.name().equals(request.getRole())) {
            throw new IllegalStateException("Peran Administrator utama tidak boleh diubah.");
        }

        Optional<Pengguna> existingUserWithEmail = penggunaRepository.findByEmail(request.getEmail());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(id)) {
            throw new IllegalStateException("Email sudah digunakan oleh pengguna lain.");
        }

        penggunaToUpdate.setNamaLengkap(request.getNamaLengkap());
        penggunaToUpdate.setEmail(request.getEmail());

        if (request.getRole() != null) {
            penggunaToUpdate.setRole(Peran.valueOf(request.getRole()));
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            penggunaToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return penggunaRepository.save(penggunaToUpdate);
    }

    public void deleteUser(Long id) {
        Pengguna pengguna = penggunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengguna dengan ID " + id + " tidak ditemukan."));

        if ("admin@sekolah.id".equalsIgnoreCase(pengguna.getEmail())) {
            throw new IllegalStateException("Akun Administrator utama tidak boleh dihapus.");
        }

        penggunaRepository.deleteById(id);
    }
}