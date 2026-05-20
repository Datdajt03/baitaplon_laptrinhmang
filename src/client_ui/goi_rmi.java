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
            String rmiHost = chucnang3.PhanLuong.laLocal() ? "localhost" : CauHinh.SERVER_IP;
            Registry quanly_rmi = LocateRegistry.getRegistry(rmiHost, 1099);
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
            String rmiHost = chucnang3.PhanLuong.laLocal() ? "localhost" : CauHinh.SERVER_IP;
            Registry quanly_rmi = LocateRegistry.getRegistry(rmiHost, 1099);
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
            String rmiHost = chucnang3.PhanLuong.laLocal() ? "localhost" : CauHinh.SERVER_IP;
            Registry quanly_rmi = LocateRegistry.getRegistry(rmiHost, 1099);
            dich_vu_rmi dichvu = (dich_vu_rmi) quanly_rmi.lookup("dichvurmi");
            
            String ketqua = dichvu.thongke_luottai();
            hienthi.append("thong ke tu rmi: \n" + ketqua + "\n");
        } catch (Exception loi) {
            hienthi.append("loi goi rmi: " + loi.getMessage() + "\n");
        }
    }

    // goi ham lay tat ca danh muc
    public static void xem_danhmuc(JTextArea hienthi) {
        try {
            String rmiHost = chucnang3.PhanLuong.laLocal() ? "localhost" : CauHinh.SERVER_IP;
            Registry quanly_rmi = LocateRegistry.getRegistry(rmiHost, 1099);
            dich_vu_rmi dichvu = (dich_vu_rmi) quanly_rmi.lookup("dichvurmi");
            
            String ketqua = dichvu.quanly_danhmuc("laytat", "");
            if (ketqua == null || ketqua.trim().isEmpty()) {
                hienthi.append("danh sach danh muc trong.\n");
            } else {
                hienthi.append("--- DANH SACH DANH MUC ---\n");
                for (String dm : ketqua.split(";;")) {
                    hienthi.append("  * " + dm + "\n");
                }
            }
        } catch (Exception loi) {
            hienthi.append("loi goi rmi: " + loi.getMessage() + "\n");
        }
    }

    // goi ham lay tat ca tag
    public static void xem_tag(JTextArea hienthi) {
        try {
            String rmiHost = chucnang3.PhanLuong.laLocal() ? "localhost" : CauHinh.SERVER_IP;
            Registry quanly_rmi = LocateRegistry.getRegistry(rmiHost, 1099);
            dich_vu_rmi dichvu = (dich_vu_rmi) quanly_rmi.lookup("dichvurmi");
            
            String ketqua = dichvu.quanly_tag("laytat", "");
            if (ketqua == null || ketqua.trim().isEmpty()) {
                hienthi.append("danh sach tag goi y trong.\n");
            } else {
                hienthi.append("--- DANH SACH TAG GOI Y ---\n");
                for (String t : ketqua.split(";;")) {
                    hienthi.append("  # " + t + "\n");
                }
            }
        } catch (Exception loi) {
            hienthi.append("loi goi rmi: " + loi.getMessage() + "\n");
        }
    }
}
