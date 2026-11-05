package btl.ltm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BTLLTM extends JFrame {

    private DefaultTableModel modelThuoc;
    private DefaultTableModel modelCongDung;

    private JTable tableThuoc;
    private JTable tableCongDung;

    private JButton btnXoaThuoc;
    private JButton btnXoaCongDung;

    private JTextArea txtKetQua;

    public BTLLTM() {

        setTitle("BTL - Quan ly thuoc theo cong dung");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel title = new JLabel(
            "QUAN LY THUOC THEO CONG DUNG",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));
        root.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        JPanel panelThuoc = new JPanel(new BorderLayout(10, 10));
        panelThuoc.setBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        modelThuoc = new DefaultTableModel(
            new String[]{
                "Ma thuoc", "Ten thuoc", "Gia",
                "Cong dung", "Dang thuoc"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableThuoc = new JTable(modelThuoc);
        tableThuoc.setRowHeight(28);
        tableThuoc.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JLabel lblThuoc = new JLabel("DANH SACH THUOC");
        lblThuoc.setFont(new Font("Arial", Font.BOLD, 17));

        btnXoaThuoc = new JButton("Xoa thuoc da chon");

        JPanel actionsThuoc = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        actionsThuoc.add(btnXoaThuoc);

        panelThuoc.add(lblThuoc, BorderLayout.NORTH);
        panelThuoc.add(
            new JScrollPane(tableThuoc), BorderLayout.CENTER
        );
        panelThuoc.add(actionsThuoc, BorderLayout.SOUTH);

        JPanel panelCongDung = new JPanel(
            new BorderLayout(10, 10)
        );

        panelCongDung.setBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        modelCongDung = new DefaultTableModel(
            new String[]{
                "Ma cong dung", "Ten cong dung", "Mo ta"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCongDung = new JTable(modelCongDung);
        tableCongDung.setRowHeight(28);
        tableCongDung.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JLabel lblCongDung = new JLabel(
            "DANH SACH CONG DUNG"
        );

        lblCongDung.setFont(
            new Font("Arial", Font.BOLD, 17)
        );

        btnXoaCongDung = new JButton(
            "Xoa cong dung da chon"
        );

        JPanel actionsCongDung = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        actionsCongDung.add(btnXoaCongDung);

        panelCongDung.add(lblCongDung, BorderLayout.NORTH);
        panelCongDung.add(
            new JScrollPane(tableCongDung),
            BorderLayout.CENTER
        );
        panelCongDung.add(
            actionsCongDung, BorderLayout.SOUTH
        );

        tabs.addTab("Xoa thuoc", panelThuoc);
        tabs.addTab("Xoa cong dung", panelCongDung);

        root.add(tabs, BorderLayout.CENTER);

        JPanel panelKetQua = new JPanel(
            new BorderLayout(5, 5)
        );

        panelKetQua.setPreferredSize(
            new Dimension(0, 135)
        );

        JLabel lblKetQua = new JLabel(
            "KET QUA THAO TAC"
        );

        lblKetQua.setFont(
            new Font("Arial", Font.BOLD, 15)
        );

        txtKetQua = new JTextArea(4, 50);
        txtKetQua.setEditable(false);
        txtKetQua.setLineWrap(true);
        txtKetQua.setWrapStyleWord(true);
        txtKetQua.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        txtKetQua.setText("Dang khoi tao giao dien...");

        panelKetQua.add(lblKetQua, BorderLayout.NORTH);
        panelKetQua.add(
            new JScrollPane(txtKetQua),
            BorderLayout.CENTER
        );

        root.add(panelKetQua, BorderLayout.SOUTH);

        setContentPane(root);
    }

    public JTable getTableThuoc() {
        return tableThuoc;
    }

    public JTable getTableCongDung() {
        return tableCongDung;
    }

    public JButton getBtnXoaThuoc() {
        return btnXoaThuoc;
    }

    public JButton getBtnXoaCongDung() {
        return btnXoaCongDung;
    }

    public void hienThiKetQua(String message) {
        txtKetQua.setText(message);
    }

    public void hienThiDanhSachThuoc(
            List<String[]> danhSach) {

        modelThuoc.setRowCount(0);

        for (String[] row : danhSach) {
            modelThuoc.addRow(row);
        }
    }

    public void hienThiDanhSachCongDung(
            List<String[]> danhSach) {

        modelCongDung.setRowCount(0);

        for (String[] row : danhSach) {
            modelCongDung.addRow(row);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            BTLLTM view = new BTLLTM();
            ClientService service = new ClientService("127.0.0.1", 5000);
            new ClientController(view, service);
            view.setVisible(true);
        });
    }
}
