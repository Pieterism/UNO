package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.SQLException;

public interface AuthenticationInterface extends Remote {

	public boolean register(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException;

	public boolean login(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException;

	public String getLoginToken(String username, String password) throws RemoteException, SQLException;

}
