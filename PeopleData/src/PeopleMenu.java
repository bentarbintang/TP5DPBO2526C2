import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PeopleMenu extends JFrame {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JComboBox<String> kategoriComboBox;
    private JSlider levelSlider;
    private JLabel levelValueLabel;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JTable peopleTable;

    private int selectedIndex = -1;
    private Database database;

    public static void main(String[] args) {
        PeopleMenu menu = new PeopleMenu();
        menu.setSize(700, 600);
        menu.setLocationRelativeTo(null);
        menu.setContentPane(menu.mainPanel);
        menu.getContentPane().setBackground(Color.WHITE);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public PeopleMenu() {
        database = new Database();

        peopleTable.setModel(setTable());

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        String[] kategoriData = {"???", "Mahasiswa", "Dosen", "Staf", "Alumni"};
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // pengaturan slider
        levelSlider.setMinimum(1);
        levelSlider.setMaximum(10);
        levelSlider.setValue(5);
        levelValueLabel.setText("5");
        levelSlider.addChangeListener(e ->
                levelValueLabel.setText(String.valueOf(levelSlider.getValue())));

        deleteButton.setVisible(false);

        addUpdateButton.addActionListener(e -> {
            if (selectedIndex == -1) insertData();
            else updateData();
        });
        deleteButton.addActionListener(e -> deleteData());
        cancelButton.addActionListener(e -> clearForm());

        // klik baris tabel
        peopleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedIndex = peopleTable.getSelectedRow();
                if (selectedIndex == -1) return;

                String curId       = peopleTable.getModel().getValueAt(selectedIndex, 0).toString();
                String curNama     = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();
                String curTahun    = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();
                String curKategori = peopleTable.getModel().getValueAt(selectedIndex, 3).toString();
                String curLevel    = peopleTable.getModel().getValueAt(selectedIndex, 4).toString();

                idField.setText(curId);
                namaField.setText(curNama);
                tahunLahirField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);
                levelSlider.setValue(Integer.parseInt(curLevel));
                levelValueLabel.setText(curLevel);

                idField.setEditable(false); // ID = primary key, jangan diubah
                addUpdateButton.setText("Update");
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        Object[] cols = { "ID", "Nama", "Tahun Lahir", "Kategori", "Level" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");
            while (resultSet.next()) {
                Object[] row = new Object[5];
                row[0] = resultSet.getString("id");
                row[1] = resultSet.getString("nama");
                row[2] = resultSet.getString("thn_lahir");
                row[3] = resultSet.getString("kategori");
                row[4] = resultSet.getString("level");
                tmp.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tmp;
    }

    public void insertData() {
        try {
            String id       = idField.getText().trim();
            String nama     = namaField.getText().trim();
            String tahunStr = tahunLahirField.getText().trim();
            Object kat      = kategoriComboBox.getSelectedItem();
            String kategori = (kat == null) ? "" : kat.toString();
            int level       = levelSlider.getValue();

            // Error: kolom kosong
            if (id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty()
                    || kategori.isEmpty() || kategori.equals("???")) {
                JOptionPane.showMessageDialog(null, "Semua kolom harus diisi!",
                        "Input Tidak Lengkap", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int tahun = Integer.parseInt(tahunStr);

            // Error: ID sudah ada
            ResultSet rs = database.selectQuery("SELECT id FROM person WHERE id = '" + id + "'");
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "ID '" + id + "' sudah digunakan!",
                        "ID Duplikat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sqlQuery = "INSERT INTO person VALUES('" + id + "', '" + nama + "', '" + tahun + "', '" + kategori + "', '" + level + "')";
            database.insertUpdateDeleteQuery(sqlQuery);

            peopleTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun Lahir harus berupa angka!",
                    "Input Salah", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void updateData() {
        try {
            String id       = idField.getText().trim();
            String nama     = namaField.getText().trim();
            String tahunStr = tahunLahirField.getText().trim();
            Object kat      = kategoriComboBox.getSelectedItem();
            String kategori = (kat == null) ? "" : kat.toString();
            int level       = levelSlider.getValue();

            if (nama.isEmpty() || tahunStr.isEmpty()
                    || kategori.isEmpty() || kategori.equals("???")) {
                JOptionPane.showMessageDialog(null, "Semua kolom harus diisi!",
                        "Input Tidak Lengkap", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int tahun = Integer.parseInt(tahunStr);

            String sqlQuery = "UPDATE person SET nama = '" + nama + "', thn_lahir = '" + tahun + "', kategori = '" + kategori
                    + "', level = '" + level + "' WHERE id = '" + id + "'";
            database.insertUpdateDeleteQuery(sqlQuery);

            peopleTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun Lahir harus berupa angka!",
                    "Input Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteData() {
        int konfirmasi = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus data ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        String id = idField.getText().trim();
        database.insertUpdateDeleteQuery("DELETE FROM person WHERE id = '" + id + "'");

        peopleTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
    }

    public void clearForm() {
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        levelSlider.setValue(5);
        levelValueLabel.setText("5");

        idField.setEditable(true);
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        selectedIndex = -1;
        peopleTable.clearSelection();
    }
}