# TP5 DPBO – PeopleData (Java Swing GUI + MySQL)

## Janji
Saya Bentar Bintang Umeir dengan nim 2509865 mengerjakan TP5 dalam mata kuliah DPBO untuk 
keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin
Tugas Praktikum 5 – Desain Pemrograman Berorientasi Objek (Modul 5: Java Swing GUI – Database dan Back-End).

Program ini adalah pengembangan project **PeopleData** dari TP4. Jika sebelumnya data disimpan di `ArrayList` (hilang saat program ditutup), sekarang seluruh operasi **CRUD** (Create, Read, Update, Delete) terhubung ke database **MySQL** (`db_people`) melalui **JDBC**.

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

<img width="516" height="440" alt="image" src="https://github.com/user-attachments/assets/cb312237-748e-4930-a2d4-ef07d4e70608" />


### 2. Create – Tambah Data Berhasil

Pengisian form, data berhasil di tambahkan dan tabel terupdate:

https://github.com/user-attachments/assets/79bbdbeb-2f95-4454-ba27-278565477444

Data tersimpan di database (phpMyAdmin):

<img width="958" height="527" alt="image" src="https://github.com/user-attachments/assets/6b093db0-8dc7-4a63-a8a3-47f48c9bf586" />

### 3. Error – Kolom Kosong saat Insert

<img width="514" height="442" alt="image" src="https://github.com/user-attachments/assets/a1025651-c8c6-4edf-ba86-51f2d165b255" />

### 4. Error – ID Duplikat saat Insert

<img width="524" height="440" alt="image" src="https://github.com/user-attachments/assets/20fef92d-fc6e-47d7-b97f-3772c0dbad66" />

### 5. Update – Ubah Data Berhasil

Memilih baris pada tabel (form terisi otomatis, tombol menjadi Update, tombol Delete muncul):

<img width="512" height="439" alt="image" src="https://github.com/user-attachments/assets/118a228d-1cd0-4b6f-b7af-c883f4b1cfee" />

update berhasil dan tabel ter-update:

https://github.com/user-attachments/assets/9252d704-e96c-499a-a925-a55b6eb2036e

### 6. Error – Kolom Kosong saat Update

<img width="514" height="438" alt="image" src="https://github.com/user-attachments/assets/2a942589-ce29-40cf-8854-d657e8704f40" />

### 7. Delete – Hapus Data

Dialog konfirmasi:

<img width="516" height="442" alt="image" src="https://github.com/user-attachments/assets/ce11681f-9bff-4b5c-b6d9-c7696e69d620" />

Data terhapus dari tabel:

<img width="516" height="446" alt="image" src="https://github.com/user-attachments/assets/6b325d01-914a-4923-be4b-82ac49c13fed" />

---

