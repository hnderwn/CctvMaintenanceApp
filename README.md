Aplikasi Manajemen Maintenance CCTV (CctvMaintenanceApp) 🛠️

Merupakan sebuah aplikasi desktop berbasis Java Swing yang dirancang untuk menjadi solusi terpusat dalam pengelolaan siklus hidup pemeliharaan dan perbaikan unit CCTV di sebuah lingkungan organisasi atau perusahaan.
Abstrak 📝

Dalam lingkungan yang bergantung pada sistem keamanan visual, manajemen aset CCTV yang efektif menjadi krusial. Aplikasi ini hadir untuk menjawab tantangan tersebut dengan menyediakan platform untuk mendata aset (model dan unit CCTV), mengelola sumber daya manusia (teknisi), serta mencatat setiap aktivitas yang terjadi, mulai dari perawatan rutin, laporan insiden kerusakan, hingga penggunaan komponen atau suku cadang. Dengan alur kerja yang terstruktur dan pencatatan yang detail, aplikasi ini bertujuan untuk meningkatkan efisiensi operasional, memperpanjang umur ekonomis perangkat, dan memastikan sistem keamanan selalu dalam kondisi optimal.
Fitur Utama ✨

Aplikasi ini dilengkapi dengan serangkaian fitur komprehensif untuk mendukung manajemen CCTV secara menyeluruh:
1. Manajemen Data Master 🗃️

Modul untuk mengelola seluruh data inti yang menjadi fondasi operasional sistem.

    Data Model CCTV: Pencatatan detail spesifikasi teknis dari setiap model CCTV, meliputi manufaktur, resolusi, fitur khusus, dan estimasi umur ekonomis.

    Data Unit CCTV: Pengelolaan setiap unit CCTV yang terpasang, mencakup ID unik, lokasi pemasangan, status operasional (Aktif, Perbaikan, dll.), dan model yang digunakan.

    Data Teknisi: Manajemen data para teknisi yang bertanggung jawab atas maintenance dan perbaikan.

    Data Komponen: Inventarisasi suku cadang atau komponen yang digunakan, lengkap dengan informasi stok dan harga satuan untuk perhitungan biaya.

2. Pencatatan Aktivitas (Logging) ✍️

Sistem pencatatan transaksional untuk melacak setiap aktivitas yang berhubungan dengan unit CCTV.

    Log Maintenance: Mencatat kegiatan perawatan rutin dan terjadwal untuk memastikan performa CCTV tetap terjaga.

    Log Kerusakan: Mencatat laporan insiden atau kerusakan yang terjadi pada unit CCTV secara reaktif.

    Log Pemakaian Komponen: Melacak setiap komponen yang digunakan dalam proses perbaikan atau maintenance, yang terhubung langsung dengan log kerusakan atau maintenance terkait.

3. Logika Bisnis Otomatis ⚙️

    Pembaruan Status Cerdas: Status sebuah unit CCTV akan diperbarui secara otomatis berdasarkan log yang dibuat. Contoh: status berubah menjadi Perbaikan saat log kerusakan baru ditambahkan.

    Manajemen Stok Transaksional: Proses penambahan atau pembaruan log pemakaian komponen secara otomatis mengurangi atau menyesuaikan stok komponen di database. Seluruh operasi (simpan log, update stok, update status CCTV) dibungkus dalam transaksi database untuk menjamin integritas data.

4. Dashboard & Pelaporan 📊

    Dashboard Utama: Menyajikan ringkasan visual dari data-data kunci, seperti jumlah CCTV yang aktif, CCTV yang bermasalah, dan total teknisi terdaftar.

    Sistem Pelaporan: Kemampuan untuk menghasilkan laporan dalam format dokumen dari setiap modul data menggunakan JasperReports.

5. Keamanan 🔒

    Autentikasi Pengguna: Sistem login yang aman dengan mekanisme password hashing menggunakan algoritma BCrypt.

Arsitektur & Teknologi 💻

Aplikasi ini dibangun menggunakan arsitektur 3-tier sederhana (Presentation, Logic, Data Access) dan didukung oleh teknologi berikut:

    Bahasa Pemrograman: Java (JDK 18).

    Framework Antarmuka: Java Swing sebagai framework utama untuk membangun antarmuka pengguna grafis (GUI).

    Sistem Manajemen Basis Data: MySQL / MariaDB (direkomendasikan melalui XAMPP) untuk penyimpanan data.

    Build & Dependency Management: Apache Maven untuk mengelola dependensi proyek dan proses kompilasi.

    Library Utama:

        FlatLaf: Library Look and Feel untuk memberikan estetika modern dan responsif pada antarmuka Swing.

        JCalendar (Toedter): Library yang menyediakan komponen JDateChooser untuk pemilihan tanggal yang intuitif.

        jBCrypt: Implementasi algoritma BCrypt untuk hashing password pengguna.

        JasperReports: Library open-source yang powerful untuk kebutuhan pembuatan laporan.

        MySQL Connector/J: Driver JDBC untuk menjembatani komunikasi antara aplikasi Java dengan database MySQL.

Panduan Instalasi dan Konfigurasi 🚀

Berikut adalah panduan langkah demi langkah untuk menginstal dan menjalankan proyek ini di lingkungan lokal.
Prasyarat Sistem ✅

Pastikan sistem Anda memenuhi prasyarat berikut:

    Java Development Kit (JDK): Versi 18 atau yang lebih baru.

    Apache Maven: Terinstal dan terkonfigurasi pada PATH sistem.

    Server MySQL/MariaDB: Disarankan menggunakan XAMPP untuk kemudahan setup.

Langkah-langkah Instalasi 🪜

    Kloning Repositori:
    Gunakan Git untuk mengkloning repositori ini ke mesin lokal Anda.

    git clone https://github.com/hnderwn/CctvMaintenanceApp.git
    cd CctvMaintenanceApp

    Ganti [NAMA_USER_GITHUB_KAMU] dengan username GitHub kamu ya.

    Konfigurasi Basis Data (Database):
    Aplikasi ini memerlukan database db_cctv_maintenance untuk dapat beroperasi.

        Jalankan layanan Apache dan MySQL dari XAMPP Control Panel.

        Buka phpMyAdmin (http://localhost/phpmyadmin) pada browser Anda.

        Buat database baru dengan nama persis db_cctv_maintenance.

        Pilih database yang baru dibuat tersebut, lalu navigasi ke tab "Import".

        Klik "Choose File" dan pilih file db_cctv_maintenance(2).sql yang ada di dalam direktori proyek.

        Gulir ke bawah dan klik tombol "Import". Proses ini akan membuat semua tabel yang dibutuhkan beserta data awalnya.

        Akun pengguna default yang siap digunakan adalah:

            Username: admin

            Password: admin

    Konfigurasi Koneksi Aplikasi:
    Secara default, aplikasi dikonfigurasi untuk terhubung ke server MySQL lokal dengan user root dan tanpa password. Jika konfigurasi Anda berbeda, lakukan penyesuaian.

        Buka file: src/main/java/com/kelompok4/cctvapp/util/DbConnector.java

        Ubah nilai konstanta DB_USER dan DB_PASS sesuai dengan konfigurasi database Anda.

    Kompilasi Proyek Menggunakan Maven:
    Buka Terminal atau Command Prompt, arahkan ke direktori root proyek (CctvMaintenanceApp), dan jalankan perintah berikut. Perintah ini akan mengunduh semua dependensi yang dibutuhkan (seperti FlatLaf, JCalendar, dll.) dan mengkompilasi kode sumber.

    mvn clean install

    Jalankan Aplikasi:
    Setelah proses build selesai, Anda bisa menjalankan aplikasi dari terminal:

    java -jar target/CctvMaintenanceApp-1.0-SNAPSHOT.jar

    Catatan: Nama file .jar mungkin bervariasi tergantung versi.

Cara Menggunakan Aplikasi 💡

    Login: Masukkan username dan password yang telah terdaftar. Jika ini pertama kali, mungkin ada default akun atau Anda perlu membuat akun secara manual di database.

    Manajemen Data Master: Gunakan panel "Data Master" untuk menambahkan, mengedit, atau menghapus data model CCTV, unit, teknisi, dan komponen.

    Pencatatan Aktivitas: Catat setiap maintenance atau kerusakan yang terjadi pada unit CCTV, serta penggunaan komponen.

    Lihat Laporan: Gunakan fitur pelaporan untuk menghasilkan dokumen ringkasan dari data yang ada.

Kontribusi 🤝

Kami sangat terbuka untuk kontribusi! Jika Anda ingin berkontribusi pada proyek ini, silakan ikuti langkah-langkah berikut:

    Fork repositori ini.

    Buat branch baru (git checkout -b feature/nama-fitur-baru).

    Lakukan perubahan Anda.

    Commit perubahan Anda (git commit -m 'Tambahkan fitur baru').

    Push ke branch Anda (git push origin feature/nama-fitur-baru).

    Buka Pull Request.

Lisensi 📜

Proyek ini dilisensikan di bawah Lisensi MIT. Lihat file LICENSE untuk detail lebih lanjut.
Kontak 📧

Jika Anda memiliki pertanyaan atau saran, jangan ragu untuk menghubungi:

    [WA: +62 812-9727-1468]
