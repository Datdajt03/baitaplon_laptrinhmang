# Hệ Thống Thư Viện Tài Liệu Môn Học

Tài liệu này giải thích toàn bộ hoạt động và logic của hệ thống. 

## 1. Kiến Trúc Tổng Quan
Hệ thống chia làm 2 phần: Máy khách (Client) và Máy chủ (Server).
Khi bạn chạy file `client_ui.java`, một luồng ngầm sẽ tự động gọi `chay_may_chu.java` để bật server. 
Sau đó Client sẽ giao tiếp với Server qua 3 giao thức: TCP (Port 8888), UDP (Port 9999), và RMI (Port 1099).

---

## 2. Logic Xử Lý Của Từng Giao Thức

### a. Giao thức TCP (Dùng cho Tải lên, Tải xuống, Tìm kiếm)
- **Mục tiêu:** Xử lý các thao tác chính liên quan đến file.
- **Hoạt động tại Máy Khách (`ket_noi_tcp.java`):** 
  - Khi bạn bấm "Tải lên", cửa sổ chọn file hiện ra. Hệ thống sẽ copy file bạn chọn vào thư mục `src/luutru/upload`. Sau đó gửi lệnh `tailen|tenfile` cho Server.
  - Khi bạn bấm "Tải xuống", nó gửi lệnh `taixuong|tenfile`.
  - Khi bạn bấm "Tìm kiếm", nó gửi lệnh `timkiem|tukhoa`.
- **Hoạt động tại Máy Chủ (`may_chu_tcp.java`):**
  - Nghe trên cổng 8888. Nếu nhận được `timkiem`, nó sẽ vào thư mục `upload` để tìm file có tên chứa từ khóa.
  - Nếu nhận được `tailen`, nó xác nhận file đã nằm ở thư mục `upload`, sau đó nó TỰ ĐỘNG gọi hàm của `may_chu_udp` để phát thông báo có file mới.
  - Nếu nhận được `taixuong`, nó kiểm tra xem file có trong `upload` không. Nếu có, nó giả lập việc tải xuống bằng cách tạo ra một file trong `src/luutru/download` và gọi sang `dich_vu_rmi` để tăng biến đếm lượt tải lên 1.

### b. Giao thức UDP (Dùng để cập nhật nhanh danh sách)
- **Mục tiêu:** Broadcast (phát sóng) thông báo cho mọi người đang online.
- **Hoạt động tại Máy Chủ (`may_chu_udp.java`):**
  - Được `may_chu_tcp` gọi khi có tài liệu vừa tải lên xong. Nó sẽ đóng gói tên file đó thành mảng byte và gửi ra toàn mạng (địa chỉ broadcast 255.255.255.255) qua cổng 9999.
- **Hoạt động tại Máy Khách (`nhan_udp.java`):**
  - Khi giao diện vừa mở, luồng `nhan_udp` luôn luôn chạy ngầm, liên tục nghe trên cổng 9999.
  - Khi bắt được gói tin từ server bay tới, nó dùng `SwingUtilities.invokeLater` để an toàn in dòng chữ "có tài liệu mới" lên màn hình giao diện mà không làm treo ứng dụng.

### c. Giao thức RMI (Dùng để quản trị hệ thống từ xa)
- **Mục tiêu:** Gọi hàm thẳng vào bộ nhớ của Server để quản lý danh mục, tag, thống kê.
- **Hoạt động tại Máy Chủ (`dich_vu_rmi_impl.java`):**
  - Duy trì 2 danh sách `ArrayList` để lưu danh mục và tag trong RAM, cùng 1 biến `integer` để đếm tổng số lượt tải.
  - Cung cấp sẵn các hàm `quanly_danhmuc`, `quanly_tag`, và `thongke_luottai` tại cổng 1099.
- **Hoạt động tại Máy Khách (`goi_rmi.java`):**
  - Không cần gửi String lệnh phức tạp như TCP. Nó chỉ cần dùng `LocateRegistry` để tìm dịch vụ trên cổng 1099.
  - Sau đó gọi thẳng vào hàm của Server ví dụ `dichvu.thongke_luottai()`, kết quả Server sẽ tính toán trong RAM và trả về nguyên một chuỗi cho giao diện hiển thị.

---

## 3. Tóm Tắt Quy Trình Tích Hợp
Chỉ với 1 hành động "Tải file xuống":
1. **TCP** xử lý việc bắn file về thư mục `download`.
2. TCP bên Server ngầm gọi sang **RMI** để báo "có người mới tải xong rồi đó, cộng thêm 1 lượt tải vào nhé".
3. Từ đó khi bạn sang tab **RMI** bấm xem thống kê, bạn sẽ thấy số lượt tải đã được cộng lên.

Tất cả được thiết kế rất logic, phân chia nhiệm vụ rõ ràng và tách biệt từng file rất gọn gàng.
