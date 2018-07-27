package clientInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface lobbyInterface extends Remote{

	public void setMsg(String msg) throws RemoteException;

}
