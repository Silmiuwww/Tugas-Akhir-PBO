import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Form1RegistrasiGym extends JPanel {

    public Form1RegistrasiGym(JFrame parent) {

        JFrame frame = new JFrame("Form Registrasi Member Gym");
        frame.setSize(750, 550);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Label Nama
        JLabel lblNama = new JLabel("Nama:");
        lblNama.setBounds(20, 20, 120, 25);
        frame.add(lblNama);

        JTextField txtNama = new JTextField();
        txtNama.setBounds(150, 20, 200, 25);
        frame.add(txtNama);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(20, 60, 150, 25);
        add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(150, 60, 200, 25);
        frame.add(txtEmail);

        JLabel lblTelepon = new JLabel("No Telepon:");
        lblTelepon.setBounds(20, 100, 150, 25);
        add(lblTelepon);

        JTextField txtTelepon = new JTextField();
        txtTelepon.setBounds(150, 100, 200, 25);
        frame.add(txtTelepon);

        JLabel lblKelamin = new JLabel("Gender:");
        lblKelamin.setBounds(20, 140, 150, 25);
        add(lblKelamin);

        String[] membership = {"Basic", "Premium", "VIP"};
        JComboBox<String> cbMembership = new JComboBox<>(membership);
        cbMembership.setBounds(150, 140, 200, 25);
        frame.add(cbMembership);

        // Tombol Simpan
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(20, 190, 150, 30);
        frame.add(btnSimpan);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(200, 190, 150, 30);
        frame.add(btnReset);

        // Tombol Hapus
        JButton btnHapus = new JButton("Hapus Data");
        btnHapus.setBounds(380, 190, 150, 30);
        frame.add(btnHapus);

        // TABEL
        String[] kolom = {"ID", "Nama", "Email", "Telepon", "Tgl Bergabung", "Membership"};

        DefaultTableModel model = new DefaultTableModel(kolom, 0);
        JTable table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 250, 700, 250);
        frame.add(scroll);

        // FUNGSI KONEKSI
        String url = "jdbc:postgresql://localhost:5432/db_gym";
        String user = "postgres";
        String pass = "cerr2407";

        // LOAD DATA KE JTABLE
        Runnable loadData = () -> {
            model.setRowCount(0);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                String sql = "SELECT * FROM member_gym ORDER BY id_member ASC";
                ResultSet rs = conn.createStatement().executeQuery(sql);

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id_member"),
                            rs.getString("nama"),
                            rs.getString("email"),
                            rs.getString("no_telepon"),
                            rs.getDate("tanggal_bergabung"),
                            rs.getString("jenis_membership")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        };

        loadData.run();

        // INSERT 5 DATA OTOMATIS
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM member_gym");
            rs.next();

            if (rs.getInt(1) < 5) {
                conn.createStatement().executeUpdate(
                    "INSERT INTO member_gym (nama, email, no_telepon, tanggal_bergabung, jenis_membership) VALUES" +
                    "('Ubai', 'ubai@mail.com', '0842241472', CURRENT_DATE, 'Basic')," +
                    "('Zaki', 'zaki@mail.com', '0854225285', CURRENT_DATE, 'Premium')," +
                    "('Rifat', 'rifat@mail.com', '0823632552', CURRENT_DATE, 'VIP')," +
                    "('Terziqo', 'terziqo@mail.com', '0823325525', CURRENT_DATE, 'Basic')," +
                    "('Tanggaq', 'tanggaq@mail.com', '0845264626', CURRENT_DATE, 'Premium')"
                );
            }
        } catch (Exception ex) {}

        loadData.run();

        // EVENT SIMPAN
        btnSimpan.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            String email = txtEmail.getText().trim();
            String telepon = txtTelepon.getText().trim();
            String membershipSelect = cbMembership.getSelectedItem().toString();

            if (nama.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Nama & Email wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, user, pass)) {

                String sql = "INSERT INTO member_gym (nama, email, no_telepon, tanggal_bergabung, jenis_membership) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nama);
                stmt.setString(2, email);
                stmt.setString(3, telepon);
                stmt.setDate(4, Date.valueOf(LocalDate.now()));
                stmt.setString(5, membershipSelect);

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Data berhasil ditambahkan!");
                loadData.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENT RESET
        btnReset.addActionListener(e -> {
            txtNama.setText("");
            txtEmail.setText("");
            txtTelepon.setText("");
            cbMembership.setSelectedIndex(0);
        });

        // EVENT HAPUS
        btnHapus.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(frame, "Pilih data di tabel!");
                return;
            }

            int idDelete = (int) model.getValueAt(selected, 0);

            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                conn.createStatement().executeUpdate("DELETE FROM member_gym WHERE id_member=" + idDelete);
                JOptionPane.showMessageDialog(frame, "Data berhasil dihapus!");

                loadData.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
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
