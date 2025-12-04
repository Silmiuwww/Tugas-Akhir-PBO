import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Form3JadwalKelasGym extends JPanel {
    public Form3JadwalKelasGym(JFrame parent) {
        setLayout(null);

        // ================== FORM FIELD ==================
        JLabel lblNamaKelas = new JLabel("Nama Kelas:");
        lblNamaKelas.setBounds(20, 20, 120, 25);
        add(lblNamaKelas);

        JTextField txtNamaKelas = new JTextField();
        txtNamaKelas.setBounds(150, 20, 200, 25);
        add(txtNamaKelas);

        JLabel lblInstruktur = new JLabel("Instruktur:");
        lblInstruktur.setBounds(20, 60, 120, 25);
        add(lblInstruktur);

        JComboBox<String> cbInstruktur = new JComboBox<>();
        cbInstruktur.setBounds(150, 60, 200, 25);
        add(cbInstruktur);

        JLabel lblHari = new JLabel("Hari:");
        lblHari.setBounds(20, 100, 120, 25);
        add(lblHari);

        JComboBox<String> cbHari = new JComboBox<>(new String[]{
                "Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu"
        });
        cbHari.setBounds(150, 100, 200, 25);
        add(cbHari);

        JLabel lblJam = new JLabel("Jam Kelas (HH:MM):");
        lblJam.setBounds(20, 140, 150, 25);
        add(lblJam);

        JTextField txtJam = new JTextField();
        txtJam.setBounds(170, 140, 180, 25);
        add(txtJam);

        // ================== BUTTON ==================
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(450, 20, 120, 30);
        add(btnSimpan);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(450, 60, 120, 30);
        add(btnUpdate);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBounds(450, 100, 120, 30);
        add(btnDelete);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(450, 140, 120, 30);
        add(btnReset);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(450, 180, 120, 30);
        add(btnRefresh);

        // ================== TABLE ==================
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nama Kelas", "Hari", "Jam", "Instruktur"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 240, 700, 230);
        add(scroll);

        // Load ComboBox + Table
        loadInstruktur(cbInstruktur);
        loadData(model);

        // ================== BUTTON SIMPAN ==================
        btnSimpan.addActionListener(e -> {
            try {
                if(txtNamaKelas.getText().isEmpty() ||
                   cbInstruktur.getSelectedItem() == null ||
                   txtJam.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "Semua field wajib diisi!");
                    return;
                }

                String jamStr = txtJam.getText();
                if(!jamStr.matches("\\d{2}:\\d{2}")) {
                    JOptionPane.showMessageDialog(parent, "Format jam harus HH:MM, contoh 08:00");
                    return;
                }
                java.sql.Time jamSql = java.sql.Time.valueOf(jamStr + ":00");

                Connection conn = getConn();
                String sql = "INSERT INTO jadwal_kelas (nama_kelas, hari, jam_kelas, id_instruktur) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, txtNamaKelas.getText());
                ps.setString(2, cbHari.getSelectedItem().toString());
                ps.setTime(3, jamSql);
                ps.setInt(4, getInstrukturId(cbInstruktur.getSelectedItem().toString()));

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(parent, "Data berhasil disimpan!");
                loadData(model);
                resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage());
            }
        });

        // ================== TABLE CLICK ==================
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int baris = table.getSelectedRow();
                if(baris >= 0) {
                    txtNamaKelas.setText(model.getValueAt(baris, 1).toString());
                    cbHari.setSelectedItem(model.getValueAt(baris, 2).toString());
                    txtJam.setText(model.getValueAt(baris, 3).toString().substring(0,5));
                    cbInstruktur.setSelectedItem(model.getValueAt(baris, 4).toString());
                }
            }
        });

        // ================== BUTTON UPDATE ==================
        btnUpdate.addActionListener(e -> {
            int baris = table.getSelectedRow();
            if(baris < 0) {
                JOptionPane.showMessageDialog(parent, "Pilih data dulu!");
                return;
            }

            try {
                if(txtNamaKelas.getText().isEmpty() ||
                   cbInstruktur.getSelectedItem() == null ||
                   txtJam.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "Semua field wajib diisi!");
                    return;
                }

                String jamStr = txtJam.getText();
                if(!jamStr.matches("\\d{2}:\\d{2}")) {
                    JOptionPane.showMessageDialog(parent, "Format jam harus HH:MM, contoh 08:00");
                    return;
                }
                java.sql.Time jamSql = java.sql.Time.valueOf(jamStr + ":00");

                int idKelas = (int) model.getValueAt(baris, 0);

                Connection conn = getConn();
                String sql = "UPDATE jadwal_kelas SET nama_kelas=?, hari=?, jam_kelas=?, id_instruktur=? WHERE id_kelas=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, txtNamaKelas.getText());
                ps.setString(2, cbHari.getSelectedItem().toString());
                ps.setTime(3, jamSql);
                ps.setInt(4, getInstrukturId(cbInstruktur.getSelectedItem().toString()));
                ps.setInt(5, idKelas);

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(parent, "Data berhasil diupdate!");
                loadData(model);
                resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage());
            }
        });

        // ================== BUTTON DELETE ==================
        btnDelete.addActionListener(e -> {
            int baris = table.getSelectedRow();
            if(baris < 0) {
                JOptionPane.showMessageDialog(parent, "Pilih data dulu!");
                return;
            }

            try {
                int idKelas = (int) model.getValueAt(baris, 0);
                Connection conn = getConn();
                String sql = "DELETE FROM jadwal_kelas WHERE id_kelas=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idKelas);
                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(parent, "Data berhasil dihapus!");
                loadData(model);
                resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage());
            }
        });

        // ================== BUTTON RESET ==================
        btnReset.addActionListener(e -> resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur));

        // ================== BUTTON REFRESH ==================
        btnRefresh.addActionListener(e -> {
            loadInstruktur(cbInstruktur);
            loadData(model);
            resetForm(txtNamaKelas, txtJam, cbHari, cbInstruktur);
        });

        parent.setVisible(true);
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
            ResultSet rs = conn.prepareStatement(
                    "SELECT id_instruktur, nama FROM instruktur_gym ORDER BY id_instruktur"
            ).executeQuery();

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
                    "SELECT jk.id_kelas, jk.nama_kelas, jk.hari, jk.jam_kelas, " +
                    "ig.id_instruktur || ' - ' || ig.nama AS instruktur " +
                    "FROM jadwal_kelas jk " +
                    "JOIN instruktur_gym ig ON jk.id_instruktur = ig.id_instruktur " +
                    "ORDER BY jk.id_kelas ASC";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_kelas"),
                        rs.getString("nama_kelas"),
                        rs.getString("hari"),
                        rs.getString("jam_kelas").substring(0,5), // tampil HH:MM saja
                        rs.getString("instruktur")
                });
            }

            conn.close();
        } catch (Exception ex) {
            System.out.println("Load error: " + ex.getMessage());
        }
    }
}
