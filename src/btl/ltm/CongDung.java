
package btl.ltm;

public class CongDung {
    private String maCongDung;
    private String tenCongDung;
    private String moTa;

    public CongDung(String maCongDung, String tenCongDung, String moTa) {
        this.maCongDung = maCongDung;
        this.tenCongDung = tenCongDung;
        this.moTa = moTa;
    }

    public String getMaCongDung() {
        return maCongDung;
    }

    public String getTenCongDung() {
        return tenCongDung;
    }

    public String getMoTa() {
        return moTa;
    }
}
