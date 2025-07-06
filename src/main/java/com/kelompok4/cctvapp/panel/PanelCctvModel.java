package com.kelompok4.cctvapp.panel;

import com.kelompok4.cctvapp.CctvModel;
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
 * PanelCctvModel(...)    - Konstruktor utama untuk panel manajemen model CCTV.
 * initComponents()         - Menginisialisasi dan menata semua komponen UI pada panel.
 * addListeners()           - Menambahkan semua event listener ke komponen UI.
 * initButtonsState()       - Mengatur kondisi awal (enabled/disabled) untuk tombol-tombol.
 * setPlaceholder()         - Mengatur teks placeholder pada field ID Model.
 * clearForm()              - Membersihkan semua inputan pada form dan mereset kondisi tombol.
 * applyRoles()             - Menerapkan pembatasan hak akses berdasarkan role user.
 * getModelFromForm()       - Mengambil & memvalidasi data dari form, lalu mengembalikannya sebagai objek.
 * loadCctvModels()         - Memuat semua data model CCTV dari database ke tabel.
 * saveModel()              - Menyimpan data model CCTV baru dari form ke database.
 * updateModel()            - Mengupdate data model CCTV yang ada di database.
 * deleteModel()            - Menghapus data model CCTV yang dipilih dari database.
 * displayModelDetails()    - Menampilkan detail data dari tabel yang dipilih ke dalam form.
 * =================================================================================
 */
public class PanelCctvModel extends JPanel {

    // Komponen UI
    private JLabel lblIdModel, lblNamaModel, lblManufaktur, lblSpesifikasi, lblUmurEkonomisTh;
    private JTextField txtIdModel, txtNamaModel, txtManufaktur, txtSpesifikasi, txtUmurEkonomisTh;
    private JButton btnNew, btnSave, btnUpdate, btnDelete, btnRefresh, btnCetak;
    private JTable tblCctvModels;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    // Service & Utility
    private DataMasterManager dataMasterManager;
    private Component parentComponentForDialog;

    // Konstanta
    private final String ID_MODEL_PLACEHOLDER_TEXT = "Contoh: DH-PTZ-1 (manufaktur-model)";

    public PanelCctvModel(DataMasterManager dmManager, Component parentForDialog) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = parentForDialog;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonsState();
        loadCctvModels();
        applyRoles();
    }

    private void initComponents() {
        // Inisialisasi Label
        lblIdModel = new JLabel("ID Model:");
        lblNamaModel = new JLabel("Nama Model:");
        lblManufaktur = new JLabel("Manufaktur:");
        lblSpesifikasi = new JLabel("Spesifikasi:");
        lblUmurEkonomisTh = new JLabel("Umur Ekonomis (Th):");

        // Inisialisasi Text Field
        txtIdModel = new JTextField(15);
        txtNamaModel = new JTextField(20);
        txtManufaktur = new JTextField(20);
        txtSpesifikasi = new JTextField(30);
        txtUmurEkonomisTh = new JTextField(5);

        // Inisialisasi Button
        btnNew = new JButton("Baru");
        btnSave = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnCetak = new JButton("Cetak");

        // Setup Tabel
        String[] columnNames = {"ID Model", "Nama Model", "Manufaktur", "Spesifikasi", "Umur"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblCctvModels = new JTable(tableModel);
        scrollPane = new JScrollPane(tblCctvModels);

        // Pengaturan Kolom Tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn kolomIdModel = tblCctvModels.getColumnModel().getColumn(0);
        kolomIdModel.setPreferredWidth(100);
        kolomIdModel.setMinWidth(80);

        TableColumn kolomNamaModel = tblCctvModels.getColumnModel().getColumn(1);
        kolomNamaModel.setPreferredWidth(120);
        kolomNamaModel.setMinWidth(100);

        TableColumn kolomManufaktur = tblCctvModels.getColumnModel().getColumn(2);
        kolomManufaktur.setPreferredWidth(150);
        kolomManufaktur.setCellRenderer(centerRenderer);

        TableColumn kolomSpesifikasi = tblCctvModels.getColumnModel().getColumn(3);
        kolomSpesifikasi.setPreferredWidth(250);
        kolomSpesifikasi.setMinWidth(200);

        TableColumn kolomUmur = tblCctvModels.getColumnModel().getColumn(4);
        kolomUmur.setPreferredWidth(50);
        kolomUmur.setMaxWidth(100);
        kolomUmur.setCellRenderer(centerRenderer);

        // Panel Form Input
        JPanel formInputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Detail Model CCTV"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formInputPanel.add(lblIdModel);
        formInputPanel.add(txtIdModel);
        formInputPanel.add(lblNamaModel);
        formInputPanel.add(txtNamaModel);
        formInputPanel.add(lblManufaktur);
        formInputPanel.add(txtManufaktur);
        formInputPanel.add(lblSpesifikasi);
        formInputPanel.add(txtSpesifikasi);
        formInputPanel.add(lblUmurEkonomisTh);
        formInputPanel.add(txtUmurEkonomisTh);

        // Panel Tombol Aksi
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionButtonPanel.add(btnNew);
        actionButtonPanel.add(btnSave);
        actionButtonPanel.add(btnUpdate);
        actionButtonPanel.add(btnDelete);
        actionButtonPanel.add(btnRefresh);
        actionButtonPanel.add(btnCetak);

        // Menambahkan panel-panel ke panel utama
        add(formInputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionButtonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveModel());
        btnUpdate.addActionListener(e -> updateModel());
        btnDelete.addActionListener(e -> deleteModel());
        btnRefresh.addActionListener(e -> loadCctvModels());

        btnCetak.addActionListener(e -> {
            HashMap<String, Object> params = new HashMap<>();
            String pathJasper = "/com/kelompok4/cctvapp/report/CctvModel.jasper";
            ReportManager.tampilkanLaporan(SwingUtilities.getWindowAncestor(this), "Laporan Daftar Model CCTV", pathJasper, params);
        });

        tblCctvModels.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayModelDetails();
            }
        });

        txtIdModel.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtIdModel.getText().equals(ID_MODEL_PLACEHOLDER_TEXT)) {
                    txtIdModel.setText("");
                    txtIdModel.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtIdModel.getText().isEmpty()) {
                    setPlaceholder();
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
        System.out.println("PANEL CCTV MODEL: Ditampilkan, merefresh data...");
        loadCctvModels();
    }
    private void initButtonsState() {
        txtIdModel.setEnabled(true);
        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void setPlaceholder() {
        txtIdModel.setForeground(Color.GRAY);
        txtIdModel.setText(ID_MODEL_PLACEHOLDER_TEXT);
    }

    private void clearForm() {
        setPlaceholder();
        txtNamaModel.setText("");
        txtManufaktur.setText("");
        txtSpesifikasi.setText("");
        txtUmurEkonomisTh.setText("");

        initButtonsState();
        tblCctvModels.clearSelection();
    }

    private void applyRoles() {
        String role = UserSession.getInstance().getRole();
        if (role != null && !role.equals("admin")) {
            // User biasa tidak bisa melakukan CUD (Create, Update, Delete)
            btnNew.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private CctvModel getModelFromForm() {
        String idModel = txtIdModel.getText().trim();
        String namaModel = txtNamaModel.getText().trim();
        String umurText = txtUmurEkonomisTh.getText().trim();

        if (idModel.isEmpty() || idModel.equals(ID_MODEL_PLACEHOLDER_TEXT)) {
            NotificationManager.showError(parentComponentForDialog, "ID Model tidak boleh kosong!");
            return null;
        }
        if (namaModel.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Nama Model tidak boleh kosong!");
            return null;
        }

        int umurEkonomisTh = 0;
        if (!umurText.isEmpty()) {
            try {
                umurEkonomisTh = Integer.parseInt(umurText);
                if (umurEkonomisTh < 0) {
                    NotificationManager.showError(parentComponentForDialog, "Umur Ekonomis tidak boleh negatif!");
                    return null;
                }
            } catch (NumberFormatException e) {
                NotificationManager.showError(parentComponentForDialog, "Umur Ekonomis harus berupa angka!");
                return null;
            }
        }
        return new CctvModel(idModel, namaModel, txtManufaktur.getText().trim(), txtSpesifikasi.getText().trim(), umurEkonomisTh);
    }

    private void loadCctvModels() {
        tableModel.setRowCount(0);
        try {
            List<CctvModel> models = dataMasterManager.getAllCctvModels();
            if (models != null) {
                for (CctvModel model : models) {
                    Object[] rowData = {
                            model.getIdModel(), model.getNamaModel(), model.getManufaktur(),
                            model.getSpesifikasi(), model.getUmurEkonomisTh()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error memuat data Model CCTV: " + e.getMessage());
            e.printStackTrace();
        }
        clearForm();
    }

    private void saveModel() {
        CctvModel model = getModelFromForm();
        if (model != null) {
            try {
                if (dataMasterManager.findCctvModelById(model.getIdModel()) != null) {
                    NotificationManager.showWarning(parentComponentForDialog, "ID Model '" + model.getIdModel() + "' sudah ada.");
                    return;
                }
                
                if (dataMasterManager.addCctvModel(model)) {
                    String pesan = "<html>Data Model CCTV <b>ID: " + model.getIdModel() + " - Model: " + model.getNamaModel() + "</b> berhasil disimpan!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadCctvModels();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menyimpan data Model CCTV.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menyimpan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateModel() {
        if (tblCctvModels.getSelectedRow() == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data dari tabel untuk diupdate.");
            return;
        }
        
        CctvModel modelToUpdate = getModelFromForm();
        if (modelToUpdate != null) {
            try {
                if (dataMasterManager.updateCctvModel(modelToUpdate)) {
                    String pesan = "<html>Data Model CCTV <b>ID: " + modelToUpdate.getIdModel() + " - Model: " + modelToUpdate.getNamaModel() + "</b> berhasil diupdate!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadCctvModels();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal mengupdate data Model CCTV.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat mengupdate: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void deleteModel() {
        int selectedRow = tblCctvModels.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data Model CCTV yang ingin dihapus.");
            return;
        }
        
        String idModelToDelete = tblCctvModels.getValueAt(selectedRow, 0).toString();
        String namaModel = tblCctvModels.getValueAt(selectedRow, 1).toString();
        
        String konfirmasiPesan = "<html>Yakin ingin menghapus Model CCTV:<br><b>(" + idModelToDelete + ") " + namaModel + "</b>?</html>";

        if (NotificationManager.showConfirm(parentComponentForDialog, konfirmasiPesan)) {
            try {
                if (dataMasterManager.deleteCctvModel(idModelToDelete)) {
                    String pesanSukses = "<html>Data Model CCTV <b>ID: " + idModelToDelete + " - Model: " + namaModel + "</b> berhasil dihapus.</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesanSukses);
                    loadCctvModels();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menghapus. Model mungkin masih digunakan oleh Unit CCTV.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menghapus: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void displayModelDetails() {
        int selectedRow = tblCctvModels.getSelectedRow();
        if (selectedRow != -1) {
            txtIdModel.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtIdModel.setForeground(Color.BLACK);
            txtNamaModel.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtManufaktur.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtSpesifikasi.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtUmurEkonomisTh.setText(tableModel.getValueAt(selectedRow, 4).toString());

            txtIdModel.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }
}