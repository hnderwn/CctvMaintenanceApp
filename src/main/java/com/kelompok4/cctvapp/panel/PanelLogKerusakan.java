package com.kelompok4.cctvapp.panel;

import com.kelompok4.cctvapp.CctvUnit;
import com.kelompok4.cctvapp.LogKerusakan;
import com.kelompok4.cctvapp.Teknisi;
import com.kelompok4.cctvapp.report.ReportManager;
import com.kelompok4.cctvapp.service.DataMasterManager;
import com.toedter.calendar.JDateChooser;
import com.kelompok4.cctvapp.util.NotificationManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/*
 * =================================================================================
 * RINGKASAN METHOD
 * =================================================================================
 * PanelLogKerusakan(dmManager)         - Konstruktor 1 untuk panel log kerusakan.
 * PanelLogKerusakan(dmManager, parent) - Konstruktor 2 untuk panel log kerusakan.
 * initComponents()                     - Menginisialisasi dan menata semua komponen UI.
 * addListeners()                       - Menambahkan semua event listener ke komponen.
 * initButtonStates()                   - Mengatur kondisi awal (enabled/disabled) tombol.
 * refreshAllData()                    - Memuat semua data awal yang dibutuhkan (unit, teknisi, log).
 * loadCctvUnits()                      - Memuat data master Unit CCTV ke dalam JComboBox.
 * loadTeknisi()                        - Memuat data master Teknisi ke dalam JComboBox.
 * loadLogKerusakan()                   - Memuat data log kerusakan dari database ke tabel.
 * clearForm()                          - Membersihkan inputan form dan mereset ke kondisi awal.
 * getLogKerusakanFromForm()            - Mengambil & memvalidasi data dari form, lalu mengembalikannya sebagai objek.
 * saveLogKerusakan()                   - Menyimpan data log kerusakan baru ke database.
 * displayLogKerusakanDetails()         - Menampilkan detail data dari tabel yang dipilih ke dalam form.
 * updateLogKerusakan()                 - Mengupdate data log kerusakan yang ada di database.
 * deleteLogKerusakan()                 - Menghapus data log kerusakan yang dipilih dari database.
 * =================================================================================
 */
public class PanelLogKerusakan extends JPanel {

    // Komponen UI
    private JLabel lblCctvUnit, lblTeknisi, lblTanggal, lblJam, lblDeskripsi;
    private JComboBox<CctvUnit> cmbCctvUnit;
    private DefaultComboBoxModel<CctvUnit> cmbCctvUnitModel;
    private JComboBox<Teknisi> cmbTeknisi;
    private DefaultComboBoxModel<Teknisi> cmbTeknisiModel;
    private JDateChooser dateChooserTanggal;
    private JTextField txtJam;
    private JTextArea txtDeskripsi;
    private JScrollPane scrollPaneDeskripsi;
    private JButton btnNew, btnSave, btnUpdate, btnDelete, btnRefresh, btnCetak;
    private JTable tblLogKerusakan;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPaneTabel;

    // Service & Utility
    private DataMasterManager dataMasterManager;
    private Component parentComponentForDialog;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormatTable = new SimpleDateFormat("yyyy-MM-dd");

    public PanelLogKerusakan(DataMasterManager dmManager) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = this;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        refreshAllData();
    }

    public PanelLogKerusakan(DataMasterManager dmManager, Component parentForDialog) {
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
        lblCctvUnit = new JLabel("Unit CCTV:");
        lblTeknisi = new JLabel("Teknisi:");
        lblTanggal = new JLabel("Tanggal:");
        lblJam = new JLabel("Jam (HH:mm):");
        lblDeskripsi = new JLabel("Deskripsi:");

        cmbCctvUnitModel = new DefaultComboBoxModel<>();
        cmbCctvUnit = new JComboBox<>(cmbCctvUnitModel);
        cmbTeknisiModel = new DefaultComboBoxModel<>();
        cmbTeknisi = new JComboBox<>(cmbTeknisiModel);
        dateChooserTanggal = new JDateChooser();
        dateChooserTanggal.setDateFormatString("yyyy-MM-dd");
        dateChooserTanggal.setMaxSelectableDate(new Date());    
        txtJam = new JTextField(5);
        txtDeskripsi = new JTextArea(2, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        scrollPaneDeskripsi = new JScrollPane(txtDeskripsi);

        btnNew = new JButton("Baru");
        btnSave = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnCetak = new JButton("Cetak");

        String[] columnNames = {"ID Log", "ID Unit", "Lokasi", "ID Teknisi", "Nama Teknisi", "Tanggal", "Jam", "Deskripsi"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblLogKerusakan = new JTable(tableModel);
        scrollPaneTabel = new JScrollPane(tblLogKerusakan);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn colIdLog = tblLogKerusakan.getColumnModel().getColumn(0);
        colIdLog.setPreferredWidth(80);
        colIdLog.setMaxWidth(100);
        colIdLog.setCellRenderer(centerRenderer);

        TableColumn colIdUnit = tblLogKerusakan.getColumnModel().getColumn(1);
        colIdUnit.setPreferredWidth(100);
        colIdUnit.setMinWidth(80);
        colIdUnit.setCellRenderer(centerRenderer);

        TableColumn colLokasi = tblLogKerusakan.getColumnModel().getColumn(2);
        colLokasi.setPreferredWidth(180);
        colLokasi.setMinWidth(150);

        TableColumn colIdTeknisi = tblLogKerusakan.getColumnModel().getColumn(3);
        colIdTeknisi.setPreferredWidth(80);
        colIdTeknisi.setCellRenderer(centerRenderer);

        TableColumn colNamaTeknisi = tblLogKerusakan.getColumnModel().getColumn(4);
        colNamaTeknisi.setPreferredWidth(120);
        colNamaTeknisi.setMinWidth(100);

        TableColumn colTanggal = tblLogKerusakan.getColumnModel().getColumn(5);
        colTanggal.setPreferredWidth(100);
        colTanggal.setCellRenderer(centerRenderer);

        TableColumn colJam = tblLogKerusakan.getColumnModel().getColumn(6);
        colJam.setPreferredWidth(80);
        colJam.setMaxWidth(100);
        colJam.setCellRenderer(centerRenderer);

        TableColumn colDeskripsi = tblLogKerusakan.getColumnModel().getColumn(7);
        colDeskripsi.setPreferredWidth(180);
        colDeskripsi.setMinWidth(100);

        JPanel formInputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Detail Log Kerusakan"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formInputPanel.add(lblCctvUnit);
        formInputPanel.add(cmbCctvUnit);
        formInputPanel.add(lblTeknisi);
        formInputPanel.add(cmbTeknisi);
        formInputPanel.add(lblTanggal);
        formInputPanel.add(dateChooserTanggal);
        formInputPanel.add(lblJam);
        formInputPanel.add(txtJam);
        formInputPanel.add(lblDeskripsi);
        formInputPanel.add(scrollPaneDeskripsi);

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
        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveLogKerusakan());
        btnUpdate.addActionListener(e -> updateLogKerusakan());
        btnDelete.addActionListener(e -> deleteLogKerusakan());
        btnRefresh.addActionListener(e -> refreshAllData());

        tblLogKerusakan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    displayLogKerusakanDetails();
                }
            }
        });

        btnCetak.addActionListener(e -> {
            HashMap<String, Object> params = new HashMap<>();
            String pathJasper = "/com/kelompok4/cctvapp/report/LogKerusakan.jasper";
            ReportManager.tampilkanLaporan(SwingUtilities.getWindowAncestor(this), "Laporan Daftar Kerusakan", pathJasper, params);
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
        System.out.println("PANEL LOG MAINTENANCE: Ditampilkan, merefresh data...");
        loadCctvUnits();
        loadTeknisi();
        loadLogKerusakan();
    }

    private void loadCctvUnits() {
        cmbCctvUnitModel.removeAllElements();
        cmbCctvUnitModel.addElement(null);
        try {
            List<CctvUnit> cctvUnits = dataMasterManager.getAllCctvUnits();
            if (cctvUnits != null) {
                for (CctvUnit unit : cctvUnits) {
                    cmbCctvUnitModel.addElement(unit);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading CCTV Units: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTeknisi() {
        cmbTeknisiModel.removeAllElements();
        cmbTeknisiModel.addElement(null);
        try {
            List<Teknisi> teknisiList = dataMasterManager.getAllTeknisi();
            if (teknisiList != null) {
                for (Teknisi teknisi : teknisiList) {
                    cmbTeknisiModel.addElement(teknisi);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading Teknisi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLogKerusakan() {
        tableModel.setRowCount(0);
        try {
            List<LogKerusakan> logs = dataMasterManager.getAllLogKerusakan();
            if (logs != null) {
                for (LogKerusakan log : logs) {
                    Object[] row = {
                            log.getIdLog(),
                            (log.getCctvUnit() != null) ? log.getCctvUnit().getIdCctv() : "N/A",
                            (log.getCctvUnit() != null) ? log.getCctvUnit().getLokasi() : "N/A",
                            (log.getTeknisi() != null) ? log.getTeknisi().getIdTeknisi() : "N/A",
                            (log.getTeknisi() != null) ? log.getTeknisi().getNamaTeknisi() : "N/A",
                            (log.getTanggal() != null) ? dateFormatTable.format(log.getTanggal()) : "",
                            log.getJam(),
                            log.getDeskripsi()
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading Log Kerusakan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        cmbCctvUnit.setSelectedIndex(0);
        cmbTeknisi.setSelectedIndex(0);
        dateChooserTanggal.setDate(new Date());
        txtJam.setText(timeFormat.format(new Date()));
        txtDeskripsi.setText("");

        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        tblLogKerusakan.clearSelection();
    }

    private LogKerusakan getLogKerusakanFromForm() {
        CctvUnit selectedUnit = (CctvUnit) cmbCctvUnit.getSelectedItem();
        Teknisi selectedTeknisi = (Teknisi) cmbTeknisi.getSelectedItem();
        Date tanggal = dateChooserTanggal.getDate();
        String jam = txtJam.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();

        if (selectedUnit == null) {
            NotificationManager.showError(parentComponentForDialog, "Unit CCTV harus dipilih!");
            return null;
        }
        if (selectedTeknisi == null) {
            NotificationManager.showError(parentComponentForDialog, "Teknisi harus dipilih!");
            return null;
        }
        if (tanggal == null) {
            NotificationManager.showError(parentComponentForDialog, "Tanggal harus diisi!");
            return null;
        }
        if (jam.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Jam harus diisi!");
            return null;
        }
        if (!jam.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            NotificationManager.showError(parentComponentForDialog, "Format jam tidak valid (HH:mm)!");
            return null;
        }
        if (deskripsi.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Deskripsi harus diisi!");
            return null;
        }
        return new LogKerusakan(0, selectedUnit, selectedTeknisi, tanggal, jam, deskripsi);
    }

    private void saveLogKerusakan() {
        LogKerusakan log = getLogKerusakanFromForm();
        if (log != null) {
            try {
                int generatedId = dataMasterManager.addLogKerusakan(log);
                if (generatedId > 0) {
                    String pesan = "<html>Log Maintenance <b>ID: " + generatedId + " - Unit CCTV: " + log.getCctvUnit().getIdCctv() + "</b> berhasil disimpan!<br><b>Status CCTV Unit diperbaharui menjadi Rusak</b></html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadLogKerusakan();
                    clearForm();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menyimpan Log Kerusakan.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menyimpan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void displayLogKerusakanDetails() {
        int selectedRow = tblLogKerusakan.getSelectedRow();
        if (selectedRow != -1) {
            Object cctvUnitIdObj = tableModel.getValueAt(selectedRow, 1);
            Object teknisiIdObj = tableModel.getValueAt(selectedRow, 3);
            String tanggalStr = tableModel.getValueAt(selectedRow, 5).toString();
            String jamStr = tableModel.getValueAt(selectedRow, 6).toString();
            String deskripsiStr = tableModel.getValueAt(selectedRow, 7).toString();

            if (cctvUnitIdObj != null) {
                String cctvUnitId = cctvUnitIdObj.toString();
                for (int i = 0; i < cmbCctvUnitModel.getSize(); i++) {
                    CctvUnit unitInCombo = cmbCctvUnitModel.getElementAt(i);
                    if (unitInCombo != null && unitInCombo.getIdCctv().equals(cctvUnitId)) {
                        cmbCctvUnit.setSelectedItem(unitInCombo);
                        break;
                    }
                }
            } else {
                cmbCctvUnit.setSelectedIndex(0);
            }

            if (teknisiIdObj != null) {
                String teknisiId = teknisiIdObj.toString();
                for (int i = 0; i < cmbTeknisiModel.getSize(); i++) {
                    Teknisi teknisiInCombo = cmbTeknisiModel.getElementAt(i);
                    if (teknisiInCombo != null && teknisiInCombo.getIdTeknisi().equals(teknisiId)) {
                        cmbTeknisi.setSelectedItem(teknisiInCombo);
                        break;
                    }
                }
            } else {
                cmbTeknisi.setSelectedIndex(0);
            }

            try {
                if (!tanggalStr.isEmpty()) {
                    dateChooserTanggal.setDate(dateFormatTable.parse(tanggalStr));
                } else {
                    dateChooserTanggal.setDate(null);
                }
            } catch (ParseException e) {
                dateChooserTanggal.setDate(null);
                e.printStackTrace();
            }

            txtJam.setText(jamStr);
            txtDeskripsi.setText(deskripsiStr);

            btnSave.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    private void updateLogKerusakan() {
        int selectedRow = tblLogKerusakan.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data yang ingin diupdate.");
            return;
        }

        String idLogStr = tableModel.getValueAt(selectedRow, 0).toString();
        LogKerusakan log = getLogKerusakanFromForm();

        if (log != null) {
            try {
                int idLogToUpdate = Integer.parseInt(idLogStr);
                log.setIdLog(idLogToUpdate);

                if (dataMasterManager.updateLogKerusakan(log)) {
                    String pesan = "<html>Log Kerusakan <b>ID: " + log.getIdLog() + " - Unit: " + log.getCctvUnit().getIdCctv() + "</b> berhasil diupdate!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadLogKerusakan();
                    clearForm();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal mengupdate data Log Kerusakan.");
                }
            } catch (NumberFormatException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat delete: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
               NotificationManager.showError(parentComponentForDialog, "Error database saat delete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void deleteLogKerusakan() {
        int selectedRow = tblLogKerusakan.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data yang ingin dihapus.");
            return;
        }

        String idLog = tableModel.getValueAt(selectedRow, 0).toString();
        String idUnit = tableModel.getValueAt(selectedRow, 1).toString();
        String lokasi = tableModel.getValueAt(selectedRow, 2).toString();
        String tanggal = tableModel.getValueAt(selectedRow, 5).toString();
        
        String konfirmasiPesan = "<html>Yakin ingin menghapus Log Kerusakan:<br><b>ID " + idLog + " (" + lokasi + " tgl " + tanggal + ")</b>?</html>";
        if (NotificationManager.showConfirm(parentComponentForDialog, konfirmasiPesan)) {
            try {
                if (dataMasterManager.deleteLogKerusakan(idLog)) {
                    String pesanSukses = "<html>Log Kerusakan <b>ID: " + idLog + " - Lokasi: " + lokasi + "</b> berhasil dihapus!<br><b>Tolong cek lagi status CCTV Unit</b></html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesanSukses);
                    loadLogKerusakan();
                    clearForm();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menghapus data Log Maintenance.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat delete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}