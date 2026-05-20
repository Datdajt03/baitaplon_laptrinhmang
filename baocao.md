# Báo Cáo: Hệ Thống Thư Viện Tài Liệu Môn Học
**Nhóm 7 — Lập Trình Mạng**

---

## 1. Tổng Quan Hệ Thống

Hệ thống **Thư Viện Tài Liệu Môn Học** là ứng dụng phân tán Client–Server được viết bằng Java, cho phép nhiều người dùng chia sẻ và tải tài liệu học tập qua mạng. Hệ thống tích hợp đồng thời **3 giao thức mạng** (TCP, UDP, RMI) để tối ưu từng loại tác vụ khác nhau.

---

## 2. Cấu Trúc Thư Mục

```
Baitaplon_Nhom7/
│
├── src/                                  # Toàn bộ mã nguồn Java
│   ├── may_chu/                          # Phía Server
│   │   ├── chay_may_chu.java             # Entry point: khởi động toàn bộ hệ thống
│   │   ├── MongoKetNoi.java              # Kết nối MongoDB (Singleton Pattern)
│   │   ├── may_chu_tcp.java              # Xử lý upload/download/tìm kiếm qua TCP
│   │   ├── may_chu_udp.java              # Phát broadcast thông báo qua UDP
│   │   ├── dich_vu_rmi.java              # Interface RMI (định nghĩa các hàm từ xa)
│   │   └── dich_vu_rmi_impl.java        # Cài đặt RMI: quản lý danh mục, tag, thống kê
│   │
│   ├── client_ui/                        # Phía Client
│   │   ├── client_ui.java               # Giao diện chính (Java Swing)
│   │   ├── client_ui.form               # File thiết kế giao diện NetBeans
│   │   ├── CauHinh.java                 # Lưu địa chỉ IP máy chủ
│   │   ├── ket_noi_tcp.java             # Gửi lệnh TCP lên Server
│   │   ├── goi_rmi.java                 # Gọi các hàm RMI từ xa
│   │   ├── nhan_udp.java                # Lắng nghe thông báo UDP
│   │   └── WrapLayout.java              # Layout icon dạng lưới (File Explorer style)
│   │
│   ├── chucnang/                         # Module tiện ích
│   │   ├── truyen_tai_file.java          # Byte streaming: chia/ghép file theo chunk 4KB
│   │   └── giao_dien_phu.java           # Cửa sổ tìm kiếm & tải xuống
│   │
│   ├── chucnang2/                        # Module mở rộng
│   │   └── GiaoDienChonTag.java          # Popup chọn danh mục + tag trước khi upload
│   │
│   ├── thongbao/                         # Module thông báo kết nối
│   │   ├── KiemTraKetNoi.java            # Kiểm tra TCP socket tới Server (timeout 3s)
│   │   └── HopThoaiThongBao.java        # Dialog hiển thị kết quả kết nối khi mở app
│   │
│   ├── resources/                        # Tài nguyên tĩnh
│   │   └── icontl.png                    # Icon đại diện tài liệu (hiển thị trong UI)
│   │
│   └── luutru/                           # Kho lưu trữ file vật lý
│       ├── upload/                       # Tài liệu do Client gửi lên (nằm trên Server)
│       └── download/                     # Tài liệu Client đã tải về (nằm trên máy Client)
│
├── lib/                                  # Thư viện bên thứ 3
│   ├── mongodb-driver-sync-4.11.1.jar
│   ├── mongodb-driver-core-4.11.1.jar
│   └── bson-4.11.1.jar
│
├── picture/                              # Ảnh gốc của dự án
│   └── icontl.png
│
├── Dockerfile                            # Đóng gói Server vào Docker image
├── docker-compose.yml                    # Orchestration: khởi động Server container
├── .env                                  # Biến môi trường: IP Radmin, MongoDB URI
├── build.xml                             # Ant build script (NetBeans)
└── readme.md                             # Hướng dẫn sử dụng
```

---

## 3. Kiến Trúc Kỹ Thuật & Cơ Chế Hoạt Động

### 3.1 Luồng Khởi Động Server

```
chay_may_chu.main()
    │
    ├── 1. Tạo thư mục upload/ download/ nếu chưa có
    ├── 2. MongoKetNoi.khoiDong()     → Kết nối MongoDB (timeout 5s nếu fail vẫn chạy tiếp)
    ├── 3. RMI Registry (port 1099)   → Đăng ký dich_vu_rmi_impl
    ├── 4. may_chu_tcp.start()        → Lắng nghe TCP (port 8888)
    └── 5. ShutdownHook               → Đóng MongoDB sạch sẽ khi tắt Server
```

### 3.2 Giao Thức TCP — Port 8888 (Truyền File Thực Tế)

**Đây là giao thức chủ lực** xử lý 3 tác vụ nặng nhất:

#### Lệnh Upload (`tailen`)
```
Client gửi:  "tailen|BaiTap.pdf|4200000|Bài tập lớn|java, mang"
                         │          │          │            │
                      tên file   kích thước  danh mục    tags

Server xử lý:
  ├── Kiểm tra kích thước > 100MB → từ chối ngay (bảo vệ ổ đĩa)
  ├── Nhận byte streaming → lắp ghép → lưu vào src/luutru/upload/BaiTap.pdf
  ├── Ghi metadata vào MongoDB:
  │     { ten_file, kich_thuoc, danh_muc, tags, luot_tai:0, ip_nguoi_gui, ngay_upload }
  └── Phát UDP broadcast → thông báo cho tất cả Client đang online
```

#### Lệnh Download (`taixuong`)
```
Client gửi:  "taixuong|BaiTap.pdf"

Server xử lý:
  ├── Kiểm tra file tồn tại trên ổ đĩa
  ├── Gửi: "ok|4200000"  (báo kích thước để Client chuẩn bị bộ nhớ)
  ├── Đọc file → đẩy byte streaming theo chunk 4KB
  └── MongoDB: tăng luot_tai của file đó lên +1
```

#### Lệnh Tìm Kiếm (`timkiem`)
```
Client gửi:  "timkiem|java"

Server xử lý:
  └── Query MongoDB: tìm tất cả document có ten_file chứa "java"
      → Trả về: "BaiTapJava.pdf;;LyThuyetJava.docx;;"
```

### 3.3 Cơ Chế Byte Streaming — Bí Quyết Bảo Toàn Định Dạng File 100%

> ❓ **Câu hỏi của người dùng:** *"Nếu máy tôi tải tài liệu từ máy khác up lên nó có trả lại đúng tài liệu đó không? Nó kiểu như là cơ chế byte gì đó hả?"*

**CÂU TRẢ LỜI:** **Có, hoàn toàn chính xác 100%.** Tài liệu tải về sẽ giống hệt file gốc ban đầu đến từng bit, không bị lỗi font, không bị hỏng cấu trúc (corrupt). Bí quyết nằm ở cơ chế **Byte Streaming (luồng byte nhị phân)**.

#### Bản chất kỹ thuật:
1. **Mọi file trên máy tính đều là Byte:** Dù là PDF, DOCX, PNG, ZIP, hay file chạy EXE, bản chất nhị phân của chúng chỉ là một chuỗi các con số byte (giá trị từ `-128` đến `127`).
2. **Không diễn dịch nội dung:** Hệ thống mạng không mở file ra đọc chữ hay phân tích định dạng, mà nó coi file là một chuỗi nhị phân thuần túy. Nó bê nguyên chuỗi số đó từ ổ cứng bên gửi đặt sang ổ cứng bên nhận.
3. **Truyền theo chunk (mảnh nhỏ) 4KB:** 
   * Nếu nạp cả file 100MB vào bộ nhớ (RAM) rồi gửi đi một lúc, máy tính sẽ lập tức bị quá tải RAM (OutOfMemoryError).
   * Thay vào đó, hệ thống sử dụng một bộ đệm **`byte[] buffer = new byte[4096]` (4 Kilobytes)** trong [truyen_tai_file.java](file:///a:/code%20javanetbean/Baitaplon_Nhom7/src/chucnang/truyen_tai_file.java).

```
[Máy Gửi (Đọc File)]                 [Đường Truyền Mạng]                [Máy Nhận (Ghi File)]
FileInputStream (File vật lý) ------> Socket OutputStream (TCP) ------> FileOutputStream (Ghi xuống đĩa)
      │                                       │                                       │
      ├── Đọc 4096 byte ──────────────────────┼───────────────────────────────► Ghi 4096 byte
      ├── Đọc 4096 byte ──────────────────────┼───────────────────────────────► Ghi 4096 byte
      ├── ... (lặp đi lặp lại)                │                                       ...
      └── Đọc 512 byte (mảnh cuối) ───────────┼───────────────────────────────► Ghi 512 byte
                                                                                      │
                                                                           File hoàn toàn khớp 100%
                                                                           (MD5 Hash trùng tuyệt đối)
```

Nhờ cơ chế này, tốc độ truyền đạt hiệu suất cao, bộ nhớ RAM tiêu thụ luôn cố định cực nhỏ (chỉ vài KB), và tính toàn vẹn dữ liệu được đảm bảo tuyệt đối qua giao thức TCP hướng kết nối tin cậy.

### 3.4 Giao Thức UDP — Port 9999 (Thông Báo Realtime)

> **UDP là giao thức "bắn và quên"** — không đảm bảo đến nơi, nhưng cực kỳ nhanh. Phù hợp với thông báo realtime.

```
Khi có file mới upload:
  Server → Broadcast UDP đến 255.255.255.255:9999
                    │
          (gửi đến TẤT CẢ thiết bị trong mạng)
                    │
       ┌────────────┴────────────┐
    Client A                  Client B
  Luồng nhan_udp            Luồng nhan_udp
  (chạy ngầm 24/7)          (chạy ngầm 24/7)
       │                         │
  Hiện thông báo:           Hiện thông báo:
  "Có tài liệu mới: BaiTap.pdf"
```

**Tại sao không dùng TCP cho thông báo?** TCP cần thiết lập kết nối riêng với từng Client (handshake 3 bước). UDP broadcast chỉ cần 1 gói tin → đến tất cả mọi người cùng lúc.

### 3.5 Giao Thức RMI — Port 1099 (Quản Trị Từ Xa & Giải pháp Tự Chữa Lành)

> **RMI (Remote Method Invocation)** cho phép Client gọi hàm Java trên Server như thể gọi hàm bình thường trong cùng một chương trình.

```java
// Client gọi (trong goi_rmi.java):
dich_vu_rmi dichvu = registry.lookup("dichvurmi");
String kq = dichvu.thongke_luottai();  // ← Hàm này chạy trên SERVER!

// Server thực thi (dich_vu_rmi_impl.java):
public String thongke_luottai() throws RemoteException {
    // Query MongoDB → trả kết quả về cho Client
    return "Tổng lượt tải: 150 lượt";
}
```

**Chức năng RMI trong hệ thống:**
* Thêm danh mục tài liệu mới.
* Thêm tag tìm kiếm gợi ý.
* Thống kê tổng lượt tải theo từng file thực tế từ MongoDB.

#### 🩹 Giải Quyết Lỗi RMI VPN & Tường Lửa (Self-Healing Connection):
Trong thực tế phát triển, Java RMI nổi tiếng là giao thức "khó tính" vì:
1. **Lỗi cổng ngẫu nhiên (Ephemeral Ports):** Mặc dù RMI Registry chạy trên cổng `1099`, nhưng khi xuất Stub RMI qua `super()`, JVM tự động mở một cổng ngẫu nhiên khác để truyền dữ liệu. Cổng này ngay lập tức bị Windows Firewall hoặc Docker Container chặn đứng!
   * 👉 *Khắc phục:* Chuyển constructor sang `super(1099)` để ghim toàn bộ luồng RMI chung cổng `1099`.
2. **Lỗi RMI Hostname (Connection Refused):** Khi máy chủ đăng ký RMI bằng IP VPN ảo (ví dụ Radmin `26.18.244.131`), nếu người dùng tắt Radmin hoặc chạy thử nội bộ (Localhost), máy trạm không thể kết nối tới IP này.
   * 👉 *Khắc phục:* Cập nhật logic tự phục hồi tại [chay_may_chu.java](file:///a:/code%20javanetbean/Baitaplon_Nhom7/src/may_chu/chay_may_chu.java). Server tự động quét qua toàn bộ các Card mạng (Network Interfaces) đang mở. Nếu phát hiện IP VPN offline, nó tự động hạ cấp (**fallback RMI hostname sang `localhost`**) để nhà phát triển kiểm thử local trơn tru mà không cần chỉnh cấu hình thủ công.
3. **Giới Hạn Băng Thông Container Docker (Docker Boundary Bypass):** Khi máy chủ (Server) chạy trong môi trường ảo hóa Docker Container, máy ảo Linux Linux của Docker không có quyền truy cập trực tiếp các card mạng của máy chủ vật lý Windows bên ngoài. Do đó, việc quét card mạng vật lý sẽ không thấy IP Radmin VPN (mặc dù cổng 1099 vẫn được port-forward thành công).
   * 👉 *Khắc phục:* Bổ sung cơ chế kiểm tra sự tồn tại của file `/.dockerenv`. Nếu phát hiện đang chạy bên trong Docker Container, máy chủ sẽ tự động tin tưởng gán `java.rmi.server.hostname` theo đúng biến cấu hình `RMI_HOSTNAME` của file `.env` mà không thực hiện hạ cấp về localhost. Điều này giúp các máy trạm ngoài LAN/VPN kết nối vào RMI trơn tru.

### 3.6 MongoDB — Lưu Trữ Metadata Vĩnh Viễn & Mô Hình 3 Lớp (3-Tier)

> ❓ **Câu hỏi của người dùng:** *"Tạo sao hệ thống có thể lưu dữ liệu vào MongoDB của máy chủ nếu Client gửi từ một máy khách khác ở rất xa?"*

**CÂU TRẢ LỜI:** Client **không hề** kết nối trực tiếp đến database MongoDB trên Server. Toàn bộ kiến trúc được xây dựng theo mô hình phân tán **3 lớp (3-Tier Distributed Architecture)**:

```
┌────────────────────────┐              ┌────────────────────────┐              ┌────────────────────────┐
│   LỚP 1: CLIENT APP   │  TCP / RMI   │  LỚP 2: SERVER APP     │   localhost   │   LỚP 3: MONGODB DB    │
│  (Mở ở nhà/lớp học...) ├─────────────►│  (Chạy 24/7 trên Docker)├─────────────►│ (Chạy khép kín bên trong)│
│                        │              │                        │              │  Port: 27020           │
└────────────────────────┘              └────────────────────────┘              └────────────────────────┘
```

1. **Lớp 1 (Client UI):** Người dùng nhập thông tin danh mục, tag hoặc upload tài liệu. Client đóng gói thông tin này và truyền qua cổng **8888 (TCP)** hoặc **1099 (RMI)** thông qua địa chỉ IP Radmin VPN của Server.
   * **[NÂNG CẤP DYNAMIC RMI]:** Popup tải lên tài liệu (`GiaoDienChonTag.java`) không còn sử dụng danh sách tĩnh nữa. Khi khởi tạo, nó tự động thực hiện cuộc gọi RMI đến các hàm `quanly_danhmuc("laytat")` và `quanly_tag("laytat")` trên Server. Lớp 2 (Server App) sẽ truy vấn nhanh toàn bộ danh mục và các thẻ tag gợi ý hiện có trong MongoDB (Lớp 3) để hiển thị trực tiếp lên ComboBox và nút gợi ý trên Client một cách động 100%!
   * Thêm hai nút quản trị mới "Xem danh mục" và "Xem tag gợi ý" trực tiếp trên màn hình chính RMI JTextArea để phục vụ kiểm thử dữ liệu thực tế MongoDB từ xa.
2. **Lớp 2 (Server App):** Server nhận gói tin mạng qua VPN, giải mã dữ liệu, lưu file vật lý vào ổ đĩa. Sau đó, Server đóng vai trò là "người trung gian" tin cậy để đại diện Client thực hiện kết nối cơ sở dữ liệu.
3. **Lớp 3 (MongoDB Database):** Do MongoDB được cấu hình chạy an toàn trên `localhost:27020` của máy chủ (được cô lập bảo mật), Server trung gian ở Lớp 2 dễ dàng ghi bản ghi mới vào cơ sở dữ liệu thông qua MongoDB Java Driver. 

Mô hình 3 lớp này giúp hệ thống đạt độ bảo mật tuyệt đối: MongoDB không bao giờ phải mở cổng ra ngoài Internet công cộng, ngăn chặn hoàn toàn nguy cơ bị hacker tấn công trực diện.

#### Phân chia nhiệm vụ lưu trữ:

| Lưu ở đâu | Lưu cái gì | Lý do |
|---|---|---|
| Ổ cứng Server (`upload/`) | File vật lý (PDF, DOCX...) | File nặng, không nên nhét trực tiếp vào Database để tránh phình dung lượng. |
| MongoDB collection `tai_lieu` | Metadata của file | Lưu thông tin mô tả, tags, kích thước để phục vụ tìm kiếm siêu tốc và thống kê. |
| MongoDB collection `danh_muc` | Danh sách danh mục | Bảo toàn danh sách vĩnh viễn, không mất khi Server tắt/reboot. |
| MongoDB collection `tag` | Danh sách tag gợi ý | Bảo toàn danh sách vĩnh viễn, không mất khi Server tắt/reboot. |

**Cấu trúc document MongoDB khi upload file:**
```json
{
  "_id": "ObjectId(...)",
  "ten_file": "BaiTapLon_Nhom7.pdf",
  "kich_thuoc": 4200000,
  "danh_muc": "Bài tập lớn",
  "tags": ["java", "mang", "rmi"],
  "luot_tai": 42,
  "ip_nguoi_gui": "26.18.244.135",
  "ngay_upload": "Mon May 19 16:45:00 ICT 2026"
}
```

### 3.7 Hệ Thống Giới Hạn 100MB

**Kiểm tra 2 lớp** để bảo vệ cả Client lẫn Server:

```
Người dùng chọn file 150MB
          │
     [Lớp 1 - Client] ket_noi_tcp.java
     150MB > 100MB → Popup cảnh báo ngay
     → DỪNG, không gửi 1 byte nào
          │
     (Nếu bypass lớp 1)
          │
     [Lớp 2 - Server] may_chu_tcp.java
     Đọc dungluong từ header lệnh
     150MB > 100MB → Gửi thông báo lỗi → Đóng socket
```

### 3.8 Kết Nối Từ Xa qua Radmin VPN

> **Radmin VPN** tạo một mạng LAN ảo qua Internet. Các máy tính ở khác địa điểm (nhà, trường...) nhìn nhau như thể cắm chung một dây mạng.

```
[Nhà - Máy Chủ]                    [Trường - Máy Client]
  Docker Server                        NetBeans Client
  Radmin IP: 26.18.244.131             Radmin IP: 26.18.244.135
       │                                     │
       └──────── Radmin VPN Tunnel ──────────┘
                  (Internet)
                  
Cấu hình:
  .env → RMI_HOSTNAME=26.18.244.131
  Client nhập IP: 26.18.244.131 khi mở app
```

**Luồng kết nối:**
1. Máy chủ bật Radmin VPN → nhận IP ảo (vd: `26.18.244.131`)
2. Điền IP đó vào file `.env` → chạy `docker-compose up -d --build`
3. Máy Client mở app → nhập `26.18.244.131` → kết nối thành công

### 3.9 Module Thông Báo Kết Nối (package `thongbao`)

> Khi Client mở app, hệ thống tự động kiểm tra xem Server có sẵn sàng không trước khi vào giao diện chính.

```
Người dùng nhập IP → KiemTraKetNoi.kiemTra(ip, 8888)
                            │
                    Thử mở TCP socket
                    (timeout 3 giây)
                            │
              ┌─────────────┴─────────────┐
           Thành công                   Thất bại
     HopThoaiThongBao               HopThoaiThongBao
     [✓ Nền xanh]                   [✗ Nền đỏ]
     "Kết nối thành công!"          "Không thể kết nối"
     (tự đóng sau 3 giây)          + gợi ý kiểm tra Radmin
```

---

## 4. Deployment bằng Docker

**Lý do dùng Docker:**
- Server chạy 24/7 ổn định, không cần mở NetBeans
- Cô lập môi trường → không lo xung đột Java version
- Chỉ cần 1 lệnh để deploy

**Biến môi trường trong `.env`:**

| Biến | Giá trị | Ý nghĩa |
|---|---|---|
| `RMI_HOSTNAME` | `26.18.244.131` | IP Radmin VPN của máy chủ |
| `MONGODB_URI` | `mongodb://emr:123456@host.docker.internal:27020/...` | Kết nối tới MongoDB trên máy thật |

> **Lưu ý quan trọng:** Trong Docker container chạy trên Linux, `localhost` = chính container (không phải máy thật). Phải dùng `host.docker.internal` để truy cập dịch vụ trên máy thật (ở đây là MongoDB port 27020).

**Lệnh deploy:**
```bash
docker-compose up -d --build
```

**Kiểm tra log Server:**
```bash
docker logs server_thuvien
```

**Log khi khởi động thành công:**
```
[MongoDB] Ket noi thanh cong -> database: thuvien_db
[RMI] May chu RMI dang chay o cong 1099...
[HE THONG] May chu san sang! TCP:8888 | RMI:1099 | UDP:9999
[TCP] May chu TCP dang chay o cong 8888
```

---

## 5. Hướng Dẫn Sử Dụng

### Phía Máy Chủ
```bash
# 1. Cấu hình .env
RMI_HOSTNAME=<Radmin VPN IP của máy chủ>
MONGODB_URI=mongodb://emr:123456@host.docker.internal:27020/?authSource=admin

# 2. Khởi động
docker-compose up -d --build
```

### Phía Máy Client
1. Mở project bằng NetBeans → **Clean and Build**
2. Chạy `client_ui.java`
3. Nhập IP Radmin VPN của máy chủ (vd: `26.18.244.131`)
4. Hộp thoại xanh = kết nối thành công → vào app
5. Tab **Hiển thị**: xem danh sách tài liệu dạng icon
6. Nút **Tải Lên**: chọn file → chọn danh mục + tag → xác nhận
7. Nút **Tải Xuống**: chọn file từ danh sách → file lưu vào `src/luutru/download/`

---

## 6. Phân Tích Lỗi Thực Tế & Giải Pháp Tối Ưu (Case Study: Race Condition)

Trong quá trình vận hành hệ thống phân tán, nhóm phát triển đã phát hiện và xử lý thành công một lỗi bất đồng bộ kinh điển liên quan đến hiệu năng mạng và đồng bộ trạng thái cơ sở dữ liệu.

### 🛑 Hiện tượng lỗi (Sự cố mất đồng bộ số lượt tải):
* Khi Client thực hiện tải xuống một tài liệu thành công, giao diện Client chính lập tức gọi hàm tự động làm mới `lamMoiDanhSach()` để cập nhật số lượt tải hiển thị trên màn hình.
* Tuy nhiên, **số lượt tải trên UI vẫn giữ nguyên giá trị cũ**. Chỉ khi người dùng bấm nút **Làm mới** thủ công một vài giây sau đó, lượt tải mới hiển thị đúng.
* Kiểm tra trực tiếp Database MongoDB tại thời điểm xảy ra sự cố: Lượt tải **đã được cập nhật tăng +1 thành công**.

### 🔍 Phân tích nguyên nhân (Race Condition):
Do Client sử dụng luồng bất đồng bộ (Thread) để tải file nhằm tránh đóng băng UI. Quy trình truyền tin diễn ra như sau:
1. Client gửi lệnh `taixuong|tenfile` qua socket TCP.
2. Server ban đầu xử lý theo trình tự tuyến tính:
   * **Bước A:** Đọc file vật lý và truyền luồng byte dữ liệu qua TCP (`truyen_tai_file.gui_file`).
   * **Bước B:** Gọi RMI đến Database MongoDB để thực hiện câu lệnh tăng lượt tải (`dichvu_rmi.tang_luottai`).
3. Vì kích thước file nhỏ hoặc băng thông luồng đệm (Network Buffer) của TCP cực cao, luồng nhận dữ liệu của Client hoàn tất việc đọc byte từ card mạng và kết thúc tiến trình nhận file **trước khi luồng xử lý của Server chạy đến Bước B** (ghi dữ liệu vào MongoDB).
4. Ngay khi nhận file xong, Client lập tức khởi chạy một kết nối TCP mới gửi lệnh `timkiem` để quét lại dữ liệu metadata vẽ lên UI. Kết nối này đến Server trước khi giao dịch ghi lượt tải ở Bước B hoàn tất. Dẫn đến Client vẽ UI bằng giá trị cũ trong MongoDB.

```
TIẾN TRÌNH SERVER: ───[Gửi File qua TCP]───────► [Ghi Lượt Tải MongoDB] ───► (Hoàn tất ghi)
                                                     ▲
                                   Gây ra tranh chấp │ (Truy vấn lúc này vẫn lấy lượt tải cũ)
                                                     │
TIẾN TRÌNH CLIENT: ───[Nhận File xong]───► [Gửi Lệnh TimKiem vẽ lại UI] ───► (Vẽ UI cũ)
```

### 🩹 Giải pháp khắc phục tối ưu:
Nhóm đã tiến hành tái cấu trúc lại trình tự thực hiện tại file [may_chu_tcp.java](file:///a:/code%20javanetbean/Baitaplon_Nhom7/src/may_chu/may_chu_tcp.java):
* **Đảo ngược logic xử lý:** Server thực hiện **Tăng lượt tải trong MongoDB trước** (`dichvu_rmi.tang_luottai`), sau đó mới **Truyền dữ liệu file qua TCP** (`truyen_tai_file.gui_file`).
* **Hiệu quả:** Trong suốt thời gian file được truyền tải trên đường truyền vật lý (dù chỉ mất vài mili-giây), dữ liệu trong MongoDB đã được cập nhật thành công từ trước. Do đó, ngay khi Client tải xong file và kích hoạt lệnh refresh UI, dữ liệu tải về hiển thị chắc chắn là dữ liệu mới nhất, đảm bảo tính realtime đồng bộ 100%!

---

## 7. Các Công Nghệ Sử Dụng

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Java | 8 (JDK 8) | Ngôn ngữ lập trình chính |
| Java Swing | - | Giao diện đồ họa Client |
| TCP Socket | - | Truyền file, tìm kiếm |
| UDP Datagram | - | Broadcast thông báo realtime |
| Java RMI | - | Quản trị từ xa |
| MongoDB | 8.0 | Lưu trữ metadata tài liệu |
| MongoDB Driver | 4.11.1 | Kết nối Java ↔ MongoDB |
| Docker | - | Container hóa Server |
| Radmin VPN | - | Kết nối mạng từ xa |
| Apache Ant | - | Build tool (NetBeans) |

---

*Báo cáo được tạo tự động — Nhóm 7, môn Lập Trình Mạng.*
