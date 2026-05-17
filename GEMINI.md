# GEMINI Context Guide: Antridong Web Apps
Dokumen ini adalah panduan konteks bagi asisten AI (Gemini) untuk memahami aturan, standar, dan arsitektur proyek Web Apps untuk keperluan management antrian pengunjung ini. Semua saran kode dan pengembangan fitur wajib mematuhi ketentuan di bawah ini.

---
## Project Overview & Architecture
- **Deskripsi**: management antrian
- **Type**: Web-based, dengan GUI control panel untuk start, stop, restart, serta textbox untuk menampilkan log warning dan servere dari backend web. 
- **Teknologi**: Java Spring Boot, thymeleaf, bootstrap 5.3, wajib touchscreen friendly.
- **Database**: Embeded, SQLite
- **Logging**: Java standard log, simpat ke output file di direktori logs, rotasi log, pertahankan log di ukuran maksimal 2Mb


---
# Fitur
## Index
Halaman index menampilkan menu utama, untuk memilih:
- Setup Kategori
- Setup Counter
- Dashboard Counter
- Dashboard Antrian
- Dashboard Pengunjung

## Setup Kategori Antrian
Digunakan untuk manage kategori antrian, dimana masing-masing kategori mempunyai kode 3 character uniqe yang akan digunakan sebagai prefix nomor antrian.
Di halaman ini juga bisa menambah, menghapus dan edit.
Di masing-masing kategori antrian, ada input untuk range jam valid (maksudnya kategori tersebut hanya bisa dipilih saat masih ada di range jam yang sesuai).


## Setup Counter
Digunakan untuk manage daftar counter. Bisa menambah, menghapus dan edit.
Di halaman ini juga bisa melakukan assign kategori antrian yang akan diasosiasiskan ke counter yang bersangkutan.
Satu counter bisa menangani lebih dari satu kategori antrian.

## Dashboard Counter
Digunakan oleh penanggung jawab counter untuk memanggil antrian sesuai dengan kategori yang diasosiasikan di counter tersebut.
Di dashboard akan dimunculkan stack antrian, dan tombol untuk memanggil antrian.
Saat tombol antrian ditekan, akan diambil satu peserta dari stack antrian, dan program akan otomatis mengupdate seluruh dashboard counter yang dibuka (bisa menggunakan push), dan mengupdate dashboard Antrian.

## Dashboard Antrian
Menapilkan daftar antrian ke pengunjung. Saat ada update panggilan antrian, dashboard bisa mengeluarkan voice yang sesuai: "nomor antrian xxxx ke counter yyyy"


## Dashboard Pengunjung
Di dashboard ini pengujung bisa mengisi nama (opsional), memilih kategori antrian dan menekan tombol registrasi.
kategori antrian yang sudah tidak valid range waktunya akan disable (tidak bisa dipilih)





