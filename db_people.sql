-- Database: db_people

DROP DATABASE IF EXISTS db_people;
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
('P001', 'Luthfi', 2003, 'Mahasiswa', 6),
('P002', 'Jaka', 1985, 'Dosen', 9),
('P003', 'Rian', 1990, 'Staf', 7),
('P004', 'Repan', 1998, 'Alumni', 8),
('P005', 'Safaraz', 2004, 'Mahasiswa', 4),
('P006', 'Bintang', 2007, 'Mahasiswa', 10),
('P007', 'Umeir', 2007, 'Mahasiswa', 10),
('P008', 'Bentar', 2007, 'Mahasiswa', 10);
