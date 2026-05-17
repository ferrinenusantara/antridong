# Antridong!
ini adalah aplikasi manajemen antrian pengunjung berbasis Web (Spring Boot) yang dibungkus atau dikontrol menggunakan JavaFX sebagai GUI Control Panel lokalnya (berjalan dalam satu kesatuan/lifecycle bersama)
- **Teknologi**: Java Spring Boot, Spring Security, Thymeleaf, Bootstrap 5.3.
- **Database**: Embedded SQLite, Ebean ORM.
- **Logging**: Java standard log, disimpan ke output file di direktori `logs`, rotasi log otomatis, pertahankan log di ukuran maksimal 2Mb.
- **Real-time Update**: Menggunakan WebSockets untuk push notifikasi perubahan data ke setiap dashboard.
- **Voice mode caller**: Memanggil antrian dengan mode suara: "Nomor antrian [XXXX] ke counter [YYYY]"