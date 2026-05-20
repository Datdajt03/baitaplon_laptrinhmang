package may_chu;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;

/**
 * May chu xu ly yeu cau TCP (port 8888).
 *
 * Giao thuc truyen lenh (phan tach bang '|'):
 *   timkiem | tukhoa
 *   tailen  | tenfile | dungluong | danhmuc | tags
 *   taixuong| tenfile
 */
public class may_chu_tcp extends Thread {

    private int cong_ket_noi;
    private dich_vu_rmi_impl dichvu_rmi;

    public may_chu_tcp(int cong, dich_vu_rmi_impl dichvu) {
        this.cong_ket_noi = cong;
        this.dichvu_rmi   = dichvu;
    }

    @Override
    public void run() {
        try {
            ServerSocket maychu = new ServerSocket(cong_ket_noi);
            System.out.println("[TCP] May chu TCP dang chay o cong " + cong_ket_noi);
            while (true) {
                Socket khachhang = maychu.accept();
                new luong_xu_ly_tcp(khachhang, dichvu_rmi).start();
            }
        } catch (Exception loi) {
            System.out.println("[TCP] Loi may chu tcp: " + loi.getMessage());
        }
    }
}

// Luong xu ly cho tung khach hang
class luong_xu_ly_tcp extends Thread {

    private Socket mang;
    private dich_vu_rmi_impl dichvu_rmi;

    public luong_xu_ly_tcp(Socket mang, dich_vu_rmi_impl dichvu) {
        this.mang       = mang;
        this.dichvu_rmi = dichvu;
    }

    @Override
    public void run() {
        try {
            DataInputStream  nhan_ve = new DataInputStream(mang.getInputStream());
            DataOutputStream gui_di  = new DataOutputStream(mang.getOutputStream());

            String yeucau = nhan_ve.readUTF();
            if (yeucau == null) { mang.close(); return; }

            String[] phan_tach = yeucau.split("\\|");
            String hanhdong    = phan_tach[0];
            String thamso      = phan_tach.length > 1 ? phan_tach[1] : "";
            String thumuc_upload = "src/luutru/upload/";

            // --------------------------------------------------
            // TIM KIEM: tra ve tu MongoDB
            // --------------------------------------------------
            if (hanhdong.equals("timkiem")) {
                List<String> ketqua = new ArrayList<>();
                MongoCollection<Document> col = MongoKetNoi.layTaiLieu();

                // Tim tat ca neu tu khoa trong, hoac loc theo ten file
                for (Document doc : col.find()) {
                    String tenFile = doc.getString("ten_file");
                    if (tenFile != null && tenFile.contains(thamso)) {
                        long kichThuoc = 0;
                        Object ktObj = doc.get("kich_thuoc");
                        if (ktObj instanceof Number) {
                            kichThuoc = ((Number) ktObj).longValue();
                        }

                        String danhmuc = doc.getString("danh_muc");
                        if (danhmuc == null) danhmuc = "Khac";

                        List<String> tagsList = doc.getList("tags", String.class);
                        String tags = (tagsList != null && !tagsList.isEmpty()) ? String.join(", ", tagsList) : "";

                        long luotTai = 0;
                        Object ltObj = doc.get("luot_tai");
                        if (ltObj instanceof Number) {
                            luotTai = ((Number) ltObj).longValue();
                        }

                        String info = tenFile + "|" + kichThuoc + "|" + danhmuc + "|" + tags + "|" + luotTai;
                        ketqua.add(info);
                    }
                }

                if (ketqua.isEmpty()) {
                    gui_di.writeUTF("khong tim thay file");
                } else {
                    gui_di.writeUTF(String.join(";;", ketqua));
                }
            }
            
            // --------------------------------------------------
            // LAY TAT CA DANH MUC TU MONGODB (De tranh loi RMI NAT qua Docker)
            // --------------------------------------------------
            else if (hanhdong.equals("laytat_danhmuc")) {
                List<String> ds = new ArrayList<>();
                for (Document doc : MongoKetNoi.layDanhMuc().find()) {
                    ds.add(doc.getString("ten"));
                }
                gui_di.writeUTF(String.join(";;", ds));
            }

            // --------------------------------------------------
            // LAY TAT CA TAG TU MONGODB (De tranh loi RMI NAT qua Docker)
            // --------------------------------------------------
            else if (hanhdong.equals("laytat_tag")) {
                List<String> ds = new ArrayList<>();
                for (Document doc : MongoKetNoi.layTag().find()) {
                    ds.add(doc.getString("ten"));
                }
                gui_di.writeUTF(String.join(";;", ds));
            }

            // --------------------------------------------------
            // TAI LEN: luu file + ghi metadata vao MongoDB
            // phan_tach: [0]=tailen [1]=tenfile [2]=dungluong [3]=danhmuc [4]=tags
            // --------------------------------------------------
            else if (hanhdong.equals("tailen")) {
                String tenfile  = phan_tach[1];
                long dungluong  = Long.parseLong(phan_tach[2]);
                String danhmuc  = phan_tach.length > 3 ? phan_tach[3] : "Khac";
                String tags_raw = phan_tach.length > 4 ? phan_tach[4] : "";

                // Bao ve server: tu choi neu file vuot qua 100 MB
                final long GIOI_HAN_SERVER = 100L * 1024 * 1024;
                if (dungluong > GIOI_HAN_SERVER) {
                    // Van phai doc het luong du lieu client da gui de giai phong socket
                    // (client gui file truoc khi doc phan hoi)
                    // Nhung vi ket noi TCP, ta dong luon va client se bi loi doc -> bao hieu tu choi
                    gui_di.writeUTF("loi: File vuot qua gioi han 100 MB (" + (dungluong / 1024 / 1024) + " MB). Server tu choi.");
                    System.out.println("[TCP] Tu choi file " + tenfile + " - dung luong: " + (dungluong / 1024 / 1024) + " MB > 100 MB");
                    mang.close();
                    return;
                }

                // 1. Luu file vat ly
                File file_moi = new File(thumuc_upload + tenfile);
                chucnang.truyen_tai_file.nhan_file(nhan_ve, file_moi, dungluong);

                // 2. Xu ly danh sach tag
                List<String> danhsach_tag = new ArrayList<>();
                if (!tags_raw.trim().isEmpty()) {
                    for (String t : tags_raw.split(",")) {
                        String trimmed = t.trim();
                        if (!trimmed.isEmpty()) danhsach_tag.add(trimmed);
                    }
                }

                // 3. Ghi metadata vao MongoDB
                Document metadata = new Document()
                        .append("ten_file",       tenfile)
                        .append("kich_thuoc",      dungluong)
                        .append("danh_muc",        danhmuc)
                        .append("tags",            danhsach_tag)
                        .append("luot_tai",        0L)
                        .append("ip_nguoi_gui",    mang.getInetAddress().getHostAddress())
                        .append("ngay_upload",     new Date().toString());

                // Neu file da ton tai: xoa ban ghi cu, luu ban ghi moi
                MongoCollection<Document> col = MongoKetNoi.layTaiLieu();
                col.deleteMany(Filters.eq("ten_file", tenfile));
                col.insertOne(metadata);

                System.out.println("[MongoDB] Da luu metadata: " + tenfile
                        + " | danh muc: " + danhmuc + " | tags: " + danhsach_tag);

                // 4. Phan hoi cho Client
                gui_di.writeUTF("tai len hoan tat: " + tenfile);

                // 5. Thong bao qua UDP
                may_chu_udp.phat_thongbao(tenfile);
            }

            // --------------------------------------------------
            // TAI XUONG: day file + tang luot tai trong MongoDB
            // --------------------------------------------------
            else if (hanhdong.equals("taixuong")) {
                File file_can_tai = new File(thumuc_upload + thamso);
                if (file_can_tai.exists()) {
                    long dungluong = file_can_tai.length();
                    gui_di.writeUTF("ok|" + dungluong);

                    // Tang luot tai trong MongoDB theo ten file cu the truoc de dam bao dong bo realtime tuc thi
                    dichvu_rmi.tang_luottai(thamso);

                    chucnang.truyen_tai_file.gui_file(gui_di, file_can_tai);
                } else {
                    gui_di.writeUTF("file khong ton tai");
                }
            }

            mang.close();
        } catch (Exception loi) {
            System.out.println("[TCP] Loi xu ly khach: " + loi.getMessage());
        }
    }
}
