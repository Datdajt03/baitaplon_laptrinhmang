package may_chu;

import java.rmi.Remote;
import java.rmi.RemoteException;

// giao dien rmi quan tri he thong
public interface dich_vu_rmi extends Remote {
    
    // ham quan ly danh muc
    public String quanly_danhmuc(String hanhdong, String tendanhmuc) throws RemoteException;
    
    // ham quan ly tag
    public String quanly_tag(String hanhdong, String tentag) throws RemoteException;
    
    // ham thong ke luot tai xuong
    public String thongke_luottai() throws RemoteException;
    
}
