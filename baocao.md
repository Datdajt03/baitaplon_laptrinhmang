# 📝 BÁO CÁO ĐỀ TÀI: HỆ THỐNG THƯ VIỆN TÀI LIỆU MÔN HỌC
### 🎓 Môn học: Lập Trình Mạng (IT4480) — Nhóm 7

> [!TIP]
> * Để xem hướng dẫn cài đặt nhanh của dự án bằng Docker & NetBeans, vui lòng xem [Hướng dẫn cài đặt readme.md](./readme.md).
> * Để tìm hiểu nhanh các kịch bản chạy thử nghiệm thực tế và hướng dẫn từng nút bấm trên giao diện Client Swing, vui lòng xem [Hướng dẫn vận hành cach_hoat_dong.md](./cach_hoat_dong.md).

---

## 📌 Mục Lục
1. [1. Tổng Quan Hệ Thống](#1-tổng-quan-hệ-thống)
2. [2. Cấu Trúc Thư Mục Dự Án](#2-cấu-trúc-thư-mục-dự-án)
3. [3. Kiến Trúc Kỹ Thuật & Cơ Chế Hoạt Động](#3-kiến-trúc-kỹ-thuật--cơ-chế-hoạt-động)
   - [3.1 Luồng Khởi Động Server](#31-luồng-khởi-động-server)
   - [3.2 Giao Thức TCP (Port 8888) — Truyền Tải Dữ Liệu Nặng](#32-giao-thức-tcp-port-8888--truyền-tải-dữ-liệu-nặng)
   - [3.3 Cơ Chế Byte Streaming — Bảo Toàn Định Dạng File Tuyệt Đối](#33-cơ-chế-byte-streaming--bảo-toàn-định-dạng-file-tuyệt-đối)
   - [3.4 Giao Thức UDP (Port 9999) — Phát Thông Báo Realtime Mạng LAN](#34-giao-thức-udp-port-9999--phát-thông-báo-realtime-mạng-lan)
   - [3.5 Giao Thức RMI (Port 1099) — Quản Trị Từ Xa & Tự Chữa Lành](#35-giao-thức-rmi-port-1099--quản-trị-từ-xa--tự-chữa-lành)
   - [3.6 MongoDB — Lưu Trữ Metadata Vĩnh Viễn & Mô Hình Phân Tán 3 Lớp](#36-mongodb--lưu-trữ-metadata-vĩnh-viễn--mô-hình-phân-tán-3-lớp)
   - [3.7 Cơ Chế Bảo Vệ 2 Lớp (Giới Hạn 100MB)](#37-cơ-chế-bảo-vệ-2-lớp-giới-hạn-100mb)
   - [3.8 Kết Nối Từ Xa Qua Mạng Riêng Ảo Radmin VPN](#38-kết-nối-từ-xa-qua-mạng-riêng-ảo-radmin-vpn)
   - [3.9 Module Thông Báo & Ping Kết Nối Trực Quan](#39-module-thông-báo--ping-kết-nối-trực-quan)
   - [3.10 Cơ Chế Xóa Tài Liệu Vĩnh Viễn & Đồng Bộ Mạng Tức Thì](#310-cơ-chế-xóa-tài-liệu-vĩnh-viễn--đồng-bộ-mạng-tức-thì)
   - [3.11 Cơ Chế Ghi Nhật Ký Hệ Thống (Logging System)](#311-cơ-chế-ghi-nhật-ký-hệ-thống-logging-system)
   - [3.12 Quản Lý Socket Timeout Chống Treo Tài Nguyên](#312-quản-lý-socket-timeout-chống-treo-tài-nguyên)
4. [4. Deployment và Vận Hành Với Docker Container](#4-deployment-và-vận-hành-với-docker-container)
5. [5. Hướng Dẫn Cài Đặt & Chạy Kiểm Thử](#5-hướng-dẫn-cài-đặt--chạy-kiểm-thử)
6. [6. Phân Tích Sự Cố Thực Tế & Các Giải Pháp Tối Ưu Core](#6-phân-tích-sự-cố-thực-tế--các-giải-pháp-tối-ưu-core)
   - [6.1 Hiện Tượng Tranh Chấp Lượt Tải (Race Condition)](#61-hiện-tượng-tranh-chấp-lượt-tải-race-condition)
   - [6.2 Lỗi Định Tuyến RMI Docker NAT & Giải Pháp TCP Fallback](#62-lỗi-định-tuyến-rmi-docker-nat--giải-pháp-tcp-fallback)
   - [6.3 Cơ Chế Quét Dữ Liệu Gợi Ý Tự Chữa Lành](#63-cơ-chế-quét-dữ-liệu-gợi-y-tự-chữa-lành)
   - [6.4 Tự Động Co Giãn Spacing UX (UX Auto-scaling)](#64-tự-động-co-giãn-spacing-ux-ux-auto-scaling)
   - [6.5 Tránh Tải Đè File & Cơ Chế Tự Động Đổi Tên Duy Nhất](#65-tránh-tải-đè-file--cơ-chế-tự-động-đổi-tên-duy-nhất)
7. [7. Tổng Hợp Các Công Nghệ Sử Dụng](#7-tổng-hợp-các-công-nghệ-sử-dụng)

---

## 1. Tổng Quan Hệ Thống

Hệ thống **Thư Viện Tài Liệu Môn Học** là ứng dụng phân tán theo mô hình Client-Server được xây dựng trên nền tảng ngôn ngữ Java. Ứng dụng cung cấp giải pháp chia sẻ tài liệu và quản lý thông tin học tập hiệu năng cao. Để tối ưu hóa từng tác vụ chuyên biệt, hệ thống tích hợp đồng thời **3 giao thức mạng**:

* ⚡ **TCP**: Truyền nhận tệp tin dung lượng lớn an toàn, đảm bảo tính toàn vẹn dữ liệu.
* 📡 **UDP**: Phát broadcast thông điệp thời gian thực đến toàn bộ máy trạm mà không cần duy trì kết nối liên tục.
* 🔗 **RMI**: Quản trị, điều khiển từ xa thông qua việc gọi hàm trực tiếp trên máy chủ.

---

## 2. Cấu Trúc Thư Mục Dự Án

Mã nguồn được phân tách rõ ràng thành các package nghiệp vụ giúp việc bảo trì dễ dàng:

```text
Baitaplon_Nhom7/
│
├── src/                                  # Toàn bộ mã nguồn Java của dự án
│   ├── may_chu/                          # Phía Server (Chạy ngầm hoặc Docker)
│   │   ├── chay_may_chu.java             # -> Entry point: Khởi động hệ thống socket & DB
│   │   ├── MongoKetNoi.java              # -> Kết nối MongoDB (Singleton Pattern)
│   │   ├── may_chu_tcp.java              # -> Xử lý upload/download/tìm kiếm qua TCP
│   │   ├── may_chu_udp.java              # -> Phát broadcast thông báo cập nhật qua UDP
│   │   ├── dich_vu_rmi.java              # -> Interface RMI khai báo các hàm quản trị từ xa
│   │   └── dich_vu_rmi_impl.java        # -> Cài đặt RMI: Quản lý danh mục, tag, thống kê
│   │
│   ├── client_ui/                        # Phía Client (Giao diện người dùng)
│   │   ├── client_ui.java               # -> Cửa sổ chính thiết kế bằng Java Swing
│   │   ├── client_ui.form               # -> File cấu trúc GUI Swing trong NetBeans
│   │   ├── CauHinh.java                 # -> Lưu địa chỉ IP Server LAN/VPN ảo
│   │   ├── ket_noi_tcp.java             # -> Thực hiện giao dịch truyền dữ liệu TCP
│   │   ├── goi_rmi.java                 # -> Thực hiện truy vấn hàm từ xa RMI
│   │   ├── nhan_udp.java                # -> Luồng ngầm lắng nghe thông điệp UDP Broadcast
│   │   └── WrapLayout.java              # -> Layout dạng lưới tự thích ứng (Responsive Grid)
│   │
│   ├── chucnang/                         # Module tiện ích chia sẻ dùng chung
│   │   ├── truyen_tai_file.java          # -> Phân mảnh (Byte Streaming) chia file thành chunk 4KB
│   │   └── giao_dien_phu.java           # -> Cửa sổ tra cứu phụ
│   │
│   ├── chucnang2/                        # Module mở rộng giao diện
│   │   └── GiaoDienChonTag.java          # -> Hộp thoại popup chọn danh mục và tag thông minh
│   │
│   ├── thongbao/                         # Module xử lý trạng thái mạng
│   │   ├── KiemTraKetNoi.java            # -> Socket TCP ping test với timeout 3 giây
│   │   └── HopThoaiThongBao.java        # -> Thông báo trực quan trạng thái kết nối Xanh/Đỏ
│   │
│   ├── resources/                        # Tài nguyên tĩnh của ứng dụng
│   │   └── icontl.png                    # -> Icon đại diện Card tài liệu
│   │
│   └── luutru/                           # Thư mục lưu trữ vật lý trên đĩa cứng
│       ├── upload/                       # -> Kho chứa file trên Server
│       └── download/                     # -> Thư mục lưu file đã tải về của Client
│
├── lib/                                  # Thư viện ngoài điều khiển MongoDB
│   ├── mongodb-driver-sync-4.11.1.jar
│   ├── mongodb-driver-core-4.11.1.jar
│   └── bson-4.11.1.jar
│
├── picture/                              # Hình ảnh phục vụ tài liệu
│   └── icontl.png
│
├── Dockerfile                            # Dockerfile đóng gói mã nguồn Server
├── docker-compose.yml                    # Thiết lập liên kết Container Server và MongoDB
├── .env                                  # Cấu hình IP Host và URI Database
├── build.xml                             # Ant build script
└── readme.md                             # Hướng dẫn nhanh sử dụng dự án
```

---

## 3. Kiến Trúc Kỹ Thuật & Cơ Chế Hoạt Động

### 3.1 Luồng Khởi Động Server

Tiến trình khởi chạy phía máy chủ tuân theo sơ đồ tuyến tính nghiêm ngặt để đảm bảo tất cả các cổng mạng và cơ sở dữ liệu được mở an toàn trước khi chấp nhận yêu cầu của Client:

```text
               [Khởi chạy chay_may_chu.main()]
                             │
            ┌────────────────┴────────────────┐
            ▼                                 ▼
   [Tạo thư mục đĩa cứng]           [MongoKetNoi.khoiDong()]
   (upload/ & download/)            (Kết nối MongoDB, timeout 5s)
            │                                 │
            └────────────────┬────────────────┘
                             │
                             ▼
                  [Đăng ký RMI Registry]
                  (Cổng 1099 - dich_vu_rmi_impl)
                             │
                             ▼
                  [Khởi chạy Socket TCP]
                  (Cổng 8888 - may_chu_tcp)
                             │
                             ▼
                   [Đăng ký ShutdownHook]
                  (Đóng kết nối DB an toàn khi dừng app)
```

---

### 3.2 Giao Thức TCP (Port 8888) — Truyền Tải Dữ Liệu Nặng

TCP được sử dụng cho các tác vụ đòi hỏi sự toàn vẹn dữ liệu tuyệt đối và xử lý gói tin lớn bao gồm: **Tải lên**, **Tải xuống** và **Tìm kiếm**. Các yêu cầu được mã hóa dưới dạng chuỗi văn bản phân tách bằng ký tự đặc biệt `|`.

#### A. Giao thức Upload tài liệu (`tailen`)
* **Cú pháp gửi tin từ Client:**
  ```text
  tailen | [tên file] | [kích thước byte] | [danh mục] | [các thẻ tag]
  ```
  *(Ví dụ: `tailen|BaitapLTM.pdf|4200000|Bài tập lớn|java, socket`)*

* **Quy trình Server xử lý:**
  1. Kiểm tra kích thước file trong header. Nếu vượt quá **100MB**, lập tức đóng socket và phản hồi lỗi từ chối nhằm bảo vệ đĩa cứng máy chủ.
  2. Tạo luồng ghi dữ liệu vào tệp tin đích `src/luutru/upload/[tên file]`.
  3. Lắp ghép các gói tin nhỏ truyền lên (Byte Streaming) cho đến khi đủ số lượng byte khai báo.
  4. Ghi thông tin Metadata vào cơ sở dữ liệu MongoDB.
  5. Kích hoạt UDP Broadcast thông báo có tài liệu mới trên toàn mạng LAN.

#### B. Giao thức Download tài liệu (`taixuong`)
* **Cú pháp gửi tin:** `taixuong | [tên file]`
* **Quy trình Server xử lý:**
  1. Truy vấn kiểm tra file vật lý trên đĩa cứng Server.
  2. Trả lời Client thông điệp: `ok | [kích thước file]` để Client cấp phát bộ đệm RAM.
  3. Thực hiện truyền luồng byte nhị phân về phía máy khách theo các khối đệm.
  4. Tăng trường thống kê số lượt tải (`luot_tai`) của tài liệu trong MongoDB thêm **+1**.

#### C. Giao thức Tìm kiếm tài liệu (`timkiem`)
* **Cú pháp gửi tin:** `timkiem | [từ khóa]`
* **Quy trình Server xử lý:**
  1. Thực hiện Regex Query tìm kiếm theo tên tệp trong MongoDB.
  2. Gom danh sách kết quả, mã hóa thành dạng chuỗi ghép: `FileA.pdf;;FileB.docx;;`.
  3. Trả về Client qua TCP Socket để phân tách và hiển thị lên UI.

---

### 3.3 Cơ Chế Byte Streaming — Bảo Toàn Định Dạng File Tuyệt Đối

> [!NOTE]
> **Byte Streaming (Luồng byte nhị phân)** là chìa khóa giúp ứng dụng truyền nhận thành công mọi định dạng tệp tin từ văn bản đến tệp thực thi nén như PDF, DOCX, ZIP, EXE mà không bị lỗi hỏng cấu trúc (corruption) hay lỗi font chữ.

#### Bản chất kỹ thuật
1. **Dữ liệu dạng số nguyên nguyên bản:** Hệ thống xử lý thông tin ở cấp độ thấp nhất. File không được biên dịch hay đọc văn bản, mà được coi là chuỗi các byte nhị phân liên tục có giá trị từ `-128` đến `127`.
2. **Kỹ thuật chia nhỏ khối đệm (Chunking 4KB):**
   * Nếu nạp trực tiếp toàn bộ tệp tin dung lượng lớn (ví dụ 80MB) vào bộ nhớ RAM một lúc để gửi đi, JVM sẽ lập tức rơi vào trạng thái tràn bộ nhớ đệm `java.lang.OutOfMemoryError`.
   * Lớp `truyen_tai_file.java` giải quyết vấn đề bằng việc khai báo mảng đệm cố định **`byte[] buffer = new byte[4096]` (4KB)**. Dữ liệu được đọc tuần tự từ đĩa, đẩy qua đường truyền Socket mạng và ghi trực tiếp xuống đĩa ở đầu nhận.

```text
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

Với mô hình truyền dẫn tuần tự trên, bộ nhớ RAM tiêu thụ luôn duy trì ở mức tối thiểu (vài Kilobytes) bất kể file nặng bao nhiêu, đồng thời đảm bảo tính chính xác tuyệt đối từng bit dữ liệu.

---

### 3.4 Giao Thức UDP (Port 9999) — Phát Thông Báo Realtime Mạng LAN

> [!TIP]
> Giao thức UDP (User Datagram Protocol) hoạt động theo cơ chế không hướng kết nối (Connectionless). Do đó, nó cực kỳ phù hợp cho tác vụ truyền thông tin broadcast thời gian thực tốc độ cao mà không gây quá tải tài nguyên mạng của Server.

```text
[Sự kiện: Có tài liệu mới được Upload hoặc Xóa trên Server]
                             │
                             ▼
      [Server phát DatagramPacket đến IP: 255.255.255.255]
                             │
       ┌─────────────────────┼─────────────────────┐
       ▼                     ▼                     ▼
[Client Máy A]        [Client Máy B]        [Client Máy C]
Lắng nghe Port 9999   Lắng nghe Port 9999   Lắng nghe Port 9999
       │                     │                     │
       ▼                     ▼                     ▼
Cập nhật Sidebar      Cập nhật Sidebar      Cập nhật Sidebar
Tự động làm mới danh sách tài liệu hiển thị trên màn hình chính
```

Nhờ cơ chế này, toàn bộ mạng phân tán luôn được đồng bộ hóa tức thì ngay khi có biến động về dữ liệu mà không cần người dùng click nút làm mới thủ công.

---

### 3.5 Giao Thức RMI (Port 1099) — Quản Trị Từ Xa & Tự Chữa Lành

RMI (Remote Method Invocation) cho phép máy trạm gọi trực tiếp các phương thức nghiệp vụ đang chạy trong môi trường JVM của Server giống như gọi các phương thức local:

```java
// Client gọi trực tiếp qua interface đăng ký:
dich_vu_rmi dichvu = (dich_vu_rmi) registry.lookup("dichvurmi");
String thongKe = dichvu.thongke_luottai(); // Hàm thực thi trực tiếp trên Server và trả kết quả về Client!
```

#### 🩹 Giải pháp RMI Tự Chữa Lành (Self-Healing Stub Binding)
Java RMI mặc định rất dễ gặp lỗi khi chạy qua các lớp mạng trung gian (như VPN ảo Radmin) hoặc Docker Container:
1. **Lỗi Cổng Ngẫu Nhiên (RMI Ephemeral Ports):** RMI mặc định mở cổng ngẫu nhiên để truyền nhận Stub dữ liệu sau khi bắt tay ở cổng `1099`. Các cổng này sẽ bị tường lửa Windows chặn.
   * **👉 Giải pháp:** Gọi tường minh hàm khởi tạo `super(1099)` tại lớp cài đặt `dich_vu_rmi_impl.java` để ghim cứng luồng truyền tin duy nhất trên cổng `1099`.
2. **Lỗi RMI Hostname (Connection Refused):** RMI yêu cầu gán địa chỉ `java.rmi.server.hostname`. Nếu máy chủ tắt VPN hoặc chạy thử nghiệm cục bộ (Localhost), Client sẽ không kết nối được nếu Server cố định IP VPN.
   * **👉 Giải pháp (Self-healing):** Server quét toàn bộ các Card mạng vật lý (Network Interfaces) khi khởi động. Nếu phát hiện IP VPN ảo trong cấu hình bị tắt, Server tự động **fallback IP RMI về `localhost`** giúp việc kiểm thử nội bộ không bao giờ bị gián đoạn.

---

### 3.6 MongoDB — Lưu Trữ Metadata Vĩnh Viễn & Mô Hình Phân Tán 3 Lớp

Hệ thống được thiết kế theo kiến trúc chuẩn **3-Tier Distributed Architecture**. Máy trạm (Client) tuyệt đối không được phép kết nối trực tiếp đến cơ sở dữ liệu nhằm bảo vệ an toàn thông tin:

```text
┌────────────────────────┐              ┌────────────────────────┐              ┌────────────────────────┐
│   LỚP 1: CLIENT APP   │  TCP / RMI   │  LỚP 2: SERVER APP     │   localhost   │   LỚP 3: MONGODB DB    │
│  (Máy trạm người dùng) ├─────────────►│  (Trung gian xử lý)    ├─────────────►│ (Cô lập trong Docker)  │
│                        │              │                        │              │  Port: 27020           │
└────────────────────────┘              └────────────────────────┘              └────────────────────────┘
```

#### Phân chia vai trò lưu trữ thông tin:

| Nơi Lưu Trữ | Loại Dữ Liệu | Vai Trò & Lý Do Kỹ Thuật |
| :--- | :--- | :--- |
| **Đĩa cứng Server (`upload/`)** | File nhị phân gốc (PDF, DOCX...) | Tránh phình to dung lượng RAM và cơ sở dữ liệu MongoDB. |
| **MongoDB (`tai_lieu` collection)**| Metadata của tệp tin | Lưu trữ thông tin tên file, dung lượng, danh mục, tag và số lượt tải để phục vụ query tìm kiếm cực nhanh. |
| **MongoDB (`danh_muc` collection)**| Danh mục tài liệu | Lưu trữ vĩnh viễn các nhóm phân loại, đồng bộ hóa xuống Client qua TCP/RMI động. |
| **MongoDB (`tag` collection)** | Các thẻ tag gợi ý | Gợi ý tự động cho người dùng khi tải lên tài liệu mới. |

*Cấu trúc một bản ghi tài liệu trong MongoDB:*
```json
{
  "_id": {"$oid": "664c781a7b8e1a123f11a4bb"},
  "ten_file": "BaoCaoLTM_Nhom7.pdf",
  "kich_thuoc": 4512900,
  "danh_muc": "Báo cáo",
  "tags": ["java", "network", "socket"],
  "luot_tai": 142,
  "ip_nguoi_gui": "26.18.244.135",
  "ngay_upload": "Sun May 24 21:55:00 ICT 2026"
}
```

---

### 3.7 Cơ Chế Bảo Vệ 2 Lớp (Giới Hạn 100MB)

Để phòng chống các cuộc tấn công từ chối dịch vụ (DoS) bằng việc gửi file dung lượng khổng lồ làm đầy ổ cứng Server, hệ thống áp dụng bộ lọc bảo vệ kép:

```text
Người dùng chọn file Upload (150MB)
               │
               ▼
   [LỚP 1: CLIENT-SIDE CHECK] ─── (150MB > 100MB) ───► Xuất thông báo lỗi lập tức
   (Ngăn chặn không gửi bất cứ byte nào lên mạng)       Dừng giao dịch truyền file
               │
    (Nếu vượt qua Lớp 1)
               │
               ▼
   [LỚP 2: SERVER-SIDE CHECK] ─── (Đọc dung lượng ở Header) ───► Từ chối đóng socket ngay
   (may_chu_tcp.java chặn bypass từ Client giả lập)              Giải phóng luồng kết nối
```

---

### 3.8 Kết Nối Từ Xa Qua Mạng Riêng Ảo Radmin VPN

Khi phát triển ứng dụng phân tán, khó khăn lớn nhất là kết nối các máy trạm ngoài Internet do IP công cộng của nhà mạng luôn bị thay đổi và chặn cổng bởi Router. Hệ thống sử dụng **Radmin VPN** để thiết lập một đường truyền mã hóa an toàn, biến mạng Internet diện rộng thành mạng cục bộ ảo:

```text
[Máy Chủ Server (Tại Nhà)]                [Máy Khách Client (Tại Trường)]
IP Radmin: 26.18.244.131                   IP Radmin: 26.18.244.135
       │                                                 │
       └────────────────── Đường Truyền VPN ─────────────┘
                     (Mã hóa lưu lượng đầu-cuối)
```

---

### 3.9 Module Thông Báo & Ping Kết Nối Trực Quan

Nhóm xây dựng package `thongbao` giúp kiểm tra sức khỏe của dịch vụ mạng trước khi cho phép người dùng đăng nhập hệ thống:

```text
                [Client nhập IP máy chủ]
                           │
                           ▼
              [TCP Ping Test - Timeout 3s]
             (KiemTraKetNoi.kiemTra(ip, 8888))
                           │
            ┌──────────────┴──────────────┐
            ▼                             ▼
     [PING THÀNH CÔNG]             [PING THẤT BẠI]
     HopThoaiThongBao              HopThoaiThongBao
     🟢 Hộp thoại nền xanh         🔴 Hộp thoại nền đỏ
     (Tự động đóng vào app)        (Gợi ý khắc phục Radmin VPN)
```

---

### 3.10 Cơ Chế Xóa Tài Liệu Vĩnh Viễn & Đồng Bộ Mạng Tức Thì

Tính năng xóa tài liệu được xây dựng đồng bộ 3 tầng để tránh dữ liệu rác tồn đọng:

1. **Client UI:** Người dùng click chuột phải vào Card tài liệu -> Chọn **Xóa tài liệu vĩnh viễn**. Xuất hiện cảnh báo xác nhận `JOptionPane.WARNING_MESSAGE`. Nếu đồng ý, gửi lệnh TCP `xoatailieu|[tên file]` lên Server.
2. **Server Wipeout:** Server nhận lệnh thực hiện xóa song song:
   * Loại bỏ tài liệu khỏi MongoDB: `col.deleteOne(Filters.eq("ten_file", tenfile))`.
   * Xóa tệp tin vật lý trên đĩa cứng: `new File("src/luutru/upload/" + tenfile).delete()`.
3. **UDP Broadcast Sync:** Server phát gói tin UDP `DELETE|[tên file]` tới toàn bộ mạng. Luồng `nhan_udp` trên tất cả Client bắt được gói tin, ghi log vào Sidebar và tự kích hoạt hàm `lamMoiDanhSach()` để xóa Card tệp tin khỏi màn hình tức thì.

---

### 3.11 Cơ Chế Ghi Nhật Ký Hệ Thống (Logging System)

Để giám sát luồng dữ liệu 24/7, máy chủ tích hợp Module ghi nhật ký tự động. Toàn bộ các sự kiện khởi tạo dịch vụ, kết nối Socket Client, truy vấn MongoDB hay lỗi ngoại lệ đều được ghi nhận kèm dấu thời gian thực tế vào tệp tin `server.log`.

---

### 3.12 Quản Lý Socket Timeout Chống Treo Tài Nguyên

Nếu một máy trạm bị mất mạng đột ngột (hoặc tắt ứng dụng không đúng cách) trong khi đang kết nối TCP, Socket ở phía máy chủ sẽ bị treo vô hạn dẫn đến cạn kiệt tài nguyên Thread. Hệ thống khắc phục triệt để bằng cách thiết lập `socket.setSoTimeout(5000)` (5 giây). Quá thời gian này không nhận được dữ liệu mới, Server sẽ tự giải phóng Socket đó an toàn.

---

## 4. Deployment và Vận Hành Với Docker Container

Để đơn giản hóa quá trình cài đặt cơ sở dữ liệu MongoDB và khởi chạy các cổng dịch vụ Java, toàn bộ cấu trúc Server đã được container hóa:

* **Tệp cấu hình biến môi trường `.env`:**
  ```env
  RMI_HOSTNAME=26.18.244.131
  MONGODB_URI=mongodb://emr:123456@host.docker.internal:27020/?authSource=admin
  ```

* **Khởi động nhanh Server:**
  ```bash
  docker-compose up -d --build
  ```

* **Theo dõi nhật ký hệ thống thời gian thực:**
  ```bash
  docker logs -f server_thuvien
  ```

---

## 5. Hướng Dẫn Cài Đặt & Chạy Kiểm Thử

### Phía Server
1. Cấu hình IP Radmin VPN của máy chủ vào file `.env`.
2. Khởi chạy Docker Desktop.
3. Chạy lệnh: `docker-compose up -d --build` tại thư mục gốc.

### Phía Client
1. Mở thư mục dự án bằng **NetBeans IDE**.
2. Thực hiện **Clean and Build** để nạp driver MongoDB JARs trong thư mục `lib/`.
3. Chạy tệp tin `src/client_ui/client_ui.java`.
4. Điền địa chỉ IP máy chủ (Localhost hoặc IP Radmin VPN máy chủ) -> Vào giao diện chính và trải nghiệm.

---

## 6. Phân Tích Sự Cố Thực Tế & Các Giải Pháp Tối Ưu Core

### 6.1 Hiện Tượng Tranh Chấp Lượt Tải (Race Condition)

#### 🛑 Hiện tượng lỗi
Khi Client thực hiện tải xuống một tệp tin thành công, luồng xử lý UI của Client lập tức gọi hàm tự động làm mới `lamMoiDanhSach()` để cập nhật số lượt tải hiển thị. Tuy nhiên trên giao diện của Client vẫn hiển thị con số cũ. Chỉ khi bấm **Làm mới** thủ công sau đó vài giây, con số mới được cập nhật chính xác (mặc dù kiểm tra MongoDB tại thời điểm tải file thành công, trường `luot_tai` đã được cộng 1).

#### 🔍 Phân tích nguyên nhân
Do Client tải file bằng một tiến trình Thread bất đồng bộ chạy riêng để tránh đơ giao diện Swing. Quy trình chạy ban đầu của Server diễn ra như sau:
* **Bước A:** Truyền dữ liệu file nhị phân qua TCP Socket (`truyen_tai_file.gui_file`).
* **Bước B:** Cập nhật cộng dồn lượt tải vào cơ sở dữ liệu MongoDB.

Vì tốc độ truyền gói dữ liệu trong mạng cục bộ LAN/localhost cực kỳ nhanh, luồng nhận dữ liệu của Client hoàn tất việc đọc tệp tin trước khi Server chuyển sang thực thi **Bước B**. Ngay khi tải xong, Client phát lệnh TCP `timkiem` để vẽ lại màn hình. Lúc này kết nối truy vấn đến Server trước khi giao dịch ghi DB ở Bước B hoàn tất, dẫn đến việc lấy ra dữ liệu cũ.

```text
TIẾN TRÌNH SERVER: ───[Gửi File qua TCP]───────► [Ghi Lượt Tải MongoDB] ───► (Hoàn tất ghi)
                                                     ▲
                                    Gây ra tranh chấp │ (Truy vấn lúc này vẫn lấy lượt tải cũ)
                                                     │
TIẾN TRÌNH CLIENT: ───[Nhận File xong]───► [Gửi Lệnh TimKiem vẽ lại UI] ───► (Vẽ UI cũ)
```

#### 🩹 Giải pháp khắc phục tối ưu
Tái cấu trúc lại luồng nghiệp vụ trong tệp tin `may_chu_tcp.java`:
* **Đảo ngược trình tự:** Server tiến hành **cập nhật lượt tải vào MongoDB trước**, sau đó mới thực hiện **gửi luồng dữ liệu file qua TCP**.
* **Hiệu quả:** Trong khoảng thời gian file truyền tải vật lý qua card mạng, thông tin lượt tải mới đã ghi xong vào database. Nhờ đó, lệnh refresh UI của Client khi tải xong luôn lấy được dữ liệu mới nhất, đảm bảo tính đồng bộ 100%.

---

### 6.2 Lỗi Định Tuyến RMI Docker NAT & Giải Pháp TCP Fallback

#### 🛑 Hiện tượng lỗi
Khi chạy ứng dụng ở chế độ **Local** ngoại tuyến (không bật Radmin VPN hoặc mất kết nối mạng), cuộc gọi RMI lấy danh mục và danh sách tag gợi ý bị treo cứng (Timeout) hoặc báo lỗi kết nối `Connection Refused`. Hai ComboBox bộ lọc phân loại trên Client bị trống.

#### 🔍 Phân tích nguyên nhân
Trong môi trường ảo hóa Docker Container, cổng RMI Registry `1099` được map ra ngoài thành công nhưng Server Java bên trong Container bị gán IP VPN cố định thông qua biến `RMI_HOSTNAME` (ví dụ `26.18.244.131`). Khi Client gọi lookup từ local, RMI Registry trả về đối tượng Stub yêu cầu Client định tuyến kết nối dữ liệu đến IP Radmin đó. Nếu IP này đang offline, Client sẽ không thể kết nối được gây treo ứng dụng.

#### 🩹 Giải pháp khắc phục tối ưu
* **Xây dựng đường truyền TCP dự phòng (TCP Fallback Tunneling):** Vì cổng TCP `8888` cực kỳ ổn định và hoạt động hoàn hảo trong mọi chế độ mạng (Local/LAN/VPN), nhóm phát triển đã bổ sung hai lệnh TCP mới: `laytat_danhmuc` và `laytat_tag` trên tệp `may_chu_tcp.java`.
* **Đồng bộ hóa giao thức:** Client chuyển hoàn toàn cơ chế lấy danh mục và gợi ý thẻ tag khi tải lên hoặc lọc danh sách sang kết nối qua **TCP**. Điều này giúp bộ lọc hoạt động mượt mà, đồng bộ dữ liệu MongoDB 100% bất kể RMI có bị tường lửa hay NAT Docker chặn.

---

### 6.3 Cơ Chế Quét Dữ Liệu Gợi Ý Tự Chữa Lành (Self-Healing Dynamic Scan)

#### 🛑 Thách thức kỹ thuật
Khi khởi tạo hệ thống lần đầu tiên, hai bộ sưu tập `danh_muc` và `tag` trên MongoDB hoàn toàn **trống rỗng** (do quản trị viên chưa thiết lập danh sách gợi ý). Điều này khiến bộ lọc Dropdown trên Client bị trắng hoàn toàn và người dùng không thể phân loại tài liệu khi upload.

#### 🩹 Giải pháp khắc phục
Nhóm bổ sung thuật toán quét hai lớp (Two-Layer Dynamic Scan) vào mã nguồn Server TCP:
1. **Lớp 1:** Đọc toàn bộ danh sách phân loại do admin định nghĩa trước trong DB.
2. **Lớp 2 (Tự chữa lành):** Nếu danh sách trống, Server tự động quét duyệt qua toàn bộ cơ sở dữ liệu `tai_lieu` đang có thực tế để trích xuất các từ khóa danh mục và tag do người dùng tự nhập trước đó để làm dữ liệu gợi ý.
* **Hiệu quả:** Bộ lọc và bảng gợi ý luôn được cập nhật đầy đủ và phản ánh chính xác nhất dữ liệu thực tế đang tồn tại trong thư viện tài liệu.

---

### 6.4 Tự Động Co Giãn Spacing UX (UX Auto-scaling)

* **Vấn đề giao diện bị co vỡ:** Khi chạy ứng dụng trên các màn hình có độ phân giải khác nhau (Full HD, 2K, 4K), các phần tử đồ họa Swing dễ bị dồn ép hoặc vỡ layout lưới 150x150 của thẻ tài liệu.
* **👉 Giải pháp:** Ghim cứng kích thước khởi động của Client Swing là **`800x800 px`** (`setSize(800, 800)`) và thực hiện tự động căn giữa màn hình (`setLocationRelativeTo(null)`). Layout hiển thị được dàn đều, thoáng đãng, mang lại trải nghiệm chuyên nghiệp nhất cho người sử dụng.

---

### 6.5 Tránh Tải Đè File & Cơ Chế Tự Động Đổi Tên Duy Nhất (Auto-renaming)

* **Vấn đề:** Khi người dùng thực hiện tải xuống một tài liệu trùng tên nhiều lần về thư mục `src/luutru/download/`, mặc định hệ thống sẽ ghi đè trực tiếp làm mất dữ liệu cũ của phiên bản trước đó.
* **👉 Giải pháp:** Nhóm xây dựng hàm xử lý tệp tin thông minh `lay_file_dich_duy_nhat` trong lớp `truyen_tai_file.java`. Trước khi ghi dữ liệu xuống ổ đĩa Client, hệ thống tự động quét kiểm tra sự tồn tại của file. Nếu trùng tên, thuật toán sẽ tự chèn thêm hậu tố số thứ tự tăng dần dạng `(1)`, `(2)`, v.v. vào trước đuôi định dạng (ví dụ: `BaoCaoLTM (1).pdf`), bảo toàn nguyên vẹn mọi phiên bản dữ liệu tải về.

---

## 7. Tổng Hợp Các Công Nghệ Sử Dụng

Hệ thống kết hợp hài hòa các công nghệ và thư viện chuẩn công nghiệp:

| Công Nghệ | Phiên Bản | Mục Đích Sử Dụng |
| :--- | :--- | :--- |
| **Java Platform** | Standard Edition 8 | Ngôn ngữ cốt lõi phát triển toàn bộ hệ thống. |
| **Java Swing** | Tích hợp sẵn trong JDK | Thiết kế giao diện đồ họa người dùng trực quan. |
| **TCP Socket** | Giao thức TCP tin cậy | Truyền tải tệp tin dung lượng lớn (Upload/Download). |
| **UDP Datagram** | Cổng 9999 | Broadcast trạng thái mạng và sự kiện thời gian thực. |
| **Java RMI** | Cổng 1099 | Gọi hàm quản trị dữ liệu, lượt tải, thống kê từ xa. |
| **MongoDB** | Phiên bản 8.0 | Cơ sở dữ liệu NoSQL lưu trữ phi cấu trúc Metadata tệp tin. |
| **MongoDB Sync Driver**| v4.11.1 | Lớp kết nối trung gian giữa mã nguồn Java và MongoDB. |
| **Docker Engine** | v25.x + | Ảo hóa, cô lập máy chủ Server và Database. |
| **Radmin VPN** | VPN Client ảo | Cầu nối thiết lập đường truyền mạng LAN nội bộ qua Internet. |

---

*Báo cáo được hoàn thiện bởi thành viên Nhóm 7 — Học kỳ 2025.2.*
