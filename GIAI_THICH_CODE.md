# 📘 BÁO CÁO GIẢI THÍCH MÃ NGUỒN BÀI TẬP LỚN LẬP TRÌNH MẠNG
> **Đề tài:** Quản lý thuốc theo công dụng  
> **Kiến trúc:** Mô hình Client - Server Socket TCP Đa luồng (Multi-threading) & Mô hình MVC (Model - View - Controller)  
> **Ngôn ngữ:** Java (JDK 21, Java Swing)

---

## 📑 MỤC LỤC
1. [PHẦN I: TỔNG QUAN KIẾN TRÚC & NGUYÊN LÝ HOẠT ĐỘNG](#phần-i-tổng-quan-kiến-trúc--nguyên-lý-hoạt-động)
2. [PHẦN II: GIẢI THÍCH CHI TIẾT TỪNG FILE & TỪNG ĐOẠN CODE CỐT LÕI](#phần-ii-giải-thích-chi-tiết-từng-file--từng-đoạn-code-cốt-lõi)
   - [1. Server.java](#1-serverjava---khởi-động-máy-chủ-và-lắng-nghe-kết-nối)
   - [2. ClientHandler.java](#2-clienthandlerjava---xử-lý-luồng-mạng-client-trên-server)
   - [3. Store.java](#3-storejava---kho-dữ-liệu-và-kiểm-tra-ràng-buộc-toàn-vẹn)
   - [4. Protocol.java](#4-protocoljava---giao-thức-đóng-gói-và-mã-hóa-base64)
   - [5. ClientService.java](#5-clientservicejava---tầng-dịch-vụ-kết-nối-socket-phía-client)
   - [6. BTLLTM.java](#6-btlltmjava---tầng-giao-diện-đồ-họa-view)
   - [7. ClientController.java](#7-clientcontrollerjava---tầng-điều-khiển-controller)
   - [8. Model (Thuoc.java, CongDung.java, SampleData.java)](#8-các-lớp-thực-thể-và-dữ-liệu-mẫu)
3. [PHẦN III: LUỒNG CHẠY CHI TIẾT CỦA TỪNG CHỨC NĂNG CỤ THỂ](#phần-iii-luồng-chạy-chi-tiết-của-từng-chức-năng-cụ-thể)
   - [Chức năng 1: Khởi động và thiết lập kết nối Client - Server](#chức-năng-1-khởi-động-và-kết-nối-mạng-client---server)
   - [Chức năng 2: Xem danh sách Thuốc và Công dụng](#chức-năng-2-xem-danh-sách-thuốc-và-công-dụng)
   - [Chức năng 3: Xóa một loại thuốc đã chọn](#chức-năng-3-xóa-một-loại-thuốc-đã-chọn)
   - [Chức năng 4: Xóa công dụng bị chặn do ràng buộc toàn vẹn (Khóa ngoại)](#chức-năng-4-xóa-công-dụng-bị-chặn-do-ràng-buộc-toàn-vẹn-khóa-ngoại)
   - [Chức năng 5: Xóa công dụng thành công khi không có thuốc liên kết](#chức-năng-5-xóa-công-dụng-thành-công-khi-không-có-thuốc-liên-kết)
   - [Chức năng 6: Bắt lỗi khi mất mạng hoặc chưa bật Server](#chức-năng-6-xử-lý-ngoại-lệ-khi-chưa-bật-server-hoặc-mất-mạng)
4. [PHẦN IV: LUỒNG XỬ LÝ ĐA LUỒNG (MULTI-THREADING ARCHITECTURE)](#phần-iv-luồng-xử-lý-đa-luồng-multi-threading-architecture)
5. [PHẦN V: CÁC CÂU HỎI VẤN ĐÁP THƯỜNG GẶP KHI BẢO VỆ BTL & GỢI Ý TRẢ LỜI](#phần-v-các-câu-hỏi-vấn-đáp-thường-gặp-khi-bảo-vệ-btl--gợi-ý-trả-lời)

---

## PHẦN I: TỔNG QUAN KIẾN TRÚC & NGUYÊN LÝ HOẠT ĐỘNG

Hệ thống được thiết kế theo mô hình **Client - Server** giao tiếp qua **TCP Socket**, trong đó phía Client được tách bạch theo mô hình chuẩn **MVC**:

```mermaid
flowchart TD
    User(["👤 Người dùng"]) <-->|Tương tác giao diện| View["🖥️ View (BTLLTM.java)"]
    View <-->|Bắt sự kiện & Render| Controller["🎮 Controller (ClientController.java)"]
    Controller <-->|Gửi nhận ngầm qua SwingWorker| Service["🔌 Service (ClientService.java)"]
    Service <-->|Socket TCP (Port 5000)| Network["🌐 Giao thức Protocol (Base64 UTF-8)"]
    Network <-->|ServerSocket.accept()| Server["🖥️ Server (Server.java)"]
    Server -->|Cấp phát Thread riêng| Handler["⚙️ ClientHandler (Worker Thread)"]
    Handler <-->|Đọc/Ghi an toàn đa luồng| Store[("📦 Store (synchronized)")]
```

### 1. Phía Server
- **Đa luồng (Multi-threading)**: Mở cổng `5000`. Khi có kết nối từ Client, `ServerSocket.accept()` sẽ trả về một `Socket` và Server lập tức sinh ra một tiến trình con `Thread(ClientHandler)` để phục vụ riêng cho Client đó.
- **An toàn luồng (Thread-safe)**: Kho dữ liệu `Store` sử dụng từ khóa `synchronized` trên tất cả phương thức thêm/xóa/đọc để đảm bảo nhiều Client cùng lúc không gây xung đột (Race Condition).

### 2. Phía Client
- **Giao diện Swing (View)**: Được xây dựng bằng `JFrame`, chứa 2 tab quản lý, 2 bảng `JTable`, các nút bấm và một ô hiển thị thông báo tiến trình `JTextArea`.
- **Luồng nền (SwingWorker)**: Tất cả tác vụ I/O mạng được chạy trong luồng nền `doInBackground()` để giữ giao diện đồ họa chạy mượt mà, không bao giờ bị đơ (UI Freeze).
- **Phân tách MVC**: View không tự tiện kết nối mạng, Controller chịu trách nhiệm lắng nghe sự kiện, gọi Service xử lý rồi cập nhật ngược về View.

### 3. Giao thức truyền tin (Protocol)
- Quy ước gói tin:
  - Client gửi lệnh: `LIST_THUOC`, `LIST_CONGDUNG`, `XOA_THUOC|<ma>`, `XOA_CONGDUNG|<ma>`.
  - Server trả kết quả: Tiền tố `OK|...` khi thành công, hoặc `ERROR|...` khi có lỗi.
- Toàn bộ dữ liệu bảng được mã hóa **Base64 UTF-8**:
  - Giữa các cột trong 1 dòng phân tách bởi dấu chấm (`.`).
  - Giữa các dòng phân tách bởi dấu chấm phẩy (`;`).
  - Tránh triệt để lỗi khi chuỗi có dấu cách, tiếng Việt có dấu, hoặc dấu phẩy.

---

## PHẦN II: GIẢI THÍCH CHI TIẾT TỪNG FILE & TỪNG ĐOẠN CODE CỐT LÕI

### 1. [Server.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/Server.java) - Khởi động máy chủ và lắng nghe kết nối

#### Đoạn code 1: Mở cổng TCP 5000
```java
Store store = new Store();
try (ServerSocket serverSocket = new ServerSocket(PORT)) {
    ...
}
```
- **Giải thích từng dòng**:
  - `Store store = new Store();`: Khởi tạo duy nhất 1 kho dữ liệu dùng chung cho toàn bộ Server trong suốt phiên làm việc.
  - `new ServerSocket(PORT)`: Đăng ký cổng TCP `5000` với hệ điều hành để chờ kết nối.
  - Cú pháp `try-with-resources`: Tự động giải phóng và đóng cổng khi chương trình Server tắt, tránh lỗi *Address already in use*.

#### Đoạn code 2: Vòng lặp đón nhận kết nối và sinh luồng đa tiến trình
```java
while (true) {
    Socket socket = serverSocket.accept();
    System.out.println("Client ket noi: " + socket.getInetAddress());
    ClientHandler handler = new ClientHandler(socket, store);
    Thread thread = new Thread(handler);
    thread.start();
}
```
- **Giải thích từng dòng**:
  - `serverSocket.accept()`: Đây là hàm chặn (blocking method). Luồng chính dừng tại đây chờ cho tới khi có Client kết nối thì trả về đối tượng `Socket`.
  - `new ClientHandler(socket, store)`: Đóng gói Socket của Client vừa kết nối kèm tham chiếu tới kho dữ liệu `store`.
  - `new Thread(handler); thread.start();`: Cấp phát một tiến trình mới và kích hoạt nó. Nhờ đó, luồng chính lập tức quay lại đầu vòng lặp đón nhận Client tiếp theo mà không bị nghẽn mạng.

---

### 2. [ClientHandler.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/ClientHandler.java) - Xử lý luồng mạng Client trên Server

#### Đoạn code 1: Vòng lặp nhận lệnh và tự động ngắt kết nối an toàn
```java
DataInputStream in = new DataInputStream(socket.getInputStream());
DataOutputStream out = new DataOutputStream(socket.getOutputStream());
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
```
- **Giải thích từng dòng**:
  - `DataInputStream / DataOutputStream`: Dùng để đọc và ghi chuỗi văn bản UTF-8 qua luồng mạng.
  - `in.readUTF()`: Đọc lệnh mà Client gửi tới.
  - `catch (EOFException e)`: Khi Client đóng Socket, `readUTF()` sẽ ném ra ngoại lệ báo hết luồng. Câu lệnh `break;` giúp thoát vòng lặp và đi vào khối `finally` để đóng Socket sạch sẽ, không gây lỗi đọng tài nguyên.
  - `out.writeUTF(response); out.flush();`: Đẩy toàn bộ dữ liệu phản hồi trong bộ nhớ đệm qua đường truyền mạng về lại Client ngay tức thì.

#### Đoạn code 2: Xóa công dụng có bắt lỗi ràng buộc toàn vẹn
```java
if (request.startsWith(Protocol.XOA_CONGDUNG + "|")) {
    String ma = request.substring((Protocol.XOA_CONGDUNG + "|").length()).trim();
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
```
- **Giải thích từng dòng**:
  - `substring(...)`: Cắt lấy mã công dụng nằm phía sau dấu gạch đứng `|`.
  - Gọi `store.xoaCongDung(ma)`.
  - Khối `catch (IllegalStateException e)`: Nếu công dụng này đang có thuốc sử dụng, tầng `Store` sẽ ném ngoại lệ `IllegalStateException`. Handler bắt được và trả về thông báo lỗi `Protocol.error(e.getMessage())` cho Client hiển thị.

---

### 3. [Store.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/Store.java) - Kho dữ liệu và kiểm tra ràng buộc toàn vẹn

#### Đoạn code 1: Đồng bộ hóa luồng và tránh xung đột bộ nhớ
```java
public synchronized List<Thuoc> getDanhSachThuoc() {
    return new ArrayList<>(danhSachThuoc);
}
```
- **Giải thích từng dòng**:
  - `synchronized`: Khóa tài nguyên (Monitor Lock). Tại một thời điểm, chỉ một Thread được phép truy cập vào phương thức này, tránh trường hợp Client A đang đọc trong lúc Client B đang xóa làm sai lệch dữ liệu.
  - `new ArrayList<>(danhSachThuoc)`: Trả về một **bản sao (Shallow Copy)** của danh sách. Nhờ đó luồng bên ngoài lặp danh sách này sẽ không bao giờ bị lỗi `ConcurrentModificationException` nếu danh sách gốc bị chỉnh sửa.

#### Đoạn code 2: Ràng buộc toàn vẹn dữ liệu (Khóa ngoại)
```java
public synchronized String xoaCongDung(String ma) throws IllegalStateException {
    ...
    for (Thuoc t : danhSachThuoc) {
        if (t.getCongDung().equalsIgnoreCase(found.getTenCongDung())
                || t.getCongDung().equalsIgnoreCase(found.getMaCongDung())) {
            throw new IllegalStateException("Khong the xoa '" + found.getTenCongDung()
                    + "' vi dang duoc su dung boi thuoc '" + t.getTenThuoc() + "' (" + t.getMaThuoc() + ").");
        }
    }
    danhSachCongDung.remove(foundIndex);
    return "Da xoa cong dung " + found.getTenCongDung() + " - Ma: " + found.getMaCongDung();
}
```
- **Giải thích từng dòng**:
  - Trước khi xóa một công dụng `found`, vòng lặp `for` sẽ rà soát tất cả các thuốc trong `danhSachThuoc`.
  - Nếu phát hiện bất kỳ thuốc nào có trường `congDung` trùng tên hoặc trùng mã công dụng, lập tức ném `IllegalStateException` chỉ đích danh tên thuốc vi phạm và dừng việc xóa.
  - Chỉ khi không có thuốc nào liên kết, lệnh `danhSachCongDung.remove(foundIndex)` mới được thực thi.

---

### 4. [Protocol.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/Protocol.java) - Giao thức đóng gói và mã hóa Base64

#### Đoạn code 1: Mã hóa và giải mã an toàn
```java
public static String encode(String value) {
    if (value == null) value = "";
    return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
}

public static String decode(String value) {
    if (value == null || value.isEmpty()) return "";
    try {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
        return value;
    }
}
```
- **Giải thích từng dòng**:
  - `getBytes(StandardCharsets.UTF_8)`: Chuyển chuỗi tiếng Việt có dấu sang mảng byte theo bảng mã UTF-8 chuẩn.
  - `Base64.getEncoder().encodeToString(...)`: Chuyển mảng byte thành chuỗi ký tự ASCII gồm chữ và số an toàn tuyệt đối khi truyền qua Socket.
  - Kiểm tra `null` và bắt `IllegalArgumentException` để đảm bảo không bao giờ bị văng lỗi crash ứng dụng.

#### Đoạn code 2: Đóng gói và giải nén dữ liệu bảng
```java
// Đóng gói:
for (String[] row : rows) {
    if (result.length() > 0) result.append(";");
    for (int i = 0; i < row.length; i++) {
        if (i > 0) result.append(".");
        result.append(encode(row[i]));
    }
}

// Giải mã:
String[] rows = data.split(";", -1);
for (String row : rows) {
    if (row.trim().isEmpty()) continue;
    String[] fields = row.split("\\.", -1);
    for (int i = 0; i < fields.length; i++) {
        decoded[i] = decode(fields[i]);
    }
    result.add(decoded);
}
```
- **Giải thích từng dòng**:
  - `split(";", -1)`: Cắt chuỗi thành các hàng.
  - `split("\\.", -1)`: Cắt hàng thành các ô (cột) theo dấu chấm.
  - Gọi `decode()` để lấy lại chuỗi gốc và đưa vào danh sách trả về cho Client.

---

### 5. [ClientService.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/ClientService.java) - Tầng dịch vụ kết nối Socket phía Client

#### Đoạn code: Kết nối và xử lý lỗi ngắt mạng
```java
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
```
- **Giải thích từng dòng**:
  - `new Socket(host, port)`: Mở kết nối TCP tới máy chủ `127.0.0.1:5000`.
  - `socket.setSoTimeout(5000)`: Đặt thời gian chờ tối đa là 5 giây. Nếu quá 5 giây Server không trả lời thì tự ngắt, chống treo ứng dụng.
  - `catch (ConnectException e)`: Nếu Server chưa bật, hệ điều hành trả về lỗi `Connection refused`. Khối này bắt lại và chuyển thành câu thông báo tiếng Việt hướng dẫn bật Server.

---

### 6. [BTLLTM.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/BTLLTM.java) - Tầng giao diện đồ họa (View)

#### Đoạn code: Khóa sửa trực tiếp ô trên bảng & Khởi động MVC
```java
// Khóa sửa ô:
modelThuoc = new DefaultTableModel(headers, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};

// Khởi tạo MVC trong hàm main:
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        BTLLTM view = new BTLLTM();
        ClientService service = new ClientService("127.0.0.1", 5000);
        new ClientController(view, service);
        view.setVisible(true);
    });
}
```
- **Giải thích từng dòng**:
  - `isCellEditable(...) = false`: Chặn người dùng nhấp đúp vào ô trên bảng để gõ chữ, đảm bảo dữ liệu hiển thị trung thực với dữ liệu Server.
  - `SwingUtilities.invokeLater`: Đảm bảo toàn bộ việc dựng giao diện được chạy trong luồng sự kiện đồ họa (EDT - Event Dispatch Thread).
  - Tách bạch rõ ràng: Tạo `View`, tạo `Service`, truyền vào `Controller` quản lý.

---

### 7. [ClientController.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/ClientController.java) - Tầng điều khiển (Controller)

#### Đoạn code 1: Nạp dữ liệu nền bằng SwingWorker
```java
new SwingWorker<List<List<String[]>>, Void>() {
    @Override
    protected List<List<String[]>> doInBackground() throws Exception {
        List<List<String[]>> ketQua = new ArrayList<>();
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
```
- **Giải thích từng dòng**:
  - `doInBackground()`: Chạy ngầm trong một luồng độc lập (Background Thread), gọi Service gửi lệnh Socket qua mạng.
  - `done()`: Tự động chạy trên luồng giao diện (EDT) ngay sau khi `doInBackground()` xong. Dùng `get()` để lấy kết quả và đổ vào các bảng `JTable`.
  - `e.getCause()`: Khi `SwingWorker` gặp lỗi mạng, lỗi sẽ bị bọc trong `ExecutionException`. Dùng `e.getCause()` sẽ lấy chính xác nguyên nhân lỗi để in ra giao diện.

#### Đoạn code 2: Xóa thuốc và hộp thoại xác nhận
```java
int row = view.getTableThuoc().getSelectedRow();
if (row == -1) {
    view.hienThiKetQua("THAT BAI: Hay chon thuoc can xoa.");
    return;
}
String ma = view.getTableThuoc().getValueAt(row, 0).toString();
String ten = view.getTableThuoc().getValueAt(row, 1).toString();

int confirm = JOptionPane.showConfirmDialog(view, "Ban co chac muon xoa thuoc " + ten + " (Ma: " + ma + ")?", ...);
if (confirm != JOptionPane.YES_OPTION) return;

view.getBtnXoaThuoc().setEnabled(false);
```
- **Giải thích từng dòng**:
  - `getSelectedRow() == -1`: Kiểm tra người dùng đã chọn thuốc trên bảng chưa. Nếu chưa chọn thì báo lỗi ngay.
  - `JOptionPane.showConfirmDialog`: Hiển thị hộp thoại hỏi lại người dùng để tránh bấm nhầm.
  - `setEnabled(false)`: Vô hiệu hóa nút xóa tạm thời để người dùng không bấm liên tục khi đang xử lý mạng.

---

### 8. Các lớp thực thể và dữ liệu mẫu
- **[Thuoc.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/Thuoc.java)**: Đại diện cho 1 loại thuốc (mã, tên, giá, công dụng, dạng thuốc).
- **[CongDung.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/CongDung.java)**: Đại diện cho 1 nhóm công dụng (mã, tên công dụng, mô tả).
- **[SampleData.java](file:///C:/Users/Admin/Documents/NetBeansProjects/BTL-LTM/src/btl/ltm/SampleData.java)**: Khởi tạo 13 thuốc mẫu và 7 công dụng mẫu (trong đó `CD07` không có thuốc nào liên kết để kiểm tra xóa thành công, các mã `CD01`-`CD06` có thuốc liên kết để kiểm tra chặn xóa).

---

## PHẦN III: LUỒNG CHẠY CHI TIẾT CỦA TỪNG CHỨC NĂNG CỤ THỂ

### Chức năng 1: Khởi động và kết nối mạng Client - Server
1. Người dùng chạy `Server.java`:
   - Hàm `main()` gọi `new Store()`, nạp dữ liệu mẫu lên bộ nhớ RAM.
   - Mở cổng `5000` qua `new ServerSocket(5000)`.
   - Tiến trình Server dừng lại tại `serverSocket.accept()` chờ đợi Client.
2. Người dùng chạy `BTLLTM.java`:
   - Giao diện được dựng lên màn hình.
   - Hàm `taiDuLieuBanDau()` của `ClientController` kích hoạt `SwingWorker`.
   - Client mở Socket kết nối tới `127.0.0.1:5000`.
3. Server chấp nhận kết nối:
   - In ra thông báo: `Client ket noi: /127.0.0.1`.
   - Cấp phát đối tượng `ClientHandler` chạy trên Thread riêng.
4. Client gửi 2 gói tin liên tiếp:
   - Gói 1: `LIST_THUOC` ➔ Server trả dữ liệu thuốc mã hóa ➔ Client nạp vào Tab 1.
   - Gói 2: `LIST_CONGDUNG` ➔ Server trả dữ liệu công dụng mã hóa ➔ Client nạp vào Tab 2.
5. Vùng kết quả hiển thị thông báo:
   > `"Da ket noi Server va tai du lieu thanh cong."`

---

### Chức năng 2: Xem danh sách Thuốc và Công dụng
1. Người dùng nhấp chuột chuyển qua lại giữa 2 Tab:
   - Tab **"Xóa thuốc"**: Xem toàn bộ 13 thuốc dạng bảng (Mã, Tên, Giá, Công dụng, Dạng bào chế).
   - Tab **"Xóa công dụng"**: Xem toàn bộ 7 nhóm công dụng (Mã, Tên, Mô tả chi tiết).
2. Dữ liệu hiển thị chế độ Read-only (chỉ đọc), không thể sửa trực tiếp trên bảng.

---

### Chức năng 3: Xóa một loại thuốc đã chọn
1. Trên Tab 1, người dùng chọn dòng thuốc `T002 - Efferalgan 500mg`.
2. Người dùng nhấp nút **"Xóa thuốc đã chọn"**:
   - Controller lấy được mã thuốc `T002` và tên thuốc `Efferalgan 500mg`.
   - Xuất hiện hộp thoại: *"Ban co chac muon xoa thuoc Efferalgan 500mg (Ma: T002)?"*.
3. Người dùng chọn **"Yes"**:
   - Nút xóa thuốc bị làm mờ (`setEnabled(false)`).
   - Luồng `SwingWorker` gửi chuỗi: `XOA_THUOC|T002` qua Socket.
   - Server nhận lệnh ➔ gọi `store.xoaThuoc("T002")` ➔ xóa phần tử khỏi `danhSachThuoc`.
   - Server phản hồi chuỗi: `OK|Da xoa thuoc Efferalgan 500mg - Ma: T002`.
   - Client nhận được `OK` ➔ tự động gửi tiếp lệnh ngầm `LIST_THUOC` để lấy lại danh sách mới nhất.
4. Khi hoàn thành:
   - Bảng thuốc biến mất dòng `T002`.
   - Nút xóa sáng trở lại.
   - Ô kết quả hiển thị: `"THANH CONG: Da xoa thuoc Efferalgan 500mg - Ma: T002"`.

---

### Chức năng 4: Xóa công dụng bị chặn do ràng buộc toàn vẹn (Khóa ngoại)
1. Người dùng sang Tab 2, chọn công dụng `CD01 - Giam dau ha sot`.
2. Nhấp nút **"Xóa công dụng đã chọn"** ➔ Xác nhận **"Yes"**.
3. Client gửi gói tin: `XOA_CONGDUNG|CD01`.
4. Phía Server:
   - `ClientHandler` gọi `store.xoaCongDung("CD01")`.
   - Trong `Store.java`, vòng lặp kiểm tra thấy các thuốc `T001` (Paracetamol), `T003` (Hapacol)... vẫn đang có công dụng này.
   - Store ném ngoại lệ:
     ```text
     IllegalStateException: Khong the xoa 'Giam dau ha sot' vi dang duoc su dung boi thuoc 'Paracetamol 500mg' (T001).
     ```
   - Server gửi phản hồi:
     ```text
     ERROR|Khong the xoa 'Giam dau ha sot' vi dang duoc su dung boi thuoc 'Paracetamol 500mg' (T001).
     ```
5. Phía Client:
   - `ClientService` phát hiện tiền tố `ERROR` ➔ ném `IOException`.
   - `SwingWorker` bắt được lỗi và in ra ô kết quả:
     ```text
     THAT BAI: Khong the xoa 'Giam dau ha sot' vi dang duoc su dung boi thuoc 'Paracetamol 500mg' (T001).
     ```
   - Công dụng `CD01` vẫn được giữ nguyên vẹn trên bảng.

---

### Chức năng 5: Xóa công dụng thành công khi không có thuốc liên kết
1. Người dùng chọn công dụng `CD07 - Bo mat - Sang mat`. *(Đây là dữ liệu mẫu không có thuốc nào liên kết để thử nghiệm tính năng xóa)*.
2. Nhấp nút **"Xóa công dụng đã chọn"** ➔ Xác nhận **"Yes"**.
3. Client gửi gói tin: `XOA_CONGDUNG|CD07`.
4. Server kiểm tra trong kho: Không có bất kỳ thuốc nào dùng `CD07`.
5. Server thực hiện xóa `CD07` khỏi `danhSachCongDung` và phản hồi: `OK|Da xoa cong dung Bo mat - Sang mat - Ma: CD07`.
6. Client tải lại danh sách mới ➔ Dòng `CD07` biến mất khỏi bảng và thông báo thành công.

---

### Chức năng 6: Xử lý ngoại lệ khi chưa bật Server hoặc mất mạng
1. Người dùng vô tình mở `BTLLTM.java` khi chưa chạy `Server.java`.
2. `ClientService` thực hiện lệnh `new Socket("127.0.0.1", 5000)`.
3. Hệ điều hành phát hiện cổng 5000 chưa mở ➔ ném `ConnectException`.
4. Khối `catch` trong `ClientService` chuyển đổi thành thông báo:
   > *"Khong the ket noi toi Server tai 127.0.0.1:5000. Vui long bat Server truoc khi chay Client!"*
5. Giao diện vẫn hiển thị bình thường và hiện thông báo lỗi rõ ràng ở ô kết quả, hoàn toàn không bị văng lỗi hay đơ ứng dụng.

---

## PHẦN IV: LUỒNG XỬ LÝ ĐA LUỒNG (MULTI-THREADING ARCHITECTURE)

Hệ thống được vận hành đồng thời bởi nhiều luồng (Thread) để giải quyết bài toán hiệu năng:

| Thành phần | Tên Luồng | Trách nhiệm chính |
| :--- | :--- | :--- |
| **Server** | `Main Thread` | Chạy vòng lặp `while(true)` và dừng tại `accept()` để chờ đón các kết nối mới từ mạng. |
| **Server** | `ClientHandler Thread` | Mỗi Client khi kết nối sẽ được phân phối một luồng riêng biệt, xử lý nhận/gửi dữ liệu độc lập. |
| **Server** | `Monitor Lock (Store)` | Khóa đồng bộ hóa `synchronized` trên kho dữ liệu, đảm bảo nếu 2 Client cùng bấm xóa tại 1 phần nghìn giây thì vẫn xếp hàng thực thi an toàn. |
| **Client** | `Main Thread` | Khởi tạo cấu hình ban đầu. |
| **Client** | `Swing EDT (Event Dispatch Thread)` | Luồng chuyên vẽ giao diện đồ họa, lắng nghe sự kiện nhấp chuột, gõ phím của người dùng. |
| **Client** | `SwingWorker Thread` | Luồng nền (Background) chuyên lo việc truyền dữ liệu Socket qua mạng để không làm nghẽn luồng EDT. |

---

## PHẦN V: CÁC CÂU HỎI VẤN ĐÁP THƯỜNG GẶP KHI BẢO VỆ BTL & GỢI Ý TRẢ LỜI

### ❓ Câu 1: Tại sao phía Server phải sử dụng Đa luồng (Multi-threading)?
> **Trả lời:**  
> Nếu Server chạy đơn luồng (Single-thread), hàm `accept()` hoặc `readUTF()` sẽ khóa chặt luồng xử lý. Khi Client A đang kết nối, tất cả các Client khác muốn kết nối đều sẽ bị treo và không được phục vụ. Bằng cách tạo một `Thread(ClientHandler)` mới cho mỗi kết nối, Server có thể phục vụ đồng thời hàng trăm Client cùng lúc mà không bị nghẽn mạng.

---

### ❓ Câu 2: Từ khóa `synchronized` trong Store.java có tác dụng gì? Nếu không dùng thì điều gì xảy ra?
> **Trả lời:**  
> Vì Server là đa luồng, nhiều Client có thể cùng gửi yêu cầu xóa hoặc đọc dữ liệu tại cùng một thời điểm. Nếu không có `synchronized`, sẽ xảy ra hiện tượng xung đột dữ liệu (**Race Condition**) hoặc văng lỗi `ConcurrentModificationException` làm sập tiến trình Server. Từ khóa `synchronized` đóng vai trò là một chìa khóa (Lock): chỉ một luồng được vào sửa/đọc dữ liệu tại một thời điểm, các luồng khác phải xếp hàng chờ.

---

### ❓ Câu 3: Tại sao lại phải mã hóa Base64 trong giao thức `Protocol`?
> **Trả lời:**  
> Dữ liệu của chúng ta là tiếng Việt có dấu, chứa khoảng trắng, dấu chấm, dấu phẩy, ký tự xuống dòng. Nếu gửi trực tiếp chuỗi gốc và dùng dấu phẩy hay dấu chấm để phân tách cột, các chuỗi chứa ký tự trùng khớp sẽ làm hàm `split()` cắt nhầm vị trí cột dẫn đến dữ liệu bị xáo trộn. Mã hóa Base64 chuyển đổi mọi chuỗi UTF-8 thành tập ký tự chuẩn `A-Z, a-z, 0-9, +, /, =`, đảm bảo an toàn tuyệt đối khi truyền qua Socket.

---

### ❓ Câu 4: Phía Client tại sao phải dùng `SwingWorker` thay vì gọi mạng trực tiếp?
> **Trả lời:**  
> Trong Java Swing, mọi thao tác vẽ giao diện và bấm nút đều chạy trên duy nhất một luồng gọi là **EDT (Event Dispatch Thread)**. Nếu chúng ta thực hiện kết nối mạng Socket trên luồng EDT, khi mạng chậm hoặc Server phản hồi lâu, toàn bộ cửa sổ ứng dụng sẽ bị đóng băng và Windows sẽ báo trạng thái *"Not Responding"*. `SwingWorker` đẩy toàn bộ công việc mạng sang luồng nền (`doInBackground`), giúp giao diện vẫn phản hồi mượt mà cho người dùng.

---

### ❓ Câu 5: Ràng buộc toàn vẹn dữ liệu (Khóa ngoại) được thực hiện như thế nào trong bài?
> **Trả lời:**  
> Trong `Store.java`, phương thức `xoaCongDung()` trước khi xóa sẽ duyệt qua danh sách các loại thuốc hiện có. Nếu phát hiện có bất kỳ loại thuốc nào đang liên kết với công dụng này, hệ thống sẽ ném ngoại lệ `IllegalStateException` từ chối xóa và báo lỗi cụ thể loại thuốc đang vi phạm. Chỉ khi không có thuốc nào sử dụng thì công dụng mới được phép xóa.
