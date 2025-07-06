package com.kelompok4.cctvapp.panel;

import com.kelompok4.cctvapp.Komponen;
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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/*
 * =================================================================================
 * RINGKASAN METHOD
 * =================================================================================
 * PanelKomponen(dmManager)         - Konstruktor 1 untuk panel komponen (dengan role).
 * PanelKomponen(dmManager, parent) - Konstruktor 2 untuk panel komponen (tanpa role).
 * initComponents()                 - Menginisialisasi dan menata semua komponen UI.
 * addListeners()                   - Menambahkan semua event listener ke komponen.
 * initButtonStates()               - Mengatur kondisi awal (enabled/disabled) tombol.
 * setPlaceholder()                 - Mengatur teks placeholder pada field ID Komponen.
 * clearForm()                      - Membersihkan inputan form dan mereset tombol.
 * applyRoles()                     - Menerapkan hak akses berdasarkan role user.
 * getKomponenFromForm()            - Mengambil & memvalidasi data dari form, lalu mengembalikannya sebagai objek.
 * loadKomponen()                   - Memuat data komponen dari database ke tabel.
 * saveKomponen()                   - Menyimpan data komponen baru ke database.
 * updateKomponen()                 - Mengupdate data komponen yang ada di database.
 * deleteKomponen()                 - Menghapus data komponen yang dipilih dari database.
 * displayKomponenDetails()         - Menampilkan detail data dari tabel ke form.
 * =================================================================================
 */
public class PanelKomponen extends JPanel {

    private JLabel lblIdKomponen, lblNamaKomponen, lblSatuan, lblStok, lblHargaSatuan;
    private JTextField txtIdKomponen, txtNamaKomponen, txtSatuan, txtStok, txtHargaSatuan;
    private JButton btnNew, btnSave, btnUpdate, btnDelete, btnRefresh, btnCetak;
    private JTable tblKomponen;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;


    private DataMasterManager dataMasterManager;
    private Component parentComponentForDialog;

    private final String ID_KOMPONEN_PREFIX = "KMP-";
    private final String ID_KOMPONEN_PLACEHOLDER_TEXT = "Contoh: KMP-HDD01";

    public PanelKomponen(DataMasterManager dmManager) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = this;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        loadKomponen();
        applyRoles();
    }

    public PanelKomponen(DataMasterManager dmManager, Component parentForDialog) {
        this.dataMasterManager = dmManager;
        this.parentComponentForDialog = parentForDialog;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        addListeners();
        initButtonStates();
        loadKomponen();
    }

    private void initComponents() {
        lblIdKomponen = new JLabel("ID Komponen:");
        lblNamaKomponen = new JLabel("Nama Komponen:");
        lblSatuan = new JLabel("Satuan:");
        lblStok = new JLabel("Stok:");
        lblHargaSatuan = new JLabel("Harga Satuan(Rp):");

        txtIdKomponen = new JTextField(10);
        txtNamaKomponen = new JTextField(20);
        txtSatuan = new JTextField(10);
        txtStok = new JTextField(5);
        txtHargaSatuan = new JTextField(10);

        btnNew = new JButton("Baru");
        btnSave = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        btnCetak = new JButton("Cetak");

        String[] columnNames = {"ID Komponen", "Nama Komponen", "Stok", "Satuan", "Harga Satuan"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblKomponen = new JTable(tableModel);
        scrollPane = new JScrollPane(tblKomponen);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn kolomIdKomponen = tblKomponen.getColumnModel().getColumn(0);
        kolomIdKomponen.setPreferredWidth(150);
        kolomIdKomponen.setMinWidth(100);

        TableColumn kolomNamaKomponen = tblKomponen.getColumnModel().getColumn(1);
        kolomNamaKomponen.setPreferredWidth(250);
        kolomNamaKomponen.setMinWidth(200);

        TableColumn kolomStok = tblKomponen.getColumnModel().getColumn(2);
        kolomStok.setPreferredWidth(70);
        kolomStok.setMaxWidth(80);
        kolomStok.setCellRenderer(centerRenderer);

        TableColumn kolomSatuan = tblKomponen.getColumnModel().getColumn(3);
        kolomSatuan.setPreferredWidth(80);
        kolomSatuan.setMaxWidth(90);
        kolomSatuan.setCellRenderer(centerRenderer);

        TableColumn kolomHarga = tblKomponen.getColumnModel().getColumn(4);
        kolomHarga.setPreferredWidth(180);
        kolomHarga.setMinWidth(120);
        kolomHarga.setCellRenderer(new DefaultTableCellRenderer() {
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

        JPanel formInputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Detail Komponen"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formInputPanel.add(lblIdKomponen);
        formInputPanel.add(txtIdKomponen);
        formInputPanel.add(lblNamaKomponen);
        formInputPanel.add(txtNamaKomponen);
        formInputPanel.add(lblSatuan);
        formInputPanel.add(txtSatuan);
        formInputPanel.add(lblStok);
        formInputPanel.add(txtStok);
        formInputPanel.add(lblHargaSatuan);
        formInputPanel.add(txtHargaSatuan);

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
        btnSave.addActionListener(e -> saveKomponen());
        btnUpdate.addActionListener(e -> updateKomponen());
        btnDelete.addActionListener(e -> deleteKomponen());
        btnRefresh.addActionListener(e -> loadKomponen());

        txtIdKomponen.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!btnUpdate.isEnabled()) {
                    if (txtIdKomponen.getText().equals(ID_KOMPONEN_PLACEHOLDER_TEXT)) {
                        txtIdKomponen.setText(ID_KOMPONEN_PREFIX);
                        txtIdKomponen.setForeground(Color.BLACK);
                    } else if (txtIdKomponen.getText().isEmpty()) {
                        txtIdKomponen.setText(ID_KOMPONEN_PREFIX);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!btnUpdate.isEnabled()) {
                    if (txtIdKomponen.getText().equals(ID_KOMPONEN_PREFIX) || txtIdKomponen.getText().isEmpty()) {
                        setPlaceholder();
                    }
                }
            }
        });

        tblKomponen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayKomponenDetails();
            }
        });

        btnCetak.addActionListener(e -> {
            HashMap<String, Object> params = new HashMap<>();
            String pathJasper = "/com/kelompok4/cctvapp/report/Komponen.jasper";
            ReportManager.tampilkanLaporan(SwingUtilities.getWindowAncestor(this), "Laporan Daftar Komponen", pathJasper, params);
        });
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData();
            }
        });
    }
    
    private void refreshData() {
        System.out.println("PANEL KOMPONEN: Ditampilkan, merefresh data...");
        loadKomponen();
    }

    private void initButtonStates() {
        txtIdKomponen.setEnabled(true);
        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void setPlaceholder() {
        txtIdKomponen.setForeground(Color.GRAY);
        txtIdKomponen.setText(ID_KOMPONEN_PLACEHOLDER_TEXT);
    }

    private void clearForm() {
        setPlaceholder();
        txtIdKomponen.setEnabled(true);
        txtNamaKomponen.setText("");
        txtSatuan.setText("");
        txtStok.setText("");
        txtHargaSatuan.setText("");

        initButtonStates();
        tblKomponen.clearSelection();
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

    private Komponen getKomponenFromForm() {
        String idKomponen = txtIdKomponen.getText().trim();
        String namaKomponen = txtNamaKomponen.getText().trim();
        String satuan = txtSatuan.getText().trim();
        String stokText = txtStok.getText().trim();
        String hargaText = txtHargaSatuan.getText().trim();

        if (idKomponen.isEmpty() || idKomponen.equals(ID_KOMPONEN_PLACEHOLDER_TEXT) || idKomponen.equals(ID_KOMPONEN_PREFIX)) {
            NotificationManager.showError(parentComponentForDialog, "ID Komponen tidak boleh kosong!");
            return null;
        }
        if (namaKomponen.isEmpty()) {
            NotificationManager.showError(parentComponentForDialog, "Nama Komponen tidak boleh kosong!");
            return null;
        }
        if (satuan.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Satuan tidak boleh kosong!", "Error Input", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        int stok = 0;
        if (!stokText.isEmpty()) {
            try {
                stok = Integer.parseInt(stokText);
                if (stok < 0) {
                    JOptionPane.showMessageDialog(parentComponentForDialog, "Stok tidak boleh negatif!", "Error Input", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentComponentForDialog, "Stok harus berupa angka!", "Error Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Stok tidak boleh kosong!", "Error Input", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        BigDecimal hargaSatuan = BigDecimal.ZERO;
        if (!hargaText.isEmpty()) {
            try {
                hargaSatuan = new BigDecimal(hargaText);
                if (hargaSatuan.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(parentComponentForDialog, "Harga Satuan tidak boleh negatif!", "Error Input", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentComponentForDialog, "Harga Satuan harus berupa angka (misal: 50000)!", "Error Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Harga Satuan tidak boleh kosong!", "Error Input", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new Komponen(idKomponen, namaKomponen, satuan, stok, hargaSatuan);
    }

    private void loadKomponen() {
        tableModel.setRowCount(0);
        try {
            List<Komponen> komponenList = dataMasterManager.getAllKomponen();
            if (komponenList != null) {
                for (Komponen komponen : komponenList) {
                    Object[] rowData = {
                            komponen.getIdKomponen(),
                            komponen.getNamaKomponen(),
                            komponen.getStok(),
                            komponen.getSatuan(),
                            komponen.getHargaSatuan()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Error saat memuat data Komponen: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        clearForm();
    }

    private void saveKomponen() {
        Komponen komponen = getKomponenFromForm();
        if (komponen != null) {
            try {
                if (dataMasterManager.findKomponenById(komponen.getIdKomponen()) != null) {
                    NotificationManager.showWarning(parentComponentForDialog, "ID Komponen '" + komponen.getIdKomponen() + "' sudah ada.");
                    return;
                }
                if (dataMasterManager.addKomponen(komponen)) {
                    String pesan = "<html>Data Komponen <b>ID: " + komponen.getIdKomponen() + " - Nama: " + komponen.getNamaKomponen() + "</b> berhasil disimpan!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadKomponen();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menyimpan data Komponen.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menyimpan data Komponen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateKomponen() {
        String idKomponenToUpdate = txtIdKomponen.getText().trim();
        if (idKomponenToUpdate.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponentForDialog, "Pilih data dari tabel untuk diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Komponen komponen = getKomponenFromForm();
        if (komponen != null) {
            if (!komponen.getIdKomponen().equals(idKomponenToUpdate)) {
                JOptionPane.showMessageDialog(parentComponentForDialog, "ID Komponen tidak boleh diubah saat update.", "Error Update", JOptionPane.ERROR_MESSAGE);
                txtIdKomponen.setText(idKomponenToUpdate);
                return;
            }
           try {
                if (dataMasterManager.updateKomponen(komponen)) {
                    String pesan = "<html>Data Komponen <b>ID: " + komponen.getIdKomponen() + " - Nama: " + komponen.getNamaKomponen() + " - Stok Terbaru: " + komponen.getStok() + "</b> berhasil diupdate!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesan);
                    loadKomponen();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal mengupdate data Komponen.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat mengupdate data Komponen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void deleteKomponen() {
        int selectedRow = tblKomponen.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(parentComponentForDialog, "Pilih data Komponen yang ingin dihapus.");
            return;
        }

        String idKomponenToDelete = tblKomponen.getValueAt(selectedRow, 0).toString();
        String namaKomponen = tblKomponen.getValueAt(selectedRow, 1).toString();

        String konfirmasiPesan = "<html>Yakin ingin menghapus Komponen: <br><b>(" + idKomponenToDelete + ") " + namaKomponen + "</b>?</html>";
        if (NotificationManager.showConfirm(parentComponentForDialog, konfirmasiPesan)) {
            try {
                if (dataMasterManager.deleteKomponen(idKomponenToDelete)) {
                    String pesanSukses = "<html>Data Komponen <b>ID: " + idKomponenToDelete + " - Nama: " + namaKomponen + "</b> berhasil dihapus!</html>";
                    NotificationManager.showSuccess(parentComponentForDialog, pesanSukses);
                    loadKomponen();
                } else {
                    NotificationManager.showError(parentComponentForDialog, "Gagal menghapus. Komponen mungkin masih direferensikan di Tabel Lainnya.");
                }
            } catch (SQLException e) {
                NotificationManager.showError(parentComponentForDialog, "Error database saat menghapus data Komponen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void displayKomponenDetails() {
        int selectedRow = tblKomponen.getSelectedRow();
        if (selectedRow != -1) {
            txtIdKomponen.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtNamaKomponen.setText(tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "");
            txtStok.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "0");
            txtSatuan.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
            txtHargaSatuan.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "0.00");

            txtIdKomponen.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }
}