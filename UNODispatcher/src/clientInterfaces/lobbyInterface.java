package clientInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface lobbyInterface extends Remote{

	public void setMsg(String msg) throws RemoteException;

}
