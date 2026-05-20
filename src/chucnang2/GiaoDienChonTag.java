package chucnang2;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import client_ui.ket_noi_tcp;

public class GiaoDienChonTag extends JDialog {
    private JComboBox<String> cb_danhmuc;
    private JTextField txt_tag;
    private JButton btn_xacnhan;
    private File file_upload;
    private JTextArea hienthi;

    public GiaoDienChonTag(JFrame parent, File file, JTextArea hienthi) {
        super(parent, "Chọn danh mục và tag", true);
        this.file_upload = file;
        this.hienthi = hienthi;
        
        setSize(450, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        JPanel pn_center = new JPanel(new GridLayout(4, 1, 5, 5));
        pn_center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Hien thi ten file
        JPanel pn_file = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pn_file.add(new JLabel("Tài liệu: " + file.getName()));
        pn_center.add(pn_file);
        
        // Danh muc
        JPanel pn_danhmuc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pn_danhmuc.add(new JLabel("Chọn danh mục: "));
        cb_danhmuc = new JComboBox<>(new String[]{"Bài giảng", "Đề thi", "Bài tập lớn", "Tham khảo", "Khác"});
        pn_danhmuc.add(cb_danhmuc);
        pn_center.add(pn_danhmuc);
        
        // Tag
        JPanel pn_tag = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pn_tag.add(new JLabel("Nhập tag: "));
        txt_tag = new JTextField(20);
        pn_tag.add(txt_tag);
        pn_center.add(pn_tag);
        
        // Goi y
        JPanel pn_goiy = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pn_goiy.add(new JLabel("Gợi ý tag nổi bật: "));
        JButton btn_goi_y_1 = new JButton("java");
        JButton btn_goi_y_2 = new JButton("lap trinh mang");
        JButton btn_goi_y_3 = new JButton("khoa hoc may tinh");
        
        btn_goi_y_1.addActionListener(e -> themTag("java"));
        btn_goi_y_2.addActionListener(e -> themTag("lap trinh mang"));
        btn_goi_y_3.addActionListener(e -> themTag("khoa hoc may tinh"));
        
        pn_goiy.add(btn_goi_y_1);
        pn_goiy.add(btn_goi_y_2);
        pn_goiy.add(btn_goi_y_3);
        pn_center.add(pn_goiy);
        
        add(pn_center, BorderLayout.CENTER);
        
        // Nut xac nhan
        JPanel pn_bottom = new JPanel();
        btn_xacnhan = new JButton("Xác nhận và tải lên");
        btn_xacnhan.addActionListener(e -> {
            String danhmuc = cb_danhmuc.getSelectedItem().toString();
            String tag = txt_tag.getText();
            
            // Hien thi thong tin tag/danh muc
            hienthi.append("\nChuẩn bị tải lên: " + file_upload.getName());
            hienthi.append("\nDanh mục: " + danhmuc);
            hienthi.append("\nTag: " + (tag.isEmpty() ? "Không có" : tag) + "\n");

            // Chạy tiến trình gửi ngầm để tránh đơ giao diện
            new Thread(() -> {
                if (chucnang3.PhanLuong.laLocal()) {
                    chucnang3.KetNoiLocal.tai_len(file_upload, hienthi, danhmuc, tag);
                } else {
                    client_ui.ket_noi_tcp.tai_len(file_upload, hienthi, danhmuc, tag);
                }
                
                // Tự động làm mới danh sách tài liệu trên giao diện chính ngay lập tức
                if (client_ui.client_ui.INSTANCE != null) {
                    client_ui.client_ui.INSTANCE.lamMoiDanhSach();
                }
            }).start();
            
            dispose();
        });
        pn_bottom.add(btn_xacnhan);
        add(pn_bottom, BorderLayout.SOUTH);
    }
    
    private void themTag(String tag) {
        String current = txt_tag.getText();
        if (current.isEmpty()) {
            txt_tag.setText(tag);
        } else {
            txt_tag.setText(current + ", " + tag);
        }
    }
    
    public static void hienThi(JFrame parent, File file, JTextArea hienthi) {
        new GiaoDienChonTag(parent, file, hienthi).setVisible(true);
    }
}
