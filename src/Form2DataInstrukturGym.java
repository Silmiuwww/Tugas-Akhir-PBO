import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Form2DataInstrukturGym extends JPanel {
    public Form2DataInstrukturGym(JFrame parent) {
        setLayout(null);

        // ============ ID INSTRUKTUR (HIDDEN) ============
        JTextField txtId = new JTextField();
        txtId.setVisible(false);  // tidak ditampilkan
        // Tidak ditambahkan ke panel agar tidak tampak di form

        // ============ FORM FIELD ============

        JLabel lblNama = new JLabel("Nama Instruktur:");
        lblNama.setBounds(20, 20, 120, 25);
        add(lblNama);

        JTextField txtNama = new JTextField();
        txtNama.setBounds(150, 20, 200, 25);
        add(txtNama);

        JLabel lblUsia = new JLabel("Usia:");
        lblUsia.setBounds(20, 60, 120, 25);
        add(lblUsia);

        JTextField txtUsia = new JTextField();
        txtUsia.setBounds(150, 60, 200, 25);
        add(txtUsia);

        JLabel lblKeahlian = new JLabel("Keahlian:");
        lblKeahlian.setBounds(20, 100, 120, 25);
        add(lblKeahlian);

        JTextField txtKeahlian = new JTextField();
        txtKeahlian.setBounds(150, 100, 200, 25);
        add(txtKeahlian);

        JLabel lblTelp = new JLabel("No. Telepon:");
        lblTelp.setBounds(20, 140, 120, 25);
        add(lblTelp);

        JTextField txtTelp = new JTextField();
        txtTelp.setBounds(150, 140, 200, 25);
        add(txtTelp);

        // ============ BUTTONS ============

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

        // ============ TABLE ============

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nama", "Usia", "Keahlian", "Telepon"}, 0);

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 250, 700, 200);
        add(scroll);

        loadData(model);

        // ============ SIMPAN DATA ============
        btnSimpan.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            String usiaStr = txtUsia.getText().trim();
            String keahlian = txtKeahlian.getText().trim();
            String telp = txtTelp.getText().trim();

            if (nama.isEmpty() || usiaStr.isEmpty() || keahlian.isEmpty() || telp.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Semua field wajib diisi!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int usia;
            try {
                usia = Integer.parseInt(usiaStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Usia harus angka!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/ManajemenGym",
                        "postgres",
                        "Triskapostgre20#"
                );

                String sql = "INSERT INTO instruktur_gym (nama, usia, keahlian, no_telepon) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, nama);
                ps.setInt(2, usia);
                ps.setString(3, keahlian);
                ps.setString(4, telp);

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(parent, "Data berhasil disimpan!");
                loadData(model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage());
            }
        });

        // ============ TABLE CLICK ============
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int baris = table.getSelectedRow();
                txtId.setText(model.getValueAt(baris, 0).toString()); // ID tetap disimpan, tapi tidak tampil
                txtNama.setText(model.getValueAt(baris, 1).toString());
                txtUsia.setText(model.getValueAt(baris, 2).toString());
                txtKeahlian.setText(model.getValueAt(baris, 3).toString());
                txtTelp.setText(model.getValueAt(baris, 4).toString());
            }
        });

        // ============ UPDATE ============
        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Pilih data di tabel terlebih dahulu!");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/ManajemenGym",
                        "postgres",
                        "Triskapostgre20#"
                );

                String sql = "UPDATE instruktur_gym SET nama=?, usia=?, keahlian=?, no_telepon=? WHERE id_instruktur=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, txtNama.getText());
                ps.setInt(2, Integer.parseInt(txtUsia.getText()));
                ps.setString(3, txtKeahlian.getText());
                ps.setString(4, txtTelp.getText());
                ps.setInt(5, Integer.parseInt(txtId.getText()));

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(parent, "Data berhasil diupdate!");
                loadData(model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage());
            }
        });

        // ============ DELETE ============
        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Pilih data yang ingin dihapus!");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/ManajemenGym",
                        "postgres",
                        "Triskapostgre20#"
                );

                String sql = "DELETE FROM instruktur_gym WHERE id_instruktur=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(txtId.getText()));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(parent, "Data berhasil dihapus!");
                conn.close();
                loadData(model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage());
            }
        });

        // ============ RESET ============
        btnReset.addActionListener(e -> {
            txtId.setText("");
            txtNama.setText("");
            txtUsia.setText("");
            txtKeahlian.setText("");
            txtTelp.setText("");
        });

        parent.setVisible(true);
    }

    private static void loadData(DefaultTableModel model) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/ManajemenGym",
                    "postgres",
                    "Triskapostgre20#"
            );

            model.setRowCount(0);

            String sql = "SELECT * FROM instruktur_gym ORDER BY id_instruktur ASC";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_instruktur"),
                        rs.getString("nama"),
                        rs.getInt("usia"),
                        rs.getString("keahlian"),
                        rs.getString("no_telepon")
                });
            }

        } catch (Exception ex) {
            System.out.println("Load error: " + ex.getMessage());
        }
    }
}
