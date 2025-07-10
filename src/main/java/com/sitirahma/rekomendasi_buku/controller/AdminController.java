package com.sitirahma.rekomendasi_buku.controller;

// --- TAMBAHKAN IMPORT INI ---
import com.sitirahma.rekomendasi_buku.model.Pengguna;
import com.sitirahma.rekomendasi_buku.service.PenggunaService;
import com.sitirahma.rekomendasi_buku.dto.PenggunaDto;
import com.sitirahma.rekomendasi_buku.dto.RegisterRequest;
// --- BATAS PENAMBAHAN ---

import com.sitirahma.rekomendasi_buku.dto.BukuDto;
import com.sitirahma.rekomendasi_buku.dto.BukuRequest;
import com.sitirahma.rekomendasi_buku.service.BukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BukuService bukuService;
    private final PenggunaService penggunaService;

    // --- Endpoint Buku ---
    @GetMapping("/buku")
    public ResponseEntity<List<BukuDto>> getAllBooks() {
        List<BukuDto> response = bukuService.getAllBuku();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/buku")
    public ResponseEntity<BukuDto> createBook(@RequestBody BukuRequest request) {
        BukuDto createdBook = bukuService.createBuku(request);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/buku/{id}")
    public ResponseEntity<BukuDto> updateBook(@PathVariable String id, @RequestBody BukuRequest request) {
        BukuDto updatedBook = bukuService.updateBuku(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/buku/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bukuService.deleteBuku(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/buku/batch")
    public ResponseEntity<Void> createBooksBatch(@RequestBody List<BukuRequest> requests) {
        bukuService.createBooksBatch(requests);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // --- Endpoint Pengguna ---
    @GetMapping("/users")
    public ResponseEntity<List<PenggunaDto>> getAllUsers() {
        // Kode ini sekarang akan mengenali 'Pengguna' karena sudah di-import
        List<Pengguna> users = penggunaService.getAllUsers();
        List<PenggunaDto> userDtos = users.stream()
                .map(user -> PenggunaDto.builder()
                        .id(user.getId())
                        .namaLengkap(user.getNamaLengkap())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @PostMapping("/users")
    public ResponseEntity<PenggunaDto> createUser(@RequestBody RegisterRequest request) {
        Pengguna newUser = penggunaService.createUser(request);
        PenggunaDto responseDto = PenggunaDto.builder()
                .id(newUser.getId())
                .namaLengkap(newUser.getNamaLengkap())
                .email(newUser.getEmail())
                .role(newUser.getRole().name())
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<PenggunaDto> updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
        Pengguna updatedUser = penggunaService.updateUser(id, request);
        PenggunaDto responseDto = PenggunaDto.builder()
                .id(updatedUser.getId())
                .namaLengkap(updatedUser.getNamaLengkap())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole().name())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        penggunaService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}