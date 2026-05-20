# 📚 Hệ Thống Thư Viện Tài Liệu Môn Học

> Ứng dụng phân tán Client–Server xây dựng bằng Java, tích hợp **TCP · UDP · RMI · MongoDB** để chia sẻ tài liệu học tập qua mạng — kể cả qua Internet nhờ Radmin VPN.

---

## ✨ Tính Năng Nổi Bật (Nhánh luongmoi)

| Tính năng | Mô tả |
|---|---|
| 📤 **Upload tài liệu** | Hỗ trợ mọi định dạng (PDF, DOCX, XLSX, EXE...) — giới hạn 100 MB |
| 📥 **Download tài liệu** | Tải file dạng byte streaming ổn định + **Kích đúp tải nhanh (Double-Click Quick Download)** trực tiếp từ màn hình Explorer. |
| 🔍 **Tìm kiếm** | Tìm tài liệu theo tên, hiển thị kết quả thời gian thực từ MongoDB. |
| 🏷️ **Danh mục & Tag** | Phân loại khi upload, gợi ý tag tự động, thẻ tag xanh lá cây hiện thị trực quan trên card. |
| 📊 **Thống kê lượt tải** | Theo dõi lượt tải, lưu vĩnh viễn trong MongoDB, tự cập nhật lượt tải ngay sau khi download thành công. |
| 📡 **Đồng bộ Real-time 100%** | Tự động cập nhật giao diện thời gian thực trên TẤT CẢ các máy Client trong mạng LAN/VPN nhờ UDP Multicast ngay khi có người upload file mới. |
| 🔔 **Kiểm tra kết nối** | Popup thông báo xanh/đỏ khi mở app — hiển thị rõ chế độ Local/LAN giúp giám sát kết nối tức thì. |
| 🔀 **Xác nhận kết nối thông minh** | Client tự phát hiện server local nhưng **luôn mở hộp thoại hỏi ý kiến** người dùng (YES chọn Local, NO mở bảng nhập IP LAN / Radmin VPN để test mạng). |
| 🩹 **RMI Tự chữa lành (Self-healing)** | Server tự quét danh sách card mạng. Nếu IP Radmin trong `.env` bị tắt/offline, Server tự động **fallback RMI về localhost** giúp test nội bộ không bao giờ bị lỗi `Connection refused`. |
| 🖼️ **Giao diện icon premium** | File Explorer card lưới 150x150 cực đẹp, tooltip HTML sinh động, JList HTML 2 dòng cao cấp ở cửa sổ phụ. |
| 🌐 **Kết nối từ xa hoàn hảo** | Ghim cố định RMI và dữ liệu trên **cổng 1099** thông qua `super(1099)`, giải quyết triệt để lỗi chặn cổng ngẫu nhiên qua Radmin VPN. |
| 🐳 **Docker Server** | Server chạy 24/7 trong container, deploy nhanh chóng bằng 1 lệnh. |

---

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────────────┐
│                     PHÍA CLIENT (Java Swing)                 │
│                                                              │
│   ┌──────────────┐            ┌───────────────────────────┐ │
│   │  client_ui   │            │  chucnang3/PhanLuong      │ │
│   │  (Giao diện) │───────────►│  (Tự động phân luồng)     │ │
│   │              │            └─────────┬─────────────────┘ │
│   │  WrapLayout  │              LOCAL?  │  LAN?             │
│   │  (Icon grid) │            ┌─────────┴─────────────────┐ │
│   │              │  TCP:8888  │ KetNoiLocal │ ket_noi_tcp  │ │
│   │              │◄──────────►│ (localhost) │ (LAN/VPN IP) │ │
│   │              │  RMI:1099  │ goi_rmi                    │ │
│   │              │◄──────────►│ (Quản trị từ xa)           │ │
│   │              │  UDP:9999  │ nhan_udp                   │ │
│   │              │◄──────────►│ (Nhận thông báo)           │ │
│   └──────────────┘            └───────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                         ▲ Radmin VPN / LAN
┌─────────────────────────────────────────────────────────────┐
│                    PHÍA SERVER (Docker)                      │
│                                                              │
│   ┌──────────────┐   ┌──────────────┐   ┌────────────────┐  │
│   │ may_chu_tcp  │   │ may_chu_udp  │   │ dich_vu_rmi    │  │
│   │ (TCP:8888)   │   │ (UDP:9999)   │   │ (RMI:1099)     │  │
│   └──────┬───────┘   └──────────────┘   └───────┬────────┘  │
│          │                                        │           │
│          └──────────────────┬─────────────────────┘           │
│                             ▼                                  │
│                    ┌────────────────┐                          │
│                    │  MongoKetNoi   │                          │
│                    │  (MongoDB)     │                          │
│                    └────────────────┘                          │
│                             │                                  │
│                    ┌────────┴────────┐                         │
│              tai_lieu         danh_muc / tag                   │
│           (metadata file)   (phân loại)                       │
└─────────────────────────────────────────────────────────────┘
                             │
                    ┌────────┴────────┐
              src/luutru/upload/    src/luutru/download/
              (File vật lý Server)  (File vật lý Client)
```

---

## 📁 Cấu Trúc Thư Mục

```
Baitaplon_Nhom7/
├── src/
│   ├── may_chu/               # Server
│   │   ├── chay_may_chu.java  # Entry point khởi động hệ thống
│   │   ├── MongoKetNoi.java   # Kết nối MongoDB (Singleton)
│   │   ├── may_chu_tcp.java   # Xử lý TCP: upload/download/tìm kiếm
│   │   ├── may_chu_udp.java   # Broadcast thông báo UDP
│   │   ├── dich_vu_rmi.java   # Interface RMI
│   │   └── dich_vu_rmi_impl.java  # Cài đặt RMI + MongoDB
│   ├── client_ui/             # Client
│   │   ├── client_ui.java     # Giao diện chính (Swing)
│   │   ├── CauHinh.java       # Cấu hình IP server
│   │   ├── ket_noi_tcp.java   # Kết nối TCP chế độ LAN (giới hạn 100MB)
│   │   ├── goi_rmi.java       # Gọi hàm RMI từ xa
│   │   ├── nhan_udp.java      # Lắng nghe UDP
│   │   └── WrapLayout.java    # Layout icon dạng lưới
│   ├── chucnang/              # Module tiện ích
│   │   ├── truyen_tai_file.java   # Byte streaming (chunk 4KB)
│   │   └── giao_dien_phu.java    # Cửa sổ tìm kiếm/tải xuống
│   ├── chucnang2/
│   │   └── GiaoDienChonTag.java  # Popup chọn danh mục + tag
│   ├── chucnang3/             # Module phân luồng kết nối
│   │   ├── PhanLuong.java        # Tự động detect Local vs LAN
│   │   └── KetNoiLocal.java      # Kết nối TCP chế độ Local (localhost)
│   ├── thongbao/              # Module thông báo kết nối
│   │   ├── KiemTraKetNoi.java    # Kiểm tra TCP socket (timeout 3s)
│   │   └── HopThoaiThongBao.java # Dialog xanh/đỏ (phân biệt Local/LAN)
│   ├── resources/
│   │   └── icontl.png         # Icon đại diện tài liệu trong UI
│   └── luutru/
│       ├── upload/            # Kho tài liệu trên Server
│       └── download/          # Tài liệu đã tải về máy Client
├── lib/                       # MongoDB Java Driver JARs
│   ├── mongodb-driver-sync-4.11.1.jar
│   ├── mongodb-driver-core-4.11.1.jar
│   └── bson-4.11.1.jar
├── picture/
│   └── icontl.png             # Icon gốc
├── Dockerfile                 # Build Docker image Server
├── docker-compose.yml         # Khởi động Server container
├── .env                       # Cấu hình IP & MongoDB URI
├── baocao.md                  # Báo cáo kỹ thuật chi tiết
└── readme.md                  # File này
```

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy

### Yêu Cầu
- **Docker Desktop** (cho máy chạy Server)
- **JDK 8** + **NetBeans** (cho máy chạy Client)
- **Radmin VPN** (nếu kết nối qua Internet)
- **MongoDB** đang chạy (port 27020)

---

### A. Thiết Lập Máy Chủ (Server)

**Bước 1:** Mở file `.env` và cấu hình:
```env
# IP Radmin VPN của máy chủ (hoặc IP LAN nếu cùng mạng)
RMI_HOSTNAME=26.18.244.131

# URI kết nối MongoDB (host.docker.internal = máy thật từ trong Docker)
MONGODB_URI=mongodb://emr:123456@host.docker.internal:27020/?authSource=admin
```

**Bước 2:** Khởi động Server:
```bash
docker-compose up -d --build
```

**Bước 3:** Kiểm tra Server đã chạy:
```bash
docker logs server_thuvien
```
✅ Log thành công:
```
[MongoDB] Ket noi thanh cong -> database: thuvien_db
[RMI] May chu RMI dang chay o cong 1099...
[HE THONG] May chu san sang! TCP:8888 | RMI:1099 | UDP:9999
[TCP] May chu TCP dang chay o cong 8888
```

---

### B. Chạy Máy Khách (Client)

**Bước 1:** Mở project bằng **NetBeans** → **Clean and Build**

**Bước 2:** Chạy file `src/client_ui/client_ui.java`

**Bước 3:** Hệ thống **tự động phân luồng** kết nối:

```
Client khởi động
    ↓
Kiểm tra localhost:8888
    ├── ✅ Có server local → Chế độ LOCAL (không hỏi IP)
    └── ❌ Không có → Hỏi IP LAN / Radmin VPN
```

- **Chạy trên cùng máy:** Client tự phát hiện server local → kết nối ngay, **không cần nhập IP**
- **Chạy khác máy (LAN):** Nhập IP Wi-Fi của máy chủ (vd: `192.168.1.5`)
- **Qua Internet (Radmin VPN):** Nhập IP Radmin của máy chủ (vd: `26.18.244.131`)

**Bước 4:** Hộp thoại thông báo xuất hiện:
- 🟢 **Nền xanh** = kết nối thành công → vào app sau 3 giây
- 🔴 **Nền đỏ (local)** = server chưa khởi động → kiểm tra Docker / server
- 🔴 **Nền đỏ (LAN)** = không kết nối được → kiểm tra IP / Radmin VPN

---

### C. Kết Nối Qua Internet (Radmin VPN)

1. Cài **Radmin VPN** trên cả máy chủ và máy khách
2. Tạo Network trên Radmin VPN, cho các máy tham gia
3. Máy chủ sẽ nhận IP ảo (vd: `26.18.244.131`)
4. Điền IP đó vào `.env` (`RMI_HOSTNAME=26.18.244.131`) và rebuild Docker
5. Máy khách nhập `26.18.244.131` khi mở app → kết nối thành công

---

## 📖 Hướng Dẫn Sử Dụng

### Tab **Hiển Thị** (màn hình chính)
| Nút | Chức năng |
|---|---|
| 🔄 **Làm Mới** | Tải danh sách tài liệu mới nhất, hiển thị dạng icon card |
| 📤 **Tải Lên** | Chọn file → chọn danh mục + nhập tag → xác nhận |
| 📥 **Tải Xuống** | Mở cửa sổ chọn file để tải về máy |
| 🔍 **Tìm Kiếm** | Mở cửa sổ tìm theo tên file |

### Tab **Chức Năng TCP**
- Tìm kiếm, tải lên, tải xuống với log chi tiết
- Nhật ký hoạt động hiển thị trực tiếp

### Tab **Chức Năng UDP**
- Hiển thị thông báo realtime khi có tài liệu mới được upload
- Tự động nhận, không cần thao tác gì

### Tab **Chức Năng RMI**
| Nút | Chức năng |
|---|---|
| **Quản lý danh mục** | Thêm danh mục mới vào MongoDB |
| **Quản lý tag** | Thêm tag gợi ý mới vào MongoDB |
| **Thống kê lượt tải** | Xem tổng lượt tải của từng tài liệu |

---

## ⚙️ Cấu Hình Nâng Cao

### Thay đổi giới hạn kích thước file (hiện tại: 100 MB)
Sửa trong `src/client_ui/ket_noi_tcp.java` và `src/chucnang3/KetNoiLocal.java`:
```java
private static final long GIOI_HAN_BYTES = 100L * 1024 * 1024; // đổi 100 thành số MB mong muốn
```
Và `src/may_chu/may_chu_tcp.java`:
```java
final long GIOI_HAN_SERVER = 100L * 1024 * 1024;
```

### Thay đổi thông tin MongoDB
Sửa trong `.env`:
```env
MONGODB_URI=mongodb://username:password@host:port/?authSource=admin
```

---

## 🛠️ Công Nghệ Sử Dụng

| Công nghệ | Phiên bản | Vai trò |
|---|---|---|
| Java | 8 (JDK 8) | Ngôn ngữ lập trình chính |
| Java Swing | - | Giao diện đồ họa |
| TCP Socket | Port 8888 | Truyền file, tìm kiếm |
| UDP Datagram | Port 9999 | Broadcast thông báo realtime |
| Java RMI | Port 1099 | Quản trị từ xa |
| MongoDB | 8.0 | Lưu metadata tài liệu vĩnh viễn |
| MongoDB Java Driver | 4.11.1 | Kết nối Java ↔ MongoDB |
| Docker | - | Container hóa Server |
| Apache Ant | - | Build tool (NetBeans) |
| Radmin VPN | - | Mạng ảo kết nối từ xa |

---

## 👨‍💻 Nhóm Phát Triển

**Nhóm 7** — Môn Lập Trình Mạng

---

*Xem chi tiết cơ chế kỹ thuật tại [`baocao.md`](./baocao.md)*
