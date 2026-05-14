# Hệ Thống Thư Viện Tài Liệu Môn Học (Document Library)

Hệ thống quản lý tài liệu phân tán được xây dựng bằng ngôn ngữ Java, tích hợp đa giao thức mạng (TCP, UDP, RMI) để tối ưu hóa hiệu suất và tính năng.

## 1. Tính Năng Nổi Bật
- **Hỗ trợ đa định dạng**: Truyền tải thực tế các file nhị phân như PDF, PowerPoint, Excel, Word, EXE... qua mạng.
- **Giao diện Tiếng Việt**: UI thân thiện, hỗ trợ đầy đủ ký tự tiếng Việt có dấu và ký tự đặc biệt.
- **Tab Hiển Thị Thông Minh**: Tích hợp tất cả các chức năng (Tìm kiếm, Tải lên, Tải xuống, Làm mới) tại một màn hình duy nhất.
- **Cơ chế Byte Streaming**: Sử dụng kỹ thuật băm file thành các mảng byte (chunks) để truyền tải ổn định, không gây tràn bộ nhớ RAM.

---

## 2. Kiến Trúc Kỹ Thuật
Dự án bao gồm 2 thành phần chính: **Máy chủ (Server)** và **Máy khách (Client)**.

### a. Giao thức TCP (Port 8888)
- **Truyền tải file thực tế**: Sử dụng `DataInputStream` và `DataOutputStream` để gửi/nhận dữ liệu byte.
- **Logic**:
  - **Tải lên**: Client băm file thành các gói 4KB và gửi qua socket. Server hứng dữ liệu và lắp ráp lại tại `src/luutru/upload/`.
  - **Tải xuống**: Server đọc file nhị phân và đẩy ngược về cho Client lưu vào `src/luutru/download/`.
  - **Tìm kiếm**: Tìm kiếm file theo tên trong kho lưu trữ của Server.

### b. Giao thức UDP (Port 9999)
- **Thông báo thời gian thực**: Khi có người dùng tải lên tài liệu mới, Server sẽ thực hiện **Broadcast** thông báo đến toàn bộ các máy khách đang online.
- **Hoạt động**: Luồng `nhan_udp` chạy ngầm ở Client sẽ liên tục lắng nghe và hiển thị thông báo ngay lập tức khi có tài liệu mới xuất hiện.

### c. Giao thức RMI (Port 1099)
- **Quản trị hệ thống**: Sử dụng phương thức gọi hàm từ xa (Remote Method Invocation) để quản lý các đối tượng trong RAM của Server.
- **Chức năng**: Quản lý danh mục tài liệu, gắn tag tìm kiếm và thống kê tổng lượt tải về trên toàn hệ thống.

---

## 3. Cấu Trúc Thư Mục & Phân Chia Trách Nhiệm
Hệ thống được thiết kế theo mô hình Client - Server và tách biệt rõ ràng các thành phần chức năng:

- **`src/may_chu/` (Server - Xử lý lõi)**: 
  - `chay_may_chu.java`: Khởi động toàn bộ các dịch vụ (RMI, TCP, UDP) và thiết lập môi trường (tự động tạo thư mục nếu chưa có).
  - `may_chu_tcp.java`: Xử lý việc upload/download file bằng byte streaming theo port 8888. Phân luồng cho từng Client kết nối đồng thời.
  - `may_chu_udp.java`: Đảm nhiệm việc phát Broadcast (Port 9999) ngay lập tức khi có tài liệu mới được tải lên để báo cho các máy khác.
  - `dich_vu_rmi_impl.java`: Thực thi các lệnh quản lý danh mục, tag và thống kê hệ thống trên máy chủ (Port 1099).

- **`src/client_ui/` (Client - Giao diện & Kết nối)**: 
  - `client_ui.java`: Cửa sổ giao diện chính của người dùng (vẽ bằng Java Swing). Xử lý các sự kiện click chuột và hiển thị.
  - `ket_noi_tcp.java`, `nhan_udp.java`, `goi_rmi.java`: Bộ 3 file "cầu nối" để Client giao tiếp với Server qua 3 giao thức tương ứng.
  - `CauHinh.java`: Lưu cấu hình địa chỉ IP động của Server (để kết nối từ xa).

- **`src/chucnang/` & `src/chucnang2/` (Các module tiện ích mở rộng)**:
  - Chứa `truyen_tai_file.java`: Công cụ chuyên dụng để băm nhỏ file (chunking) thành nhiều mảng byte và truyền đi an toàn.
  - Chứa `GiaoDienChonTag.java`: Cửa sổ popup (dialog) cho phép người dùng chọn danh mục, tự nhập hoặc bấm gợi ý tag trước khi tải tài liệu lên.

- **`src/luutru/` (Kho dữ liệu vật lý)**:
  - `upload/`: Thư mục nằm trên Server, lưu giữ tất cả các tài liệu mà toàn bộ hệ thống đã đóng góp.
  - `download/`: Thư mục nằm trên máy Client, đây là nơi chứa tài liệu sau khi người dùng chọn tải một file từ kho chung về máy cá nhân của mình.

---

## 4. Hướng Dẫn Sử Dụng

### A. Thiết lập Máy chủ (Server) bằng Docker
Hệ thống Server hiện tại được thiết kế để chạy độc lập và ổn định 24/24 qua Docker.
1. **Mở file `.env`** ở thư mục gốc và cấu hình IP của máy tính sẽ chạy Server. Nếu chạy ở mạng LAN nội bộ, điền IP Wi-Fi (Vd: `192.168.1.5`). Nếu dùng mạng ảo Radmin VPN để kết nối qua Internet, hãy điền IP Radmin của máy chủ (Vd: `26.18.244.131`).
2. **Khởi chạy Server**: Mở Terminal/CMD tại thư mục dự án và chạy lệnh:
   ```bash
   docker-compose up -d --build
   ```
   *(Server sẽ tự động compile code và chạy ngầm ở port 8888, 1099, 9999).*

### B. Sử dụng Máy khách (Client)
1. **Khởi chạy**: Chạy file `src/client_ui/client_ui.java` trên bất kỳ máy tính nào cùng mạng LAN.
2. **Kết nối**: Ngay khi mở app, một hộp thoại sẽ hiện lên yêu cầu nhập IP Server. Hãy nhập đúng IP bạn đã cấu hình ở bước trên (vd: `192.168.1.5`).
3. **Tải lên**: Tại tab **Hiển thị**, chọn nút **Tải lên**, chọn tài liệu và điền danh mục, tag (hỗ trợ gợi ý tag tự động).
4. **Tải xuống**: Chọn file từ danh sách hoặc bấm **Tải xuống**, file sẽ được lưu vào thư mục `src/luutru/download/`.
5. **Quản trị**: Sử dụng tab **Chức năng RMI** để thêm danh mục hoặc xem thống kê lượt tải.

### C. Kết nối từ xa qua Internet (Khuyên dùng Radmin VPN / ZeroTier)
Để các máy tính ở xa (khác mạng Wi-Fi, ví dụ mang lên trường học) vẫn kết nối được về máy chủ ở nhà:
1. Cài đặt **Radmin VPN** trên cả máy chủ (chạy Docker) và máy khách (Client).
2. Tạo một Network trên Radmin VPN và cho các máy kết nối vào chung.
3. Radmin VPN sẽ cấp cho máy chủ một địa chỉ IP ảo (Ví dụ: `26.18.244.131`).
4. Tại máy chủ, điền IP này vào file `.env` (`RMI_HOSTNAME=26.18.244.131`) và chạy lại Docker.
5. Tại máy khách, mở App lên và điền đúng IP `26.18.244.131` đó, hệ thống sẽ kết nối thành công bất chấp khoảng cách địa lý.

---
*Dự án được phát triển bởi Nhóm 7 - Hệ thống phân tán.*
