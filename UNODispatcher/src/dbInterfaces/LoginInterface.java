package dbInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoginInterface extends Remote {

	String getToken(String username, String password)throws RemoteException;

	boolean loginToken(String token)throws RemoteException;
}
