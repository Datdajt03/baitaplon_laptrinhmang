# 📚 Hệ Thống Thư Viện Tài Liệu Môn Học (Distributed Library System)

<p align="center">
  <a href="#">
    <img src="https://img.shields.io/badge/Java-8%20%7C%2011%20%7C%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Support" />
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/MongoDB-8.0-47A248?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB Database" />
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Container" />
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/Radmin_VPN-Ready-red?style=for-the-badge&logo=virtualprivate-network&logoColor=white" alt="VPN Compatibility" />
  </a>
</p>

<p align="center">
  <b>Một ứng dụng phân tán Client-Server hiện đại xây dựng bằng Java Swing, kết hợp hài hòa sức mạnh của các giao thức mạng cốt lõi: TCP, UDP, RMI và Cơ sở dữ liệu MongoDB để chia sẻ tài liệu học tập trực quan và đồng bộ hóa thời gian thực.</b>
</p>

---

## 🌟 Tổng Quan Dự Án

Hệ thống được thiết kế để giải quyết bài toán chia sẻ giáo trình, tài liệu môn học giữa các máy trạm (Client) thông qua máy chủ trung tâm (Server). Đặc biệt, hệ thống được tối ưu hóa tối đa để có thể vận hành ổn định không chỉ trong mạng nội bộ **LAN** mà còn qua **Internet (WAN)** nhờ công cụ **Radmin VPN**, hỗ trợ tính năng tự phục hồi kết nối thông minh và đồng bộ hóa dữ liệu tức thì.

---

## ✨ Tính Năng Nổi Bật (Nhánh `luongmoi`)

| Phân Loại | Tính Năng | Mô Tả Chi Tiết |
| :--- | :--- | :--- |
| 📤 **Truyền Tải** | **Upload Tài Liệu** | Hỗ trợ mọi định dạng (PDF, DOCX, XLSX, EXE...) — Giới hạn tải lên đến **100 MB** giúp bảo vệ máy chủ. |
| 📥 **Truyền Tải** | **Download Tài Liệu** | Tải file dạng *byte streaming* cực kỳ ổn định + **Kích đúp tải nhanh (Double-Click Quick Download)** trực tiếp trên Card màn hình chính. |
| 🔍 **Tìm Kiếm** | **Tìm Kiếm Siêu Tốc** | Tìm tài liệu theo tên, hiển thị kết quả truy vấn thời gian thực trực tiếp từ MongoDB. |
| 🏷️ **Tổ Chức** | **Danh Mục & Thẻ Tag** | Phân loại thông minh khi upload, gợi ý tag tự động trực quan qua các thẻ màu bắt mắt trong ứng dụng. |
| 📊 **Tương Tác** | **Thống Kê Lượt Tải** | Lưu vết lượt tải vĩnh viễn trong MongoDB, tự động cập nhật số liệu ngay sau khi tải thành công. |
| ⭐ **Tương Tác** | **Đánh Giá Sao (RMI)** | <kbd>NEW</kbd> Đánh giá tài liệu từ 1-5 sao qua RMI, hiển thị trung bình sao trực quan trên card và đồng bộ UDP. |
| 🗑️ **Quản Trị** | **Xóa Tài Liệu Vĩnh Viễn** | <kbd>NEW</kbd> Click chuột phải để xóa hoàn toàn tài liệu (MongoDB record + file vật lý trên server) và tự động broadcast realtime. |
| 📡 **Đồng Bộ** | **Real-time LAN Sync** | Tự động đồng bộ giao diện trên **tất cả** Client đang hoạt động nhờ cơ chế **UDP Multicast** khi có file mới hoặc có thay đổi. |
| 🩹 **Kết Nối** | **RMI Tự Chữa Lành** | <kbd>NEW</kbd> Tự động phát hiện mạng và fallback hostname RMI về `localhost` nếu IP Radmin trong `.env` bị offline, ngăn lỗi kết nối. |
| 🔀 **Kết Nối** | **Phân Luồng Thông Minh** | Tự động phát hiện Server local, mở hộp thoại tương tác hỏi người dùng (YES chạy Local, NO mở bảng nhập IP mạng LAN). |
| 📋 **Giám Sát** | **Thanh Hoạt Động (Sidebar)**| <kbd>NEW</kbd> Thanh Activity Sidebar bên trái cập nhật trực tiếp mọi sự kiện mạng (Upload, Download, Xóa, Thêm...) kèm mốc thời gian. |
| ⏱️ **Đồng Bộ** | **Auto-Refresh Daemon** | <kbd>NEW</kbd> Bộ đếm thời gian ngầm (Swing Timer) tự chạy mỗi 10 giây để đồng bộ danh sách và bộ lọc từ máy chủ. |

---

## 🏗️ Kiến Trúc Hệ Thống

Dưới đây là sơ đồ luồng hoạt động giữa **Client (Java Swing)** và **Server (Docker Container)** sử dụng kết hợp 3 giao thức mạng:

```text
┌─────────────────────────────────────────────────────────────┐
│                     PHÍA CLIENT (Java Swing)                 │
│                                                             │
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
│                                                             │
│   ┌──────────────┐   ┌──────────────┐   ┌────────────────┐  │
│   │ may_chu_tcp  │   │ may_chu_udp  │   │ dich_vu_rmi    │  │
│   │ (TCP:8888)   │   │ (UDP:9999)   │   │ (RMI:1099)     │  │
│   └──────┬───────┘   └──────────────┘   └───────┬────────┘  │
│          │                                        │         │
│          └──────────────────┬─────────────────────┘         │
│                             ▼                               │
│                    ┌────────────────┐                       │
│                    │  MongoKetNoi   │                       │
│                    │  (MongoDB)     │                       │
│                    └────────────────┘                       │
│                             │                               │
│                    ┌────────┴────────┐                      │
│              tai_lieu         danh_muc / tag                │
│           (metadata file)   (phân loại)                     │
└─────────────────────────────────────────────────────────────┘
                             │
                    ┌────────┴────────┐
              src/luutru/upload/    src/luutru/download/
              (File vật lý Server)  (File vật lý Client)
```

---

## 📁 Cấu Trúc Thư Mục Dự Án

```text
Baitaplon_Nhom7/
├── src/
│   ├── may_chu/               # 🖥️ MODULE SERVER
│   │   ├── chay_may_chu.java  # -> File chạy chính, khởi động các service
│   │   ├── MongoKetNoi.java   # -> Kết nối và truy vấn MongoDB (Singleton Pattern)
│   │   ├── may_chu_tcp.java   # -> Socket TCP (Port 8888): Upload/Download/Tìm kiếm
│   │   ├── may_chu_udp.java   # -> Datagram UDP (Port 9999): Broadcast thông báo realtime
│   │   ├── dich_vu_rmi.java   # -> Interface RMI khai báo các hàm từ xa
│   │   └── dich_vu_rmi_impl.java # -> Triển khai RMI: Thống kê, quản trị danh mục/tag
│   ├── client_ui/             # 💻 MODULE CLIENT
│   │   ├── client_ui.java     # -> Giao diện chính đồ họa Java Swing
│   │   ├── CauHinh.java       # -> Lưu cấu hình IP máy chủ
│   │   ├── ket_noi_tcp.java   # -> Gửi lệnh, truyền nhận file qua TCP với Server LAN
│   │   ├── goi_rmi.java       # -> Tạo kết nối RMI Client đến Server
│   │   ├── nhan_udp.java      # -> Tiến trình chạy ngầm lắng nghe gói tin UDP
│   │   └── WrapLayout.java    # -> Layout grid tự động điều chỉnh hiển thị danh sách
│   ├── chucnang/              # ⚙️ MODULE TIỆN ÍCH CỐT LÕI
│   │   ├── truyen_tai_file.java # -> Byte Streaming phân mảnh file 4KB tin cậy
│   │   └── giao_dien_phu.java  # -> Cửa sổ quản lý nâng cao
│   ├── chucnang2/
│   │   └── GiaoDienChonTag.java # -> Form lựa chọn danh mục và nhập tag thông minh
│   ├── chucnang3/             # 🔀 PHÂN LUỒNG MẠNG
│   │   ├── PhanLuong.java     # -> Quét mạng, tự động rẽ nhánh kết nối Local/LAN
│   │   └── KetNoiLocal.java   # -> Xử lý TCP trực tiếp trên localhost
│   ├── thongbao/              # 🔔 TRẠNG THÁI HỆ THỐNG
│   │   ├── KiemTraKetNoi.java # -> Thử nghiệm Ping/Socket với timeout 3s
│   │   └── HopThoaiThongBao.java # -> Hộp thoại thông báo kết nối Đỏ (Lỗi) / Xanh (Ok)
│   ├── resources/             # 🎨 TÀI NGUYÊN GIAO DIỆN
│   │   └── icontl.png         # -> Icon hiển thị đại diện tài liệu
│   └── luutru/                # 💾 KHO DỮ LIỆU VẬT LÝ
│       ├── upload/            # -> Thư mục chứa tài liệu trên Server
│       └── download/          # -> Thư mục lưu tài liệu tải về phía Client
├── lib/                       # 📚 THƯ VIỆN BÊN NGOÀI (MongoDB Drivers Java)
│   ├── mongodb-driver-sync-4.11.1.jar
│   ├── mongodb-driver-core-4.11.1.jar
│   └── bson-4.11.1.jar
├── picture/                   # 🖼️ HÌNH ẢNH DỰ ÁN
├── Dockerfile                 # 🐳 File cấu hình build Docker Image cho Server
├── docker-compose.yml         # 🎛️ Orchestration tự động khởi chạy Server & DB
├── .env                       # 📝 File biến môi trường chứa IP & MongoDB URI
├── baocao.md                  # 📄 Báo cáo chi tiết kỹ thuật hệ thống
└── readme.md                  # 📄 File hướng dẫn này
```

---

## 🚀 Hướng Dẫn Cài Đặt & Khởi Chạy

### ĐIỀU KIỆN TIÊN QUYẾT
* **Docker Desktop** (Dành cho máy chạy Server)
* **JDK 8** (hoặc cao hơn) + **NetBeans IDE** (Dành cho máy chạy Client)
* **Radmin VPN** (Nếu muốn kết nối từ xa qua Internet)
* **MongoDB** (Nếu chạy Server thủ công bên ngoài Docker - Port mặc định `27020`)

---

### 🖥️ Bước A: Thiết Lập Phía Máy Chủ (Server)

> [!TIP]
> Sử dụng Docker giúp triển khai máy chủ và cơ sở dữ liệu nhanh chóng chỉ với một câu lệnh mà không cần cài đặt rườm rà.

**1. Cấu hình tệp tin môi trường `.env`**  
Mở tệp `.env` ở thư mục gốc dự án và thay đổi thông tin:
```env
# Địa chỉ IP Radmin VPN của máy chủ (hoặc IP mạng LAN nếu chạy nội bộ)
RMI_HOSTNAME=26.18.244.131

# URI kết nối MongoDB (host.docker.internal chỉ định máy thật từ bên trong Docker)
MONGODB_URI=mongodb://emr:123456@host.docker.internal:27020/?authSource=admin
```

**2. Khởi chạy hệ thống Container Docker**  
Mở Terminal tại thư mục gốc dự án và chạy:
```bash
docker-compose up -d --build
```

**3. Kiểm tra trạng thái hoạt động của Server**  
Xem log từ container để đảm bảo các dịch vụ mạng đã sẵn sàng:
```bash
docker logs -f server_thuvien
```
**✅ Log hiển thị khởi động thành công:**
```text
[MongoDB] Ket noi thanh cong -> database: thuvien_db
[RMI] May chu RMI dang chay o cong 1099...
[HE THONG] May chu san sang! TCP:8888 | RMI:1099 | UDP:9999
[TCP] May chu TCP dang chay o cong 8888
```

---

### 💻 Bước B: Triển Khai Phía Máy Khách (Client)

**1. Mở mã nguồn dự án**  
Import thư mục dự án vào **NetBeans IDE**. Click chuột phải vào Project và chọn **Clean and Build**.

**2. Khởi chạy giao diện chính**  
Mở file `src/client_ui/client_ui.java` -> chuột phải chọn **Run File** (hoặc nhấn `Shift + F6`).

**3. Cơ chế phân luồng thông minh hoạt động**  
Client sẽ tự động quét trạng thái mạng theo sơ đồ sau:
```text
      Khởi chạy Client UI
               │
      Thử kết nối localhost:8888
         ├── [Thành công] ──► Chế độ LOCAL (Vào thẳng App, không hỏi IP)
         └── [Thất bại]   ──► Chế độ LAN/VPN (Hiện popup nhập IP Server)
```

* **Kết nối Local:** Tự động kết nối trực tiếp đến Server đang chạy trên cùng máy.
* **Kết nối cùng mạng LAN:** Nhập địa chỉ IP Wi-Fi/Ethernet của máy chủ (ví dụ: `192.168.1.5`).
* **Kết nối qua Internet:** Nhập địa chỉ IP VPN ảo nhận từ Radmin VPN (ví dụ: `26.18.244.131`).

**4. Xác nhận trạng thái bằng hộp thoại visual**
* 🟢 **Hộp thoại xanh lá cây:** Kết nối thành công, hiển thị thông số và tự chuyển vào màn hình chính sau 3 giây.
* 🔴 **Hộp thoại đỏ:** Kết nối thất bại. Vui lòng kiểm tra lại IP Server, trạng thái Docker hoặc kết nối VPN.

---

### 🌐 Bước C: Hướng Dẫn Kết Nối Internet qua Radmin VPN

1. Cài đặt phần mềm **Radmin VPN** trên cả máy chủ (Server) và máy khách (Client).
2. Trên máy chủ, bấm **Create Network**, đặt tên và mật khẩu mạng.
3. Trên máy khách, bấm **Join Network**, nhập thông tin mạng vừa tạo để tham gia phòng.
4. Ghi lại IP ảo của máy chủ do Radmin cung cấp (ví dụ: `26.18.244.131`).
5. Điền IP này vào `.env` (`RMI_HOSTNAME=26.18.244.131`), sau đó rebuild Docker trên Server.
6. Máy khách khởi chạy ứng dụng Swing, nhập IP `26.18.244.131` khi được hỏi để liên kết trực tiếp qua mạng Internet.

---

## 📖 Hướng Dẫn Sử Dụng Ứng Dụng

### 📱 1. Màn Hình Chính (Tab Hiển Thị)
* **Làm Mới (Auto & Manual):** Danh sách tài liệu tự cập nhật mỗi 10 giây, hiển thị trực quan dưới dạng các thẻ lưới (Card layout) kích thước 150x150 cực kỳ đẹp mắt kèm đánh giá sao trung bình.
* **Upload Tài Liệu:** Bấm **Tải Lên**, chọn file của bạn. Popup gợi ý danh mục và thẻ tag từ MongoDB sẽ hiển thị giúp bạn gán phân loại chính xác nhất.
* **Tải Nhanh:** Click đúp trực tiếp vào Card tài liệu mong muốn để tải ngay về thư mục máy khách.
* **Xóa Tài Liệu:** Click chuột phải vào Card tài liệu -> Chọn **Xóa tài liệu vĩnh viễn** để dọn sạch file trên đĩa cứng và metadata trên MongoDB Server.

### 📋 2. Thanh Hoạt Động (Activity Sidebar - Left)
* Cập nhật thời gian thực không bỏ lỡ bất cứ sự kiện nào trong mạng: máy trạm nào vừa upload, download, xóa file hay thêm cấu hình phân loại kèm mốc giờ cụ thể.

### 📡 3. Quản Trị Hệ Thống RMI
* **Quản lý phân loại:** Cho phép admin thêm mới các danh mục (Categories) và thẻ (Tags) gợi ý trực tiếp vào MongoDB để phục vụ Client chọn nhanh khi tải lên.
* **Thống kê tổng quan:** Xem tổng số lượt tải thực tế của từng tài liệu được tổng hợp từ Database.

---

## ⚙️ Cấu Hình Nâng Cao

### Thay đổi giới hạn dung lượng tải lên (Mặc định: 100 MB)
1. Trong file Client `src/client_ui/ket_noi_tcp.java` và `src/chucnang3/KetNoiLocal.java`, thay đổi giá trị biến:
   ```java
   private static final long GIOI_HAN_BYTES = 100L * 1024 * 1024; // Thay đổi 100 thành số MB mong muốn
   ```
2. Đồng bộ giá trị trên file Server `src/may_chu/may_chu_tcp.java`:
   ```java
   final long GIOI_HAN_SERVER = 100L * 1024 * 1024;
   ```

### Thay đổi kết nối MongoDB của riêng bạn
Chỉnh sửa biến môi trường trong file `.env` ở root folder:
```env
MONGODB_URI=mongodb://[username]:[password]@[host]:[port]/?authSource=admin
```

---

## 🛠️ Công Nghệ Sử Dụng

| Công Nghệ | Vai Trò Hệ Thống | Mô Tả |
| :--- | :--- | :--- |
| **Java 8 (JDK 8)** | Ngôn ngữ chính | Nền tảng phát triển ứng dụng Client-Server. |
| **Java Swing** | Giao diện người dùng | Thiết kế GUI trực quan, sử dụng WrapLayout linh hoạt. |
| **TCP Socket (Port 8888)** | Truyền dữ liệu lớn | Chịu trách nhiệm Upload/Download file và tìm kiếm nội bộ. |
| **UDP Datagram (Port 9999)** | Đồng bộ Realtime | Broadcast thông báo LAN khi có sự kiện thay đổi dữ liệu. |
| **Java RMI (Port 1099)** | Quản trị từ xa | Quản trị bộ lọc danh mục/tag, đánh giá sao, thống kê. |
| **MongoDB (v8.0)** | Cơ sở dữ liệu | Lưu trữ metadata file, danh mục và thẻ tag gợi ý. |
| **Docker & Compose** | Container hóa | Đóng gói môi trường Server & Database, deploy tức thì. |
| **Radmin VPN** | Mạng riêng ảo | Kết nối gián tiếp các máy trạm qua môi trường Internet. |

---

## 👨‍💻 Nhóm Phát Triển (Group 7)

Dự án được xây dựng và hoàn thiện bởi các thành viên **Nhóm 7 — Môn học Lập Trình Mạng**:

> [!NOTE]
> * Để tìm hiểu sâu về cơ sở lý thuyết, phân tích sự cố tranh chấp và kiến trúc 3 lớp của dự án, vui lòng xem [Báo cáo chi tiết baocao.md](./baocao.md).
> * Để xem tài liệu hướng dẫn quy trình vận hành chi tiết, các kịch bản chạy thử và cách khắc phục sự cố kết nối nhanh, vui lòng xem [Hướng dẫn vận hành cach_hoat_dong.md](./cach_hoat_dong.md).
