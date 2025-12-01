import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Form3JadwalKelasGym {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Form Jadwal Kelas Gym");
        frame.setSize(820, 520);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================== FORM FIELD ==================
        JLabel lblNamaKelas = new JLabel("Nama Kelas:");
        lblNamaKelas.setBounds(20, 20, 120, 25);
        frame.add(lblNamaKelas);

        JTextField txtNamaKelas = new JTextField();
        txtNamaKelas.setBounds(150, 20, 200, 25);
        frame.add(txtNamaKelas);

        JLabel lblInstruktur = new JLabel("Instruktur:");
        lblInstruktur.setBounds(20, 60, 120, 25);
        frame.add(lblInstruktur);

        JComboBox<String> cbInstruktur = new JComboBox<>();
        cbInstruktur.setBounds(150, 60, 200, 25);
        frame.add(cbInstruktur);

        JLabel lblHari = new JLabel("Hari:");
        lblHari.setBounds(20, 100, 120, 25);
        frame.add(lblHari);

        JComboBox<String> cbHari = new JComboBox<>(new String[]{
                "Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu"
        });
        cbHari.setBounds(150, 100, 200, 25);
        frame.add(cbHari);

        JLabel lblJam = new JLabel("Jam Kelas (HH:MM):");
        lblJam.setBounds(20, 140, 150, 25);
        frame.add(lblJam);

        JTextField txtJam = new JTextField();
        txtJam.setBounds(170, 140, 180, 25);
        frame.add(txtJam);

        // ================== BUTTON ==================
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(390, 20, 120, 30);
        frame.add(btnSimpan);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(390, 60, 120, 30);
        frame.add(btnUpdate);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBounds(390, 100, 120, 30);
        frame.add(btnDelete);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(390, 140, 120, 30);
        frame.add(btnReset);

        // ================== TABLE ==================
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nama Kelas", "Hari", "Jam", "Instruktur"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 200, 760, 260);
        frame.add(scroll);

        loadInstruktur(cbInstruktur);
        loadData(model);

        // ================== BUTTON SIMPAN ==================
        btnSimpan.addActionListener(e -> {
            try {
                // Validasi
                if(txtNamaKelas.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Nama kelas harus diisi!");
                    return;
                }
                if(cbInstruktur.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(frame, "Pilih instruktur dulu!");
                    return;
                }
                if(txtJam.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Jam kelas harus diisi!");
                    return;
                }

                // Format jam HH:MM:SS
                String jam = txtJam.getText();
                if(!jam.matches("\\d{2}:\\d{2}:\\d{2}")) {
                    jam += ":00";
                }

                Connection conn = getConn();
                String sql = "INSERT INTO jadwal_kelas (nama_kelas, hari, jam_kelas, id_instruktur) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, txtNamaKelas.getText());
                ps.setString(2, cbHari.getSelectedItem().toString());
                ps.setString(3, jam);
                ps.setInt(4, getInstrukturId(cbInstruktur.getSelectedItem().toString()));

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Data berhasil disimpan!");
                loadData(model);
                resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // ================== TABLE CLICK ==================
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int baris = table.getSelectedRow();
                if(baris >= 0) {
                    txtNamaKelas.setText(model.getValueAt(baris, 1).toString());
                    cbHari.setSelectedItem(model.getValueAt(baris, 2).toString());
                    txtJam.setText(model.getValueAt(baris, 3).toString());
                    cbInstruktur.setSelectedItem(model.getValueAt(baris, 4).toString());
                }
            }
        });

        // ================== BUTTON UPDATE ==================
        btnUpdate.addActionListener(e -> {
            int baris = table.getSelectedRow();
            if(baris < 0) {
                JOptionPane.showMessageDialog(frame, "Pilih data dulu!");
                return;
            }

            try {
                // Validasi
                if(txtNamaKelas.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Nama kelas harus diisi!");
                    return;
                }
                if(cbInstruktur.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(frame, "Pilih instruktur dulu!");
                    return;
                }
                if(txtJam.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Jam kelas harus diisi!");
                    return;
                }

                String jam = txtJam.getText();
                if(!jam.matches("\\d{2}:\\d{2}:\\d{2}")) {
                    jam += ":00";
                }

                int idKelas = (int) model.getValueAt(baris, 0); // ambil ID dari JTable

                Connection conn = getConn();
                String sql = "UPDATE jadwal_kelas SET nama_kelas=?, hari=?, jam_kelas=?, id_instruktur=? WHERE id_kelas=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, txtNamaKelas.getText());
                ps.setString(2, cbHari.getSelectedItem().toString());
                ps.setString(3, jam);
                ps.setInt(4, getInstrukturId(cbInstruktur.getSelectedItem().toString()));
                ps.setInt(5, idKelas);

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Data berhasil diupdate!");
                loadData(model);
                resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // ================== BUTTON DELETE ==================
        btnDelete.addActionListener(e -> {
            int baris = table.getSelectedRow();
            if(baris < 0) {
                JOptionPane.showMessageDialog(frame, "Pilih data dulu!");
                return;
            }

            try {
                int idKelas = (int) model.getValueAt(baris, 0); // ambil ID dari JTable
                Connection conn = getConn();
                String sql = "DELETE FROM jadwal_kelas WHERE id_kelas=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idKelas);
                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Data berhasil dihapus!");
                loadData(model);
                resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // ================== BUTTON RESET ==================
        btnReset.addActionListener(e -> resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur));

        frame.setVisible(true);
    }

    // ================== RESET FORM ==================
    private static void resetForm(JTextField txtNama, JTextField txtJam,
                                  JComboBox<String> cbHari, JComboBox<String> cbInstruktur) {
        txtNama.setText("");
        txtJam.setText("");
        cbHari.setSelectedIndex(0);
        if(cbInstruktur.getItemCount() > 0) cbInstruktur.setSelectedIndex(0);
    }

    // ================== KONEKSI ==================
    private static Connection getConn() throws Exception {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ManajemenGym",
                "postgres",
                "Triskapostgre20#"
        );
    }

    // ================== LOAD INSTRUKTUR ==================
    private static void loadInstruktur(JComboBox<String> cb) {
        try {
            Connection conn = getConn();
            ResultSet rs = conn.prepareStatement("SELECT id_instruktur, nama FROM instruktur_gym ORDER BY id_instruktur").executeQuery();

            cb.removeAllItems();
            while (rs.next()) {
                cb.addItem(rs.getInt("id_instruktur") + " - " + rs.getString("nama"));
            }

            conn.close();
        } catch (Exception ex) {
            System.out.println("Load Instruktur Error: " + ex.getMessage());
        }
    }

    // ================== GET ID INSTRUKTUR ==================
    private static int getInstrukturId(String comboItem) {
        String[] parts = comboItem.split(" - ");
        return Integer.parseInt(parts[0]);
    }

    // ================== LOAD DATA TABLE ==================
    private static void loadData(DefaultTableModel model) {
        try {
            Connection conn = getConn();
            model.setRowCount(0);
            String sql =
                    "SELECT jk.id_kelas, jk.nama_kelas, jk.hari, jk.jam_kelas, ig.id_instruktur || ' - ' || ig.nama AS instruktur " +
                    "FROM jadwal_kelas jk " +
                    "JOIN instruktur_gym ig ON jk.id_instruktur = ig.id_instruktur " +
                    "ORDER BY jk.id_kelas ASC";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_kelas"),
                        rs.getString("nama_kelas"),
                        rs.getString("hari"),
                        rs.getString("jam_kelas"),
                        rs.getString("instruktur")
                });
            }

            conn.close();
        } catch (Exception ex) {
            System.out.println("Load error: " + ex.getMessage());
        }
    }
}