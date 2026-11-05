package btl.ltm;

import javax.swing.*;
import java.util.List;

public class ClientController {

    private final BTLLTM view;
    private final ClientService service;

    public ClientController(BTLLTM view, ClientService service) {
        this.view = view;
        this.service = service;

        view.getBtnXoaThuoc().addActionListener(e -> xoaThuoc());
        view.getBtnXoaCongDung().addActionListener(e -> xoaCongDung());

        taiDuLieuBanDau();
    }

    public void taiDuLieuBanDau() {
        view.hienThiKetQua("Dang ket noi den Server...");

        new SwingWorker<List<List<String[]>>, Void>() {

            @Override
            protected List<List<String[]>> doInBackground() throws Exception {
                List<List<String[]>> ketQua = new java.util.ArrayList<>();
                ketQua.add(service.layDanhSachThuoc());
                ketQua.add(service.layDanhSachCongDung());
                return ketQua;
            }

            @Override
            protected void done() {
                try {
                    List<List<String[]>> data = get();
                    view.hienThiDanhSachThuoc(data.get(0));
                    view.hienThiDanhSachCongDung(data.get(1));
                    view.hienThiKetQua("Da ket noi Server va tai du lieu thanh cong.");
                } catch (Exception e) {
                    Throwable cause = (e.getCause() != null) ? e.getCause() : e;
                    view.hienThiKetQua("LOI KET NOI: " + cause.getMessage());
                }
            }
        }.execute();
    }

    private void xoaThuoc() {
        int row = view.getTableThuoc().getSelectedRow();

        if (row == -1) {
            view.hienThiKetQua("THAT BAI: Hay chon thuoc can xoa.");
            return;
        }

        String ma = view.getTableThuoc().getValueAt(row, 0).toString();
        String ten = view.getTableThuoc().getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Ban co chac muon xoa thuoc " + ten + " (Ma: " + ma + ")?",
            "Xac nhan xoa thuoc",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        view.getBtnXoaThuoc().setEnabled(false);

        new SwingWorker<List<String[]>, Void>() {

            private String thongBao;

            @Override
            protected List<String[]> doInBackground() throws Exception {
                thongBao = service.xoaThuoc(ma);
                return service.layDanhSachThuoc();
            }

            @Override
            protected void done() {
                view.getBtnXoaThuoc().setEnabled(true);

                try {
                    List<String[]> ds = get();
                    view.hienThiDanhSachThuoc(ds);
                    view.hienThiKetQua("THANH CONG: " + thongBao);
                } catch (Exception e) {
                    Throwable cause = (e.getCause() != null) ? e.getCause() : e;
                    view.hienThiKetQua("THAT BAI: " + cause.getMessage());
                }
            }
        }.execute();
    }

    private void xoaCongDung() {
        int row = view.getTableCongDung().getSelectedRow();

        if (row == -1) {
            view.hienThiKetQua("THAT BAI: Hay chon cong dung can xoa.");
            return;
        }

        String ma = view.getTableCongDung().getValueAt(row, 0).toString();
        String ten = view.getTableCongDung().getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Ban co chac muon xoa cong dung " + ten + " (Ma: " + ma + ")?",
            "Xac nhan xoa cong dung",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        view.getBtnXoaCongDung().setEnabled(false);

        new SwingWorker<List<String[]>, Void>() {

            private String thongBao;

            @Override
            protected List<String[]> doInBackground() throws Exception {
                thongBao = service.xoaCongDung(ma);
                return service.layDanhSachCongDung();
            }

            @Override
            protected void done() {
                view.getBtnXoaCongDung().setEnabled(true);

                try {
                    List<String[]> ds = get();
                    view.hienThiDanhSachCongDung(ds);
                    view.hienThiKetQua("THANH CONG: " + thongBao);
                } catch (Exception e) {
                    Throwable cause = (e.getCause() != null) ? e.getCause() : e;
                    view.hienThiKetQua("THAT BAI: " + cause.getMessage());
                }
            }
        }.execute();
    }
}
