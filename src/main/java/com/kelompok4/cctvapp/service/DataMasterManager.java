package com.kelompok4.cctvapp.service;

import com.kelompok4.cctvapp.*;
import com.kelompok4.cctvapp.dao.*;
import com.kelompok4.cctvapp.util.DbConnector;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/*
 * =================================================================================
 * RINGKASAN METHOD (DATA MASTER MANAGER)
 * =================================================================================
 * Kelas ini berfungsi sebagai Service Layer atau Facade, yang menjembatani antara
 * UI (Panel) dan lapisan akses data (DAO). Semua logika bisnis dan transaksi
 * diatur di sini.
 * ---------------------------------------------------------------------------------
 * FUNGSI UTAMA & STATISTIK
 * ---------------------------------------------------------------------------------
 * DataMasterManager()        - Konstruktor, inisialisasi semua objek DAO.
 * authenticateUser(...)      - Memvalidasi username dan password user dengan BCrypt.
 * getJumlahCctvAktif()       - Menghitung jumlah CCTV yang statusnya 'Aktif'.
 * getJumlahCctvRusak()       - Menghitung jumlah CCTV yang statusnya 'Rusak' atau 'Perbaikan'.
 * getTotalTeknisi()          - Menghitung total teknisi yang terdaftar.
 * ---------------------------------------------------------------------------------
 * METHOD UNTUK CCTV MODEL
 * ---------------------------------------------------------------------------------
 * addCctvModel(...)          - Menyimpan CctvModel baru.
 * getAllCctvModels()         - Mengambil semua CctvModel.
 * findCctvModelById(...)     - Mencari CctvModel berdasarkan ID.
 * updateCctvModel(...)       - Mengupdate CctvModel.
 * deleteCctvModel(...)       - Menghapus CctvModel.
 * ---------------------------------------------------------------------------------
 * METHOD UNTUK CCTV UNIT
 * ---------------------------------------------------------------------------------
 * addCctvUnit(...)           - Menyimpan CctvUnit baru.
 * getAllCctvUnits()          - Mengambil semua CctvUnit.
 * findCctvUnitById(...)      - Mencari CctvUnit berdasarkan ID.
 * updateCctvUnit(...)        - Mengupdate CctvUnit.
 * deleteCctvUnit(...)        - Menghapus CctvUnit.
 * ---------------------------------------------------------------------------------
 * METHOD UNTUK TEKNISI
 * ---------------------------------------------------------------------------------
 * addTeknisi(...)            - Menyimpan Teknisi baru.
 * getAllTeknisi()            - Mengambil semua Teknisi.
 * findTeknisiById(...)       - Mencari Teknisi berdasarkan ID.
 * updateTeknisi(...)         - Mengupdate Teknisi.
 * deleteTeknisi(...)         - Menghapus Teknisi.
 * ---------------------------------------------------------------------------------
 * METHOD UNTUK LOG MAINTENANCE (DENGAN TRANSAKSI)
 * ---------------------------------------------------------------------------------
 * addLogMaintenance(...)     - Menyimpan LogMaintenance & update status CCTV (transaksional).
 * getAllLogMaintenance()     - Mengambil semua LogMaintenance.
 * findLogMaintenanceById(...)  - Mencari LogMaintenance berdasarkan ID.
 * updateLogMaintenance(...)    - Mengupdate LogMaintenance.
 * deleteLogMaintenance(...)    - Menghapus LogMaintenance.
 * ---------------------------------------------------------------------------------
 * METHOD UNTUK LOG KERUSAKAN (DENGAN TRANSAKSI)
 * ---------------------------------------------------------------------------------
 * addLogKerusakan(...)       - Menyimpan LogKerusakan & update status CCTV (transaksional).
 * getAllLogKerusakan()       - Mengambil semua LogKerusakan.
 * findLogKerusakanById(...)  - Mencari LogKerusakan berdasarkan ID.
 * updateLogKerusakan(...)    - Mengupdate LogKerusakan.
 * deleteLogKerusakan(...)    - Menghapus LogKerusakan.
 * ---------------------------------------------------------------------------------
 * METHOD UNTUK KOMPONEN
 * ---------------------------------------------------------------------------------
 * addKomponen(...)           - Menyimpan Komponen baru.
 * getAllKomponen()           - Mengambil semua Komponen.
 * findKomponenById(...)      - Mencari Komponen berdasarkan ID.
 * updateKomponen(...)        - Mengupdate Komponen.
 * deleteKomponen(...)        - Menghapus Komponen.
 * ---------------------------------------------------------------------------------
 * METHOD UNTUK LOG KOMPONEN DIPAKAI
 * ---------------------------------------------------------------------------------
 * addLogKomponenDipakai(...)     - Menyimpan LogKomponenDipakai baru.
 * getAllLogKomponenDipakai()     - Mengambil semua LogKomponenDipakai.
 * findLogKomponenDipakaiById(...)  - Mencari LogKomponenDipakai berdasarkan ID.
 * updateLogKomponenDipakai(...)    - Mengupdate LogKomponenDipakai.
 * deleteLogKomponenDipakai(...)    - Menghapus LogKomponenDipakai.
 * =================================================================================
 */
public class DataMasterManager {

    private CctvModelDAO cctvModelDAO;
    private CctvUnitDAO cctvUnitDAO;
    private TeknisiDAO teknisiDAO;
    private LogMaintenanceDAO logMaintenanceDAO;
    private LogKerusakanDAO logKerusakanDAO;
    private KomponenDAO komponenDAO;
    private LogKomponenDipakaiDAO logKomponenDipakaiDAO;
    private UserDAO userDAO;

    public DataMasterManager() {
        cctvModelDAO = new CctvModelDAO();
        cctvUnitDAO = new CctvUnitDAO();
        teknisiDAO = new TeknisiDAO();
        logMaintenanceDAO = new LogMaintenanceDAO();
        logKerusakanDAO = new LogKerusakanDAO();
        komponenDAO = new KomponenDAO();
        logKomponenDipakaiDAO = new LogKomponenDipakaiDAO();
        userDAO = new UserDAO();
        System.out.println("Manager: DataMasterManager initialized.");
    }

    public User authenticateUser(String username, String password) {
        System.out.println("Manager: Menerima permintaan autentikasi (via DAO) untuk user: " + username);
        try {
            User userFromDb = userDAO.findUserByUsername(username);

            if (userFromDb != null) {
                if (BCrypt.checkpw(password, userFromDb.getPasswordHash())) {
                    System.out.println("Manager: Autentikasi berhasil untuk user: " + userFromDb.getNamaLengkap());
                    return userFromDb;
                } else {
                    System.out.println("Manager: Password salah untuk user: " + username);
                }
            } else {
                System.out.println("Manager: Username '" + username + "' tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.err.println("Manager: Error database saat autentikasi: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int getJumlahCctvAktif() throws SQLException {
        System.out.println("Manager: Menghitung jumlah CCTV status 'Aktif'...");
        return cctvUnitDAO.getJumlahByStatus("Aktif");
    }

    public int getJumlahCctvRusak() throws SQLException {
        System.out.println("Manager: Menghitung jumlah CCTV status 'Rusak' dan 'Perbaikan'...");
        int jumlahRusak = cctvUnitDAO.getJumlahByStatus("Rusak");
        int jumlahPerbaikan = cctvUnitDAO.getJumlahByStatus("Perbaikan");
        return jumlahRusak + jumlahPerbaikan;
    }

    public int getTotalTeknisi() throws SQLException {
        System.out.println("Manager: Menghitung total Teknisi...");
        return teknisiDAO.getTotalTeknisi();
    }

    public boolean addCctvModel(CctvModel model) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menyimpan CctvModel dengan ID " + model.getIdModel() + ".");
        boolean success = cctvModelDAO.insertCctvModel(model);
        if (success) {
            System.out.println("Manager: CctvModel dengan ID " + model.getIdModel() + " BERHASIL disimpan di database.");
        } else {
            System.out.println("Manager: GAGAL menyimpan CctvModel dengan ID " + model.getIdModel() + " di database.");
        }
        return success;
    }

    public List<CctvModel> getAllCctvModels() throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mengambil semua CctvModel dari database...");
        List<CctvModel> models = cctvModelDAO.getAllCctvModels();
        System.out.println("Manager: Berhasil mengambil " + models.size() + " CctvModel dari database.");
        return models;
    }

    public CctvModel findCctvModelById(String id) throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mencari CctvModel dengan ID " + id + "...");
        CctvModel model = cctvModelDAO.findCctvModelById(id);
        if (model != null) {
            System.out.println("Manager: CctvModel dengan ID " + id + " ditemukan.");
        } else {
            System.out.println("Manager: CctvModel dengan ID " + id + " tidak ditemukan.");
        }
        return model;
    }

    public boolean updateCctvModel(CctvModel model) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk mengupdate CctvModel dengan ID " + model.getIdModel() + ".");
        boolean result = cctvModelDAO.updateCctvModel(model);
        if (result) {
            System.out.println("Manager: CctvModel dengan ID " + model.getIdModel() + " BERHASIL diupdate.");
        } else {
            System.out.println("Manager: GAGAL mengupdate CctvModel dengan ID " + model.getIdModel() + ".");
        }
        return result;
    }

    public boolean deleteCctvModel(String id) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menghapus CctvModel dengan ID " + id + ".");
        boolean result = cctvModelDAO.deleteCctvModel(id);
        if (result) {
            System.out.println("Manager: CctvModel dengan ID " + id + " BERHASIL dihapus.");
        } else {
            System.out.println("Manager: GAGAL menghapus CctvModel dengan ID " + id + ".");
        }
        return result;
    }

    public boolean addCctvUnit(CctvUnit unit) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menyimpan CctvUnit dengan ID " + unit.getIdCctv() + ".");
        boolean success = cctvUnitDAO.insertCctvUnit(unit);
        if (success) {
            System.out.println("Manager: CctvUnit dengan ID " + unit.getIdCctv() + " BERHASIL disimpan di database.");
        } else {
            System.out.println("Manager: GAGAL menyimpan CctvUnit dengan ID " + unit.getIdCctv() + " di database.");
        }
        return success;
    }

    public List<CctvUnit> getAllCctvUnits() throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mengambil semua CctvUnit dari database...");
        List<CctvUnit> units = cctvUnitDAO.getAllCctvUnits();
        System.out.println("Manager: Berhasil mengambil " + units.size() + " CctvUnit dari database.");
        return units;
    }

    public CctvUnit findCctvUnitById(String id) throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mencari CctvUnit dengan ID " + id + "...");
        CctvUnit unit = cctvUnitDAO.findCctvUnitById(id);
        if (unit != null) {
            System.out.println("Manager: CctvUnit dengan ID " + id + " ditemukan.");
        } else {
            System.out.println("Manager: CctvUnit dengan ID " + id + " tidak ditemukan.");
        }
        return unit;
    }

    public boolean updateCctvUnit(CctvUnit unit) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk mengupdate CctvUnit dengan ID " + unit.getIdCctv() + ".");
        boolean result = cctvUnitDAO.updateCctvUnit(unit);
        if (result) {
            System.out.println("Manager: CctvUnit dengan ID " + unit.getIdCctv() + " BERHASIL diupdate.");
        } else {
            System.out.println("Manager: GAGAL mengupdate CctvUnit dengan ID " + unit.getIdCctv() + ".");
        }
        return result;
    }

    public boolean deleteCctvUnit(String id) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menghapus CctvUnit dengan ID " + id + ".");
        boolean result = cctvUnitDAO.deleteCctvUnit(id);
        if (result) {
            System.out.println("Manager: CctvUnit dengan ID " + id + " BERHASIL dihapus.");
        } else {
            System.out.println("Manager: GAGAL menghapus CctvUnit dengan ID " + id + ".");
        }
        return result;
    }

    public boolean addTeknisi(Teknisi teknisi) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menyimpan Teknisi dengan ID " + teknisi.getIdTeknisi() + ".");
        boolean success = teknisiDAO.insertTeknisi(teknisi);
        if (success) {
            System.out.println("Manager: Teknisi dengan ID " + teknisi.getIdTeknisi() + " BERHASIL disimpan di database.");
        } else {
            System.out.println("Manager: GAGAL menyimpan Teknisi dengan ID " + teknisi.getIdTeknisi() + " di database.");
        }
        return success;
    }

    public List<Teknisi> getAllTeknisi() throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mengambil semua Teknisi dari database...");
        List<Teknisi> teknisiList = teknisiDAO.getAllTeknisi();
        System.out.println("Manager: Berhasil mengambil " + teknisiList.size() + " Teknisi dari database.");
        return teknisiList;
    }

    public Teknisi findTeknisiById(String id) throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mencari Teknisi dengan ID " + id + "...");
        Teknisi teknisi = teknisiDAO.findTeknisiById(id);
        if (teknisi != null) {
            System.out.println("Manager: Teknisi dengan ID " + id + " ditemukan.");
        } else {
            System.out.println("Manager: Teknisi dengan ID " + id + " tidak ditemukan.");
        }
        return teknisi;
    }

    public boolean updateTeknisi(Teknisi teknisi) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk mengupdate Teknisi dengan ID " + teknisi.getIdTeknisi() + ".");
        boolean result = teknisiDAO.updateTeknisi(teknisi);
        if (result) {
            System.out.println("Manager: Teknisi dengan ID " + teknisi.getIdTeknisi() + " BERHASIL diupdate.");
        } else {
            System.out.println("Manager: GAGAL mengupdate Teknisi dengan ID " + teknisi.getIdTeknisi() + ".");
        }
        return result;
    }

    public boolean deleteTeknisi(String id) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menghapus Teknisi dengan ID " + id + ".");
        boolean result = teknisiDAO.deleteTeknisi(id);
        if (result) {
            System.out.println("Manager: Teknisi dengan ID " + id + " BERHASIL dihapus.");
        } else {
            System.out.println("Manager: GAGAL menghapus Teknisi dengan ID " + id + ".");
        }
        return result;
    }

    public int addLogMaintenance(LogMaintenance log) throws SQLException {
        Connection conn = null;
        int generatedId = -1;
        try {
            conn = DbConnector.getNewConnection();
            conn.setAutoCommit(false);
            System.out.println("Manager: Transaksi dimulai untuk Log Maintenance...");

            generatedId = logMaintenanceDAO.insertLogMaintenance(log, conn);

            if (generatedId > 0) {
                CctvUnit unitToUpdate = log.getCctvUnit();
                unitToUpdate.setStatus("Perawatan");
                boolean updateSuccess = cctvUnitDAO.updateCctvUnit(unitToUpdate, conn);
                if (!updateSuccess) {
                    throw new SQLException("Gagal mengupdate status CCTV, transaksi akan di-rollback.");
                }
                System.out.println("Manager: Status CCTV ID " + unitToUpdate.getIdCctv() + " berhasil diupdate menjadi 'Perawatan'.");
            } else {
                throw new SQLException("Gagal menyimpan log maintenance, transaksi akan di-rollback.");
            }

            conn.commit();
            System.out.println("Manager: Transaksi Log Maintenance berhasil di-commit!");

        } catch (SQLException e) {
            System.err.println("Manager: Terjadi error, transaksi Log Maintenance akan di-rollback! Pesan: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Manager: Transaksi berhasil di-rollback.");
                } catch (SQLException ex) {
                    System.err.println("Manager: Gagal melakukan rollback! Pesan: " + ex.getMessage());
                }
            }
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("Manager: Koneksi ditutup.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return generatedId;
    }

    public int addLogKerusakan(LogKerusakan log) throws SQLException {
        Connection conn = null;
        int generatedId = -1;
        try {
            conn = DbConnector.getNewConnection();
            conn.setAutoCommit(false);
            System.out.println("Manager: Transaksi dimulai untuk Log Kerusakan...");

            generatedId = logKerusakanDAO.insertLogKerusakan(log, conn);

            if (generatedId > 0) {
                CctvUnit unitToUpdate = log.getCctvUnit();
                unitToUpdate.setStatus("Perbaikan");
                boolean updateSuccess = cctvUnitDAO.updateCctvUnit(unitToUpdate, conn);
                if (!updateSuccess) {
                    throw new SQLException("Gagal mengupdate status CCTV, transaksi akan di-rollback.");
                }
                System.out.println("Manager: Status CCTV ID " + unitToUpdate.getIdCctv() + " berhasil diupdate menjadi 'Perbaikan'.");
            } else {
                throw new SQLException("Gagal menyimpan log kerusakan, transaksi akan di-rollback.");
            }

            conn.commit();
            System.out.println("Manager: Transaksi Log Kerusakan berhasil di-commit!");

        } catch (SQLException e) {
            System.err.println("Manager: Terjadi error pada transaksi Log Kerusakan, akan di-rollback! Pesan: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Manager: Transaksi berhasil di-rollback.");
                } catch (SQLException ex) {
                    System.err.println("Manager: Gagal melakukan rollback! Pesan: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("Manager: Koneksi ditutup.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return generatedId;
    }

    public List<LogMaintenance> getAllLogMaintenance() throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mengambil semua LogMaintenance dari database...");
        List<LogMaintenance> logs = logMaintenanceDAO.getAllLogMaintenance();
        System.out.println("Manager: Berhasil mengambil " + logs.size() + " LogMaintenance dari database.");
        return logs;
    }

    public LogMaintenance findLogMaintenanceById(String id) throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mencari LogMaintenance dengan ID " + id + "...");
        LogMaintenance log = logMaintenanceDAO.findLogMaintenanceById(id);
        if (log != null) {
            System.out.println("Manager: LogMaintenance dengan ID " + id + " ditemukan.");
        } else {
            System.out.println("Manager: LogMaintenance dengan ID " + id + " tidak ditemukan.");
        }
        return log;
    }

    public boolean updateLogMaintenance(LogMaintenance log) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk mengupdate LogMaintenance dengan ID " + log.getIdLog() + ".");
        boolean result = logMaintenanceDAO.updateLogMaintenance(log);
        if (result) {
            System.out.println("Manager: LogMaintenance dengan ID " + log.getIdLog() + " BERHASIL diupdate.");
        } else {
            System.out.println("Manager: GAGAL mengupdate LogMaintenance dengan ID " + log.getIdLog() + ".");
        }
        return result;
    }

    public boolean deleteLogMaintenance(String id) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menghapus LogMaintenance dengan ID " + id + ".");
        boolean result = logMaintenanceDAO.deleteLogMaintenance(id);
        if (result) {
            System.out.println("Manager: LogMaintenance dengan ID " + id + " BERHASIL dihapus.");
        } else {
            System.out.println("Manager: GAGAL menghapus LogMaintenance dengan ID " + id + ".");
        }
        return result;
    }

    public List<LogKerusakan> getAllLogKerusakan() throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mengambil semua LogKerusakan dari database...");
        List<LogKerusakan> logs = logKerusakanDAO.getAllLogKerusakan();
        System.out.println("Manager: Berhasil mengambil " + logs.size() + " LogKerusakan dari database.");
        return logs;
    }

    public LogKerusakan findLogKerusakanById(String id) throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mencari LogKerusakan dengan ID " + id + "...");
        LogKerusakan log = logKerusakanDAO.findLogKerusakanById(id);
        if (log != null) {
            System.out.println("Manager: LogKerusakan dengan ID " + id + " ditemukan.");
        } else {
            System.out.println("Manager: LogKerusakan dengan ID " + id + " tidak ditemukan.");
        }
        return log;
    }

    public boolean updateLogKerusakan(LogKerusakan log) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk mengupdate LogKerusakan dengan ID " + log.getIdLog() + ".");
        boolean result = logKerusakanDAO.updateLogKerusakan(log);
        if (result) {
            System.out.println("Manager: LogKerusakan dengan ID " + log.getIdLog() + " BERHASIL diupdate.");
        } else {
            System.out.println("Manager: GAGAL mengupdate LogKerusakan dengan ID " + log.getIdLog() + ".");
        }
        return result;
    }

    public boolean deleteLogKerusakan(String id) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menghapus LogKerusakan dengan ID " + id + ".");
        boolean result = logKerusakanDAO.deleteLogKerusakan(id);
        if (result) {
            System.out.println("Manager: LogKerusakan dengan ID " + id + " BERHASIL dihapus.");
        } else {
            System.out.println("Manager: GAGAL menghapus LogKerusakan dengan ID " + id + ".");
        }
        return result;
    }

    public boolean addKomponen(Komponen komponen) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menyimpan Komponen dengan ID " + komponen.getIdKomponen() + ".");
        boolean success = komponenDAO.insertKomponen(komponen);
        if (success) {
            System.out.println("Manager: Komponen dengan ID " + komponen.getIdKomponen() + " BERHASIL disimpan di database.");
        } else {
            System.out.println("Manager: GAGAL menyimpan Komponen dengan Komponen dengan ID " + komponen.getIdKomponen() + " di database.");
        }
        return success;
    }

    public List<Komponen> getAllKomponen() throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mengambil semua Komponen dari database...");
        List<Komponen> komponenList = komponenDAO.getAllKomponen();
        System.out.println("Manager: Berhasil mengambil " + komponenList.size() + " Komponen dari database.");
        return komponenList;
    }

    public Komponen findKomponenById(String id) throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mencari Komponen dengan ID " + id + "...");
        Komponen komponen = komponenDAO.findKomponenById(id);
        if (komponen != null) {
            System.out.println("Manager: Komponen dengan ID " + id + " ditemukan.");
        } else {
            System.out.println("Manager: Komponen dengan ID " + id + " tidak ditemukan.");
        }
        return komponen;
    }

    public boolean updateKomponen(Komponen komponen) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk mengupdate Komponen dengan ID " + komponen.getIdKomponen() + ".");
        boolean result = komponenDAO.updateKomponen(komponen);
        if (result) {
            System.out.println("Manager: Komponen dengan ID " + komponen.getIdKomponen() + " BERHASIL diupdate.");
        } else {
            System.out.println("Manager: GAGAL mengupdate Komponen dengan ID " + komponen.getIdKomponen() + ".");
        }
        return result;
    }

    public boolean deleteKomponen(String id) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menghapus Komponen dengan ID " + id + ".");
        boolean result = komponenDAO.deleteKomponen(id);
        if (result) {
            System.out.println("Manager: Komponen dengan ID " + id + " BERHASIL dihapus.");
        } else {
            System.out.println("Manager: GAGAL menghapus Komponen dengan ID " + id + ".");
        }
        return result;
    }

    public int addLogKomponenDipakai(LogKomponenDipakai logKomponen) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menyimpan LogKomponenDipakai dengan ID null.");
        int generatedId = logKomponenDipakaiDAO.insertLogKomponenDipakai(logKomponen);
        if (generatedId > 0) {
            System.out.println("Manager: LogKomponenDipakai dengan ID " + generatedId + " BERHASIL disimpan di database.");
        } else {
            System.out.println("Manager: GAGAL menyimpan LogKomponenDipakai dengan ID null di database.");
        }
        return generatedId;
    }

    public List<LogKomponenDipakai> getAllLogKomponenDipakai() throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mengambil semua LogKomponenDipakai dari database...");
        List<LogKomponenDipakai> logs = logKomponenDipakaiDAO.getAllLogKomponenDipakai();
        System.out.println("Manager: Berhasil mengambil " + logs.size() + " LogKomponenDipakai dari database.");
        return logs;
    }

    public LogKomponenDipakai findLogKomponenDipakaiById(int id) throws SQLException {
        System.out.println("Manager: Meminta DAO untuk mencari LogKomponenDipakai dengan ID " + id + "...");
        LogKomponenDipakai log = logKomponenDipakaiDAO.findLogKomponenDipakaiById(id);
        if (log != null) {
            System.out.println("Manager: LogKomponenDipakai dengan ID " + id + " ditemukan.");
        } else {
            System.out.println("Manager: LogKomponenDipakai dengan ID " + id + " tidak ditemukan.");
        }
        return log;
    }

    public boolean updateLogKomponenDipakai(LogKomponenDipakai logKomponen) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk mengupdate LogKomponenDipakai dengan ID " + logKomponen.getId() + ".");
        boolean result = logKomponenDipakaiDAO.updateLogKomponenDipakai(logKomponen);
        if (result) {
            System.out.println("Manager: LogKomponenDipakai dengan ID " + logKomponen.getId() + " BERHASIL diupdate.");
        } else {
            System.out.println("Manager: GAGAL mengupdate LogKomponenDipakai dengan ID " + logKomponen.getId() + ".");
        }
        return result;
    }

    public boolean deleteLogKomponenDipakai(int id) throws SQLException {
        System.out.println("Manager: Menerima permintaan untuk menghapus LogKomponenDipakai dengan ID " + id + ".");
        boolean result = logKomponenDipakaiDAO.deleteLogKomponenDipakai(id);
        if (result) {
            System.out.println("Manager: LogKomponenDipakai dengan ID " + id + " BERHASIL dihapus.");
        } else {
            System.out.println("Manager: GAGAL menghapus LogKomponenDipakai dengan ID " + id + ".");
        }
        return result;
    }
}