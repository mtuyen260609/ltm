package btl.ltm;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Store store;

    public ClientHandler(Socket socket, Store store) {
        this.socket = socket;
        this.store = store;
    }

    @Override
    public void run() {
        try (
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                String request;
                try {
                    request = in.readUTF();
                } catch (EOFException e) {
                    break;
                }

                if (request == null || request.equals("QUIT")) {
                    break;
                }

                String response = xuLyYeuCau(request);
                out.writeUTF(response);
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("Loi xu ly Client (" + socket.getInetAddress() + "): " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Client da ngat ket noi: " + socket.getInetAddress());
        }
    }

    private String xuLyYeuCau(String request) {

        if (request.equals(Protocol.LIST_THUOC)) {
            List<Thuoc> ds = store.getDanhSachThuoc();
            List<String[]> rows = new ArrayList<>();

            for (Thuoc t : ds) {
                rows.add(new String[]{
                    t.getMaThuoc(),
                    t.getTenThuoc(),
                    t.getGia(),
                    t.getCongDung(),
                    t.getDangThuoc()
                });
            }

            return Protocol.success(Protocol.encodeRows(rows));
        }

        if (request.equals(Protocol.LIST_CONGDUNG)) {
            List<CongDung> ds = store.getDanhSachCongDung();
            List<String[]> rows = new ArrayList<>();

            for (CongDung cd : ds) {
                rows.add(new String[]{
                    cd.getMaCongDung(),
                    cd.getTenCongDung(),
                    cd.getMoTa()
                });
            }

            return Protocol.success(Protocol.encodeRows(rows));
        }

        if (request.startsWith(Protocol.XOA_THUOC + "|")) {
            String ma = request.substring(
                (Protocol.XOA_THUOC + "|").length()
            ).trim();

            if (ma.isEmpty()) {
                return Protocol.error("Ma thuoc khong duoc de trong.");
            }

            String result = store.xoaThuoc(ma);

            if (result != null) {
                return Protocol.success(result);
            }

            return Protocol.error("Khong tim thay thuoc co ma: " + ma);
        }

        if (request.startsWith(Protocol.XOA_CONGDUNG + "|")) {
            String ma = request.substring(
                (Protocol.XOA_CONGDUNG + "|").length()
            ).trim();

            if (ma.isEmpty()) {
                return Protocol.error("Ma cong dung khong duoc de trong.");
            }

            try {
                String result = store.xoaCongDung(ma);

                if (result != null) {
                    return Protocol.success(result);
                }

                return Protocol.error("Khong tim thay cong dung co ma: " + ma);
            } catch (IllegalStateException e) {
                return Protocol.error(e.getMessage());
            }
        }

        return Protocol.error("Yeu cau khong hop le: " + request);
    }
}
