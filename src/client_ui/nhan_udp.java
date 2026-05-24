package client_ui;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

// lop lang nghe thong bao tu may chu qua udp
public class nhan_udp extends Thread {
    
    private JTextArea hienthi;
    private DatagramSocket mang_udp;
    
    public nhan_udp(JTextArea hienthi) {
        this.hienthi = hienthi;
    }
    
    @Override
    public void run() {
        try {
            mang_udp = new DatagramSocket(9999);
            byte[] bo_dem = new byte[1024];
            
            while (true) {
                DatagramPacket goi_tin = new DatagramPacket(bo_dem, bo_dem.length);
                mang_udp.receive(goi_tin);
                
                String thongbao = new String(goi_tin.getData(), 0, goi_tin.getLength(), "UTF-8");
                
                // cap nhat giao dien an toan qua luong cua swing
                SwingUtilities.invokeLater(() -> {
                    if (thongbao.startsWith("RATING|")) {
                        String tenfile = thongbao.substring(7);
                        hienthi.append("có lượt đánh giá mới cho tài liệu: " + tenfile + "\n");
                        client_ui.them_hoat_dong("Một người dùng khác vừa đánh giá tài liệu: " + tenfile);
                    } else {
                        hienthi.append("co tai lieu moi: " + thongbao + "\n");
                        client_ui.them_hoat_dong("Tài liệu mới vừa được tải lên: " + thongbao);
                    }
                    // Tu dong lam moi danh sach realtime cho tat ca client trong mang
                    if (client_ui.INSTANCE != null) {
                        client_ui.INSTANCE.lamMoiDanhSach();
                    }
                });
            }
        } catch (Exception loi) {
            SwingUtilities.invokeLater(() -> {
                hienthi.append("loi mang udp: " + loi.getMessage() + "\n");
            });
        }
    }
}
