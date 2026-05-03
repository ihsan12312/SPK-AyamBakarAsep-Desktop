-- ============================================================
-- DATA SAMPLE SPK AYAM BAKAR ASEP
-- ============================================================

USE spk_ayambakar;

-- Hapus data lama (urutan: hasil, nilai, alternatif, kriteria, admin)
DELETE FROM tb_hasil;
DELETE FROM tb_sesi;
DELETE FROM tb_nilai;
DELETE FROM tb_alternatif;
DELETE FROM tb_kriteria;
DROP TABLE IF EXISTS tb_admin;

-- ============================================================
-- ADMIN (Tabel Baru)
-- ============================================================
CREATE TABLE tb_admin (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    no_hp VARCHAR(20),
    alamat TEXT,
    nama_ibu_kandung VARCHAR(100) NOT NULL
);

INSERT INTO tb_admin (username, password, nama_lengkap, no_hp, alamat, nama_ibu_kandung) VALUES
('admin', 'admin', 'Administrator Utama', '081234567890', 'Jl. Merdeka No. 1', 'Fatimah');

-- Reset auto increment
ALTER TABLE tb_kriteria   AUTO_INCREMENT = 1;
ALTER TABLE tb_alternatif AUTO_INCREMENT = 1;
ALTER TABLE tb_sesi        AUTO_INCREMENT = 1;

-- ============================================================
-- KRITERIA (8 kriteria, total bobot = 100%)
-- ============================================================
INSERT INTO tb_kriteria (nama_kriteria, jenis, bobot, satuan, keterangan) VALUES
('Harga',              'cost',    20, 'Ribu Rp',  'Semakin murah semakin baik'),
('Cita Rasa',          'benefit', 25, 'Poin',     'Nilai rasa (1-10)'),
('Ukuran Porsi',       'benefit', 15, 'Poin',     'Besarnya sajian (1-10)'),
('Popularitas',        'benefit', 10, 'Poin',     'Tingkat peminat (1-10)'),
('Waktu Penyajian',    'cost',    10, 'Menit',    'Semakin cepat semakin baik'),
('Rating Pelanggan',   'benefit', 10, 'Bintang',  'Rating rata-rata pelanggan (1-10)'),
('Ketersediaan Bahan', 'benefit',  5, 'Poin',     'Kemudahan mendapat bahan (1-10)'),
('Nilai Gizi',         'benefit',  5, 'Poin',     'Kandungan gizi (1-10)');

-- ============================================================
-- ALTERNATIF / PAKET MENU (6 paket)
-- Note: kolom tb_alternatif perlu disesuaikan (alter table jika ada, atau buat baru)
-- Asumsi tabel sudah ada, maka kita alter:
-- ============================================================
ALTER TABLE tb_alternatif ADD COLUMN kode_makanan VARCHAR(20) AFTER id_alternatif;
ALTER TABLE tb_alternatif MODIFY COLUMN harga VARCHAR(50);

INSERT INTO tb_alternatif (kode_makanan, nama_paket, deskripsi, harga, is_active) VALUES
('PKT-001', 'Paket Hemat',    'Ayam 1/4 + Nasi + Es Teh',                     'Sangat Murah',  1),
('PKT-002', 'Paket Spesial',  'Ayam 1/2 + Nasi + Lalapan + Es Jeruk',          'Murah',  1),
('PKT-003', 'Paket Premium',  'Ayam Utuh + Nasi + Lalapan + Soup + Jus',       'Rata-rata',  1),
('PKT-004', 'Paket Keluarga', '2 Ayam + Nasi 4P + Lalapan + 4 Minuman',       'Sangat Mahal',  1),
('PKT-005', 'Paket Mini',     'Paha Bawah + Nasi Kecil + Teh Hangat',          'Sangat Murah',  1),
('PKT-006', 'Paket Jumbo',    '2 Ayam + Nasi Besar + Sambal Komplit + Jus',    'Mahal',  1);

-- ============================================================
-- NILAI MATRIKS (id_alternatif x id_kriteria)
-- Kriteria: 1=Harga, 2=Rasa, 3=Porsi, 4=Popularitas,
--           5=WaktuSaji, 6=Rating, 7=Ketersediaan, 8=Gizi
-- ============================================================

-- Paket Hemat (id=1)
INSERT INTO tb_nilai (id_alternatif, id_kriteria, nilai) VALUES
(1,1,25),(1,2,7),(1,3,6),(1,4,9),(1,5,15),(1,6,7),(1,7,10),(1,8,6);

-- Paket Spesial (id=2)
INSERT INTO tb_nilai (id_alternatif, id_kriteria, nilai) VALUES
(2,1,40),(2,2,8),(2,3,8),(2,4,8),(2,5,20),(2,6,8),(2,7,9),(2,8,7);

-- Paket Premium (id=3)
INSERT INTO tb_nilai (id_alternatif, id_kriteria, nilai) VALUES
(3,1,65),(3,2,9),(3,3,9),(3,4,6),(3,5,30),(3,6,9),(3,7,8),(3,8,9);

-- Paket Keluarga (id=4)
INSERT INTO tb_nilai (id_alternatif, id_kriteria, nilai) VALUES
(4,1,120),(4,2,8),(4,3,10),(4,4,7),(4,5,45),(4,6,8),(4,7,7),(4,8,8);

-- Paket Mini (id=5)
INSERT INTO tb_nilai (id_alternatif, id_kriteria, nilai) VALUES
(5,1,18),(5,2,6),(5,3,5),(5,4,7),(5,5,10),(5,6,6),(5,7,10),(5,8,5);

-- Paket Jumbo (id=6)
INSERT INTO tb_nilai (id_alternatif, id_kriteria, nilai) VALUES
(6,1,85),(6,2,9),(6,3,10),(6,4,5),(6,5,35),(6,6,8),(6,7,7),(6,8,8);

-- ============================================================
-- VERIFIKASI
-- ============================================================
SELECT 'Total bobot:' AS info, SUM(bobot) AS nilai FROM tb_kriteria;
SELECT nama_kriteria, jenis, bobot FROM tb_kriteria;
SELECT nama_paket, harga FROM tb_alternatif;
SELECT COUNT(*) AS total_nilai FROM tb_nilai;
