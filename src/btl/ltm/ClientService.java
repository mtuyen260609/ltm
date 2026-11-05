package btl.ltm;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

public class ClientService {

    private final String host;
    private final int port;

    public ClientService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String guiYeuCau(String request) throws IOException {
        try (
            Socket socket = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(5000);

            out.writeUTF(request);
            out.flush();

            return in.readUTF();
        } catch (ConnectException e) {
            throw new IOException("Khong the ket noi toi Server tai " + host + ":" + port + ". Vui long bat Server truoc khi chay Client!");
        }
    }

    private String kiemTraPhanHoi(String response) throws IOException {
        if (response == null || response.trim().isEmpty()) {
            throw new IOException("Server khong tra ve du lieu.");
        }

        String[] parts = response.split("\\|", 2);

        if (parts.length < 2) {
            throw new IOException("Phan hoi tu Server khong hop le: " + response);
        }

        if (parts[0].equals("ERROR")) {
            throw new IOException(parts[1]);
        }

        if (!parts[0].equals("OK")) {
            throw new IOException("Khong xac dinh duoc phan hoi tu Server.");
        }

        return parts[1];
    }

    public List<String[]> layDanhSachThuoc() throws IOException {
        String response = guiYeuCau(Protocol.LIST_THUOC);
        return Protocol.decodeRows(kiemTraPhanHoi(response));
    }

    public List<String[]> layDanhSachCongDung() throws IOException {
        String response = guiYeuCau(Protocol.LIST_CONGDUNG);
        return Protocol.decodeRows(kiemTraPhanHoi(response));
    }

    public String xoaThuoc(String ma) throws IOException {
        String response = guiYeuCau(Protocol.XOA_THUOC + "|" + ma);
        return kiemTraPhanHoi(response);
    }

    public String xoaCongDung(String ma) throws IOException {
        String response = guiYeuCau(Protocol.XOA_CONGDUNG + "|" + ma);
        return kiemTraPhanHoi(response);
    }
}
