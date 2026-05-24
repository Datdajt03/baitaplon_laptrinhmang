package chucnang3;

import client_ui.CauHinh;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Ket noi TCP danh rieng cho che do LOCAL.
 * Luon ket noi toi localhost:8888, khong bao gio thu ket noi LAN.
 *
 * Giao thuc lenh giong voi ket_noi_tcp goc (phan cach bang '|'):
 *   timkiem | tukhoa
 *   tailen  | tenfile | dungluong | danhmuc | tags
 *   taixuong| tenfile
 */
public class KetNoiLocal {

    private static final String LOCAL_IP = "localhost";
    private static final int    LOCAL_PORT = 8888;

    // Gioi han kich thuoc file toi da cho phep upload: 100 MB
    private static final long GIOI_HAN_BYTES = 100L * 1024 * 1024;

    /**
     * Tim kiem tai lieu - hien thi vao JTextArea.
     */
    public static void tim_kiem(String tukhoa, JTextArea hienthi) {
        try {
            Socket mang = new Socket(LOCAL_IP, LOCAL_PORT);
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());

            gui_di.writeUTF("timkiem|" + tukhoa);
            String ketqua = nhan_ve.readUTF();
            hienthi.append("Ket qua tim kiem (local): " + ketqua + "\n");
            mang.close();
        } catch (Exception loi) {
            hienthi.append("Loi ket noi local: " + loi.getMessage() + "\n");
        }
    }

    /**
     * Tim kiem tra ve mang String de hien thi tren danh sach UI.
     */
    public static String[] tim_kiem_mang(String tukhoa) {
        try {
            Socket mang = new Socket(LOCAL_IP, LOCAL_PORT);
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());

            gui_di.writeUTF("timkiem|" + tukhoa);
            String ketqua = nhan_ve.readUTF();
            mang.close();

            if (ketqua != null && !ketqua.equals("khong tim thay file") && !ketqua.isEmpty()) {
                return ketqua.split(";;");
            }
        } catch (Exception loi) {
            System.out.println("[LOCAL] Loi tim kiem: " + loi.getMessage());
        }
        return new String[0];
    }

    /**
     * Tai file len may chu local (voi danh muc mac dinh).
     */
    public static void tai_len(File file_goc, JTextArea hienthi) {
        tai_len(file_goc, hienthi, "Khac", "");
    }

    /**
     * Tai file len may chu local - day du thong tin danh muc va tags.
     */
    public static void tai_len(File file_goc, JTextArea hienthi, String danhmuc, String tags) {
        // Kiem tra kich thuoc file truoc khi gui
        if (file_goc.length() > GIOI_HAN_BYTES) {
            long mb = file_goc.length() / (1024 * 1024);
            String thongbao = "File \"" + file_goc.getName() + "\" co dung luong " + mb
                    + " MB, vuot qua gioi han 100 MB cho phep.\n"
                    + "Vui long chon file nho hon.";
            JOptionPane.showMessageDialog(null, thongbao, "File Qua Lon",
                    JOptionPane.WARNING_MESSAGE);
            hienthi.append("[LOI] File " + file_goc.getName() + " (" + mb + " MB) vuot gioi han 100 MB, huy tai len.\n");
            return;
        }

        try {
            Socket mang = new Socket(LOCAL_IP, LOCAL_PORT);
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());

            String tenfile   = file_goc.getName();
            long   dungluong = file_goc.length();

            // Lenh: tailen | tenfile | dungluong | danhmuc | tags
            gui_di.writeUTF("tailen|" + tenfile + "|" + dungluong + "|" + danhmuc + "|" + tags);

            // Truyen du lieu thuc te
            chucnang.truyen_tai_file.gui_file(gui_di, file_goc);

            String ketqua = nhan_ve.readUTF();
            hienthi.append("Tai len (local): " + ketqua + "\n");
            mang.close();
        } catch (Exception loi) {
            hienthi.append("Loi tai len local: " + loi.getMessage() + "\n");
        }
    }

    /**
     * Tai file xuong tu may chu local.
     */
    public static void tai_xuong(String tenfile, JTextArea hienthi) {
        try {
            Socket mang = new Socket(LOCAL_IP, LOCAL_PORT);
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());

            gui_di.writeUTF("taixuong|" + tenfile);
            String phan_hoi = nhan_ve.readUTF();

            if (phan_hoi.startsWith("ok|")) {
                long dungluong = Long.parseLong(phan_hoi.split("\\|")[1]);
                File file_dich = chucnang.truyen_tai_file.lay_file_dich_duy_nhat(tenfile);
                chucnang.truyen_tai_file.nhan_file(nhan_ve, file_dich, dungluong);
                hienthi.append("Tai xuong (local) hoan tat: " + file_dich.getName() + " (" + dungluong + " bytes)\n");
                client_ui.client_ui.them_hoat_dong("Tải xuống (local) hoàn tất: " + file_dich.getName());
            } else {
                hienthi.append("Loi tai xuong: " + phan_hoi + "\n");
                client_ui.client_ui.them_hoat_dong("Lỗi tải xuống local: " + phan_hoi);
            }
            mang.close();
        } catch (Exception loi) {
            hienthi.append("Loi tai xuong local: " + loi.getMessage() + "\n");
            client_ui.client_ui.them_hoat_dong("Lỗi tải xuống local: " + loi.getMessage());
        }
    }

    public static String laytat_danhmuc() {
        try {
            Socket mang = new Socket(LOCAL_IP, LOCAL_PORT);
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());

            gui_di.writeUTF("laytat_danhmuc");
            String ketqua = nhan_ve.readUTF();
            mang.close();
            return ketqua;
        } catch (Exception loi) {
            System.out.println("[LOCAL] Loi laytat_danhmuc: " + loi.getMessage());
        }
        return "";
    }

    public static String laytat_tag() {
        try {
            Socket mang = new Socket(LOCAL_IP, LOCAL_PORT);
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());

            gui_di.writeUTF("laytat_tag");
            String ketqua = nhan_ve.readUTF();
            mang.close();
            return ketqua;
        } catch (Exception loi) {
            System.out.println("[LOCAL] Loi laytat_tag: " + loi.getMessage());
        }
        return "";
    }

    // Ham xoa tai lieu vinh vien (Local)
    public static String xoa_tailieu(String tenfile) {
        try {
            Socket mang = new Socket(LOCAL_IP, LOCAL_PORT);
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());

            gui_di.writeUTF("xoatailieu|" + tenfile);
            String phan_hoi = nhan_ve.readUTF();
            mang.close();
            return phan_hoi;
        } catch (Exception loi) {
            return "loi: " + loi.getMessage();
        }
    }
}
