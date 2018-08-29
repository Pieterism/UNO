package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.SQLException;

public interface AuthenticationInterface extends Remote {

	/**
	 * @param username
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public boolean register(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException;

	/**
	 * @param username
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public boolean login(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException;

	/**
	 * @param username
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws SQLException
	 */
	public String getLoginToken(String username, String password) throws RemoteException, SQLException;

}
