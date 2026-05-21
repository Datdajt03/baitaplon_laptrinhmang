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

        // Buoc 1: Thu ket noi localhost
        if (KiemTraKetNoi.kiemTra("localhost", 8888)) {
            // Server dang chay tren may nay → che do LOCAL
            cheDo = CheDo.LOCAL;
            CauHinh.SERVER_IP = "localhost";
            System.out.println("[PHAN LUONG] → Tim thay server LOCAL. Ket noi localhost.");
            return cheDo;
        }

        // Buoc 2: Localhost khong co → hoi IP LAN
        System.out.println("[PHAN LUONG] → Khong tim thay server local. Chuyen sang che do LAN.");
        cheDo = CheDo.LAN;

        String ip = javax.swing.JOptionPane.showInputDialog(null,
            "Không tìm thấy server trên máy này.\n"
            + "Nhập địa chỉ IP của Server LAN (VD: 192.168.1.100):",
            "Kết Nối LAN",
            javax.swing.JOptionPane.QUESTION_MESSAGE);

        if (ip != null && !ip.trim().isEmpty()) {
            CauHinh.SERVER_IP = ip.trim();
        } else {
            // Nguoi dung huy hoac de trong → thu lai localhost
            CauHinh.SERVER_IP = "localhost";
            cheDo = CheDo.LOCAL;
            System.out.println("[PHAN LUONG] → Nguoi dung khong nhap IP, quay ve localhost.");
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
