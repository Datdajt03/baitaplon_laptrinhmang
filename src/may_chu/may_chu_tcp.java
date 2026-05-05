package may_chu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

// may chu xu ly yeu cau tcp
public class may_chu_tcp extends Thread {
    
    private int cong_ket_noi;
    private dich_vu_rmi_impl dichvu_rmi;
    
    public may_chu_tcp(int cong, dich_vu_rmi_impl dichvu) {
        this.cong_ket_noi = cong;
        this.dichvu_rmi = dichvu;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket maychu = new ServerSocket(cong_ket_noi);
            System.out.println("may chu tcp dang chay o cong " + cong_ket_noi);
            
            while (true) {
                Socket khachhang = maychu.accept();
                new luong_xu_ly_tcp(khachhang, dichvu_rmi).start();
            }
        } catch (Exception loi) {
            System.out.println("loi may chu tcp: " + loi.getMessage());
        }
    }
}

// luong xu ly cho tung khach hang
class luong_xu_ly_tcp extends Thread {
    private Socket mang;
    private dich_vu_rmi_impl dichvu_rmi;
    
    public luong_xu_ly_tcp(Socket mang, dich_vu_rmi_impl dichvu) {
        this.mang = mang;
        this.dichvu_rmi = dichvu;
    }
    
    @Override
    public void run() {
        try {
            DataInputStream nhan_ve = new DataInputStream(mang.getInputStream());
            DataOutputStream gui_di = new DataOutputStream(mang.getOutputStream());
            
            String yeucau = nhan_ve.readUTF();
            if (yeucau != null) {
                String[] phan_tach = yeucau.split("\\|");
                String hanhdong = phan_tach[0];
                String thamso = phan_tach.length > 1 ? phan_tach[1] : "";
                
                String thumuc_upload = "src/luutru/upload/";
                
                if (hanhdong.equals("timkiem")) {
                    File luutru = new File(thumuc_upload);
                    File[] danhsach = luutru.listFiles();
                    String ketqua = "";
                    if (danhsach != null) {
                        for (File f : danhsach) {
                            if (f.getName().contains(thamso)) {
                                ketqua += f.getName() + ";;";
                            }
                        }
                    }
                    if (ketqua.isEmpty()) ketqua = "khong tim thay file";
                    gui_di.writeUTF(ketqua);
                } 
                else if (hanhdong.equals("tailen")) {
                    long dungluong = Long.parseLong(phan_tach[2]);
                    File file_moi = new File(thumuc_upload + thamso);
                    
                    // hung du lieu file tu client gui len
                    chucnang.truyen_tai_file.nhan_file(nhan_ve, file_moi, dungluong);
                    
                    gui_di.writeUTF("tải lên hoàn tất: " + thamso);
                    
                    // goi thong bao qua udp cho tat ca nguoi dung
                    may_chu_udp.phat_thongbao(thamso);
                }
                else if (hanhdong.equals("taixuong")) {
                    File file_can_tai = new File(thumuc_upload + thamso);
                    if (file_can_tai.exists()) {
                        long dungluong = file_can_tai.length();
                        gui_di.writeUTF("ok|" + dungluong);
                        
                        // day du lieu file ve cho client
                        chucnang.truyen_tai_file.gui_file(gui_di, file_can_tai);
                        
                        dichvu_rmi.tang_luottai(); // cap nhat luot tai
                    } else {
                        gui_di.writeUTF("file khong ton tai");
                    }
                }
            }
            mang.close();
        } catch (Exception loi) {
            System.out.println("loi xu ly khach: " + loi.getMessage());
        }
    }
}
