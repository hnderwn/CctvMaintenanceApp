package com.kelompok4.cctvapp.panel;

import com.kelompok4.cctvapp.CctvModel;
import com.kelompok4.cctvapp.CctvUnit;
import com.kelompok4.cctvapp.report.ReportManager;
import com.kelompok4.cctvapp.service.DataMasterManager;
import com.kelompok4.cctvapp.service.UserSession;
import com.kelompok4.cctvapp.util.NotificationManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/*
 * =================================================================================
 * RINGKASAN METHOD
 * =================================================================================
 * PanelCctvUnit(...)         - Konstruktor utama untuk panel manajemen unit CCTV.
 * initComponents()             - Menginisialisasi dan menata semua komponen UI pada panel.
 * addListeners()               - Menambahkan semua event listener ke komponen UI.
 * initButtonStates()           - Mengatur kondisi awal (enabled/disabled) untuk tombol-tombol.
 * loadInitialData()            - Memuat data awal yang dibutuhkan panel (model & unit).
 * setPlaceholder()             - Mengatur teks placeholder pada field ID CCTV.
 * clearForm()                  - Membersihkan semua inputan pada form dan mereset kondisi.
 * applyRoles()                 - Menerapkan pembatasan hak akses berdasarkan role user.
 * getCctvUnitFromForm()        - Mengambil & memvalidasi data dari form, lalu mengembalikannya sebagai objek.
 * displayCctvUnitDetails()     - Menampilkan detail data dari tabel yang dipilih ke dalam form.
 * loadCctvModelsToComboBox()   - Memuat data master Model CCTV ke dalam JComboBox.
 * loadCctvUnits()              - Memuat semua data unit CCTV dari database ke tabel.
 * saveCctvUnit()               - Menyimpan data unit CCTV baru dari form ke database.
 * updateCctvUnit()             - Mengupdate data unit CCTV yang ada di database.
 * deleteCctvUnit()             - Menghapus data unit CCTV yang dipilih dari database.
 * =================================================================================
 */
public class PanelCctvUnit extends JPanel {

    // Komponen UI
    private JLabel lblIdCctv, lblLokasi, lblModel, lblStatus, lblKeterangan;
    private JTextField txtIdCctv, txtLokasi, txtKeterangan;
    private JComboBox<CctvModel> cmbModel;
    private DefaultComboBoxModel<CctvModel> cmbModelModel;
    private JComboBox<String> cmbStatus;
    private DefaultComboBoxModel<String> cmbStatusModel;
    private String[] statusOptions = {"Aktif", "Tidak Aktif", "Rusak", "Perbaikan", "Perawatan"};
    private JButton btnNew, btnSave, btnUpdate, btnDelete, btnRefresh, btnCetak;
    private JTable tblCctvUnit;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPaneTabel;

    // Service & Utility
    private DataMasterManager dataMasterManager;
    private Component parentComponentForDialog;

    // Konstanta
    private final String ID_CCTV_PREFIX = "CCTV-";
    private final String ID_CCTV_PLACEHOLDER_TEXT = "Contoh: CCTV-001 (lantai-ruangan-urutan)";

    public PanelCctvUnit(DataMasterManager dmManager, Component parentForDialog) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = parentForDialog;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        loadInitialData();
        applyRoles();
    }

    private void initComponents() {
        lblIdCctv = new JLabel("ID Unit CCTV:");
        lblLokasi = new JLabel("Lokasi:");
        lblModel = new JLabel("Model CCTV:");
        lblStatus = new JLabel("Status:");
        lblKeterangan = new JLabel("Keterangan:");

        txtIdCctv = new JTextField(15);
        txtLokasi = new JTextField(20);
        txtKeterangan = new JTextField(30);

        cmbModelModel = new DefaultComboBoxModel<>();
        cmbModel = new JComboBox<>(cmbModelModel);

        cmbStatusModel = new DefaultComboBoxModel<>(statusOptions);
        cmbStatus = new JComboBox<>(cmbStatusModel);

        btnNew = new JButton("Baru");
        btnSave = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnCetak = new JButton("Cetak");

        String[] columnNames = {"ID Unit", "Lokasi", "Model", "Status", "Keterangan"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblCctvUnit = new JTable(tableModel);
        scrollPaneTabel = new JScrollPane(tblCctvUnit);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn kolomIdUnit = tblCctvUnit.getColumnModel().getColumn(0);
        kolomIdUnit.setPreferredWidth(100);
        kolomIdUnit.setMinWidth(80);

        TableColumn kolomLokasi = tblCctvUnit.getColumnModel().getColumn(1);
        kolomLokasi.setPreferredWidth(200);
        kolomLokasi.setMinWidth(150);

        TableColumn kolomModel = tblCctvUnit.getColumnModel().getColumn(2);
        kolomModel.setCellRenderer(centerRenderer);
        kolomModel.setPreferredWidth(150);
        kolomModel.setMinWidth(150);

        TableColumn kolomStatus = tblCctvUnit.getColumnModel().getColumn(3);
        kolomStatus.setPreferredWidth(100);
        kolomStatus.setMaxWidth(150);
        kolomStatus.setCellRenderer(centerRenderer);

        TableColumn kolomKeterangan = tblCctvUnit.getColumnModel().getColumn(4);
        kolomKeterangan.setPreferredWidth(250);
        kolomKeterangan.setMinWidth(200);

        JPanel formInputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Detail Unit CCTV"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formInputPanel.add(lblIdCctv);
        formInputPanel.add(txtIdCctv);
        formInputPanel.add(lblLokasi);
        formInputPanel.add(txtLokasi);
        formInputPanel.add(lblModel);
        formInputPanel.add(cmbModel);
        formInputPanel.add(lblStatus);
        formInputPanel.add(cmbStatus);
        formInputPanel.add(lblKeterangan);
        formInputPanel.add(txtKeterangan);

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
        btnSave.addActionListener(e -> saveCctvUnit());
        btnUpdate.addActionListener(e -> updateCctvUnit());
        btnDelete.addActionListener(e -> deleteCctvUnit());
        btnRefresh.addActionListener(e -> loadInitialData());

        tblCctvUnit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    displayCctvUnitDetails();
                }
            }
        });

        btnCetak.addActionListener(e -> {
            HashMap<String, Object> params = new HashMap<>();
            String pathJasper = "/com/kelompok4/cctvapp/report/CctvUnit.jasper";
            ReportManager.tampilkanLaporan(SwingUtilities.getWindowAncestor(this), "Laporan Daftar Unit CCTV", pathJasper, params);
        });

        txtIdCctv.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!btnUpdate.isEnabled()) {
                    if (txtIdCctv.getText().equals(ID_CCTV_PLACEHOLDER_TEXT)) {
                        txtIdCctv.setText(ID_CCTV_PREFIX);
                        txtIdCctv.setForeground(Color.BLACK);
                    } else if (txtIdCctv.getText().isEmpty()) {
                        txtIdCctv.setText(ID_CCTV_PREFIX);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!btnUpdate.isEnabled()) {
                    if (txtIdCctv.getText().equals(ID_CCTV_PREFIX) || txtIdCctv.getText().isEmpty()) {
                        setPlaceholder();
                    }
                }
            }
        });
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData();
            }
        });
    }
    
    private void refreshData() {
        System.out.println("PANEL CCTV UNIT: Ditampilkan, merefresh data...");
        loadCctvModelsToComboBox();
        loadCctvUnits();
    }

    private void initButtonStates() {
        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void loadInitialData() {
        loadCctvModelsToComboBox();
        loadCctvUnits();
        clearForm();
    }

    private void setPlaceholder() {
        txtIdCctv.setForeground(Color.GRAY);
        txtIdCctv.setText(ID_CCTV_PLACEHOLDER_TEXT);
    }

    private void clearForm() {
        setPlaceholder();
        txtIdCctv.setEnabled(true);
        txtLokasi.setText("");
        if (cmbModelModel.getSize() > 0) cmbModel.setSelectedIndex(0);
        if (cmbStatusModel.getSize() > 0) cmbStatus.setSelectedIndex(0);
        txtKeterangan.setText("");

        initButtonStates();
        tblCctvUnit.clearSelection();
    }

    private void applyRoles() {
        String role = UserSession.getInstance().getRole();
        if (role != null && !role.equals("admin")) {
            btnNew.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private CctvUnit getCctvUnitFromForm() {
        String idCctv = txtIdCctv.getText().trim();
        
        if (btnSave.isEnabled() && (idCctv.isEmpty() || idCctv.equals(ID_CCTV_PLACEHOLDER_TEXT) || idCctv.equals(ID_CCTV_PREFIX))) {
            NotificationManager.showError(parentComponentForDialog, "ID Unit CCTV harus dilengkapi setelah prefix '" + ID_CCTV_PREFIX + "'!");
            return null;
        }
        if (btnSave.isEnabled() && !idCctv.startsWith(ID_CCTV_PREFIX)) {
            NotificationManager.showError(parentComponentForDialog, "Format ID Unit CCTV harus diawali dengan '" + ID_CCTV_PREFIX + "'!");
            return null;
        }

        String lokasi = txtLokasi.getText().trim();
        CctvModel model = (CctvModel) cmbModel.getSelectedItem();
        String status = (String) cmbStatus.getSelectedItem();
        String keterangan = txtKeterangan.getText().trim();

        if (lokasi.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "ID Unit CCTV tidak boleh kosong!");
            return null;
        }
        if (model == null) {
            NotificationManager.showError(parentComponentForDialog, "Lokasi harus diisi!");
            return null;
        }
        if (status == null || status.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Model CCTV harus dipilih!");
            return null;
        }

        return new CctvUnit(idCctv, lokasi, status, keterangan, model);
    }

    private void displayCctvUnitDetails() {
        int selectedRow = tblCctvUnit.getSelectedRow();
        if (selectedRow != -1) {
            String idCctv = tableModel.getValueAt(selectedRow, 0).toString();
            txtIdCctv.setText(idCctv);
            txtIdCctv.setForeground(Color.BLACK);

            String lokasi = tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "";
            String modelName = tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "";
            String status = tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "";
            String keterangan = tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "";

            txtLokasi.setText(lokasi);
            txtKeterangan.setText(keterangan);
            cmbStatus.setSelectedItem(status);

            if (cmbModelModel.getSize() > 0) {
                boolean modelFound = false;
                for (int i = 0; i < cmbModelModel.getSize(); i++) {
                    CctvModel modelInCombo = cmbModelModel.getElementAt(i);
                    if (modelInCombo != null && modelInCombo.getNamaModel().equals(modelName)) {
                        cmbModel.setSelectedIndex(i);
                        modelFound = true;
                        break;
                    }
                }
                if (!modelFound && cmbModelModel.getSize() > 0) cmbModel.setSelectedIndex(0);
            }

            txtIdCctv.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    private void loadCctvModelsToComboBox() {
        cmbModelModel.removeAllElements();
        cmbModelModel.addElement(null);
        try {
            List<CctvModel> models = dataMasterManager.getAllCctvModels();
            if (models != null) {
                for (CctvModel model : models) {
                    cmbModelModel.addElement(model);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading CCTV Models: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadCctvUnits() {
        tableModel.setRowCount(0);
        try {
            List<CctvUnit> units = dataMasterManager.getAllCctvUnits();
            if (units != null) {
                for (CctvUnit unit : units) {
                    Object[] rowData = {
                            unit.getIdCctv(), unit.getLokasi(), (unit.getModel() != null) ? unit.getModel().getNamaModel() : "N/A",
                            unit.getStatus(), unit.getKeterangan()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error loading CCTV Units: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveCctvUnit() {
        CctvUnit unit = getCctvUnitFromForm();
        if (unit != null) {
            try {
                if (dataMasterManager.findCctvUnitById(unit.getIdCctv()) != null) {
                    NotificationManager.showWarning(parentComponentForDialog, "ID Unit CCTV '" + unit.getIdCctv() + "' sudah ada.");
                    return;
                }
                if (dataMasterManager.addCctvUnit(unit)) {
                    String pesan = "<html>Data Unit CCTV <b>ID: " + unit.getIdCctv() + " - Lokasi: " + unit.getLokasi() + "</b> berhasil disimpan!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadCctvUnits();
                    clearForm();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menyimpan data Unit CCTV.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menyimpan Unit CCTV: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateCctvUnit() {
         String idCctvToUpdate = txtIdCctv.getText().trim();
         if (tblCctvUnit.getSelectedRow() == -1) {
             NotificationManager.showWarning(parentComponentForDialog, "Pilih data dari tabel untuk diupdate.");
             return;
         }
         CctvUnit unit = getCctvUnitFromForm();
         if (unit != null) {
             if (!unit.getIdCctv().equals(idCctvToUpdate)) {
                 NotificationManager.showError(parentComponentForDialog, "ID Unit CCTV tidak boleh diubah saat update.");
                 txtIdCctv.setText(idCctvToUpdate);
                 return;
             }
             try {
                 if (dataMasterManager.updateCctvUnit(unit)) {
                     String pesan = "<html>Data Unit CCTV <b>ID: " + unit.getIdCctv() + " - Lokasi: " + unit.getLokasi() + "</b> berhasil diupdate!</html>";
                     NotificationManager.showSuccess(parentComponentForDialog, pesan);
                     loadCctvUnits();
                     clearForm();
                 } else {
                     NotificationManager.showError(parentComponentForDialog, "Gagal mengupdate data Unit CCTV.");
                 }
             } catch (SQLException e) {
                 NotificationManager.showError(parentComponentForDialog, "Error database saat mengupdate Unit CCTV: " + e.getMessage());
                 e.printStackTrace();
             }
         }
     }

    private void deleteCctvUnit() {
        int selectedRow = tblCctvUnit.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data Unit CCTV yang ingin dihapus.");
            return;
        }

        String idCctv = tblCctvUnit.getValueAt(selectedRow, 0).toString();
        String lokasi = tblCctvUnit.getValueAt(selectedRow, 1).toString();
        
        String konfirmasiPesan = "<html>Yakin ingin menghapus Unit CCTV:<br><b>(" + idCctv + ") di " + lokasi + "</b>?</html>";

        if (NotificationManager.showConfirm(parentComponentForDialog, konfirmasiPesan)) {
            try {
                if (dataMasterManager.deleteCctvUnit(idCctv)) {
                    String pesanSukses = "<html>Data Unit CCTV <b>ID: " + idCctv + " - Lokasi: " + lokasi + "</b> berhasil dihapus!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesanSukses);
                    loadCctvUnits();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menghapus. Unit mungkin masih direferensikan di dalam Log.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menghapus Unit CCTV: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}