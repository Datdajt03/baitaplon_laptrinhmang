package thongbao;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Lop tien ich kiem tra ket noi TCP den may chu.
 * Dung de xac nhan Server (qua Radmin VPN hoac LAN) co dang chay hay khong.
 */
public class KiemTraKetNoi {

    // Timeout 3 giay - du de biet co ket noi hay khong, khong lam block UI qua lau
    private static final int TIMEOUT_MS = 3000;

    /**
     * Kiem tra xem co the mo TCP socket den serverIp:port hay khong.
     * @param serverIp  Dia chi IP cua Server (Radmin VPN IP hoac LAN IP)
     * @param port      Cong can kiem tra (thuong la 8888 - cong TCP chinh)
     * @return true neu ket noi thanh cong, false neu that bai
     */
    public static boolean kiemTra(String serverIp, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(serverIp, port), TIMEOUT_MS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
