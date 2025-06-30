# --- Tahap 1: Build Aplikasi ---
# Menggunakan image Maven yang sudah berisi JDK 17 untuk membangun proyek
FROM maven:3.9-eclipse-temurin-17 AS build

# Tentukan direktori kerja di dalam container
WORKDIR /app

# Salin file definisi proyek (pom.xml) terlebih dahulu
# Ini memanfaatkan cache Docker. Jika pom.xml tidak berubah, langkah ini tidak akan diulang.
COPY pom.xml .

# Salin seluruh source code proyek
COPY src ./src

# Jalankan perintah Maven untuk membangun aplikasi menjadi file .jar
# -DskipTests digunakan untuk melewati pengujian agar proses build lebih cepat
RUN mvn clean package -DskipTests


# --- Tahap 2: Run Aplikasi ---
# Menggunakan image JRE (Java Runtime Environment) yang lebih kecil dan ringan
FROM eclipse-temurin:17-jre-alpine

# Tentukan direktori kerja
WORKDIR /app

# Salin file .jar yang sudah di-build dari tahap sebelumnya
COPY --from=build /app/target/*.jar app.jar

# Buka port 8080 (port default Spring Boot) di dalam container
EXPOSE 8080

# Perintah untuk menjalankan aplikasi saat container dimulai
ENTRYPOINT ["java", "-jar", "app.jar"]