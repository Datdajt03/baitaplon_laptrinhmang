package chucnang;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// lop xu ly truyen tai file nhi phan qua mang (pdf, ppt, excel...)
public class truyen_tai_file {

    // ham doc tu file o cung va day len mang
    public static void gui_file(DataOutputStream mang_ra, File file_goc) throws Exception {
        FileInputStream doc_file = new FileInputStream(file_goc);
        byte[] bo_dem = new byte[4096];
        int byte_doc_duoc;
        
        while ((byte_doc_duoc = doc_file.read(bo_dem)) != -1) {
            mang_ra.write(bo_dem, 0, byte_doc_duoc);
        }
        
        mang_ra.flush();
        doc_file.close();
    }

    // ham hung byte tren mang va ghi xuong o cung
    public static void nhan_file(DataInputStream mang_vao, File file_dich, long dungluong) throws Exception {
        FileOutputStream ghi_file = new FileOutputStream(file_dich);
        byte[] bo_dem = new byte[4096];
        long da_nhan = 0;
        int byte_doc_duoc;
        
        while (da_nhan < dungluong && (byte_doc_duoc = mang_vao.read(bo_dem, 0, (int) Math.min(bo_dem.length, dungluong - da_nhan))) != -1) {
            ghi_file.write(bo_dem, 0, byte_doc_duoc);
            da_nhan += byte_doc_duoc;
        }
        
        ghi_file.flush();
        ghi_file.close();
    }
}
