package may_chu;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// may chu phat thong bao qua udp
public class may_chu_udp {
    
    // ham phat thong bao
    public static void phat_thongbao(String tenfile) {
        try {
            DatagramSocket mang_udp = new DatagramSocket();
            String thongbao = tenfile;
            byte[] bo_dem = thongbao.getBytes("UTF-8");
            
            // gui cho tat ca ung dung tren may
            InetAddress diachi = InetAddress.getByName("255.255.255.255");
            DatagramPacket goi_tin = new DatagramPacket(bo_dem, bo_dem.length, diachi, 9999);
            
            // cho phep phat da chieu
            mang_udp.setBroadcast(true);
            mang_udp.send(goi_tin);
            
            mang_udp.close();
            System.out.println("da phat udp: " + tenfile);
        } catch (Exception loi) {
            System.out.println("loi phat udp: " + loi.getMessage());
        }
    }
}
