
package btl.ltm;

public class Thuoc {
    private String maThuoc;
    private String tenThuoc;
    private String gia;
    private String congDung;
    private String dangThuoc;

    public Thuoc(String maThuoc, String tenThuoc, String gia,
                 String congDung, String dangThuoc) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.gia = gia;
        this.congDung = congDung;
        this.dangThuoc = dangThuoc;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public String getGia() {
        return gia;
    }

    public String getCongDung() {
        return congDung;
    }

    public String getDangThuoc() {
        return dangThuoc;
    }
}
