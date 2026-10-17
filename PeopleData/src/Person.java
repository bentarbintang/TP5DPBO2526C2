public class Person {
    private String id;
    private String nama;
    private int tahunLahir;
    private String kategori;
    private int level; // atribut baru

    public Person(String id, String nama, int tahunLahir, String kategori, int level) {
        this.id = id;
        this.nama = nama;
        this.tahunLahir = tahunLahir;
        this.kategori = kategori;
        this.level = level;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public int getTahunLahir() { return tahunLahir; }
    public void setTahunLahir(int tahunLahir) { this.tahunLahir = tahunLahir; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}