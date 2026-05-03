# 🍗 SPK Ayam Bakar Asep - Keputusan Paket Menu Terbaik

Sistem Pendukung Keputusan (SPK) untuk menentukan prioritas paket menu di rumah makan Ayam Bakar Asep menggunakan metode **Simple Additive Weighting (SAW)**.

---

## 📋 Daftar Isi
1. [Fitur Utama](#-fitur-utama)
2. [Teknologi yang Digunakan](#-teknologi-yang-digunakan)
3. [Panduan Instalasi Lengkap](#-panduan-instalasi-lengkap)
4. [Tutorial Pembuatan (Step-by-Step)](#-tutorial-pembuatan-step-by-step)
5. [Struktur Folder](#-struktur-folder)

---

## 🚀 Fitur Utama
- **Autentikasi Aman:** Login, Registrasi Admin, dan Reset Password (keamanan menggunakan pertanyaan Nama Ibu Kandung).
- **Dashboard Statis:** Ringkasan jumlah data kriteria, alternatif, dan hasil perhitungan.
- **Manajemen Kriteria:** Pengelolaan bobot dan jenis kriteria (Benefit/Cost).
- **Manajemen Alternatif:** Pengelolaan data paket menu/makanan.
- **Matriks Penilaian:** Input nilai untuk setiap alternatif terhadap kriteria yang ada.
- **Perhitungan SAW:** Proses normalisasi matriks dan perangkingan otomatis.
- **Laporan & Riwayat:** Menyimpan hasil perhitungan dan mencetak laporan dalam format tabel yang rapi.
- **UI Modern:** Menggunakan tema kustom Indigo & Slate yang profesional.

---

## 🛠 Teknologi yang Digunakan
- **Bahasa Pemrograman:** Java (JDK 8)
- **IDE:** NetBeans 8.2 (Compatible up to Apache NetBeans 21)
- **Database:** MySQL / MariaDB (via XAMPP)
- **Library:** `mysql-connector-j-8.3.0.jar` (Konektor database)
- **Framework UI:** Java Swing (Native)

---

## 📥 Panduan Instalasi Lengkap

### 1. Persiapan Database
1. Buka **XAMPP Control Panel** dan jalankan service **Apache** & **MySQL**.
2. Masuk ke browser, ketik `localhost/phpmyadmin`.
3. Buat database baru dengan nama `spk_ayambakar`.
4. Klik tab **Import**, pilih file `data_sample.sql` dari folder projek ini, lalu klik **Go**.

### 2. Konfigurasi Projek di NetBeans
1. Buka NetBeans (Rekomendasi: 8.2 atau versi terbaru).
2. Pilih **File** > **Open Project** > arahkan ke folder `SPK-AyamBakarAsep-Desktop`.
3. Jika ada icon "kunci" atau tanda seru merah pada folder **Libraries**:
   - Klik kanan projek > **Properties**.
   - Pilih **Libraries** > Klik **Add JAR/Folder**.
   - Cari file `mysql-connector-j-8.3.0.jar` di dalam folder `lib` projek ini.
4. Pastikan **JDK** yang digunakan adalah JDK 8 atau yang setara.

### 3. Menjalankan Aplikasi
1. Klik kanan pada projek di panel kiri.
2. Pilih **Clean and Build** (Tunggu hingga selesai).
3. Klik kanan lagi, pilih **Run**.
4. Login menggunakan akun default:
   - **Username:** `admin`
   - **Password:** `admin`

---

## 🛠 Tutorial Pembuatan (Step-by-Step)

Berikut adalah ringkasan proses pembuatan sistem ini dari awal:

### Langkah 1: Desain Database (ERD)
Pertama, kami merancang skema database MySQL dengan 5 tabel utama:
- `tb_admin`: Menyimpan data user pengelola.
- `tb_kriteria`: Parameter penilaian (Harga, Rasa, Porsi, dll).
- `tb_alternatif`: Data paket menu yang akan dinilai.
- `tb_nilai`: Matriks nilai (penghubung alternatif dan kriteria).
- `tb_hasil`: Hasil akhir perangkingan SAW.

### Langkah 2: Pembuatan Koneksi (Singleton Pattern)
Membuat class `DBConnection.java` di package `spk.config` menggunakan pola *Singleton* agar koneksi database hanya dibuat satu kali selama aplikasi berjalan, sehingga lebih hemat *resource*.

### Langkah 3: Implementasi DAO (Data Access Object)
Memisahkan logika database dari UI. Setiap tabel memiliki class DAO (di package `spk.dao`) untuk melakukan operasi CRUD (Create, Read, Update, Delete) sehingga kode lebih rapi dan mudah di-maintain.

### Langkah 4: Logika Perhitungan SAW
Implementasi rumus SAW di `SAWCalculator.java`:
1. **Normalisasi:** Membagi nilai dengan nilai maksimal (untuk benefit) atau nilai minimal dibagi nilai (untuk cost).
2. **Perangkingan:** Mengalikan hasil normalisasi dengan bobot kriteria, lalu menjumlahkannya untuk mendapatkan nilai akhir (V).

### Langkah 5: Desain UI (Java Swing)
Membangun antarmuka menggunakan `JFrame` dan `JPanel`:
- `MainFrame`: Frame utama yang menampung sidebar navigasi.
- `UITheme`: Class khusus untuk mengatur warna (Indigo/Slate), font, dan styling komponen agar terlihat modern.
- Form dinamis untuk input data kriteria dan alternatif.

### Langkah 6: Integrasi & Testing
Menghubungkan semua komponen, melakukan testing fungsionalitas login, validasi input, hingga verifikasi keakuratan hasil perhitungan dengan perhitungan manual.

---

## 📂 Struktur Folder
```text
SPK-AyamBakarAsep-Desktop/
├── src/
│   └── spk/
│       ├── config/    # Konfigurasi Database
│       ├── dao/       # Logika Query SQL
│       ├── model/     # Representasi Objek Data
│       ├── ui/        # Desain Antarmuka (Swing)
│       └── util/      # Logika SAW & Helper
├── lib/               # Driver Database (.jar)
├── data_sample.sql    # Database Dump
└── README.md          # Dokumentasi (File ini)
```

---
**Dibuat oleh:** [SPK Ayam Bakar Asep Team]
**Lisensi:** Terbuka untuk keperluan edukasi.
