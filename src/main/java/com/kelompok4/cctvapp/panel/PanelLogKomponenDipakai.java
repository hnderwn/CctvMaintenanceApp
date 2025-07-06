package com.kelompok4.cctvapp.panel;

import com.kelompok4.cctvapp.Komponen;
import com.kelompok4.cctvapp.LogKerusakan;
import com.kelompok4.cctvapp.LogKomponenDipakai;
import com.kelompok4.cctvapp.LogMaintenance;
import com.kelompok4.cctvapp.report.ReportManager;
import com.kelompok4.cctvapp.service.DataMasterManager;
import com.kelompok4.cctvapp.util.NotificationManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/*
 * =================================================================================
 * RINGKASAN METHOD
 * =================================================================================
 * PanelLogKomponenDipakai(dmManager)         - Konstruktor 1 untuk panel log komponen.
 * PanelLogKomponenDipakai(dmManager, parent) - Konstruktor 2 untuk panel log komponen.
 * initComponents()                         - Menginisialisasi dan menata semua komponen UI.
 * addListeners()                           - Menambahkan semua event listener ke komponen.
 * initButtonStates()                       - Mengatur kondisi awal (enabled/disabled) tombol.
 * refreshAllData()                         - Memuat semua data awal (log MT, log KR, komponen, etc).
 * loadLogMaintenances()                    - Memuat data Log Maintenance ke dalam JComboBox.
 * loadLogKerusakans()                      - Memuat data Log Kerusakan ke dalam JComboBox.
 * loadKomponens()                          - Memuat data Komponen ke dalam JComboBox.
 * loadLogKomponenDipakai()                 - Memuat data log komponen yang dipakai ke tabel utama.
 * clearForm()                              - Membersihkan inputan form dan mereset ke kondisi awal.
 * calculateBiaya()                         - Menghitung total biaya secara dinamis dari komponen dan jumlah.
 * updateSisaStokInfo()                     - Menampilkan sisa stok komponen yang dipilih secara dinamis.
 * getLogKomponenDipakaiFromForm()          - Mengambil & memvalidasi data dari form, lalu mengembalikannya sebagai objek.
 * saveLogKomponenDipakai()                 - Menyimpan log baru dan mengurangi stok komponen.
 * displayLogKomponenDipakaiDetails()       - Menampilkan detail data dari tabel yang dipilih ke dalam form.
 * updateLogKomponenDipakai()               - Mengupdate log dan menyesuaikan kembali stok komponen.
 * deleteLogKomponenDipakai()               - Menghapus log dan mengembalikan stok komponen.
 * =================================================================================
 */
public class PanelLogKomponenDipakai extends JPanel {

    // Komponen UI
    private JLabel lblLogMaintenance, lblLogKerusakan, lblKomponen, lblJumlah, lblBiaya, lblSisaStokInfo;
    private JComboBox<LogMaintenance> cmbLogMaintenance;
    private DefaultComboBoxModel<LogMaintenance> cmbLogMaintenanceModel;
    private JComboBox<LogKerusakan> cmbLogKerusakan;
    private DefaultComboBoxModel<LogKerusakan> cmbLogKerusakanModel;
    private JComboBox<Komponen> cmbKomponen;
    private DefaultComboBoxModel<Komponen> cmbKomponenModel;
    private JTextField txtJumlah;
    private JTextField txtBiaya;
    private JButton btnNew, btnSave, btnUpdate, btnDelete, btnRefresh, btnCetak;
    private JTable tblLogKomponenDipakai;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPaneTabel;

    // Service & Utility
    private DataMasterManager dataMasterManager;
    private Component parentComponentForDialog;

    public PanelLogKomponenDipakai(DataMasterManager dmManager) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = this;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        refreshAllData();
    }

    public PanelLogKomponenDipakai(DataMasterManager dmManager, Component parentForDialog) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = parentForDialog;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        refreshAllData();
    }

    private void initComponents() {
        lblLogMaintenance = new JLabel("Log Maintenance (Opsional):");
        lblLogKerusakan = new JLabel("Log Kerusakan (Opsional):");
        lblKomponen = new JLabel("Komponen:");
        lblJumlah = new JLabel("Jumlah Dipakai:");
        lblBiaya = new JLabel("Total Biaya:");
        lblSisaStokInfo = new JLabel("Sisa Stok Komponen: -");

        cmbLogMaintenanceModel = new DefaultComboBoxModel<>();
        cmbLogMaintenance = new JComboBox<>(cmbLogMaintenanceModel);
        cmbLogKerusakanModel = new DefaultComboBoxModel<>();
        cmbLogKerusakan = new JComboBox<>(cmbLogKerusakanModel);
        cmbKomponenModel = new DefaultComboBoxModel<>();
        cmbKomponen = new JComboBox<>(cmbKomponenModel);

        txtJumlah = new JTextField(10);
        txtBiaya = new JTextField(10);
        txtBiaya.setEditable(false);

        btnNew = new JButton("Baru");
        btnSave = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnCetak = new JButton("Cetak");

        String[] columnNames = {"ID", "ID Log MT", "ID Log Rusak", "ID Komp.", "Nama Komponen", "Jumlah", "Biaya"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblLogKomponenDipakai = new JTable(tableModel);
        scrollPaneTabel = new JScrollPane(tblLogKomponenDipakai);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn colId = tblLogKomponenDipakai.getColumnModel().getColumn(0);
        colId.setPreferredWidth(50);
        colId.setMaxWidth(60);
        colId.setCellRenderer(centerRenderer);

        TableColumn colIdLogMT = tblLogKomponenDipakai.getColumnModel().getColumn(1);
        colIdLogMT.setPreferredWidth(80);
        colIdLogMT.setCellRenderer(centerRenderer);

        TableColumn colIdLogRusak = tblLogKomponenDipakai.getColumnModel().getColumn(2);
        colIdLogRusak.setPreferredWidth(80);
        colIdLogRusak.setCellRenderer(centerRenderer);

        TableColumn colIdKomponen = tblLogKomponenDipakai.getColumnModel().getColumn(3);
        colIdKomponen.setPreferredWidth(100);

        TableColumn colNamaKomponen = tblLogKomponenDipakai.getColumnModel().getColumn(4);
        colNamaKomponen.setPreferredWidth(250);

        TableColumn colJumlah = tblLogKomponenDipakai.getColumnModel().getColumn(5);
        colJumlah.setPreferredWidth(70);
        colJumlah.setMaxWidth(80);
        colJumlah.setCellRenderer(centerRenderer);

        TableColumn colBiaya = tblLogKomponenDipakai.getColumnModel().getColumn(6);
        colBiaya.setPreferredWidth(150);
        colBiaya.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setText("Rp. " + value.toString());
                    setHorizontalAlignment(LEFT);
                }
                return c;
            }
        });

        JPanel formInputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Detail Log Komponen Dipakai"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formInputPanel.add(lblLogMaintenance);
        formInputPanel.add(cmbLogMaintenance);
        formInputPanel.add(lblLogKerusakan);
        formInputPanel.add(cmbLogKerusakan);
        formInputPanel.add(lblKomponen);
        formInputPanel.add(cmbKomponen);
        formInputPanel.add(lblJumlah);
        formInputPanel.add(txtJumlah);
        formInputPanel.add(lblBiaya);
        formInputPanel.add(txtBiaya);
        formInputPanel.add(lblSisaStokInfo);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionButtonPanel.add(btnNew);
        actionButtonPanel.add(btnSave);
        actionButtonPanel.add(btnUpdate);
        actionButtonPanel.add(btnDelete);
        actionButtonPanel.add(btnRefresh);
        actionButtonPanel.add(btnCetak);

        add(formInputPanel, BorderLayout.NORTH);
        add(scrollPaneTabel, BorderLayout.CENTER);
        add(actionButtonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        cmbKomponen.addActionListener(e -> {
            calculateBiaya();
            updateSisaStokInfo();
        });

        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateBiaya();
                updateSisaStokInfo();
            }
        });

        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveLogKomponenDipakai());
        btnUpdate.addActionListener(e -> updateLogKomponenDipakai());
        btnDelete.addActionListener(e -> deleteLogKomponenDipakai());
        btnRefresh.addActionListener(e -> refreshAllData());

        btnCetak.addActionListener(e -> {
            HashMap<String, Object> params = new HashMap<>();
            String pathJasper = "/com/kelompok4/cctvapp/report/LogKomponenDipakai.jasper";
            ReportManager.tampilkanLaporan(SwingUtilities.getWindowAncestor(this), "Laporan Log Komponen Dipakai", pathJasper, params);
        });

        tblLogKomponenDipakai.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    displayLogKomponenDipakaiDetails();
                }
            }
        });
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshAllData();
            }
        });
    }

    private void initButtonStates() {
        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    
    private void refreshAllData() {
        System.out.println("PANEL LOG KOMPONEN: Ditampilkan, merefresh data...");
        loadLogMaintenances();
        loadLogKerusakans();
        loadKomponens();
        loadLogKomponenDipakai();
    }

    private void loadLogMaintenances() {
        cmbLogMaintenanceModel.removeAllElements();
        cmbLogMaintenanceModel.addElement(null);
        try {
            List<LogMaintenance> logs = dataMasterManager.getAllLogMaintenance();
            if (logs != null) {
                for (LogMaintenance log : logs) {
                    cmbLogMaintenanceModel.addElement(log);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading Log Maintenance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLogKerusakans() {
        cmbLogKerusakanModel.removeAllElements();
        cmbLogKerusakanModel.addElement(null);
        try {
            List<LogKerusakan> logs = dataMasterManager.getAllLogKerusakan();
            if (logs != null) {
                for (LogKerusakan log : logs) {
                    cmbLogKerusakanModel.addElement(log);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading Log Kerusakan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadKomponens() {
        cmbKomponenModel.removeAllElements();
        cmbKomponenModel.addElement(null);
        try {
            List<Komponen> komponens = dataMasterManager.getAllKomponen();
            if (komponens != null) {
                for (Komponen komponen : komponens) {
                    cmbKomponenModel.addElement(komponen);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading Komponen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLogKomponenDipakai() {
        tableModel.setRowCount(0);
        try {
            List<LogKomponenDipakai> logs = dataMasterManager.getAllLogKomponenDipakai();
            if (logs != null) {
                for (LogKomponenDipakai log : logs) {
                    Object[] row = {
                            log.getId(),
                            (log.getLogMaintenance() != null ? log.getLogMaintenance().getIdLog() : null),
                            (log.getLogKerusakan() != null ? log.getLogKerusakan().getIdLog() : null),
                            (log.getKomponen() != null ? log.getKomponen().getIdKomponen() : "N/A"),
                            (log.getKomponen() != null ? log.getKomponen().getNamaKomponen() : "N/A"),
                            log.getJumlahDipakai(),
                            log.getBiaya()
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading Log Komponen Dipakai: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        cmbLogMaintenance.setSelectedIndex(0);
        cmbLogKerusakan.setSelectedIndex(0);
        cmbKomponen.setSelectedIndex(0);
        txtJumlah.setText("");
        txtBiaya.setText("");
        lblSisaStokInfo.setText("Sisa Stok Komponen: -");

        initButtonStates();
        tblLogKomponenDipakai.clearSelection();
    }

    private void calculateBiaya() {
        Komponen selectedKomponen = (Komponen) cmbKomponen.getSelectedItem();
        String jumlahStr = txtJumlah.getText().trim();

        if (selectedKomponen != null && !jumlahStr.isEmpty()) {
            try {
                int jumlah = Integer.parseInt(jumlahStr);
                if (jumlah < 0) {
                    txtBiaya.setText("");
                    return;
                }
                BigDecimal hargaSatuan = selectedKomponen.getHargaSatuan();
                if (hargaSatuan != null) {
                    BigDecimal biaya = hargaSatuan.multiply(BigDecimal.valueOf(jumlah));
                    txtBiaya.setText(biaya.toString());
                } else {
                    txtBiaya.setText("");
                }
            } catch (NumberFormatException e) {
                txtBiaya.setText("");
            }
        } else {
            txtBiaya.setText("");
        }
    }

    private void updateSisaStokInfo() {
        Komponen selectedKomponen = (Komponen) cmbKomponen.getSelectedItem();
        if (selectedKomponen != null) {
            lblSisaStokInfo.setText("Sisa Stok Komponen: " + selectedKomponen.getStok());
        } else {
            lblSisaStokInfo.setText("Sisa Stok Komponen: -");
        }
    }

    private LogKomponenDipakai getLogKomponenDipakaiFromForm() {
        LogMaintenance selectedLogMaintenance = (LogMaintenance) cmbLogMaintenance.getSelectedItem();
        LogKerusakan selectedLogKerusakan = (LogKerusakan) cmbLogKerusakan.getSelectedItem();
        Komponen selectedKomponen = (Komponen) cmbKomponen.getSelectedItem();
        String jumlahStr = txtJumlah.getText().trim();
        String biayaStr = txtBiaya.getText().trim();

        if (selectedKomponen == null) {
            NotificationManager.showError(parentComponentForDialog, "Komponen harus dipilih!");
            return null;
        }
        if (jumlahStr.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Jumlah dipakai tidak boleh kosong!");
            return null;
        }
        if (biayaStr.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Biaya belum terhitung atau kosong.");
            return null;
        }
        if (selectedLogMaintenance == null && selectedLogKerusakan == null) {
            NotificationManager.showError(parentComponentForDialog, "Pilih salah satu referensi Log (Maintenance atau Kerusakan)!");
            return null;
        }
        if (selectedLogMaintenance != null && selectedLogKerusakan != null) {
            NotificationManager.showError(parentComponentForDialog, "Hanya boleh memilih satu jenis referensi Log (Maintenance ATAU Kerusakan)!");
            return null;
        }

        try {
            int jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) {
                NotificationManager.showError(parentComponentForDialog, "Jumlah dipakai harus lebih dari 0!");
                return null;
            }
            BigDecimal biaya = new BigDecimal(biayaStr);
            return new LogKomponenDipakai(0, selectedLogMaintenance, selectedLogKerusakan, selectedKomponen, jumlah, biaya);
        } catch (NumberFormatException e) {
            NotificationManager.showError(parentComponentForDialog, "Jumlah harus berupa angka dan biaya harus valid!");
            return null;
        }
    }

    private void saveLogKomponenDipakai() {
        LogKomponenDipakai logKomponen = getLogKomponenDipakaiFromForm();
        if (logKomponen != null) {
            try {
                Komponen komponen = logKomponen.getKomponen();
                int jumlahDipakai = logKomponen.getJumlahDipakai();
                int stokAwal = komponen.getStok();
                int stokBaru = stokAwal - jumlahDipakai;

                if (stokBaru < 0) {
                    NotificationManager.showError(parentComponentForDialog, "Stok komponen '" + komponen.getNamaKomponen() + "' tidak mencukupi! Sisa: " + stokAwal);
                    return;
                }

                int generatedId = dataMasterManager.addLogKomponenDipakai(logKomponen);
                if (generatedId > 0) {
                    komponen.setStok(stokBaru);
                    if (dataMasterManager.updateKomponen(komponen)) {
                        String pesan = "<html>Log Pemakaian <b>ID: " + generatedId + " - " + komponen.getNamaKomponen() + "</b> (x" + jumlahDipakai + ") berhasil disimpan.<br><b>Stok Terbaru: " + stokBaru + "</b></html>";
                        NotificationManager.showSuccess(parentComponentForDialog, pesan);
                        refreshAllData();
                    } else {
                        NotificationManager.showWarning(parentComponentForDialog, "Log disimpan, tapi gagal memperbarui stok. Cek manual.");
                        loadLogKomponenDipakai();
                    }
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menyimpan Log Komponen Dipakai.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menyimpan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void displayLogKomponenDipakaiDetails() {
        int selectedRow = tblLogKomponenDipakai.getSelectedRow();
        if (selectedRow != -1) {
            int idLogKomp = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                LogKomponenDipakai log = dataMasterManager.findLogKomponenDipakaiById(idLogKomp);
                if (log != null) {
                    if (log.getLogMaintenance() != null) {
                        boolean found = false;
                        for (int i = 0; i < cmbLogMaintenanceModel.getSize(); i++) {
                            LogMaintenance item = cmbLogMaintenanceModel.getElementAt(i);
                            if (item != null && item.getIdLog() == log.getLogMaintenance().getIdLog()) {
                                cmbLogMaintenance.setSelectedItem(item);
                                found = true;
                                break;
                            }
                        }
                        if (!found) cmbLogMaintenance.setSelectedIndex(0);
                    } else {
                        cmbLogMaintenance.setSelectedIndex(0);
                    }

                    if (log.getLogKerusakan() != null) {
                        boolean found = false;
                        for (int i = 0; i < cmbLogKerusakanModel.getSize(); i++) {
                            LogKerusakan item = cmbLogKerusakanModel.getElementAt(i);
                            if (item != null && item.getIdLog() == log.getLogKerusakan().getIdLog()) {
                                cmbLogKerusakan.setSelectedItem(item);
                                found = true;
                                break;
                            }
                        }
                        if (!found) cmbLogKerusakan.setSelectedIndex(0);
                    } else {
                        cmbLogKerusakan.setSelectedIndex(0);
                    }

                    if (log.getKomponen() != null) {
                        boolean found = false;
                        for (int i = 0; i < cmbKomponenModel.getSize(); i++) {
                            Komponen item = cmbKomponenModel.getElementAt(i);
                            if (item != null && item.getIdKomponen().equals(log.getKomponen().getIdKomponen())) {
                                cmbKomponen.setSelectedItem(item);
                                found = true;
                                break;
                            }
                        }
                        if (!found) cmbKomponen.setSelectedIndex(0);
                    } else {
                        cmbKomponen.setSelectedIndex(0);
                    }

                    
                    txtJumlah.setText(String.valueOf(log.getJumlahDipakai()));
                    txtBiaya.setText((log.getBiaya() != null) ? log.getBiaya().toString() : "");
                    updateSisaStokInfo();

                    btnSave.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Data Log dengan ID " + idLogKomp + " tidak ditemukan.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error loading details: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateLogKomponenDipakai() {
        int selectedRow = tblLogKomponenDipakai.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data yang ingin diupdate.");
            return;
        }

        int idToUpdate = (int) tableModel.getValueAt(selectedRow, 0);
        LogKomponenDipakai logKomponenBaru = getLogKomponenDipakaiFromForm();

        if (logKomponenBaru != null) {
            logKomponenBaru.setId(idToUpdate);
            try {
                LogKomponenDipakai logKomponenLama = dataMasterManager.findLogKomponenDipakaiById(idToUpdate);
                if (logKomponenLama == null) {
                    NotificationManager.showError(parentComponentForDialog, "Data lama tidak ditemukan untuk ID: " + idToUpdate);
                    return;
                }

                Komponen komponenLama = logKomponenLama.getKomponen();
                int jumlahLama = logKomponenLama.getJumlahDipakai();
                Komponen komponenBaru = logKomponenBaru.getKomponen();
                int jumlahBaru = logKomponenBaru.getJumlahDipakai();


                komponenLama.setStok(komponenLama.getStok() + jumlahLama);
                if (!dataMasterManager.updateKomponen(komponenLama)) {
                    NotificationManager.showError(parentComponentForDialog, "Gagal mengembalikan stok komponen lama. Update dibatalkan.");
                    loadKomponens();
                    return;
                }

                Komponen komponenBaruUpdated = dataMasterManager.findKomponenById(komponenBaru.getIdKomponen());
                int stokKomponenBaruSaatIni = komponenBaruUpdated.getStok();
                int stokKomponenBaruSetelahDipakai = stokKomponenBaruSaatIni - jumlahBaru;

                if (stokKomponenBaruSetelahDipakai < 0) {
                    NotificationManager.showError(parentComponentForDialog, "Stok '" + komponenBaru.getNamaKomponen() + "' tidak mencukupi! Sisa: " + stokKomponenBaruSaatIni);
                    komponenLama.setStok(komponenLama.getStok() - jumlahLama);
                    dataMasterManager.updateKomponen(komponenLama);
                    loadKomponens();
                    return;
                }
                komponenBaru.setStok(stokKomponenBaruSetelahDipakai);

                if (dataMasterManager.updateLogKomponenDipakai(logKomponenBaru) && dataMasterManager.updateKomponen(komponenBaru)) {
                    String pesan = "<html>Log Pemakaian <b>ID: " + logKomponenBaru.getId() + " - " + komponenBaru.getNamaKomponen() + "</b> berhasil diupdate.<br><b>Stok Terbaru: " + komponenBaru.getStok() +"</b></html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    refreshAllData();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal mengupdate data atau stok komponen baru.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void deleteLogKomponenDipakai() {
        int selectedRow = tblLogKomponenDipakai.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data yang ingin dihapus.");
            return;
        }

        int idToDelete = (int) tableModel.getValueAt(selectedRow, 0);
        
        String namaKomponen = tableModel.getValueAt(selectedRow, 4).toString();
        String konfirmasiPesan = "<html>Yakin hapus log <b>ID: " + idToDelete + "</b> untuk komponen <b>" + namaKomponen + "</b>?<br>Ini akan mengembalikan stok komponen yang dipakai.</html>";
    
        if (NotificationManager.showConfirm(parentComponentForDialog, konfirmasiPesan)) {
            try {
                LogKomponenDipakai logToDelete = dataMasterManager.findLogKomponenDipakaiById(idToDelete);
                if (logToDelete != null) {
                    Komponen komponen = logToDelete.getKomponen();
                    int jumlahDipakai = logToDelete.getJumlahDipakai();
                    komponen.setStok(komponen.getStok() + jumlahDipakai);

                    if (dataMasterManager.deleteLogKomponenDipakai(idToDelete) && dataMasterManager.updateKomponen(komponen)) {
                        String pesan = "<html>Log Pemakaian <b>ID: " + idToDelete + " - " + komponen.getNamaKomponen() + "</b> berhasil dihapus.<br><b>Stok Terbaru: " + komponen.getStok() +"</b></html>";
                        NotificationManager.showSuccess(parentComponentForDialog, pesan);
                        refreshAllData();
                    } else {
                        NotificationManager.showError(parentComponentForDialog, "Gagal menghapus data atau gagal mengembalikan stok.");
                    }
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Data Log dengan ID " + idToDelete + " tidak ditemukan.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat delete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}