import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Form2DataInstrukturGym {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Form Data Instruktur Gym");
        frame.setSize(750, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblId = new JLabel("ID Instruktur:");
        lblId.setBounds(20, 20, 120, 25);
        frame.add(lblId);

        JTextField txtId = new JTextField();
        txtId.setBounds(150, 20, 200, 25);
        txtId.setEditable(false);  
        frame.add(txtId);

        JLabel lblNama = new JLabel("Nama:");
        lblNama.setBounds(20, 60, 120, 25);
        frame.add(lblNama);

        JTextField txtNama = new JTextField();
        txtNama.setBounds(150, 60, 200, 25);
        frame.add(txtNama);

        JLabel lblUsia = new JLabel("Usia:");
        lblUsia.setBounds(20, 100, 120, 25);
        frame.add(lblUsia);

        JTextField txtUsia = new JTextField();
        txtUsia.setBounds(150, 100, 200, 25);
        frame.add(txtUsia);

        JLabel lblKeahlian = new JLabel("Keahlian:");
        lblKeahlian.setBounds(20, 140, 120, 25);
        frame.add(lblKeahlian);

        JTextField txtKeahlian = new JTextField();
        txtKeahlian.setBounds(150, 140, 200, 25);
        frame.add(txtKeahlian);

        JLabel lblTelp = new JLabel("No. Telepon:");
        lblTelp.setBounds(20, 180, 120, 25);
        frame.add(lblTelp);

        JTextField txtTelp = new JTextField();
        txtTelp.setBounds(150, 180, 200, 25);
        frame.add(txtTelp);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(20, 230, 120, 30);
        frame.add(btnSimpan);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 230, 120, 30);
        frame.add(btnUpdate);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBounds(280, 230, 120, 30);
        frame.add(btnDelete);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(410, 230, 120, 30);
        frame.add(btnReset);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nama", "Usia", "Keahlian", "Telepon"}, 0);

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 280, 700, 170);
        frame.add(scroll);

        loadData(model);

        btnSimpan.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            String usiaStr = txtUsia.getText().trim();
            String keahlian = txtKeahlian.getText().trim();
            String telp = txtTelp.getText().trim();

            if (nama.isEmpty() || usiaStr.isEmpty() || keahlian.isEmpty() || telp.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Semua field wajib diisi!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int usia;
            try {
                usia = Integer.parseInt(usiaStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Usia harus angka!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/db_gym",
                        "postgres",
                        "bakmi1"
                );

                String sql = "INSERT INTO instruktur_gym (nama, usia, keahlian, no_telepon) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, nama);
                ps.setInt(2, usia);
                ps.setString(3, keahlian);
                ps.setString(4, telp);

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Data berhasil disimpan!");
                loadData(model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int baris = table.getSelectedRow();
                txtId.setText(model.getValueAt(baris, 0).toString());
                txtNama.setText(model.getValueAt(baris, 1).toString());
                txtUsia.setText(model.getValueAt(baris, 2).toString());
                txtKeahlian.setText(model.getValueAt(baris, 3).toString());
                txtTelp.setText(model.getValueAt(baris, 4).toString());
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Pilih data di tabel terlebih dahulu!");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/db_gym",
                        "postgres",
                        "bakmi1"
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

                JOptionPane.showMessageDialog(frame, "Data berhasil diupdate!");
                loadData(model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Pilih data yang ingin dihapus!");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/db_gym",
                        "postgres",
                        "bakmi1"
                );

                String sql = "DELETE FROM instruktur_gym WHERE id_instruktur=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(txtId.getText()));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Data berhasil dihapus!");
                conn.close();
                loadData(model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> {
            txtId.setText("");
            txtNama.setText("");
            txtUsia.setText("");
            txtKeahlian.setText("");
            txtTelp.setText("");
        });

        frame.setVisible(true);
    }

    private static void loadData(DefaultTableModel model) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/db_gym",
                    "postgres",
                    "bakmi1"
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
