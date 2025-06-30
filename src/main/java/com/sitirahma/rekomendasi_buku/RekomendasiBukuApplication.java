package com.sitirahma.rekomendasi_buku;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitirahma.rekomendasi_buku.model.Buku;
import com.sitirahma.rekomendasi_buku.model.Pengguna;
import com.sitirahma.rekomendasi_buku.model.Peran;
import com.sitirahma.rekomendasi_buku.repository.BukuRepository;
import com.sitirahma.rekomendasi_buku.repository.PenggunaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class RekomendasiBukuApplication {

	public static void main(String[] args) {
		SpringApplication.run(RekomendasiBukuApplication.class, args);
	}

	// --- BEAN UNTUK SEEDING DATA ---
	@Bean
	CommandLineRunner run(BukuRepository bukuRepository, PenggunaRepository penggunaRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// 1. Seed Akun Administrator (tidak berubah)
			if (penggunaRepository.findByUsername("admin").isEmpty()) {
				Pengguna admin = Pengguna.builder()
						.namaLengkap("Administrator")
						.username("admin")
						.email("admin@sekolah.id")
						.password(passwordEncoder.encode("password123"))
						.role(Peran.ROLE_ADMIN)
						.build();
				penggunaRepository.save(admin);
				System.out.println(">>> Akun Administrator berhasil dibuat!");
			}

			// 2. Seed Data Buku dari file JSON
			if (bukuRepository.count() == 0) {
				ObjectMapper mapper = new ObjectMapper();
				try (InputStream inputStream = new ClassPathResource("books.json").getInputStream()) {
					List<Buku> booksToSave = mapper.readValue(inputStream, new TypeReference<>() {
					});

					// PERBAIKAN: Logika disesuaikan dengan nama field baru
					List<Buku> processedBooks = booksToSave.stream().map(book -> {
						if (book.getKeywordsFromJson() != null && !book.getKeywordsFromJson().isEmpty()) {
							book.setKeywords(String.join(",", book.getKeywordsFromJson()));
						}
						return book;
					}).collect(Collectors.toList());

					bukuRepository.saveAll(processedBooks);
					System.out.println(">>> " + processedBooks.size() + " data buku berhasil di-seed ke database!");
				} catch (Exception e) {
					System.err.println("!!! Gagal membaca atau menyimpan data buku dari JSON: " + e.getMessage());
					e.printStackTrace();
				}
			}
		};
	};
}