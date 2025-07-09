package com.kelompok4.cctvapp.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.kelompok4.cctvapp.User;
import com.kelompok4.cctvapp.service.DataMasterManager;
import com.kelompok4.cctvapp.service.UserSession;
import com.kelompok4.cctvapp.util.NotificationManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/*
 * =================================================================================
 * RINGKASAN METHOD (FORM LOGIN)
 * =================================================================================
 * Kelas ini merepresentasikan window (JFrame) untuk login aplikasi.
 * ---------------------------------------------------------------------------------
 * FormLogin()                      - Konstruktor, membangun seluruh UI window dan form login.
 * createInputPanelWithSVGIcon(...) - Helper method untuk membuat panel input (icon + text field).
 * styleButton(...)                 - Helper method untuk memberikan styling standar pada tombol.
 * prosesLogin()                    - Menangani proses validasi dan autentikasi saat tombol login diklik.
 * main(...)                        - Method utama (entry point) untuk menjalankan aplikasi.
 * =================================================================================
 */
public class FormLogin extends JFrame {

    // Komponen UI
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnBatal;

    // Service & Utility
    private DataMasterManager dataMasterManager;

    // Properti Styling
    private Color colorLeftPanelBg = new Color(34, 101, 151);
    private Color colorRightPanelBg = Color.WHITE;
    private Color colorButtonSignInBg = new Color(34, 101, 151);
    private Color colorButtonSignInText = Color.WHITE;
    private Color colorTextFieldBorder = new Color(200, 200, 200);
    private Color colorLoginTitle = new Color(50, 50, 50);
    private Color colorAppNameText = Color.WHITE;

    // Konstanta
    private final String ICON_CLASSPATH_BASE = "com/kelompok4/cctvapp/images/";

    public FormLogin() {
        setTitle("Login Aplikasi CCTV");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            String iconWindowPath = "/com/kelompok4/cctvapp/images/icon_window.png";
            ImageIcon windowIcon = new ImageIcon(getClass().getResource(iconWindowPath));
            if (windowIcon.getImage() != null && windowIcon.getIconWidth() > 0) {
                this.setIconImage(windowIcon.getImage());
                System.out.println("Ikon window aplikasi berhasil di-set.");
            } else {
                System.err.println("Gagal load ikon window dari: " + iconWindowPath);
            }
        } catch (Exception e) {
            System.err.println("Error saat nge-set ikon window aplikasi: " + e.getMessage());
            e.printStackTrace();
        }

        dataMasterManager = new DataMasterManager();
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));

        // Panel Kiri
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(colorLeftPanelBg);

        JPanel leftContentPanel = new JPanel();
        leftContentPanel.setOpaque(false);
        leftContentPanel.setLayout(new BoxLayout(leftContentPanel, BoxLayout.Y_AXIS));

        JLabel lblAppLogo = new JLabel();
        try {
            String logoSvgPath = ICON_CLASSPATH_BASE + "logo_aplikasi.svg";
            FlatSVGIcon appLogoSVG = new FlatSVGIcon(logoSvgPath, 200, 200);
            if (appLogoSVG.getIconWidth() > 0) {
                lblAppLogo.setIcon(appLogoSVG);
            } else {
                System.err.println("FlatSVGIcon gagal memuat gambar dari: " + logoSvgPath);
                lblAppLogo.setText("Logo Gagal");
                lblAppLogo.setForeground(colorAppNameText);
            }
        } catch (Exception e) {
            System.err.println("Error memuat logo aplikasi SVG: " + e.getMessage());
            lblAppLogo.setText("Error Logo");
            lblAppLogo.setForeground(colorAppNameText);
        }
        lblAppLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAppName = new JLabel("CCTV Maintenance App");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblAppName.setForeground(colorAppNameText);
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftContentPanel.add(Box.createVerticalGlue());
        leftContentPanel.add(lblAppLogo);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftContentPanel.add(lblAppName);
        leftContentPanel.add(Box.createVerticalGlue());

        leftPanel.add(leftContentPanel);
        mainPanel.add(leftPanel);

        // Panel Kanan
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(colorRightPanelBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel loginFormContainer = new JPanel();
        loginFormContainer.setOpaque(false);
        loginFormContainer.setLayout(new BoxLayout(loginFormContainer, BoxLayout.Y_AXIS));

        JLabel lblLoginTitle = new JLabel("LOGIN");
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLoginTitle.setForeground(colorLoginTitle);
        lblLoginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLoginTitle.setBorder(new EmptyBorder(0, 0, 25, 0));

        JPanel usernamePanel = createInputPanelWithSVGIcon(ICON_CLASSPATH_BASE + "user.svg", "Username");
        JPanel passwordPanel = createInputPanelWithSVGIcon(ICON_CLASSPATH_BASE + "password.svg", "Password");

        btnLogin = new JButton("Masuk");
        styleButton(btnLogin);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(320, btnLogin.getPreferredSize().height + 10));

        btnBatal = new JButton("Batal");
        styleButton(btnBatal);
        btnBatal.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBatal.setMaximumSize(new Dimension(320, btnBatal.getPreferredSize().height + 10));

        loginFormContainer.add(lblLoginTitle);
        loginFormContainer.add(usernamePanel);
        loginFormContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        loginFormContainer.add(passwordPanel);
        loginFormContainer.add(Box.createRigidArea(new Dimension(0, 30)));
        loginFormContainer.add(btnLogin);
        loginFormContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        loginFormContainer.add(btnBatal);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(loginFormContainer, gbc);

        mainPanel.add(rightPanel);
        add(mainPanel);

        btnLogin.addActionListener(e -> prosesLogin());
        btnBatal.addActionListener(e -> System.exit(0));
    }

    private JPanel createInputPanelWithSVGIcon(String svgIconClasspath, String fieldTypeIdentifier) {
        JPanel fieldWrapper = new JPanel(new BorderLayout(8, 0));
        fieldWrapper.setOpaque(false);
        fieldWrapper.setPreferredSize(new Dimension(280, 40));
        fieldWrapper.setMaximumSize(new Dimension(300, 45));

        JTextField localFieldReference;

        if (fieldTypeIdentifier.equalsIgnoreCase("Password")) {
            JPasswordField passField = new JPasswordField();
            passField.setEchoChar('●');
            this.txtPassword = passField;
            localFieldReference = passField;
        } else {
            JTextField userField = new JTextField();
            this.txtUsername = userField;
            localFieldReference = userField;
        }

        localFieldReference.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        localFieldReference.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorTextFieldBorder, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblIcon = new JLabel();
        try {
            FlatSVGIcon fieldSVGIcon = new FlatSVGIcon(svgIconClasspath, 24, 24);
            if (fieldSVGIcon.getIconWidth() > 0) {
                lblIcon.setIcon(fieldSVGIcon);
            } else {
                System.err.println("Gagal load SVG untuk field icon: " + svgIconClasspath);
                lblIcon.setText("X");
            }
        } catch (Exception e) {
            System.err.println("Error load SVG untuk field icon: " + svgIconClasspath + " - " + e.getMessage());
            lblIcon.setText("X");
        }
        lblIcon.setForeground(Color.GRAY);

        fieldWrapper.add(localFieldReference, BorderLayout.CENTER);
        fieldWrapper.add(lblIcon, BorderLayout.WEST);

        return fieldWrapper;
    }

    private void styleButton(JButton button) {
        button.setBackground(colorButtonSignInBg);
        button.setForeground(colorButtonSignInText);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setMargin(new Insets(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void prosesLogin() {
        String usernameInput = txtUsername.getText().trim();
        String passwordInput = new String(txtPassword.getPassword());

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
             NotificationManager.showError(this, "Username atau Password salah!");
            return;
        }

        User loggedInUser = dataMasterManager.authenticateUser(usernameInput, passwordInput);

        if (loggedInUser != null) {
            UserSession.getInstance().login(loggedInUser);
            String namaSapaan = (loggedInUser.getNamaLengkap() != null && !loggedInUser.getNamaLengkap().isEmpty())
                    ? loggedInUser.getNamaLengkap()
                    : loggedInUser.getUsername();
            JOptionPane.showMessageDialog(this, "Login berhasil! Selamat datang, " + namaSapaan + "!", "Login Sukses", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtUsername.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                com.formdev.flatlaf.FlatLightLaf.setup();
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF.");
            }
            new FormLogin().setVisible(true);
        });
    }
}