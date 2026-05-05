package client_ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import javax.swing.JTextArea;

// lop xu ly ket noi tcp voi may chu
public class ket_noi_tcp {
    
    // ham tim kiem tai lieu tren may chu
    public static void tim_kiem(String tukhoa, JTextArea hienthi) {
        try {
            Socket mang = new Socket("localhost", 8888);
            DataOutputStream gui_di = new DataOutputStream(mang.getOutputStream());
            DataInputStream nhan_ve = new DataInputStream(mang.getInputStream());
            
            // gui lenh tim kiem kem tu khoa
            gui_di.writeUTF("timkiem|" + tukhoa);
            
            // nhan ket qua tra ve
            String ketqua = nhan_ve.readUTF();
            hienthi.append("Kết quả tìm kiếm: " + ketqua + "\n");
            
            mang.close();
        } catch (Exception loi) {
            hienthi.append("Lỗi kết nối tcp: " + loi.getMessage() + "\n");
        }
    }
    
    // ham tim kiem tra ve mang de hien thi tren danh sach
    public static String[] tim_kiem_mang(String tukhoa) {
        try {
            Socket mang = new Socket("localhost", 8888);
            DataOutputStream gui_di = new DataOutputStream(mang.getOutputStream());
            DataInputStream nhan_ve = new DataInputStream(mang.getInputStream());
            
            // gui lenh tim kiem
            gui_di.writeUTF("timkiem|" + tukhoa);
            
            // nhan ket qua
            String ketqua = nhan_ve.readUTF();
            mang.close();
            
            if (ketqua != null && !ketqua.equals("khong tim thay file") && !ketqua.isEmpty()) {
                // may chu tra ve cac file cach nhau bang dau cham phay kep
                return ketqua.split(";;");
            }
        } catch (Exception loi) {
            System.out.println("lỗi kết nối tcp tìm kiếm: " + loi.getMessage());
        }
        return new String[0];
    }
    
    // ham tai file len may chu
    public static void tai_len(File file_goc, JTextArea hienthi) {
        try {
            Socket mang = new Socket("localhost", 8888);
            DataOutputStream gui_di = new DataOutputStream(mang.getOutputStream());
            DataInputStream nhan_ve = new DataInputStream(mang.getInputStream());
            
            String tenfile = file_goc.getName();
            long dungluong = file_goc.length();
            
            // gui lenh tai len kem ten file va dung luong
            gui_di.writeUTF("tailen|" + tenfile + "|" + dungluong);
            
            // truyen tai du lieu thuc te
            chucnang.truyen_tai_file.gui_file(gui_di, file_goc);
            
            // nhan ket qua
            String ketqua = nhan_ve.readUTF();
            hienthi.append("Trạng thái tải lên: " + ketqua + "\n");
            
            mang.close();
        } catch (Exception loi) {
            hienthi.append("Lỗi tải lên: " + loi.getMessage() + "\n");
        }
    }
    
    // ham tai file xuong
    public static void tai_xuong(String tenfile, JTextArea hienthi) {
        try {
            Socket mang = new Socket("localhost", 8888);
            DataOutputStream gui_di = new DataOutputStream(mang.getOutputStream());
            DataInputStream nhan_ve = new DataInputStream(mang.getInputStream());
            
            // gui lenh tai xuong kem ten file
            gui_di.writeUTF("taixuong|" + tenfile);
            
            // nhan phan hoi tu server
            String phan_hoi = nhan_ve.readUTF();
            
            if (phan_hoi.startsWith("ok|")) {
                long dungluong = Long.parseLong(phan_hoi.split("\\|")[1]);
                File file_dich = new File("src/luutru/download/" + tenfile);
                
                // hung du lieu file va ghi vao thu muc download
                chucnang.truyen_tai_file.nhan_file(nhan_ve, file_dich, dungluong);
                
                hienthi.append("Tải xuống hoàn tất: " + tenfile + " (" + dungluong + " bytes)\n");
            } else {
                hienthi.append("Lỗi tải xuống: " + phan_hoi + "\n");
            }
            
            mang.close();
        } catch (Exception loi) {
            hienthi.append("Lỗi tải xuống: " + loi.getMessage() + "\n");
        }
    }
}
