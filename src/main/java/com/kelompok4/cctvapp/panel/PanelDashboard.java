package com.kelompok4.cctvapp.panel;

import com.kelompok4.cctvapp.LogKerusakan;
import com.kelompok4.cctvapp.LogKomponenDipakai;
import com.kelompok4.cctvapp.LogMaintenance;
import com.kelompok4.cctvapp.service.DataMasterManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/*
 * =================================================================================
 * RINGKASAN METHOD
 * =================================================================================
 * PanelDashboard(...)          - Konstruktor utama untuk panel dashboard.
 * createStatCardsPanel()       - Membuat dan menata panel kartu statistik di bagian atas.
 * createSingleStatCard(...)    - Helper method untuk membuat satu buah kartu statistik.
 * createDynamicTablePanel()    - Membuat panel yang berisi kontrol dan tabel dinamis (CardLayout).
 * addListeners()               - Mendaftarkan semua event listener untuk komponen interaktif.
 * refreshDashboardData()       - Method publik untuk me-refresh semua data di dashboard.
 * switchTabel(...)             - Mengganti tabel yang ditampilkan berdasarkan pilihan JComboBox.
 * loadStatistikData()          - Memuat data statistik (jumlah CCTV, teknisi) secara asynchronous.
 * loadTabelMaintenance()       - Memuat data dari log maintenance ke dalam tabelnya.
 * loadTabelKerusakan()         - Memuat data dari log kerusakan ke dalam tabelnya.
 * loadTabelKomponen()          - Memuat data dari log komponen yang dipakai ke dalam tabelnya.
 * =================================================================================
 */
public class PanelDashboard extends JPanel {

    // Service & Utility
    private DataMasterManager dataMasterManager;
    private Component parentComponentForDialog;

    // Komponen Kartu Statistik
    private JLabel lblJumlahAktif;
    private JLabel lblJumlahRusak;
    private JLabel lblJumlahTeknisi;

    // Komponen Kontrol Tabel
    private JComboBox<String> cmbTabelChooser;
    private JButton btnRefreshData;

    // Komponen Tabel Dinamis
    private JPanel panelTabelContainer;
    private CardLayout cardLayoutTabel;
    private JTable tblLogMaintenance;
    private DefaultTableModel modelTblMaintenance;
    private JTable tblLogKerusakan;
    private DefaultTableModel modelTblKerusakan;
    private JTable tblLogKomponenDipakai;
    private DefaultTableModel modelTblKomponen;

    // Konstanta Nama Kartu
    private static final String KARTU_MAINTENANCE = "MAINTENANCE";
    private static final String KARTU_KERUSAKAN = "KERUSAKAN";
    private static final String KARTU_KOMPONEN = "KOMPONEN";

    public PanelDashboard(DataMasterManager dmManager, Component parentForDialog) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = parentForDialog;

        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(UIManager.getColor("Panel.background"));

        createStatCardsPanel();
        createDynamicTablePanel();
        addListeners();

        refreshDashboardData();
    }

    private void createStatCardsPanel() {
        JPanel panelKartu = new JPanel(new GridLayout(1, 3, 20, 0));
        panelKartu.setOpaque(false);

        lblJumlahAktif = new JLabel("...", SwingConstants.CENTER);
        lblJumlahRusak = new JLabel("...", SwingConstants.CENTER);
        lblJumlahTeknisi = new JLabel("...", SwingConstants.CENTER);

        panelKartu.add(createSingleStatCard("CCTV AKTIF", lblJumlahAktif, new Color(39, 174, 96)));
        panelKartu.add(createSingleStatCard("CCTV BERMASALAH", lblJumlahRusak, new Color(231, 76, 60)));
        panelKartu.add(createSingleStatCard("TOTAL TEKNISI", lblJumlahTeknisi, new Color(52, 152, 219)));

        add(panelKartu, BorderLayout.NORTH);
    }

    private JPanel createSingleStatCard(String judul, JLabel labelAngka, Color warnaGaris) {
        JPanel kartu = new JPanel(new BorderLayout(0, 5));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 8, 0, 0, warnaGaris),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblJudul.setForeground(Color.GRAY);

        labelAngka.setFont(new Font("Segoe UI", Font.BOLD, 48));

        kartu.add(lblJudul, BorderLayout.NORTH);
        kartu.add(labelAngka, BorderLayout.CENTER);

        return kartu;
    }

    private void createDynamicTablePanel() {
        JPanel panelInformasi = new JPanel(new BorderLayout(0, 10));
        panelInformasi.setBorder(BorderFactory.createTitledBorder("Informasi Log Terbaru"));

        JPanel panelKontrolTabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel lblPilihTabel = new JLabel("Tampilkan Tabel:");
        String[] pilihanTabel = {"Log Maintenance", "Log Kerusakan", "Log Komponen Dipakai"};
        cmbTabelChooser = new JComboBox<>(pilihanTabel);
        btnRefreshData = new JButton("Refresh Semua Data");

        panelKontrolTabel.add(lblPilihTabel);
        panelKontrolTabel.add(cmbTabelChooser);
        panelKontrolTabel.add(btnRefreshData);

        panelInformasi.add(panelKontrolTabel, BorderLayout.NORTH);

        cardLayoutTabel = new CardLayout();
        panelTabelContainer = new JPanel(cardLayoutTabel);

        String[] columnsMaintenance = {"ID Log", "Tanggal", "Lokasi", "Teknisi", "Deskripsi"};
        modelTblMaintenance = new DefaultTableModel(columnsMaintenance, 0);
        tblLogMaintenance = new JTable(modelTblMaintenance);
        panelTabelContainer.add(new JScrollPane(tblLogMaintenance), KARTU_MAINTENANCE);

        String[] columnsKerusakan = {"ID Log", "Tanggal", "Lokasi", "Teknisi", "Deskripsi Kerusakan"};
        modelTblKerusakan = new DefaultTableModel(columnsKerusakan, 0);
        tblLogKerusakan = new JTable(modelTblKerusakan);
        panelTabelContainer.add(new JScrollPane(tblLogKerusakan), KARTU_KERUSAKAN);

        String[] columnsKomponen = {"ID", "Referensi Log", "Nama Komponen", "Jumlah", "Biaya"};
        modelTblKomponen = new DefaultTableModel(columnsKomponen, 0);
        tblLogKomponenDipakai = new JTable(modelTblKomponen);
        panelTabelContainer.add(new JScrollPane(tblLogKomponenDipakai), KARTU_KOMPONEN);

        panelInformasi.add(panelTabelContainer, BorderLayout.CENTER);

        add(panelInformasi, BorderLayout.CENTER);
    }

    private void addListeners() {
        cmbTabelChooser.addActionListener(e -> {
            String pilihan = (String) cmbTabelChooser.getSelectedItem();
            if (pilihan != null) switchTabel(pilihan);
        });
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                System.out.println("PANEL DASHBOARD: Ditampilkan, merefresh data statistik & log...");
                refreshDashboardData();
            }
        });

        btnRefreshData.addActionListener(e -> refreshDashboardData());
    }

    public void refreshDashboardData() {
        System.out.println("Merefresh semua data dashboard...");
        loadStatistikData();
        String pilihanTabel = (String) cmbTabelChooser.getSelectedItem();
        if (pilihanTabel != null) {
            switchTabel(pilihanTabel);
        }
    }

    private void switchTabel(String namaTabel) {
        switch (namaTabel) {
            case "Log Maintenance":
                loadTabelMaintenance();
                cardLayoutTabel.show(panelTabelContainer, KARTU_MAINTENANCE);
                break;
            case "Log Kerusakan":
                loadTabelKerusakan();
                cardLayoutTabel.show(panelTabelContainer, KARTU_KERUSAKAN);
                break;
            case "Log Komponen Dipakai":
                loadTabelKomponen();
                cardLayoutTabel.show(panelTabelContainer, KARTU_KOMPONEN);
                break;
        }
    }

    private void loadStatistikData() {
        SwingWorker<int[], Void> worker = new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() throws Exception {
                int[] stats = new int[3];
                stats[0] = dataMasterManager.getJumlahCctvAktif();
                stats[1] = dataMasterManager.getJumlahCctvRusak();
                stats[2] = dataMasterManager.getTotalTeknisi();
                return stats;
            }

            @Override
            protected void done() {
                try {
                    int[] stats = get();
                    lblJumlahAktif.setText(String.valueOf(stats[0]));
                    lblJumlahRusak.setText(String.valueOf(stats[1]));
                    lblJumlahTeknisi.setText(String.valueOf(stats[2]));
                } catch (Exception e) {
                    lblJumlahAktif.setText("Error");
                    lblJumlahRusak.setText("Error");
                    lblJumlahTeknisi.setText("Error");
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentComponentForDialog, "Gagal memuat data statistik: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadTabelMaintenance() {
        modelTblMaintenance.setRowCount(0);
        try {
            List<LogMaintenance> logs = dataMasterManager.getAllLogMaintenance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            for (LogMaintenance log : logs) {
                Object[] row = {
                        log.getIdLog(),
                        (log.getTanggal() != null) ? dateFormat.format(log.getTanggal()) : "N/A",
                        (log.getCctvUnit() != null) ? log.getCctvUnit().getLokasi() : "N/A",
                        (log.getTeknisi() != null) ? log.getTeknisi().getNamaTeknisi() : "N/A",
                        log.getDeskripsiLog()
                };
                modelTblMaintenance.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Error memuat data Log Maintenance: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTabelKerusakan() {
        modelTblKerusakan.setRowCount(0);
        try {
            List<LogKerusakan> logs = dataMasterManager.getAllLogKerusakan();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            for (LogKerusakan log : logs) {
                Object[] row = {
                        log.getIdLog(),
                        (log.getTanggal() != null) ? dateFormat.format(log.getTanggal()) : "N/A",
                        (log.getCctvUnit() != null) ? log.getCctvUnit().getLokasi() : "N/A",
                        (log.getTeknisi() != null) ? log.getTeknisi().getNamaTeknisi() : "N/A",
                        log.getDeskripsi()
                };
                modelTblKerusakan.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Error memuat data Log Kerusakan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTabelKomponen() {
        modelTblKomponen.setRowCount(0);
        try {
            List<LogKomponenDipakai> logs = dataMasterManager.getAllLogKomponenDipakai();
            for (LogKomponenDipakai log : logs) {
                String referensi = "";
                if (log.getLogMaintenance() != null) {
                    referensi = "MT-" + log.getLogMaintenance().getIdLog();
                } else if (log.getLogKerusakan() != null) {
                    referensi = "KR-" + log.getLogKerusakan().getIdLog();
                }

                Object[] row = {
                        log.getId(),
                        referensi,
                        (log.getKomponen() != null) ? log.getKomponen().getNamaKomponen() : "N/A",
                        log.getJumlahDipakai(),
                        log.getBiaya()
                };
                modelTblKomponen.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Error memuat data Log Komponen Dipakai: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}