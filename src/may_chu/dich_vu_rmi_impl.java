package may_chu;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// lop thuc thi cac chuc nang rmi
public class dich_vu_rmi_impl extends UnicastRemoteObject implements dich_vu_rmi {
    
    // luu tru tam thoi danh muc va tag
    private ArrayList<String> danhsach_danhmuc;
    private ArrayList<String> danhsach_tag;
    private int so_luot_tai = 0;
    
    public dich_vu_rmi_impl() throws RemoteException {
        super();
        danhsach_danhmuc = new ArrayList<>();
        danhsach_tag = new ArrayList<>();
    }

    @Override
    public String quanly_danhmuc(String hanhdong, String tendanhmuc) throws RemoteException {
        if (hanhdong.equals("them")) {
            danhsach_danhmuc.add(tendanhmuc);
            return "da them danh muc: " + tendanhmuc;
        }
        return "hanh dong khong hop le";
    }

    @Override
    public String quanly_tag(String hanhdong, String tentag) throws RemoteException {
        if (hanhdong.equals("them")) {
            danhsach_tag.add(tentag);
            return "da them tag: " + tentag;
        }
        return "hanh dong khong hop le";
    }

    @Override
    public String thongke_luottai() throws RemoteException {
        return "so luot tai tai lieu hien tai la: " + so_luot_tai;
    }
    
    // ham ho tro de tang so luot tai tu may chu tcp
    public void tang_luottai() {
        this.so_luot_tai++;
    }
}
