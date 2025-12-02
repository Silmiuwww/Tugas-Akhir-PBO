import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class Form1RegistrasiGym {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Form Registrasi Member Gym");
        frame.setSize(450, 450);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Label Nama
        JLabel lblNama = new JLabel("Nama Member:");
        lblNama.setBounds(20, 20, 120, 25);
        frame.add(lblNama);

        // Input Nama
        JTextField txtNama = new JTextField();
        txtNama.setBounds(160, 20, 250, 25);
        frame.add(txtNama);

        // Label Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(20, 60, 120, 25);
        frame.add(lblEmail);

        // Input Email
        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(160, 60, 250, 25);
        frame.add(txtEmail);

        // Label No Telepon
        JLabel lblTelepon = new JLabel("No Telepon:");
        lblTelepon.setBounds(20, 100, 120, 25);
        frame.add(lblTelepon);

        JTextField txtTelepon = new JTextField();
        txtTelepon.setBounds(160, 100, 250, 25);
        frame.add(txtTelepon);

        // Label Membership
        JLabel lblMembership = new JLabel("Membership:");
        lblMembership.setBounds(20, 140, 120, 25);
        frame.add(lblMembership);

        String[] membership = {"Basic", "Premium", "VIP"};
        JComboBox<String> cbMembership = new JComboBox<>(membership);
        cbMembership.setBounds(160, 140, 250, 25);
        frame.add(cbMembership);

        // Tombol Simpan
        JButton btnSimpan = new JButton("Daftarkan Member");
        btnSimpan.setBounds(20, 190, 180, 30);
        frame.add(btnSimpan);

        // Tombol Reset
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(230, 190, 180, 30);
        frame.add(btnReset);

        // TextArea hasil
        JTextArea areaHasil = new JTextArea();
        areaHasil.setBounds(20, 240, 390, 150);
        areaHasil.setEditable(false);
        frame.add(areaHasil);

        // EVENT SIMPAN KE POSTGRESQL
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = txtNama.getText().trim();
                String email = txtEmail.getText().trim();
                String telepon = txtTelepon.getText().trim();
                String membership = cbMembership.getSelectedItem().toString();

                // tanggal otomatis hari ini
                LocalDate tanggalBergabung = LocalDate.now();

                // Validasi input
                if (nama.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                        "Nama dan Email wajib diisi!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Koneksi PostgreSQL
                    Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/db_gym",
                        "postgres",      // user PostgreSQL kamu
                        "cerr2407"          // password PostgreSQL
                    );

                    // Query insert
                    String sql = "INSERT INTO member_gym (nama, email, no_telepon, tanggal_bergabung, jenis_membership) VALUES (?, ?, ?, ?, ?)";

                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, nama);
                    stmt.setString(2, email);
                    stmt.setString(3, telepon);
                    stmt.setDate(4, java.sql.Date.valueOf(tanggalBergabung));
                    stmt.setString(5, membership);

                    stmt.executeUpdate();
                    conn.close();

                    // hasil ke text area
                    areaHasil.setText("REGISTRASI BERHASIL:\n");
                    areaHasil.append("Nama : " + nama + "\n");
                    areaHasil.append("Email : " + email + "\n");
                    areaHasil.append("Telepon : " + telepon + "\n");
                    areaHasil.append("Tanggal Bergabung : " + tanggalBergabung + "\n");
                    areaHasil.append("Membership : " + membership + "\n");

                    JOptionPane.showMessageDialog(frame,
                        "Member berhasil didaftarkan!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                        "Gagal menyimpan ke database!\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // EVENT RESET
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtNama.setText("");
                txtEmail.setText("");
                txtTelepon.setText("");
                cbMembership.setSelectedIndex(0);
                areaHasil.setText("");
            }
        });

        frame.setVisible(true);
    }
}
