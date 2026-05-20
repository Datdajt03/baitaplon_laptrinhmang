package chucnang3;

import client_ui.CauHinh;
import thongbao.KiemTraKetNoi;

/**
 * Phan luong ket noi: Local vs LAN.
 *
 * Logic:
 *   1. Kiem tra localhost:8888 truoc.
 *   2. Neu thanh cong → che do LOCAL, SERVER_IP = "localhost", khong hoi IP.
 *   3. Neu that bai → che do LAN, hoi nguoi dung nhap IP server LAN.
 *
 * Goi PhanLuong.xacDinhKetNoi() trong main() truoc khi tao giao dien.
 */
public class PhanLuong {

    // Che do ket noi hien tai
    public enum CheDo {
        LOCAL, // Server chay tren may nay (localhost)
        LAN    // Server chay tren may khac (qua LAN / Radmin VPN)
    }

    private static CheDo cheDo = CheDo.LOCAL;

    /**
     * Xac dinh che do ket noi tu dong.
     * - Uu tien kiem tra localhost truoc.
     * - Neu localhost khong co server → chuyen sang che do LAN va hoi IP.
     *
     * @return CheDo da xac dinh (LOCAL hoac LAN)
     */
    public static CheDo xacDinhKetNoi() {
        System.out.println("[PHAN LUONG] Dang kiem tra server local (localhost:8888)...");

        // Buoc 1: Kiem tra xem server local co dang bat khong
        boolean coLocal = KiemTraKetNoi.kiemTra("localhost", 8888);

        if (coLocal) {
            // Tim thay server local -> Hỏi nguoi dung xem muon vao luon local hay muon nhap IP LAN khac
            int luaChon = javax.swing.JOptionPane.showConfirmDialog(null,
                "Tìm thấy Server đang chạy trên máy này (localhost).\n"
                + "Bạn có muốn kết nối tới Server Local này không?\n"
                + "(Chọn 'No' để nhập IP của Server khác qua mạng LAN / VPN)",
                "Phát Hiện Server Local",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);

            if (luaChon == javax.swing.JOptionPane.YES_OPTION) {
                cheDo = CheDo.LOCAL;
                CauHinh.SERVER_IP = "localhost";
                System.out.println("[PHAN LUONG] → Nguoi dung chon ket noi LOCAL (localhost).");
                return cheDo;
            }
        }

        // Buoc 2: Neu khong co server local, hoac nguoi dung chon 'No' (muon ket noi LAN)
        System.out.println("[PHAN LUONG] → Chuyen sang che do nhap IP LAN.");
        cheDo = CheDo.LAN;

        String ip = javax.swing.JOptionPane.showInputDialog(null,
            "Nhập địa chỉ IP của Server LAN / Radmin VPN\n(Ví dụ: 192.168.1.100 hoặc 26.18.244.131):",
            "Kết Nối LAN / VPN",
            javax.swing.JOptionPane.QUESTION_MESSAGE);

        if (ip != null && !ip.trim().isEmpty()) {
            CauHinh.SERVER_IP = ip.trim();
        } else {
            // Nguoi dung huy hoac de trong → mac dinh quay ve localhost
            CauHinh.SERVER_IP = "localhost";
            cheDo = CheDo.LOCAL;
            System.out.println("[PHAN LUONG] → Nguoi dung de trong IP, mac dinh ve localhost.");
        }

        return cheDo;
    }

    /**
     * Lay che do ket noi hien tai.
     */
    public static CheDo layCheDo() {
        return cheDo;
    }

    /**
     * Kiem tra dang o che do local hay khong.
     */
    public static boolean laLocal() {
        return cheDo == CheDo.LOCAL;
    }

    /**
     * Kiem tra dang o che do LAN hay khong.
     */
    public static boolean laLAN() {
        return cheDo == CheDo.LAN;
    }
}
