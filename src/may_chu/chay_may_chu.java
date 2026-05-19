package may_chu;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Lop chinh de chay cac dich vu may chu.
 * Thu tu khoi dong:
 *   1. Ket noi MongoDB
 *   2. Dang ky RMI
 *   3. Chay TCP (port 8888)
 *   4. Hook tat server sach se
 */
public class chay_may_chu {

    public static void main(String[] args) {
        try {
            // 0. Tao thu muc luu tru neu chua co
            java.io.File thumuc_upload = new java.io.File("src/luutru/upload");
            if (!thumuc_upload.exists()) thumuc_upload.mkdirs();
            java.io.File thumuc_download = new java.io.File("src/luutru/download");
            if (!thumuc_download.exists()) thumuc_download.mkdirs();

            // 1. Khoi dong MongoDB
            MongoKetNoi.khoiDong();

            // 2. Khoi dong RMI
            LocateRegistry.createRegistry(1099);
            Registry quanly_rmi = LocateRegistry.getRegistry(1099);
            dich_vu_rmi_impl thuc_thi = new dich_vu_rmi_impl();
            quanly_rmi.rebind("dichvurmi", thuc_thi);
            System.out.println("[RMI] May chu RMI dang chay o cong 1099...");

            // 3. Khoi dong TCP
            may_chu_tcp tcp = new may_chu_tcp(8888, thuc_thi);
            tcp.start();

            System.out.println("[HE THONG] May chu san sang! TCP:8888 | RMI:1099 | UDP:9999");

            // 4. Hook dong MongoDB khi tat Server
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[HE THONG] Dang tat server...");
                MongoKetNoi.dongKetNoi();
            }));

        } catch (Exception loi) {
            System.out.println("[HE THONG] Loi khoi dong: " + loi.getMessage());
            loi.printStackTrace();
        }
    }
}
