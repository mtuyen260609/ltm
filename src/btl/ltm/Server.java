package btl.ltm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        Store store = new Store();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("================================");
            System.out.println("SERVER QUAN LY THUOC");
            System.out.println("Server dang chay...");
            System.out.println("Cong TCP: " + PORT);
            System.out.println("Dang cho Client ket noi...");
            System.out.println("================================");

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println(
                    "Client ket noi: " + socket.getInetAddress()
                );

                ClientHandler handler =
                    new ClientHandler(socket, store);

                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println(
                "Loi Server: " + e.getMessage()
            );
        }
    }
}
