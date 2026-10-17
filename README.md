# TP5 DPBO – PeopleData (Java Swing GUI + MySQL)

Tugas Praktikum 5 – Desain Pemrograman Berorientasi Objek (Modul 5: Java Swing GUI – Database dan Back-End).

Program ini adalah pengembangan project **PeopleData** dari TP4. Jika sebelumnya data disimpan di `ArrayList` (hilang saat program ditutup), sekarang seluruh operasi **CRUD** (Create, Read, Update, Delete) terhubung ke database **MySQL** (`db_people`) melalui **JDBC**.

---

## Daftar Isi
1. [Struktur Folder](#struktur-folder)
2. [Spesifikasi yang Dipenuhi](#spesifikasi-yang-dipenuhi)
3. [Desain Program](#desain-program)
4. [Penjelasan Alur](#penjelasan-alur)
5. [Cara Menjalankan](#cara-menjalankan)
6. [Dokumentasi Program Berjalan](#dokumentasi-program-berjalan)

---

## Struktur Folder

```
TP5DPBO2526C2/
├── PeopleData/          # source code project Java (termasuk folder lib berisi MySQL Connector)
│   ├── lib/
│   │   └── mysql-connector-j-x.x.x.jar
│   └── src/
│       ├── Database.java
│       ├── Person.java
│       ├── PeopleMenu.java
│       ├── PeopleMenu.form
│       └── Main.java
├── Dokumentasi/         # screenshot / GIF yang ditampilkan di README
├── db_people.sql        # file database
└── README.md
```

---

## Spesifikasi yang Dipenuhi

| No | Spesifikasi | Status |
|----|-------------|--------|
| 1 | Semua proses CRUD terhubung dengan database MySQL | ✅ |
| 2 | Penggunaan variabel `ArrayList` dihapus | ✅ |
| 3 | Dialog/prompt error jika masih ada kolom input kosong saat insert/update | ✅ |
| 4 | Dialog/prompt error jika ID sudah ada saat insert | ✅ |

---

## Desain Program

### 1. Struktur Tabel Database

Database: `db_people`, tabel: `person`

| Kolom | Tipe Data | Keterangan |
|-------|-----------|------------|
| `id` | VARCHAR(20) | Primary key |
| `nama` | VARCHAR(100) | Nama person |
| `thn_lahir` | INT | Tahun lahir |
| `kategori` | VARCHAR(50) | Kategori person |
| `level` | INT | Level (1–10), diatur lewat slider |

```sql
CREATE DATABASE IF NOT EXISTS db_people;
USE db_people;

CREATE TABLE IF NOT EXISTS person (
    id        VARCHAR(20)  PRIMARY KEY,
    nama      VARCHAR(100) NOT NULL,
    thn_lahir INT          NOT NULL,
    kategori  VARCHAR(50)  NOT NULL,
    level     INT          NOT NULL
);
```

### 2. Desain Kelas

| Kelas | Peran |
|-------|-------|
| `Database` | Mengelola koneksi JDBC ke MySQL dan menyediakan method eksekusi query. |
| `Person` | Model data person (id, nama, tahun lahir, kategori, level). |
| `PeopleMenu` | Tampilan GUI (JFrame) sekaligus pengendali logika CRUD. |
| `Main` | Entry point program. |

**Kelas `Database`**

| Member | Keterangan |
|--------|------------|
| `Connection connection` | Koneksi ke `jdbc:mysql://localhost:3306/db_people` |
| `Statement statement` | Objek untuk mengeksekusi query |
| `Database()` | Constructor: membuka koneksi dan membuat statement |
| `selectQuery(String sql)` | Untuk `SELECT`, mengembalikan `ResultSet` |
| `insertUpdateDeleteQuery(String sql)` | Untuk `INSERT`, `UPDATE`, `DELETE`, mengembalikan jumlah baris terpengaruh |
| `getStatement()` | Getter statement |

**Kelas `PeopleMenu`**

| Member | Keterangan |
|--------|------------|
| `Database database` | Objek database (menggantikan `ArrayList<Person>`) |
| `int selectedIndex` | Index baris tabel yang dipilih (`-1` artinya tidak ada yang dipilih) |
| `setTable()` | Mengambil semua data dari database lalu mengisi `DefaultTableModel` |
| `insertData()` | Validasi input, cek ID duplikat, lalu `INSERT` |
| `updateData()` | Validasi input, lalu `UPDATE` berdasarkan ID |
| `deleteData()` | Konfirmasi, lalu `DELETE` berdasarkan ID |
| `clearForm()` | Mengosongkan form dan mengembalikan tombol ke mode "Add" |

### 3. Desain Antarmuka (GUI)

Komponen utama pada form:

| Komponen | Fungsi |
|----------|--------|
| `idField` | Input ID (tidak dapat diubah saat mode update) |
| `namaField` | Input nama |
| `tahunLahirField` | Input tahun lahir (harus angka) |
| `kategoriComboBox` | Pilihan kategori (placeholder `???`) |
| `levelSlider` + `levelValueLabel` | Pengatur level 1–10 beserta nilai yang ditampilkan |
| `addUpdateButton` | Tombol **Add** (mode tambah) atau **Update** (mode ubah) |
| `deleteButton` | Tombol **Delete**, hanya tampil saat baris dipilih |
| `cancelButton` | Mengosongkan form |
| `peopleTable` | Tabel data person dari database |

---

## Penjelasan Alur

### Alur Umum

```
GUI (PeopleMenu)  →  Database (JDBC)  →  MySQL (db_people)
        ↑                                      │
        └────────── ResultSet / hasil ─────────┘
```

1. Saat program dijalankan, constructor `PeopleMenu` membuat objek `Database` yang membuka koneksi ke MySQL.
2. `setTable()` dipanggil untuk menjalankan `SELECT * FROM person` lalu menampilkan hasilnya di tabel.
3. Setiap operasi tambah/ubah/hapus mengirim query lewat `Database`, lalu tabel dimuat ulang dengan `setTable()` agar tampilan selalu sinkron dengan database.

### 1. Read (Menampilkan Data)
- Dijalankan otomatis saat program dibuka dan setiap selesai insert, update, atau delete.
- Query: `SELECT * FROM person`.
- Setiap baris `ResultSet` dimasukkan ke `DefaultTableModel` lalu dipasang ke `peopleTable`.

### 2. Create (Insert)
1. Pengguna mengisi semua field lalu menekan **Add**.
2. **Validasi kolom kosong**: jika ada field kosong atau kategori masih `???`, muncul dialog error dan proses dihentikan.
3. **Validasi tahun**: jika tahun lahir bukan angka, muncul dialog error.
4. **Validasi ID duplikat**: program menjalankan `SELECT id FROM person WHERE id = '...'`. Jika ada hasil, muncul dialog error dan proses dihentikan.
5. Jika lolos semua validasi, program menjalankan `INSERT INTO person VALUES (...)`.
6. Tabel dimuat ulang, form dikosongkan, dan muncul dialog "Data berhasil ditambahkan".

### 3. Update
1. Pengguna mengklik baris pada tabel. `MouseListener` mengambil nilai tiap kolom langsung dari model tabel (tanpa `ArrayList`) dan mengisinya ke form. Slider level ikut menyesuaikan.
2. Tombol berubah menjadi **Update**, tombol **Delete** muncul, dan field ID dikunci karena merupakan primary key.
3. Pengguna mengubah data lalu menekan **Update**.
4. **Validasi kolom kosong** dijalankan lagi, dengan dialog error yang sama seperti pada insert.
5. Jika valid, program menjalankan `UPDATE person SET ... WHERE id = '...'`.
6. Tabel dimuat ulang, form dikosongkan, dan muncul dialog "Data berhasil diubah".

### 4. Delete
1. Pengguna mengklik baris pada tabel, lalu menekan **Delete**.
2. Muncul dialog konfirmasi (Yes/No).
3. Jika **Yes**, program menjalankan `DELETE FROM person WHERE id = '...'`.
4. Tabel dimuat ulang, form dikosongkan, dan muncul dialog "Data berhasil dihapus".

### Daftar Dialog Error

| Kondisi | Dialog |
|---------|--------|
| Ada kolom kosong saat **insert** | "Semua kolom harus diisi!" |
| Ada kolom kosong saat **update** | "Semua kolom harus diisi!" |
| ID sudah ada saat **insert** | "ID '...' sudah digunakan!" |
| Tahun lahir bukan angka | "Tahun Lahir harus berupa angka!" |

---

## Cara Menjalankan

1. Nyalakan **MySQL** (XAMPP / Laragon).
2. Import file `db_people.sql` melalui phpMyAdmin.
3. Buka folder `PeopleData` dengan IntelliJ IDEA.
4. Pastikan MySQL Connector sudah terpasang:
   `File → Project Structure → Modules → Dependencies → + → JARs or Directories` lalu pilih `lib/mysql-connector-j-x.x.x.jar`.
5. Sesuaikan konfigurasi koneksi di `Database.java` bila perlu (URL, user, password).
6. Jalankan `Main.java`.

---

## Dokumentasi Program Berjalan

> Ganti nama file gambar di bawah sesuai file yang kamu simpan di folder `Dokumentasi/`.

### 1. Tampilan Awal (Read)
Tabel menampilkan data yang tersimpan di database.

![Tampilan awal](Dokumentasi/01-tampilan-awal.png)

### 2. Create – Tambah Data Berhasil

Pengisian form:

![Isi form insert](Dokumentasi/02-insert-form.png)

Dialog sukses dan tabel ter-update:

![Insert berhasil](Dokumentasi/03-insert-berhasil.png)

Data tersimpan di database (phpMyAdmin):

![Data di phpMyAdmin](Dokumentasi/04-insert-phpmyadmin.png)

### 3. Error – Kolom Kosong saat Insert

![Error kolom kosong insert](Dokumentasi/05-error-kosong-insert.png)

### 4. Error – ID Duplikat saat Insert

![Error ID duplikat](Dokumentasi/06-error-id-duplikat.png)

### 5. Update – Ubah Data Berhasil

Memilih baris pada tabel (form terisi otomatis, tombol menjadi Update, tombol Delete muncul):

![Pilih baris](Dokumentasi/07-pilih-baris.png)

Dialog sukses dan tabel ter-update:

![Update berhasil](Dokumentasi/08-update-berhasil.png)

### 6. Error – Kolom Kosong saat Update

![Error kolom kosong update](Dokumentasi/09-error-kosong-update.png)

### 7. Delete – Hapus Data

Dialog konfirmasi:

![Konfirmasi hapus](Dokumentasi/10-konfirmasi-hapus.png)

Data terhapus dari tabel:

![Delete berhasil](Dokumentasi/11-delete-berhasil.png)

### 8. Bukti Data Tetap Ada Setelah Program Dibuka Ulang

![Persistensi data](Dokumentasi/12-data-persisten.png)

### 9. Screen Record (opsional)

![Demo lengkap](Dokumentasi/demo.gif)

---

## Identitas

- **Nama** : _isi nama kamu_
- **NIM** : _isi NIM kamu_
- **Kelas** : C2
- **Mata Kuliah** : Desain Pemrograman Berorientasi Objek
