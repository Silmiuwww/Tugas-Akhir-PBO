import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class Form1RegistrasiGym extends JPanel {

    public Form1RegistrasiGym(JFrame parent) {

        setLayout(null);

        // ================= FORM FIELD ==================
        JLabel lblNama = new JLabel("Nama Member:");
        lblNama.setBounds(20, 20, 150, 25);
        add(lblNama);

        JTextField txtNama = new JTextField();
        txtNama.setBounds(170, 20, 200, 25);
        add(txtNama);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(20, 60, 150, 25);
        add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(170, 60, 200, 25);
        add(txtEmail);

        JLabel lblTelepon = new JLabel("No Telepon:");
        lblTelepon.setBounds(20, 100, 150, 25);
        add(lblTelepon);

        JTextField txtTelepon = new JTextField();
        txtTelepon.setBounds(170, 100, 200, 25);
        add(txtTelepon);

        JLabel lblKelamin = new JLabel("Gender:");
        lblKelamin.setBounds(20, 140, 150, 25);
        add(lblKelamin);

        String[] gender = {"Laki-laki", "Perempuan"};
        JComboBox<String> cbKelamin = new JComboBox<>(gender);
        cbKelamin.setBounds(170, 140, 200, 25);
        add(cbKelamin);

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

        // ================== TABEL MEMBER ==================
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Nama", "Email", "Telepon", "Jenis Kelamin", "Tgl Bergabung"}, 0
        );
        table.setModel(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 240, 700, 230);
        add(scroll);

        // ================== FUNGSI LOAD TABEL ==================
        Runnable loadTable = () -> {
            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/ManajemenGym",
                    "postgres",
                    "Triskapostgre20#"
                );
                model.setRowCount(0);
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM member_gym ORDER BY id_member ASC");

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_member"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("no_telepon"),
                        rs.getString("jenis_kelamin"),
                        rs.getDate("tanggal_bergabung")
                    });
                }
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Gagal memuat tabel!\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadTable.run();

        // ================== EVENT PILIH DATA TABEL ==================
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtEmail.setText(model.getValueAt(row, 2).toString());
                    txtTelepon.setText(model.getValueAt(row, 3).toString());
                    cbKelamin.setSelectedItem(model.getValueAt(row, 4).toString());
                }
            }
        });

        // ================== EVENT SIMPAN ==================
        btnSimpan.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            String email = txtEmail.getText().trim();
            String telepon = txtTelepon.getText().trim();
            String kelamin = cbKelamin.getSelectedItem().toString();
            LocalDate tgl = LocalDate.now();

            if (nama.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Nama dan Email wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/ManajemenGym",
                    "postgres",
                    "Triskapostgre20#"
                );

                String sql = "INSERT INTO member_gym (nama, email, no_telepon, tanggal_bergabung, jenis_kelamin) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nama);
                stmt.setString(2, email);
                stmt.setString(3, telepon);
                stmt.setDate(4, java.sql.Date.valueOf(tgl));
                stmt.setString(5, kelamin);

                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(parent, "Data berhasil disimpan!");
                loadTable.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Gagal menyimpan!\n" + ex.getMessage());
            }
        });

        // ================== EVENT UPDATE ==================
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(parent, "Pilih data pada tabel yang ingin diperbarui!");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            String nama = txtNama.getText().trim();
            String email = txtEmail.getText().trim();
            String telepon = txtTelepon.getText().trim();
            String kelamin = cbKelamin.getSelectedItem().toString();

            if (nama.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Nama dan Email wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/ManajemenGym",
                    "postgres",
                    "Triskapostgre20#"
                );

                String sql = "UPDATE member_gym SET nama=?, email=?, no_telepon=?, jenis_kelamin=? WHERE id_member=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nama);
                stmt.setString(2, email);
                stmt.setString(3, telepon);
                stmt.setString(4, kelamin);
                stmt.setInt(5, id);

                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(parent, "Data berhasil diperbarui!");
                loadTable.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Gagal memperbarui!\n" + ex.getMessage());
            }
        });

        // ================== EVENT HAPUS ==================
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(parent, "Pilih data pada tabel terlebih dahulu!");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/ManajemenGym",
                    "postgres",
                    "Triskapostgre20#"
                );

                String sql = "DELETE FROM member_gym WHERE id_member = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();

                conn.close();
                JOptionPane.showMessageDialog(parent, "Data berhasil dihapus!");
                loadTable.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Gagal menghapus!\n" + ex.getMessage());
            }
        });

        // ================== EVENT RESET ==================
        btnReset.addActionListener(e -> {
            txtNama.setText("");
            txtEmail.setText("");
            txtTelepon.setText("");
            cbKelamin.setSelectedIndex(0);
        });

    }
}
