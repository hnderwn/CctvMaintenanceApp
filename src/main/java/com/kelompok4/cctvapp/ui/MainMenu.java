package com.kelompok4.cctvapp.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.kelompok4.cctvapp.panel.*;
import com.kelompok4.cctvapp.report.ReportManager;
import com.kelompok4.cctvapp.service.DataMasterManager;
import com.kelompok4.cctvapp.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/*
 * =================================================================================
 * RINGKASAN METHOD (MAIN MENU)
 * =================================================================================
 * Kelas ini merupakan window utama (JFrame) aplikasi setelah user berhasil login.
 * Mengatur layout utama (header, sidebar, content) dan navigasi antar panel.
 * ---------------------------------------------------------------------------------
 * MainMenu()                 - Konstruktor, membangun seluruh UI window utama aplikasi.
 * setupUIForRole()           - Mengatur visibilitas/status tombol menu berdasarkan role user.
 * setWindowIcon()            - Mengatur ikon untuk window aplikasi.
 * createHeaderPanel()        - Helper method untuk membuat panel header di bagian atas.
 * createSidebarPanel()       - Helper method untuk membuat panel sidebar menu di sebelah kiri.
 * createMainContentPanel()   - Helper method untuk membuat panel utama yang menggunakan CardLayout.
 * addListeners()             - Menambahkan semua event listener untuk tombol menu.
 * createLaporanMenu()        - Membuat dan mengembalikan JPopupMenu yang berisi daftar laporan.
 * addLaporanMenuItem(...)    - Helper method untuk menambahkan satu item ke menu laporan.
 * createWelcomePanel()       - Helper method untuk membuat panel selamat datang (tampilan awal).
 * createMenuButton(...)      - Helper method untuk membuat dan men-styling satu tombol di sidebar.
 * createSeparator()          - Helper method untuk membuat JSeparator.
 * =================================================================================
 */
public class MainMenu extends JFrame {

    // Komponen UI
    private JButton btnManageModels, btnManageUnits, btnManageTeknisi, btnManageKomponen;
    private JButton btnManageLogMaintenance, btnManageLogKerusakan, btnManageLogKomponenDipakai;
    private JButton btnBukaMenuLaporan;
    private JButton btnShowDashboard;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Service & Utility
    private DataMasterManager dataMasterManager;

    // Konstanta Nama Kartu
    private static final String WELCOME_PANEL_KEY = "WELCOME_PANEL";
    private static final String DASHBOARD_PANEL_KEY = "DASHBOARD_PANEL";
    private static final String CCTV_MODEL_PANEL_KEY = "CCTV_MODEL_PANEL";
    private static final String CCTV_UNIT_PANEL_KEY = "CCTV_UNIT_PANEL";
    private static final String TEKNISI_PANEL_KEY = "TEKNISI_PANEL_KEY";
    private static final String KOMPONEN_PANEL_KEY = "KOMPONEN_PANEL_KEY";
    private static final String LOG_MAINTENANCE_PANEL_KEY = "LOG_MAINTENANCE_PANEL_KEY";
    private static final String LOG_KERUSAKAN_PANEL_KEY = "LOG_KERUSAKAN_PANEL_KEY";
    private static final String LOG_KOMPONEN_DIPAKAI_PANEL_KEY = "LOG_KOMPONEN_DIPAKAI_PANEL_KEY";

    // Properti Styling
    private final String ICON_CLASSPATH_BASE = "com/kelompok4/cctvapp/icons/";
    private final Color colorHeaderBg = new Color(34, 101, 151);
    private final Color colorHeaderText = Color.WHITE;
    private final Color colorSidebarBg = new Color(240, 240, 240);
    private final Color colorMainContentBg = Color.WHITE;
    private final Color colorButtonText = new Color(50, 50, 50);

    public MainMenu() {
        setTitle("Dashboard - Aplikasi Manajemen Maintenance CCTV");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        setWindowIcon();

        dataMasterManager = new DataMasterManager();

        createHeaderPanel();
        createSidebarPanel();
        createMainContentPanel();
        addListeners();
        setupUIForRole();

        cardLayout.show(mainContentPanel, WELCOME_PANEL_KEY);
    }

    private void setupUIForRole() {
        String role = UserSession.getInstance().getRole();
        if (role != null && !role.equals("admin")) {
            btnManageModels.setEnabled(false);
            btnManageUnits.setEnabled(false);
            btnManageTeknisi.setEnabled(false);
            btnManageKomponen.setEnabled(false);
            btnManageKomponen.setEnabled(false);
        }
    }

    private void setWindowIcon() {
        try {
            String iconWindowPath = "/com/kelompok4/cctvapp/images/icon_window.png";
            ImageIcon windowIcon = new ImageIcon(getClass().getResource(iconWindowPath));
            if (windowIcon.getImage() != null && windowIcon.getIconWidth() > 0) {
                this.setIconImage(windowIcon.getImage());
            }
        } catch (Exception e) {
            System.err.println("Error saat nge-set ikon window aplikasi: " + e.getMessage());
        }
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(colorHeaderBg);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel("Aplikasi Maintenance CCTV");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblHeader.setForeground(colorHeaderText);

        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebarPanel() {
        JPanel sidebarOuterPanel = new JPanel(new BorderLayout(0, 10));
        sidebarOuterPanel.setBackground(colorSidebarBg);
        sidebarOuterPanel.setPreferredSize(new Dimension(280, 0));
        sidebarOuterPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblMenuUtama = new JLabel("MENU UTAMA");
        lblMenuUtama.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMenuUtama.setForeground(colorHeaderBg);
        lblMenuUtama.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));
        sidebarOuterPanel.add(lblMenuUtama, BorderLayout.NORTH);

        JPanel buttonContainerPanel = new JPanel();
        buttonContainerPanel.setLayout(new GridLayout(0, 1, 0, 10));
        buttonContainerPanel.setOpaque(false);
        buttonContainerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        btnShowDashboard = createMenuButton("📊 Dashboard Utama");
        btnManageModels = createMenuButton("🗃️ Data Model CCTV");
        btnManageUnits = createMenuButton("💻 Data Unit CCTV");
        btnManageTeknisi = createMenuButton("🛠️ Data Teknisi");
        btnManageKomponen = createMenuButton("⚙️ Data Komponen");
        btnManageLogMaintenance = createMenuButton("📖 Log Maintenance");
        btnManageLogKerusakan = createMenuButton("⚠️ Log Kerusakan");
        btnManageLogKomponenDipakai = createMenuButton("🔩 Log Komponen Dipakai");
        btnBukaMenuLaporan = createMenuButton("📊 Laporan");

        buttonContainerPanel.add(btnShowDashboard);
        buttonContainerPanel.add(createSeparator());
        buttonContainerPanel.add(btnManageModels);
        buttonContainerPanel.add(btnManageUnits);
        buttonContainerPanel.add(btnManageTeknisi);
        buttonContainerPanel.add(btnManageKomponen);
        buttonContainerPanel.add(createSeparator());
        buttonContainerPanel.add(btnManageLogMaintenance);
        buttonContainerPanel.add(btnManageLogKerusakan);
        buttonContainerPanel.add(btnManageLogKomponenDipakai);
        buttonContainerPanel.add(createSeparator());
        buttonContainerPanel.add(btnBukaMenuLaporan);

        sidebarOuterPanel.add(buttonContainerPanel, BorderLayout.CENTER);
        add(sidebarOuterPanel, BorderLayout.WEST);
    }

    private void createMainContentPanel() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(colorMainContentBg);

        mainContentPanel.add(createWelcomePanel(), WELCOME_PANEL_KEY);
        mainContentPanel.add(new PanelDashboard(dataMasterManager, this), DASHBOARD_PANEL_KEY);
        mainContentPanel.add(new PanelCctvModel(dataMasterManager, this), CCTV_MODEL_PANEL_KEY);
        mainContentPanel.add(new PanelCctvUnit(dataMasterManager, this), CCTV_UNIT_PANEL_KEY);
        mainContentPanel.add(new PanelTeknisi(dataMasterManager, this), TEKNISI_PANEL_KEY);
        mainContentPanel.add(new PanelKomponen(dataMasterManager, this), KOMPONEN_PANEL_KEY);
        mainContentPanel.add(new PanelLogMaintenance(dataMasterManager, this), LOG_MAINTENANCE_PANEL_KEY);
        mainContentPanel.add(new PanelLogKerusakan(dataMasterManager, this), LOG_KERUSAKAN_PANEL_KEY);
        mainContentPanel.add(new PanelLogKomponenDipakai(dataMasterManager, this), LOG_KOMPONEN_DIPAKAI_PANEL_KEY);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        btnShowDashboard.addActionListener(e -> cardLayout.show(mainContentPanel, WELCOME_PANEL_KEY));
        btnManageModels.addActionListener(e -> cardLayout.show(mainContentPanel, CCTV_MODEL_PANEL_KEY));
        btnManageUnits.addActionListener(e -> cardLayout.show(mainContentPanel, CCTV_UNIT_PANEL_KEY));
        btnManageTeknisi.addActionListener(e -> cardLayout.show(mainContentPanel, TEKNISI_PANEL_KEY));
        btnManageKomponen.addActionListener(e -> cardLayout.show(mainContentPanel, KOMPONEN_PANEL_KEY));
        btnManageLogMaintenance.addActionListener(e -> cardLayout.show(mainContentPanel, LOG_MAINTENANCE_PANEL_KEY));
        btnManageLogKerusakan.addActionListener(e -> cardLayout.show(mainContentPanel, LOG_KERUSAKAN_PANEL_KEY));
        btnManageLogKomponenDipakai.addActionListener(e -> cardLayout.show(mainContentPanel, LOG_KOMPONEN_DIPAKAI_PANEL_KEY));

        btnBukaMenuLaporan.addActionListener(e -> {
            JPopupMenu laporanMenu = createLaporanMenu();
            laporanMenu.show(btnBukaMenuLaporan, 0, btnBukaMenuLaporan.getHeight());
        });
    }

    private JPopupMenu createLaporanMenu() {
        JPopupMenu menu = new JPopupMenu();
        String role = UserSession.getInstance().getRole();
        boolean isAdmin = "admin".equals(role);

        if (isAdmin) {
            addLaporanMenuItem(menu, "Data Model CCTV", "Laporan Daftar Model CCTV", "/com/kelompok4/cctvapp/report/CctvModel.jasper");
            addLaporanMenuItem(menu, "Data Unit CCTV", "Laporan Daftar Unit CCTV", "/com/kelompok4/cctvapp/report/CctvUnit.jasper");
            addLaporanMenuItem(menu, "Data Teknisi", "Laporan Daftar Teknisi", "/com/kelompok4/cctvapp/report/Teknisi.jasper");
            addLaporanMenuItem(menu, "Data Komponen", "Laporan Daftar Komponen", "/com/kelompok4/cctvapp/report/Komponen.jasper");
            menu.addSeparator();
        }
        addLaporanMenuItem(menu, "Log Maintenance", "Laporan Log Maintenance", "/com/kelompok4/cctvapp/report/LogMaintenance.jasper");
        addLaporanMenuItem(menu, "Log Kerusakan", "Laporan Log Kerusakan", "/com/kelompok4/cctvapp/report/LogKerusakan.jasper");
        addLaporanMenuItem(menu, "Log Komponen Dipakai", "Laporan Log Komponen Dipakai", "/com/kelompok4/cctvapp/report/LogKomponenDipakai.jasper");

        return menu;
    }

    private void addLaporanMenuItem(JPopupMenu menu, String menuItemTitle, String reportFrameTitle, String jasperPath) {
        JMenuItem menuItem = new JMenuItem(menuItemTitle);
        menuItem.addActionListener(e -> {
            Map<String, Object> params = new HashMap<>();
            ReportManager.tampilkanLaporan(this, reportFrameTitle, jasperPath, params);
        });
        menu.add(menuItem);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("Selamat Datang!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblWelcome.setForeground(new Color(60, 60, 60));

        JLabel lblSubWelcome = new JLabel("Aplikasi Manajemen Maintenance CCTV Siap Digunakan.");
        lblSubWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubWelcome.setForeground(new Color(100, 100, 100));

        JButton btnGoToDashboard = new JButton("Lihat Statistik Dashboard");
        btnGoToDashboard.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGoToDashboard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoToDashboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGoToDashboard.addActionListener(e -> cardLayout.show(mainContentPanel, DASHBOARD_PANEL_KEY));

        textPanel.add(Box.createVerticalGlue());

        JLabel lblAppIcon = new JLabel();
        try {
            String logoSvgPath = "com/kelompok4/cctvapp/images/logo_aplikasi.svg";
            FlatSVGIcon appLogoSVG = new FlatSVGIcon(logoSvgPath, 128, 128);
            if (appLogoSVG.getIconWidth() > 0) {
                lblAppIcon.setIcon(appLogoSVG);
            } else {
                lblAppIcon.setText("Logo Gagal Dimuat");
                lblAppIcon.setForeground(Color.DARK_GRAY);
            }
        } catch (Exception e) {
            lblAppIcon.setText("Error Logo SVG");
            lblAppIcon.setForeground(Color.DARK_GRAY);
        }
        lblAppIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (lblAppIcon.getIcon() != null) {
            textPanel.add(lblAppIcon);
            textPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        textPanel.add(lblWelcome);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(lblSubWelcome);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(btnGoToDashboard);
        textPanel.add(Box.createVerticalGlue());

        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        button.setForeground(colorButtonText);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private Component createSeparator() {
        return new JSeparator(SwingConstants.HORIZONTAL);
    }
}