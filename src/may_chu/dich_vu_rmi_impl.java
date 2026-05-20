package may_chu;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 * Lop thuc thi cac chuc nang RMI.
 * Du lieu danh muc, tag va luot tai duoc luu vinh vien trong MongoDB
 * thay vi luu tam thoi tren RAM.
 */
public class dich_vu_rmi_impl extends UnicastRemoteObject implements dich_vu_rmi {

    public dich_vu_rmi_impl() throws RemoteException {
        super(1099); // Ghim cong 1099 cố định để không bị Firewall/Docker chặn cổng ngẫu nhiên
    }

    // =========================================================
    // QUAN LY DANH MUC
    // =========================================================
    @Override
    public String quanly_danhmuc(String hanhdong, String tendanhmuc) throws RemoteException {
        try {
            MongoCollection<Document> col = MongoKetNoi.layDanhMuc();
            if (hanhdong.equals("them")) {
                // Kiem tra trung lap truoc khi them
                Document ton_tai = col.find(Filters.eq("ten", tendanhmuc)).first();
                if (ton_tai != null) {
                    return "danh muc da ton tai: " + tendanhmuc;
                }
                col.insertOne(new Document("ten", tendanhmuc));
                return "da them danh muc: " + tendanhmuc;
            } else if (hanhdong.equals("laytat")) {
                // Lay toan bo danh muc de hien thi
                List<String> ds = new ArrayList<>();
                for (Document doc : col.find()) {
                    ds.add(doc.getString("ten"));
                }
                return String.join(";;", ds);
            }
        } catch (Exception loi) {
            System.out.println("[MongoDB] loi quanly_danhmuc: " + loi.getMessage());
            return "loi: " + loi.getMessage();
        }
        return "hanh dong khong hop le";
    }

    // =========================================================
    // QUAN LY TAG
    // =========================================================
    @Override
    public String quanly_tag(String hanhdong, String tentag) throws RemoteException {
        try {
            MongoCollection<Document> col = MongoKetNoi.layTag();
            if (hanhdong.equals("them")) {
                Document ton_tai = col.find(Filters.eq("ten", tentag)).first();
                if (ton_tai != null) {
                    return "tag da ton tai: " + tentag;
                }
                col.insertOne(new Document("ten", tentag));
                return "da them tag: " + tentag;
            } else if (hanhdong.equals("laytat")) {
                List<String> ds = new ArrayList<>();
                for (Document doc : col.find()) {
                    ds.add(doc.getString("ten"));
                }
                return String.join(";;", ds);
            }
        } catch (Exception loi) {
            System.out.println("[MongoDB] loi quanly_tag: " + loi.getMessage());
            return "loi: " + loi.getMessage();
        }
        return "hanh dong khong hop le";
    }

    // =========================================================
    // THONG KE LUOT TAI
    // =========================================================
    @Override
    public String thongke_luottai() throws RemoteException {
        try {
            MongoCollection<Document> col = MongoKetNoi.layTaiLieu();
            // Tinh tong luot tai cua toan bo tai lieu
            long tongLuotTai = 0;
            StringBuilder sb = new StringBuilder();
            sb.append("=== THONG KE LUOT TAI TOAN HE THONG ===\n");
            for (Document doc : col.find().sort(new Document("luot_tai", -1))) {
                long luot = doc.get("luot_tai", 0L);
                tongLuotTai += luot;
                sb.append("  - ").append(doc.getString("ten_file"))
                  .append(": ").append(luot).append(" luot\n");
            }
            sb.append("TONG CONG: ").append(tongLuotTai).append(" luot tai");
            return sb.toString();
        } catch (Exception loi) {
            return "loi doc thong ke: " + loi.getMessage();
        }
    }

    // =========================================================
    // HAM HO TRO: Tang luot tai cho mot file cu the
    // Duoc goi tu may_chu_tcp sau khi client tai xuong thanh cong
    // =========================================================
    public void tang_luottai(String tenfile) {
        try {
            MongoKetNoi.layTaiLieu().updateOne(
                Filters.eq("ten_file", tenfile),
                Updates.inc("luot_tai", 1L)
            );
            System.out.println("[MongoDB] Tang luot tai: " + tenfile);
        } catch (Exception loi) {
            System.out.println("[MongoDB] loi tang luot tai: " + loi.getMessage());
        }
    }

    // Giu lai chu ky cu (khong tham so) de khong lam loi code cu dau than
    public void tang_luottai() {
        // Goi an khi khong biet ten file - chi tang tong chung
        System.out.println("[MongoDB] tang_luottai() duoc goi ma khong biet ten file.");
    }

    // =========================================================
    // DANH GIA TAI LIEU
    // =========================================================
    @Override
    public String danhgia_tailieu(String tenfile, int soSao) throws java.rmi.RemoteException {
        try {
            MongoKetNoi.layTaiLieu().updateOne(
                Filters.eq("ten_file", tenfile),
                Updates.combine(
                    Updates.inc("tong_diem_danh_gia", (long) soSao),
                    Updates.inc("so_luot_danh_gia", 1L)
                )
            );
            System.out.println("[MongoDB] Da luu danh gia " + soSao + " sao cho: " + tenfile);
            
            // Phat UDP broadcast de thong bao cap nhat realtime cho cac client
            may_chu_udp.phat_thongbao("RATING|" + tenfile);
            
            return "Đánh giá " + soSao + " sao thành công cho tài liệu \"" + tenfile + "\"";
        } catch (Exception loi) {
            System.out.println("[MongoDB] loi danhgia_tailieu: " + loi.getMessage());
            return "Lỗi: " + loi.getMessage();
        }
    }
}
