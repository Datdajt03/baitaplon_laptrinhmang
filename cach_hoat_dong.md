# 📖 HƯỚNG DẪN VẬN HÀNH & CƠ CHẾ HOẠT ĐỘNG
### 🌐 Hệ Thống Phân Tán Thư Viện Tài Liệu Môn Học — Nhóm 7

> [!NOTE]
> * Để xem hướng dẫn cài đặt và thiết lập nhanh dự án bằng Docker & NetBeans, vui lòng xem [Hướng dẫn cài đặt readme.md](./readme.md).
> * Để tìm hiểu sâu về kiến trúc hệ thống 3 lớp và các phân tích lỗi nâng cao, vui lòng xem [Báo cáo kỹ thuật baocao.md](./baocao.md).

---

Tệp tin này mô tả chi tiết, trực quan cách hệ thống hoạt động từ lúc khởi động, thiết lập liên kết mạng, đến từng bước xử lý dữ liệu chi tiết của các tính năng cốt lõi (Upload, Download, Đánh giá, Xóa file, Đồng bộ Sidebar).

---

## 📌 1. Sơ Đồ Tổng Quan Vận Hành Hệ Thống

Hệ thống hoạt động dựa trên sự phối hợp nhịp nhàng giữa **Client (Java Swing)** và **Server (Docker Container)** thông qua 3 giao thức chính: **TCP (8888)**, **UDP (9999)** và **RMI (1099)** kết nối tới **MongoDB (27020)**.

```text
    [ Docker Server ] ◄─── (Cổng 27020 - Localhost) ───► [ MongoDB Database ]
       │      ▲      ▲
       │      │      │
    RMI:   TCP:   UDP:
    1099   8888   9999
       │      │      │
       ▼      ▼      ▼
    [ Client A (Swing) ] ◄────────── (Mạng LAN / Radmin VPN) ──────────► [ Client B (Swing) ]
```

---

## ⚙️ 2. Luồng Khởi Động & Thiết Lập Kết Nối

Khi khởi chạy hệ thống, trình tự thiết lập kết nối diễn ra tự động qua các bước nghiêm ngặt sau:

### Bước 1: Máy Chủ (Server) Lên Cấu Hình & Bật Service
1. **Docker Compose** kích hoạt song song Container **MongoDB** và Container **Server Java**.
2. **MongoDB** mở cổng bảo mật `27020` nội bộ.
3. **Server App** khởi chạy:
   * Kết nối tới MongoDB qua chuỗi URI xác thực trong `.env` (Timeout kết nối 5s).
   * Tạo sẵn các thư mục lưu trữ vật lý trên đĩa cứng: `src/luutru/upload/`.
   * Đăng ký đối tượng **RMI Stub** lên Registry ở cổng `1099`.
   * Mở Socket **TCP Server** lắng nghe tại cổng `8888`.
   * Sẵn sàng cổng **UDP Socket** `9999` phục vụ phát tin Broadcast.

---

### Bước 2: Máy Trạm (Client) Khởi Động & Tự Động Rẽ Nhánh Mạng
Khi người dùng chạy file `client_ui.java`, module `PhanLuong.java` thực hiện quét cổng mạng tự động:

```text
                     Khởi chạy Client UI
                              │
                     Thử Ping localhost:8888
                              │
             ┌────────────────┴────────────────┐
             ▼ (Thành Công)                    ▼ (Thất Bại)
     [ Chế độ LOCAL ]                 [ Chế Độ LAN / WAN ]
   - Nhận diện Server chạy          - Xuất hiện Popup yêu cầu
     trên cùng một máy trạm.          nhập IP Server vật lý.
   - Tự động liên kết vào           - Người dùng nhập IP Wi-Fi
     giao diện chính.                 hoặc IP Radmin VPN.
```

---

### Bước 3: Hộp Thoại Chẩn Đoán Sức Khỏe Mạng
Sau khi xác định địa chỉ IP máy chủ:
* Module `KiemTraKetNoi.java` thử mở một Socket TCP kết nối đến Server với thời gian chờ tối đa **3 giây** (Timeout).
* **Nếu Kết Nối Thành Công:** Xuất hiện hộp thoại `HopThoaiThongBao` **🟢 Nền Xanh Lá Cây** ghi rõ thông số mạng và tự đóng để chuyển vào giao diện chính sau 3 giây.
* **Nếu Kết Nối Thất Bại:** Xuất hiện hộp thoại **🔴 Nền Đỏ**, hiển thị cảnh báo lỗi định tuyến và đề xuất người dùng kiểm tra trạng thái Radmin VPN / Docker Server.

---

## 🛠️ 3. Quy Trình Hoạt Động Chi Tiết Của Các Tính Năng

### 📤 3.1 Luồng Upload Tài Liệu (Truyền tải TCP + Ghi DB NoSQL + Broadcast UDP)

Khi người dùng thực hiện tải lên một tài liệu mới:

```text
[ Client ] ──── 1. Bấm Tải Lên ───► [ Chọn File ] ───► [ Gọi RMI/TCP lấy Danh mục & Tag gợi ý ]
    │                                                            │
    ├── 2. Nhập thông tin, bấm Xác nhận ◄────────────────────────┘
    │
    ├── 3. Kiểm tra kích thước file:
    │      - Nếu > 100MB ──► Hiện cảnh báo, DỪNG ngay lập tức.
    │      - Nếu <= 100MB ──► Gửi lệnh TCP: "tailen|Ten.pdf|DungLuong|DanhMuc|Tags"
    │
    ▼
[ Server ] ─── 4. Đọc Header lệnh:
    │          - Nếu kích thước > 100MB (Bypass Client) ──► Đóng Socket ngay.
    │          - Nếu Hợp lệ ──► Mở tệp ghi "src/luutru/upload/Ten.pdf".
    │
    ├── 5. Nhận Byte Streaming theo từng khối đệm 4KB (Chunking) cho đến khi đủ dung lượng.
    │
    ├── 6. Kết nối MongoDB ──► Tạo Document Metadata ghi vào Collection "tai_lieu".
    │
    ├── 7. Phát gói tin UDP Broadcast: "UPLOAD|[tên file]" tới cổng 9999 trên toàn mạng.
    │
    ▼
[ Mạng LAN ] ─── 8. Luồng ngầm nhan_udp của TẤT CẢ Client đang online bắt được gói tin:
               - Ghi sự kiện lên Activity Sidebar bên trái: "[HH:mm:ss] Phát hiện file mới tải lên..."
               - Tự động kích hoạt luồng làm mới danh sách Card tài liệu trên giao diện.
```

---

### 📥 3.2 Luồng Download Tài Liệu (TCP Byte Streaming + Tự Động Đổi Tên)

Khi người dùng kích đúp chuột vào một Card tài liệu (Tải nhanh) hoặc bấm nút **Tải Xuống**:

1. **Client** gửi lệnh TCP: `taixuong|[tên file]` lên Server qua cổng `8888`.
2. **Server** nhận lệnh, kiểm tra sự tồn tại vật lý của file trong thư mục `src/luutru/upload/`.
3. **Server** gửi thông điệp phản hồi: `ok|[kích thước file byte]`.
4. **Server** cập nhật MongoDB: Tăng trường thống kê `luot_tai` của tài liệu đó thêm **+1** trong Database.
5. **Server** thực hiện đọc file và truyền dữ liệu dạng luồng byte nhị phân thô (`truyen_tai_file.gui_file`) theo các mảnh **4KB** liên tục.
6. **Client** nhận dữ liệu:
   * Quét kiểm tra thư mục lưu trữ đích `src/luutru/download/`.
   * Sử dụng thuật toán tự động quét tên file trùng: Nếu phát hiện tệp tin đã tồn tại, tự động thêm hậu tố thứ tự tăng dần dạng `(1)`, `(2)` (ví dụ: `Tailieu (1).pdf`) để **tránh ghi đè mất file cũ**.
   * Nhận các mảnh byte từ card mạng và lắp ghép hoàn chỉnh ghi xuống ổ đĩa cục bộ.

---

### ⭐ 3.3 Luồng Đánh Giá Sao Tài Liệu (Remote Method Invocation)

Tính năng tương tác cho phép người dùng đánh giá tài liệu từ 1 đến 5 sao:

```text
[ Client Swing ] ─── 1. Bấm chọn số sao (1-5) trên Card tài liệu.
       │
       ├── 2. Gọi hàm RMI từ xa: dichvurmi.danhGiaSao(tenFile, soSao)
       │
       ▼
[ Server RMI ]   ─── 3. Tiếp nhận cuộc gọi trên cổng 1099, truy vấn MongoDB.
       │
       ├── 4. Cập nhật trường dữ liệu điểm đánh giá trung bình của tài liệu.
       │
       ├── 5. Phát gói tin UDP Broadcast: "RATE|[tên file]|[số sao TB]" ra mạng.
       │
       ▼
[ Các Client ]   ─── 6. Lắng nghe UDP ──► Tự động cập nhật hiển thị điểm sao trung bình
                        trực tiếp trên giao diện Card mà không cần tải lại trang.
```

---

### 🗑️ 3.4 Luồng Xóa Tài Liệu Vĩnh Viễn (Wipeout & Realtime LAN Sync)

Khi người dùng có thẩm quyền thực hiện dọn dẹp hệ thống:

1. **Client UI:** Click chuột phải vào Card tài liệu -> Chọn **Xóa tài liệu vĩnh viễn**.
2. **Xác nhận an toàn:** Ứng dụng hiển thị Dialog cảnh báo `JOptionPane.WARNING_MESSAGE`. Người dùng phải nhấn **YES** để tiếp tục.
3. **TCP Lệnh:** Client gửi thông điệp `xoatailieu|[tên file]` lên Server.
4. **Server thực hiện xóa kép (Double Wipeout):**
   * **Database:** Kết nối MongoDB, xóa record metadata tương ứng `col.deleteOne(Filters.eq("ten_file", tenfile))`.
   * **Ổ cứng:** Tìm đường dẫn file trong `src/luutru/upload/` và gọi lệnh xóa vật lý `file.delete()`.
5. **UDP Broadcast Sync:** Server phát đi gói tin UDP `DELETE|[tên file]` tới toàn mạng LAN.
6. **Đồng bộ hóa giao diện:**
   * Giao diện của Client yêu cầu xóa hiển thị thông báo thành công.
   * Tiến trình `nhan_udp` trên các Client khác nhận được tin, tự ghi nhận sự kiện vào Activity Sidebar bên trái và xóa biến mất hoàn toàn Card tài liệu đó khỏi màn hình chính ngay lập tức.

---

## 📱 4. Bố Cục Và Cách Thao Tác Trên Giao Diện Client UI

Giao diện chính được chia thành 4 khu vực hoạt động trực quan:

```text
┌────────────────────────────────────────────────────────────────────────┐
│  Hệ Thống Thư Viện Tài Liệu Môn Học - Nhóm 7                           │
├───────────────────────┬────────────────────────────────────────────────┤
│ 📋 THANH HOẠT ĐỘNG    │ 📚 KHU VỰC HIỂN THỊ CHÍNH (Card Layout Grid)   │
│    (Activity Sidebar) │                                                │
│                       │   ┌───────────┐   ┌───────────┐   ┌───────────┐│
│  [21:55:01] Máy trạm  │   │ Tài liệuA │   │ Tài liệuB │   │ Tài liệuC ││
│  26.18.244.135 vừa    │   │  ⭐⭐⭐⭐  │   │  ⭐⭐⭐   │   │  ⭐⭐⭐⭐⭐ ││
│  tải lên:             │   │ [Tải Nhanh│   │ [Tải Nhanh│   │ [Tải Nhanh││
│  "BaoCaoLTM.pdf"      │   └───────────┘   └───────────┘   └───────────┘│
│                       ├────────────────────────────────────────────────┤
│  [21:56:12] Một người │ ⚙️ THANH ĐIỀU KHIỂN & BỘ LỌC                   │
│  dùng vừa xóa file:   │ [ Lọc Danh Mục: Tất Cả ]   [ Lọc Tag: Tất Cả ]  │
│  "Baitap_rac.zip"     │                                                │
│                       │ 🔄 [Làm Mới]   📤 [Tải Lên]   🔍 [Tìm Kiếm]    │
└───────────────────────┴────────────────────────────────────────────────┘
```

### 1. Tab Hiển Thị (Màn hình chính)
* **Khu vực lưới Card tài liệu (WrapLayout):** Hiển thị các ô vuông kích thước 150x150 trực quan cho từng tài liệu.
* **Cách Download nhanh:** Kích đúp chuột trái vào bất kỳ Card tài liệu nào để tải về ngay lập tức.
* **Cách Xóa nhanh:** Click chuột phải vào Card tài liệu -> Chọn **Xóa tài liệu vĩnh viễn**.
* **Activity Sidebar (Bên trái):** Hiển thị danh sách các hoạt động mạng theo thời gian thực (Realtime Logging Sidebar) nhận từ UDP socket.
* **Auto-Refresh Timer:** Bộ đếm thời gian ngầm tự cập nhật danh sách card tài liệu mới và đồng bộ bộ lọc ComboBox từ Server mỗi **10 giây**.

### 2. Tab TCP & UDP
* Hiển thị bảng nhật ký kỹ thuật, lịch sử dòng lệnh gửi nhận tệp tin và thông tin gói tin UDP Broadcast thô để phục vụ giám sát kỹ thuật.

### 3. Tab RMI (Quản Trị Cơ Sở Dữ Liệu)
* **Thêm danh mục / Thêm tag:** Cho phép người dùng thêm các bộ lọc gợi ý vào cơ sở dữ liệu MongoDB từ xa.
* **Thống Kê Lượt Tải:** Truy vấn trực tiếp MongoDB kết xuất danh sách tổng số lượt tải thực tế của từng tài liệu.

---

## 🩹 5. Hướng Dẫn Khắc Phục Sự Cố Nhanh (Troubleshooting)

### 🔴 Lỗi 1: Giao diện Client hiện bảng thông báo màu đỏ khi khởi động
* **Nguyên nhân:** Không Ping được tới TCP Socket của Server.
* **Cách xử lý:** 
  1. Kiểm tra xem Docker Server đã khởi chạy chưa bằng lệnh: `docker ps`.
  2. Nếu chạy qua Radmin VPN, kiểm tra xem cả máy khách và máy chủ đã tham gia chung một phòng VPN chưa và trạng thái đèn Radmin có màu xanh không.
  3. Kiểm tra xem IP nhập vào ô kết nối có trùng khớp hoàn toàn với IP ảo của Server trên Radmin VPN không.

### 🔴 Lỗi 2: Lấy được danh sách tài liệu nhưng không thể chọn Danh mục/Tag khi Upload
* **Nguyên nhân:** Lỗi kết nối cổng RMI do định tuyến Docker NAT (RMI Ephemeral Port bị chặn).
* **Cách xử lý:** 
  * Hệ thống đã tích hợp sẵn cơ chế **TCP Fallback Tunneling**. Hệ thống tự chuyển đổi toàn bộ cơ chế đồng bộ danh mục/tag sang cổng TCP `8888` để chạy ổn định.
  * Hãy đảm bảo cổng `8888` đã được mở (Port Forward) thành công trên Docker.
