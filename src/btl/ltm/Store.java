package btl.ltm;

import java.util.List;
import java.util.ArrayList;

public class Store {

    private final List<Thuoc> danhSachThuoc;
    private final List<CongDung> danhSachCongDung;

    public Store() {
        danhSachThuoc = SampleData.taoDanhSachThuoc();
        danhSachCongDung = SampleData.taoDanhSachCongDung();
    }

    public synchronized List<Thuoc> getDanhSachThuoc() {
        return new ArrayList<>(danhSachThuoc);
    }

    public synchronized List<CongDung> getDanhSachCongDung() {
        return new ArrayList<>(danhSachCongDung);
    }

    public synchronized String xoaThuoc(String ma) {
        if (ma == null) {
            return null;
        }

        for (int i = 0; i < danhSachThuoc.size(); i++) {
            Thuoc t = danhSachThuoc.get(i);

            if (t.getMaThuoc().equalsIgnoreCase(ma.trim())) {
                danhSachThuoc.remove(i);
                return "Da xoa thuoc " + t.getTenThuoc()
                        + " - Ma: " + t.getMaThuoc();
            }
        }

        return null;
    }

    public synchronized String xoaCongDung(String ma) throws IllegalStateException {
        if (ma == null) {
            return null;
        }

        CongDung found = null;
        int foundIndex = -1;

        for (int i = 0; i < danhSachCongDung.size(); i++) {
            CongDung cd = danhSachCongDung.get(i);
            if (cd.getMaCongDung().equalsIgnoreCase(ma.trim())) {
                found = cd;
                foundIndex = i;
                break;
            }
        }

        if (found == null) {
            return null;
        }

        for (Thuoc t : danhSachThuoc) {
            if (t.getCongDung().equalsIgnoreCase(found.getTenCongDung())
                    || t.getCongDung().equalsIgnoreCase(found.getMaCongDung())) {
                throw new IllegalStateException("Khong the xoa '" + found.getTenCongDung()
                        + "' vi dang duoc su dung boi thuoc '" + t.getTenThuoc() + "' (" + t.getMaThuoc() + ").");
            }
        }

        danhSachCongDung.remove(foundIndex);
        return "Da xoa cong dung " + found.getTenCongDung()
                + " - Ma: " + found.getMaCongDung();
    }
}
