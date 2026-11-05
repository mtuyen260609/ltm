package btl.ltm;

import java.util.ArrayList;
import java.util.List;

public class SampleData {

    public static List<Thuoc> taoDanhSachThuoc() {
        List<Thuoc> ds = new ArrayList<>();

        ds.add(new Thuoc("T001", "Paracetamol 500mg", "25000", "Giam dau ha sot", "Vien nen"));
        ds.add(new Thuoc("T002", "Efferalgan 500mg", "48000", "Giam dau ha sot", "Vien sui"));
        ds.add(new Thuoc("T003", "Hapacol 250", "35000", "Giam dau ha sot", "Goi bot"));

        ds.add(new Thuoc("T004", "Amoxicillin 500mg", "65000", "Khang sinh", "Vien nang"));
        ds.add(new Thuoc("T005", "Augmentin 1g", "185000", "Khang sinh", "Vien bao phim"));

        ds.add(new Thuoc("T006", "Siro Prospan", "78000", "Tri ho - Cam cum", "Siro uong"));
        ds.add(new Thuoc("T007", "Decolgen Forte", "22000", "Tri ho - Cam cum", "Vien nen"));

        ds.add(new Thuoc("T008", "Vitamin C Plussz", "55000", "Bo sung vitamin", "Vien sui"));
        ds.add(new Thuoc("T009", "Pharmaton Geriavit", "165000", "Bo sung vitamin", "Vien nang mem"));

        ds.add(new Thuoc("T010", "Phosphalugel (Chu P)", "110000", "Tieu hoa - Da day", "Gel uong"));
        ds.add(new Thuoc("T011", "Enterogermina", "145000", "Tieu hoa - Da day", "Ong dung dich"));

        ds.add(new Thuoc("T012", "Clorpheniramin 4mg", "18000", "Chong di ung", "Vien nen"));
        ds.add(new Thuoc("T013", "Cetirizin 10mg", "32000", "Chong di ung", "Vien bao phim"));

        return ds;
    }

    public static List<CongDung> taoDanhSachCongDung() {
        List<CongDung> ds = new ArrayList<>();

        ds.add(new CongDung("CD01", "Giam dau ha sot", "Giam đau dau, ha sot, dau nhuc co"));
        ds.add(new CongDung("CD02", "Khang sinh", "Dieu tri viem nhiem do vi khuan gay ra"));
        ds.add(new CongDung("CD03", "Tri ho - Cam cum", "Giam ho, nghet mui, so mui, long dom"));
        ds.add(new CongDung("CD04", "Bo sung vitamin", "Tang cuong de khang va nang luong co the"));
        ds.add(new CongDung("CD05", "Tieu hoa - Da day", "Giam dau bao tu, trao nguoc, tieu hoa tot"));
        ds.add(new CongDung("CD06", "Chong di ung", "Tri me day, ngua, viem mui di ung thoi tiet"));
        ds.add(new CongDung("CD07", "Bo mat - Sang mat", "Bo sung vitamin A va giam moi mat"));

        return ds;
    }
}
