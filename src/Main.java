import javax.swing.*;

public class Main extends JFrame {
    public Main() {
        setTitle("Aplikasi Gym");
        setSize(750, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(10, 10, 730, 460);

        // Sesuaikan dengan nama form yang kamu miliki!
        tabbedPane.addTab("Daftar Member Gym", new Form1RegistrasiGym(this));
        tabbedPane.addTab("Data Instruktur Gym", new Form2DataInstrukturGym(this));
        tabbedPane.addTab("Jadwal Kelas Gym", new Form3JadwalKelasGym(this));
        tabbedPane.addTab("Pendaftaran Kelas Gym", new Form4PendaftaranKelasGym(this));

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
