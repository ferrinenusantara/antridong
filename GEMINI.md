# GEMINI Context Guide: Antridong Web Apps
Dokumen ini adalah panduan konteks bagi asisten AI (Gemini) untuk memahami aturan, standar, dan arsitektur proyek Web Apps untuk keperluan manajemen antrian pengunjung ini. Semua saran kode dan pengembangan fitur wajib mematuhi ketentuan di bawah ini.

---
## Project Overview & Architecture
- **Deskripsi**: Aplikasi manajemen antrian pengunjung berbasis Web (Spring Boot) yang dibungkus atau dikontrol menggunakan JavaFX sebagai GUI Control Panel lokalnya (berjalan dalam satu kesatuan/lifecycle bersama)
- **Teknologi**: Java Spring Boot, Thymeleaf, Bootstrap 5.3, wajib touchscreen-friendly.
- **Database**: Embedded SQLite, Ebean ORM.
- **Logging**: Java standard log, disimpan ke output file di direktori `logs`, rotasi log otomatis, pertahankan log di ukuran maksimal 2Mb.
- **Real-time Update**: Menggunakan WebSockets untuk push notifikasi perubahan data ke setiap dashboard.

---
# Fitur
## GUI Control Panel
panel untuk start, stop, restart, serta textbox untuk menampilkan log warning dan severe/error dari backend web
Setup Kategori, Setup Counter, Setup User dilakukan di GUI Control panel. 



### Setup Kategori Antrian 
Digunakan untuk mengelola kategori antrian. Masing-masing kategori mempunyai kode 3 karakter unik (unique code) yang akan digunakan sebagai prefix nomor antrian.
- Fitur: Tambah, hapus, dan edit.
- Validasi Waktu: Terdapat input untuk range jam valid (kategori hanya bisa dipilih oleh pengunjung saat waktu server berada di dalam range jam tersebut).



### Setup Counter
Digunakan untuk mengelola daftar counter meja pelayanan.
- Fitur: Tambah, hapus, dan edit.
- Asosiasi: Bisa melakukan assign satu atau lebih kategori antrian ke counter yang bersangkutan (One-to-Many/Many-to-Many).


## Halaman Index
Halaman ini hanya bisa diakses apabila sudah login.
Halaman index menampilkan menu utama untuk memilih:
- Dashboard Antrian
- Dashboard Pengunjung
- Dashboard Counter (dimunculkan semua list counter yang telah dibuat pada Setup Counter)


## Dashboard Counter
Halaman ini hanya bisa diakses apabila sudah login.
Digunakan oleh petugas counter untuk memanggil antrian sesuai dengan kategori yang diasosiasikan pada counter tersebut.
- Di dashboard akan dimunculkan daftar tunggu antrian (Queue) berdasarkan urutan kedatangan (FIFO), sesuai dengan kategori yang diasosiasikan ke counter.
- Saat tombol panggil ditekan, sistem mengambil satu nomor terdepan dari antrian, memperbarui status di database, dan otomatis memperbarui (push via WebSocket) data di Dashboard Counter lain serta Dashboard Antrian utama.
- disini juga bisa ada tombol untuk set status counter aktif/nonaktif.
Halaman ini harus bisa merestore dirinya sendiri saat server di restart.

## Dashboard Antrian
Halaman ini hanya bisa diakses apabila sudah login.
Menampilkan daftar antrian yang sedang berjalan kepada pengunjung (layar besar/TV).
- Saat ada update panggilan antrian, dashboard akan memicu suara (voice/Text-to-Speech) yang sesuai: *"Nomor antrian [XXXX] ke counter [YYYY]"*.
Halaman ini harus bisa merestore dirinya sendiri saat server di restart.

## Dashboard Pengunjung
Halaman ini hanya bisa diakses apabila sudah login.
Kios mandiri tempat pengunjung mengambil nomor antrian.
- Pengunjung bisa mengisi nama (opsional), memilih kategori antrian yang aktif, dan menekan tombol registrasi untuk mencetak/mendapatkan nomor.
- Kategori antrian yang sudah di luar range waktu valid akan otomatis berstatus *disabled* (tidak bisa dipilih).
Halaman ini harus bisa merestore dirinya sendiri saat server di restart.

---
# Data & Database Rules
Gunakan Ebean sebagai ORM.

## Metadata Tabel (Single Source of Truth)
- Semua query database HARUS menggunakan nama tabel dan kolom yang didefinisikan dari kelas metadata/konstanta.
- Hardcoded string untuk nama tabel atau kolom TIDAK BOLEH digunakan dalam query.
- Perubahan nama tabel atau kolom hanya boleh dilakukan dari satu tempat di kelas metadata.
- Query yang tidak menggunakan metadata dianggap invalid.
- Review kode HARUS menolak query yang melanggar aturan ini.

## Struktur Tabel
Struktur tabel di database SQLite dibuat secara otomatis melalui fitur auto-generation/migration dari Ebean, tidak dibuat secara manual.