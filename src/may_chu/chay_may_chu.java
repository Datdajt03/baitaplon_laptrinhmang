package may_chu;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// lop chinh de chay cac dich vu may chu
public class chay_may_chu {
    
    public static void main(String[] args) {
        try {
            // tao thu muc luu tru neu chua co
            java.io.File thumuc_upload = new java.io.File("src/luutru/upload");
            if (!thumuc_upload.exists()) {
                thumuc_upload.mkdirs();
            }
            java.io.File thumuc_download = new java.io.File("src/luutru/download");
            if (!thumuc_download.exists()) {
                thumuc_download.mkdirs();
            }
            
            // 1. khoi dong rmi
            LocateRegistry.createRegistry(1099);
            Registry quanly_rmi = LocateRegistry.getRegistry(1099);
            dich_vu_rmi_impl thuc_thi = new dich_vu_rmi_impl();
            quanly_rmi.rebind("dichvurmi", thuc_thi);
            System.out.println("may chu rmi dang chay...");
            
            // 2. khoi dong tcp (va truyen rmi vao de ho tro cap nhat luot tai)
            may_chu_tcp tcp = new may_chu_tcp(8888, thuc_thi);
            tcp.start();
            
            System.out.println("he thong may chu san sang!");
            
        } catch (Exception loi) {
            System.out.println("loi khoi dong he thong: " + loi.getMessage());
        }
    }
}
