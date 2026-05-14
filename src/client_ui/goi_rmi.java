package client_ui;

import may_chu.dich_vu_rmi;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JTextArea;

// lop goi cac ham rmi tu may chu
public class goi_rmi {
    
    // goi ham them danh muc
    public static void them_danhmuc(String tendanhmuc, JTextArea hienthi) {
        try {
            Registry quanly_rmi = LocateRegistry.getRegistry(CauHinh.SERVER_IP, 1099);
            dich_vu_rmi dichvu = (dich_vu_rmi) quanly_rmi.lookup("dichvurmi");
            
            String ketqua = dichvu.quanly_danhmuc("them", tendanhmuc);
            hienthi.append("rmi tra ve: " + ketqua + "\n");
        } catch (Exception loi) {
            hienthi.append("loi goi rmi: " + loi.getMessage() + "\n");
        }
    }
    
    // goi ham them tag
    public static void them_tag(String tentag, JTextArea hienthi) {
        try {
            Registry quanly_rmi = LocateRegistry.getRegistry(CauHinh.SERVER_IP, 1099);
            dich_vu_rmi dichvu = (dich_vu_rmi) quanly_rmi.lookup("dichvurmi");
            
            String ketqua = dichvu.quanly_tag("them", tentag);
            hienthi.append("rmi tra ve: " + ketqua + "\n");
        } catch (Exception loi) {
            hienthi.append("loi goi rmi: " + loi.getMessage() + "\n");
        }
    }
    
    // goi ham thong ke luot tai
    public static void xem_thongke(JTextArea hienthi) {
        try {
            Registry quanly_rmi = LocateRegistry.getRegistry(CauHinh.SERVER_IP, 1099);
            dich_vu_rmi dichvu = (dich_vu_rmi) quanly_rmi.lookup("dichvurmi");
            
            String ketqua = dichvu.thongke_luottai();
            hienthi.append("thong ke tu rmi: \n" + ketqua + "\n");
        } catch (Exception loi) {
            hienthi.append("loi goi rmi: " + loi.getMessage() + "\n");
        }
    }
}
