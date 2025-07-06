package com.kelompok4.cctvapp.panel;

import com.kelompok4.cctvapp.Teknisi;
import com.kelompok4.cctvapp.report.ReportManager;
import com.kelompok4.cctvapp.service.DataMasterManager;
import com.kelompok4.cctvapp.service.UserSession;
import com.kelompok4.cctvapp.util.NotificationManager; 

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
 * PanelTeknisi(dmManager)         - Konstruktor 1 untuk panel teknisi (dengan role).
 * PanelTeknisi(dmManager, parent) - Konstruktor 2 untuk panel teknisi (tanpa role).
 * initComponents()                - Menginisialisasi dan menata semua komponen UI.
 * addListeners()                  - Menambahkan semua event listener ke komponen.
 * initButtonStates()              - Mengatur kondisi awal (enabled/disabled) tombol.
 * setPlaceholder()                - Mengatur teks placeholder pada field ID Teknisi.
 * clearForm()                     - Membersihkan inputan form dan mereset tombol.
 * applyRoles()                    - Menerapkan hak akses berdasarkan role user.
 * getTeknisiFromForm()            - Mengambil & memvalidasi data dari form, lalu mengembalikannya sebagai objek.
 * loadTeknisi()                   - Memuat data teknisi dari database ke tabel.
 * saveTeknisi()                   - Menyimpan data teknisi baru ke database.
 * updateTeknisi()                 - Mengupdate data teknisi yang ada di database.
 * deleteTeknisi()                 - Menghapus data teknisi yang dipilih dari database.
 * displayTeknisiDetails()         - Menampilkan detail data dari tabel ke form.
 * =================================================================================
 */
public class PanelTeknisi extends JPanel {

    // Komponen UI
    private JLabel lblIdTeknisi, lblNamaTeknisi, lblKontak;
    private JTextField txtIdTeknisi, txtNamaTeknisi, txtKontak;
    private JButton btnNew, btnSave, btnUpdate, btnDelete, btnRefresh, btnCetak;
    private JTable tblTeknisi;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    // Service & Utility
    private DataMasterManager dataMasterManager;
    private Component parentComponentForDialog;

    // Konstanta
    private final String ID_TEKNISI_PREFIX = "TKN-";
    private final String ID_TEKNISI_PLACEHOLDER_TEXT = "Contoh: TKN-001";

    public PanelTeknisi(DataMasterManager dmManager) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = this;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        loadTeknisi();
        applyRoles();
        clearForm();
    }

    public PanelTeknisi(DataMasterManager dmManager, Component parentForDialog) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = parentForDialog;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        loadTeknisi();
    }

    private void initComponents() {
        lblIdTeknisi = new JLabel("ID Teknisi:");
        lblNamaTeknisi = new JLabel("Nama Teknisi:");
        lblKontak = new JLabel("Kontak:");

        txtIdTeknisi = new JTextField(10);
        txtNamaTeknisi = new JTextField(20);
        txtKontak = new JTextField(15);

        btnNew = new JButton("Baru");
        btnSave = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnCetak = new JButton("Cetak");

        String[] columnNames = {"ID Teknisi", "Nama Teknisi", "Kontak"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblTeknisi = new JTable(tableModel);
        scrollPane = new JScrollPane(tblTeknisi);

        JPanel formInputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Detail Teknisi"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formInputPanel.add(lblIdTeknisi);
        formInputPanel.add(txtIdTeknisi);
        formInputPanel.add(lblNamaTeknisi);
        formInputPanel.add(txtNamaTeknisi);
        formInputPanel.add(lblKontak);
        formInputPanel.add(txtKontak);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionButtonPanel.add(btnNew);
        actionButtonPanel.add(btnSave);
        actionButtonPanel.add(btnUpdate);
        actionButtonPanel.add(btnDelete);
        actionButtonPanel.add(btnRefresh);
        actionButtonPanel.add(btnCetak);

        add(formInputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionButtonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveTeknisi());
        btnUpdate.addActionListener(e -> updateTeknisi());
        btnDelete.addActionListener(e -> deleteTeknisi());
        btnRefresh.addActionListener(e -> loadTeknisi());

        tblTeknisi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    displayTeknisiDetails();
                }
            }
        });

        btnCetak.addActionListener(e -> {
            HashMap<String, Object> params = new HashMap<>();
            String pathJasper = "/com/kelompok4/cctvapp/report/Teknisi.jasper";
            ReportManager.tampilkanLaporan(SwingUtilities.getWindowAncestor(this), "Laporan Daftar Teknisi", pathJasper, params);
        });

        txtIdTeknisi.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!btnUpdate.isEnabled()) {
                    if (txtIdTeknisi.getText().equals(ID_TEKNISI_PLACEHOLDER_TEXT)) {
                        txtIdTeknisi.setText(ID_TEKNISI_PREFIX);
                        txtIdTeknisi.setForeground(Color.BLACK);
                    } else if (txtIdTeknisi.getText().isEmpty()) {
                        txtIdTeknisi.setText(ID_TEKNISI_PREFIX);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!btnUpdate.isEnabled()) {
                    if (txtIdTeknisi.getText().equals(ID_TEKNISI_PREFIX) || txtIdTeknisi.getText().isEmpty()) {
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
        System.out.println("PANEL TEKNISI: Ditampilkan, merefresh data...");
        loadTeknisi();
    }

    private void initButtonStates() {
        txtIdTeknisi.setEnabled(true);
        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void setPlaceholder() {
        txtIdTeknisi.setForeground(Color.GRAY);
        txtIdTeknisi.setText(ID_TEKNISI_PLACEHOLDER_TEXT);
    }

    private void clearForm() {
        txtNamaTeknisi.setText("");
        txtKontak.setText("");
        txtIdTeknisi.setEnabled(true);
        tblTeknisi.clearSelection();
        initButtonStates();
        setPlaceholder();
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

    private Teknisi getTeknisiFromForm() {
        String idTeknisi = txtIdTeknisi.getText().trim();
        String namaTeknisi = txtNamaTeknisi.getText().trim();

        if (idTeknisi.isEmpty() || idTeknisi.equals(ID_TEKNISI_PLACEHOLDER_TEXT) || idTeknisi.equals(ID_TEKNISI_PREFIX)) {
            NotificationManager.showError(parentComponentForDialog, "ID Teknisi tidak boleh kosong!");
            return null;
        }
        if (namaTeknisi.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Nama Teknisi tidak boleh kosong!");
            return null;
        }
        return new Teknisi(idTeknisi, namaTeknisi, txtKontak.getText().trim());
    }
    
    private void loadTeknisi() {
        tableModel.setRowCount(0);
        try {
            List<Teknisi> teknisiList = dataMasterManager.getAllTeknisi();
            if (teknisiList != null) {
                for (Teknisi teknisi : teknisiList) {
                    Object[] rowData = {
                            teknisi.getIdTeknisi(),
                            teknisi.getNamaTeknisi(),
                            teknisi.getKontak()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            NotificationManager.showError(parentComponentForDialog, "Error saat memuat data Teknisi: " + e.getMessage());
            e.printStackTrace();;
        }
        clearForm();
    }

    private void saveTeknisi() {
        Teknisi teknisi = getTeknisiFromForm();
        if (teknisi != null) {
            try {
                if (dataMasterManager.findTeknisiById(teknisi.getIdTeknisi()) != null) {
                    NotificationManager.showWarning(parentComponentForDialog, "ID Teknisi '" + teknisi.getIdTeknisi() + "' sudah ada.");
                    return;
                }
                if (dataMasterManager.addTeknisi(teknisi)) {
                    String pesan = "<html>Data Teknisi <b>ID: " + teknisi.getIdTeknisi() + " - Nama: " + teknisi.getNamaTeknisi() + "</b> berhasil disimpan!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadTeknisi();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menyimpan data Teknisi.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menyimpan data Teknisi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateTeknisi() {
        if (tblTeknisi.getSelectedRow() == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data dari tabel untuk diupdate.");
            return;
        }
        Teknisi teknisi = getTeknisiFromForm();
        if (teknisi != null) {
            try {
                if (dataMasterManager.updateTeknisi(teknisi)) {
                    String pesan = "<html>Data Teknisi <b>ID: " + teknisi.getIdTeknisi() + " - Nama: " + teknisi.getNamaTeknisi() + "</b> berhasil diupdate!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadTeknisi();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal mengupdate data Teknisi.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat mengupdate data Teknisi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void deleteTeknisi() {
        int selectedRow = tblTeknisi.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data Teknisi yang ingin dihapus.");
            return;
        }
        
        String idTeknisiToDelete = tblTeknisi.getValueAt(selectedRow, 0).toString();
        String namaTeknisi = tblTeknisi.getValueAt(selectedRow, 1).toString();

        String konfirmasiPesan = "<html>Yakin ingin menghapus Teknisi:<br><b>(" + idTeknisiToDelete + ") " + namaTeknisi + "</b>?</html>";
        if (NotificationManager.showConfirm(parentComponentForDialog, konfirmasiPesan)) {
            try {
                if (dataMasterManager.deleteTeknisi(idTeknisiToDelete)) {
                    String pesanSukses = "<html>Data Teknisi <b>ID: " + idTeknisiToDelete + " - Nama: " + namaTeknisi + "</b> berhasil dihapus!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesanSukses);
                    loadTeknisi();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menghapus. Teknisi mungkin masih terkait dengan Log.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menghapus data Teknisi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void displayTeknisiDetails() {
        int selectedRow = tblTeknisi.getSelectedRow();
        if (selectedRow != -1) {
            txtIdTeknisi.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtNamaTeknisi.setText(tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "");
            txtKontak.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");

            txtIdTeknisi.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }
}