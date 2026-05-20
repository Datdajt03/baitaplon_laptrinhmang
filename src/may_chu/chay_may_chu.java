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

            // Doc tu file .env hoac environment variable de cau hinh java.rmi.server.hostname
            String rmiHostname = System.getenv("RMI_HOSTNAME");
            if (rmiHostname == null || rmiHostname.trim().isEmpty()) {
                java.io.File envFile = new java.io.File(".env");
                if (envFile.exists()) {
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.startsWith("RMI_HOSTNAME=")) {
                                rmiHostname = line.substring("RMI_HOSTNAME=".length()).trim();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[RMI] Khong the doc file .env de cau hinh hostname: " + e.getMessage());
                    }
                }
            }
            if (rmiHostname != null && !rmiHostname.trim().isEmpty()) {
                // Kiem tra xem IP nay co dang hoat dong tren may nay khong (tranh truong hop tat Radmin VPN gay loi RMI)
                boolean ipHopLe = false;
                try {
                    java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                        java.net.NetworkInterface iface = interfaces.nextElement();
                        java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            java.net.InetAddress addr = addresses.nextElement();
                            if (addr.getHostAddress().equals(rmiHostname.trim())) {
                                ipHopLe = true;
                                break;
                            }
                        }
                        if (ipHopLe) break;
                    }
                } catch (Exception ignored) {}

                boolean isDocker = new java.io.File("/.dockerenv").exists();
                if (ipHopLe || isDocker) {
                    System.setProperty("java.rmi.server.hostname", rmiHostname.trim());
                    System.out.println("[RMI] Da dat java.rmi.server.hostname = " + rmiHostname.trim() + (isDocker ? " (Docker Mode)" : ""));
                } else {
                    System.out.println("[RMI] CANH BAO: IP RMI \"" + rmiHostname.trim() + "\" khong hoat dong tren may chu nay.");
                    System.out.println("[RMI] (Co the do Radmin VPN dang tat hoac day la may khac).");
                    System.setProperty("java.rmi.server.hostname", "localhost");
                    System.out.println("[RMI] -> Tu dong chuyen (Fallback) java.rmi.server.hostname = localhost de kiem tra noi bo.");
                }
            } else {
                System.setProperty("java.rmi.server.hostname", "localhost");
                System.out.println("[RMI] Mac dinh java.rmi.server.hostname = localhost");
            }

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
