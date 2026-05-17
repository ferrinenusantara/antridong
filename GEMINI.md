# GEMINI Context Guide: Antridong Web Apps
Dokumen ini adalah panduan konteks bagi asisten AI (Gemini) untuk memahami aturan, standar, dan arsitektur proyek Web Apps untuk keperluan manajemen antrian pengunjung ini. Semua saran kode dan pengembangan fitur wajib mematuhi ketentuan di bawah ini.

---
## Project Overview & Architecture
- **Deskripsi**: Aplikasi manajemen antrian pengunjung berbasis Web (Spring Boot) yang dibungkus atau dikontrol menggunakan JavaFX sebagai GUI Control Panel lokalnya (berjalan dalam satu kesatuan/lifecycle bersama).
- **Teknologi**: Java Spring Boot, Spring Security (untuk proteksi halaman web), Thymeleaf, Bootstrap 5.3, wajib touchscreen-friendly.
- **Database**: Embedded SQLite, Ebean ORM.
- **Logging**: Java standard log, disimpan ke output file di direktori `logs`, rotasi log otomatis, pertahankan log di ukuran maksimal 2Mb.
- **Real-time Update**: Menggunakan WebSockets untuk push notifikasi perubahan data ke setiap dashboard.

---
# Fitur

## GUI Control Panel (JavaFX)
Panel desktop untuk start, stop, restart backend web, serta memiliki textbox untuk menampilkan log warning dan severe/error dari backend.
Fitur administrasi utama dilakukan di panel ini:
- **Setup User**: Mengelola user/petugas yang bisa login ke aplikasi web.
- **Setup Kategori Antrian**: Mengelola kategori antrian. Masing-masing kategori mempunyai kode 3 karakter unik (unique code) sebagai prefix nomor antrian. Memiliki input range jam valid (hanya bisa dipilih pengunjung jika waktu server masuk dalam range). Fitur: Tambah, hapus, edit.
- **Setup Counter**: Mengelola daftar counter meja pelayanan. Bisa assign satu atau lebih kategori antrian ke counter tersebut (Many-to-Many). Fitur: Tambah, hapus, edit.

## Halaman Index (Web)
Halaman ini hanya bisa diakses apabila user sudah login. Menampilkan menu utama untuk memilih:
- Dashboard Antrian
- Dashboard Pengunjung
- Dashboard Counter (menampilkan seluruh daftar counter yang telah dibuat pada Setup Counter di GUI, tampilkan secara urut).

## Dashboard Counter (Web)
Halaman ini hanya bisa diakses apabila user sudah login. Digunakan oleh petugas counter untuk memanggil antrian.
- Memunculkan daftar tunggu antrian (Queue) berdasarkan urutan kedatangan (FIFO) sesuai kategori yang diasosiasikan ke counter tersebut.
- Tombol Panggil: Mengambil satu nomor terdepan, update status di database, dan push via WebSocket untuk memperbarui Dashboard Counter lain serta Dashboard Antrian secara real-time.
- Tombol Status: Set status counter menjadi aktif/nonaktif.
- **State Restoration**: Harus bisa merestore kondisi terakhirnya sendiri (menampilkan data yang sesuai) dari database SQLite jika server di-restart.

## Dashboard Antrian (Web)
Halaman ini hanya bisa diakses apabila user sudah login. Menampilkan daftar antrian yang sedang berjalan kepada pengunjung (layar besar/TV).
- Menerima push WebSocket saat ada panggilan, lalu memicu suara (Text-to-Speech): *"Nomor antrian [XXXX] ke counter [YYYY]"*.
- **State Restoration**: Harus bisa merestore kondisi tampilan terakhir dari database jika server di-restart.

## Dashboard Pengunjung (Web)
Halaman ini hanya bisa diakses apabila user sudah login (sebagai kios mandiri). Tempat pengunjung mengambil nomor antrian.
- Pengunjung bisa mengisi nama (opsional), memilih kategori antrian yang aktif, dan menekan tombol registrasi.
- Kategori yang di luar range jam valid otomatis berstatus *disabled*.
- **State Restoration**: Harus bisa merestore kondisi terakhir dari database jika server di-restart.

---
# Data & Database Rules
Gunakan Ebean sebagai ORM. Gaya penulisan model dan query wajib menggunakan standar Ebean (`io.ebean.Model`, `Finder`), bukan Spring Data JPA.

## Metadata Tabel (Single Source of Truth)
- Semua query database HARUS menggunakan nama tabel dan kolom yang didefinisikan dari kelas metadata/konstanta.
- Hardcoded string untuk nama tabel atau kolom TIDAK BOLEH digunakan dalam query.
- Perubahan nama tabel atau kolom hanya boleh dilakukan dari satu tempat di kelas metadata.
- Query yang tidak menggunakan metadata dianggap invalid.
- Review kode HARUS menolak query yang melanggar aturan ini.

## Struktur Tabel
Struktur tabel di database SQLite dibuat secara otomatis melalui fitur auto-generation/migration dari Ebean, tidak dibuat secara manual.