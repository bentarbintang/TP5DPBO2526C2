-- Database: db_people

CREATE DATABASE db_people;
USE db_people;

CREATE TABLE person (
  id varchar(255) PRIMARY KEY,
  nama varchar(255) NOT NULL,
  thn_lahir int NOT NULL,
  kategori varchar(255) NOT NULL,
  level varchar(255) NOT NULL
);

INSERT INTO `person` (id, nama, thn_lahir, kategori, level) VALUES
('P001', 'Budi Santoso', 2003, 'Mahasiswa', 6),
('P002', 'Siti Aminah', 1985, 'Dosen', 9),
('P003', 'Rian Pratama', 1990, 'Staf', 7),
('P004', 'Dewi Lestari', 1998, 'Alumni', 8),
('P005', 'Ahmad Fauzi', 2004, 'Mahasiswa', 4);