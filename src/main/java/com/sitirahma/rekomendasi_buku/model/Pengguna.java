package com.sitirahma.rekomendasi_buku.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pengguna")
// PERUBAHAN UTAMA: Implementasikan interface UserDetails
public class Pengguna implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_lengkap", nullable = false)
    private String namaLengkap;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Peran role;

    // --- Implementasi Metode dari UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Metode ini mengubah 'Peran' kita menjadi format yang dimengerti Spring
        // Security
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        // Metode ini sudah sesuai dengan field 'password' kita
        return password;
    }

    @Override
    public String getUsername() {
        // Metode ini sudah sesuai dengan field 'username' kita
        return username;
    }

    // Untuk proyek ini, kita anggap semua akun selalu aktif dan valid.
    // Jadi, kita kembalikan 'true' untuk metode-metode di bawah ini.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}