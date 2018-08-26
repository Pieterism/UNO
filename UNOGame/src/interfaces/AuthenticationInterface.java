package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationInterface extends Remote {

	public boolean register(String username, String password) throws RemoteException;

	public boolean login(String username, String password) throws RemoteException;

	public String getLoginToken(String username, String password);

	public boolean tokenLogin(String token);
}
