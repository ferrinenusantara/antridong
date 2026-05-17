# Rencana Pengerjaan Proyek: Antridong Web Apps

Berdasarkan panduan dari `GEMINI.md`, proyek ini adalah aplikasi antrian pengunjung berbasis Spring Boot yang dikontrol melalui JavaFX (sebagai Control Panel). Aplikasi ini menggunakan SQLite dan Ebean ORM, serta memanfaatkan WebSockets untuk pembaruan *real-time*.

Berikut adalah urutan pengerjaan yang akan kita lakukan secara bertahap:

## Fase 1: Setup & Konfigurasi Dasar
- [x] Integrasi lifecycle Spring Boot dan JavaFX.
- [x] Konfigurasi dependensi utama: Spring Boot, Ebean ORM, SQLite JDBC, WebSockets, Spring Security, dan Thymeleaf.
- [x] Setup `java.util.logging` dengan rotasi file (maksimal 2MB) yang diarahkan ke direktori `logs`.
- [x] Pembuatan struktur kelas Metadata/Konstanta untuk nama tabel dan kolom di database (Single Source of Truth).
- [x] Konfigurasi Ebean ORM agar melakukan auto-generation/migration SQLite saat aplikasi berjalan.

## Fase 2: Pembuatan Model Database (Ebean)
- [x] Buat model `User` untuk petugas yang akan login ke aplikasi web.
- [x] Buat model `KategoriAntrian` (dengan properti: kode unik 3 karakter, jam valid mulai, jam valid selesai).
- [x] Buat model `Counter` dan relasi Many-to-Many dengan `KategoriAntrian`.
- [x] Buat model `Antrian` / `QueueTicket` (menyimpan urutan, relasi ke kategori, status, dan data pengunjung).
- [x] Buat class `Finder`/Repository untuk setiap entitas menggunakan standar Ebean yang wajib merujuk ke class Metadata.

## Fase 3: Pengembangan GUI Control Panel (JavaFX)
- [x] Buat tampilan antarmuka utama (Tombol Start, Stop, Restart untuk backend Spring Boot).
- [x] Integrasikan textbox/log viewer untuk menangkap dan menampilkan log berlevel `WARNING` dan `SEVERE` dari backend.
- [x] Buat antarmuka dan fungsi CRUD untuk **Setup User**.
- [x] Buat antarmuka dan fungsi CRUD untuk **Setup Kategori Antrian**.
- [x] Buat antarmuka dan fungsi CRUD untuk **Setup Counter** (termasuk assign kategori antrian).

## Fase 4: Core Backend Spring Boot & WebSockets
- [ ] Setup Spring Security dengan login form custom untuk melindungi halaman dashboard.
- [ ] Konfigurasi Spring WebSockets & STOMP untuk mengirim push notifikasi secara real-time.
- [ ] Buat service untuk mengelola *State Restoration* agar state terakhir bisa dibaca kembali dari SQLite ketika server di-restart.

## Fase 5: Pengembangan Tampilan Web (Thymeleaf + Bootstrap 5.3)
- [ ] Buat layout utama yang responsif dan touchscreen-friendly menggunakan Bootstrap 5.3.
- [ ] **Halaman Index**: Halaman menu utama bagi user yang sudah login.
- [ ] **Dashboard Pengunjung**: Halaman kios mandiri (touchscreen) untuk mengambil antrian, lengkap dengan form (nama opsional) dan validasi kategori antrian berdasarkan jam (disable/enable otomatis).
- [ ] **Dashboard Counter**: Halaman petugas dengan daftar antrian FIFO, fungsi panggil antrian (meng-update DB & mengirim event WebSocket), serta tombol toggle status aktif/nonaktif.
- [ ] **Dashboard Antrian**: Layar informasi/TV antrian untuk pengunjung yang secara real-time merespons event WebSocket untuk melakukan pemanggilan antrian dan membunyikan suara (Text-to-Speech).

## Fase 6: Testing, Polish & Packaging
- [ ] End-to-End Testing alur antrian (dari pengambilan tiket di kios hingga pemanggilan di counter).
- [ ] Pengujian fungsi *State Restoration* dengan merestart server.
- [ ] Packaging aplikasi ke format yang mudah didistribusikan.
