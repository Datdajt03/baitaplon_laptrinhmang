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

## 3. Cấu Trúc Thư Mục
- `src/may_chu/`: Chứa mã nguồn khởi tạo và xử lý logic của Server.
- `src/client_ui/`: Giao diện chính và các lớp xử lý kết nối mạng của Client.
- `src/chucnang/`: Các bộ công cụ hỗ trợ như `truyen_tai_file.java` và giao diện phụ.
- `src/luutru/`: Nơi lưu trữ tài liệu (chia làm thư mục `upload` và `download`).

---

## 4. Hướng Dẫn Sử Dụng

### A. Thiết lập Máy chủ (Server) bằng Docker
Hệ thống Server hiện tại được thiết kế để chạy độc lập và ổn định 24/24 qua Docker.
1. **Mở file `.env`** ở thư mục gốc và cấu hình IP của máy tính sẽ chạy Server (Ví dụ: `RMI_HOSTNAME=192.168.1.5`).
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

---
*Dự án được phát triển bởi Nhóm 7 - Hệ thống phân tán.*
