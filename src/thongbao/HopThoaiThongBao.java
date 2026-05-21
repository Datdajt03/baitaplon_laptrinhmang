package thongbao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Hop thoai thong bao ket qua ket noi khi Client khoi dong.
 * Hien thi "Ket noi thanh cong" (xanh la) hoac "Ket noi that bai" (do)
 * va tu dong dong sau 3 giay.
 */
public class HopThoaiThongBao extends JDialog {

    // Mau sac
    private static final Color MAU_XANH_DAM   = new Color(34, 139, 34);
    private static final Color MAU_DO_DAM      = new Color(180, 30, 30);
    private static final Color MAU_XANH_NHAT  = new Color(230, 255, 230);
    private static final Color MAU_DO_NHAT    = new Color(255, 230, 230);
    private static final Color MAU_CHU_PHAN   = new Color(80, 80, 80);

    private static final int TU_DONG_DONG_MS = 3000; // 3 giay

    /**
     * Tao hop thoai thong bao ket noi.
     * @param ketNoiThanhCong true = hien thi thanh cong, false = hien thi that bai
     * @param serverIp        Dia chi IP dang ket noi den (hien thi cho nguoi dung biet)
     */
    public HopThoaiThongBao(boolean ketNoiThanhCong, String serverIp) {
        super((java.awt.Frame) null, "Ket Qua Ket Noi", true);

        // ------ Chon mau sac theo ket qua ------
        Color mauNen   = ketNoiThanhCong ? MAU_XANH_NHAT : MAU_DO_NHAT;
        Color mauChinh = ketNoiThanhCong ? MAU_XANH_DAM  : MAU_DO_DAM;
        String kyHieu  = ketNoiThanhCong ? "✓" : "✗";
        String tieuDe  = ketNoiThanhCong ? "Kết Nối Thành Công!" : "Kết Nối Thất Bại";
        String moTaPhanTu;
        if (ketNoiThanhCong) {
            moTaPhanTu = "Đã kết nối tới máy chủ tại:\n" + serverIp + ":8888";
        } else if ("localhost".equals(serverIp) || "127.0.0.1".equals(serverIp)) {
            moTaPhanTu = "Không thể kết nối tới máy chủ tại:\n" + serverIp + ":8888\n\nKiểm tra lại server đã được khởi động chưa.";
        } else {
            moTaPhanTu = "Không thể kết nối tới máy chủ tại:\n" + serverIp + ":8888\n\nKiểm tra lại Radmin VPN hoặc IP máy chủ.";
        }
        String moTa = moTaPhanTu;

        // ------ Layout chinh ------
        JPanel pn_chinh = new JPanel(new GridBagLayout());
        pn_chinh.setBackground(mauNen);
        pn_chinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mauChinh, 2),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Icon chu lon
        JLabel lbl_icon = new JLabel(kyHieu);
        lbl_icon.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lbl_icon.setForeground(mauChinh);
        lbl_icon.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        pn_chinh.add(lbl_icon, gbc);

        // Tieu de
        JLabel lbl_tieude = new JLabel(tieuDe);
        lbl_tieude.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl_tieude.setForeground(mauChinh);
        lbl_tieude.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        pn_chinh.add(lbl_tieude, gbc);

        // Mo ta
        JLabel lbl_mota = new JLabel("<html><center>" + moTa.replace("\n", "<br>") + "</center></html>");
        lbl_mota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl_mota.setForeground(MAU_CHU_PHAN);
        lbl_mota.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        pn_chinh.add(lbl_mota, gbc);

        // Dong dem nguoc
        JLabel lbl_demngoc = new JLabel("Tự động đóng sau 3 giây...");
        lbl_demngoc.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_demngoc.setForeground(new Color(150, 150, 150));
        lbl_demngoc.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 6, 0, 6);
        pn_chinh.add(lbl_demngoc, gbc);

        // Dem nguoc cap nhat label moi giay
        final int[] con_lai = {3};
        Timer demNgoc = new Timer(1000, e -> {
            con_lai[0]--;
            if (con_lai[0] > 0) {
                lbl_demngoc.setText("Tu dong dong sau " + con_lai[0] + " giay...");
            } else {
                dispose();
            }
        });
        demNgoc.start();

        // ------ Cau hinh cua so ------
        setContentPane(pn_chinh);
        setSize(380, 260);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    /**
     * Ham tien ich tinh: Kiem tra ket noi va hien thi hop thoai thong bao.
     * Goi ham nay ngay sau khi nguoi dung nhap IP.
     *
     * @param serverIp Dia chi IP Server nguoi dung vua nhap
     */
    public static void hienThiVaKiemTra(String serverIp) {
        boolean thanh_cong = KiemTraKetNoi.kiemTra(serverIp, 8888);
        HopThoaiThongBao hop = new HopThoaiThongBao(thanh_cong, serverIp);
        hop.setVisible(true); // blocking do modal=true, cho den khi dong
    }
}
