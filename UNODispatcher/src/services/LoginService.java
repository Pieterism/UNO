package services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Database.Database;
import UNO.User;
import dbInterfaces.LoginInterface;
import serverInterfaces.serverInterfaceImpl;
import security.JWTUtils;

public class LoginService extends UnicastRemoteObject implements LoginInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final long timeToLive = 24 * 60 * 60 * 1000; // TODO hoelang moet token geldig blijven?

	serverInterfaceImpl server;
	Database db;

	public LoginService(serverInterfaceImpl server) throws RemoteException {
		this.server = server;
	}

	// controleert of er geldige username-password combinatie is en geeft Token
	// terug indien
	// dit het geval is.
	@Override
	public String getToken(String username, String password) throws RemoteException {
		// TODO checken of user bestaat in databank
		if (db.checkUsername(username)) {

			String token = JWTUtils.createJWT(username, null, username, timeToLive, server.secret);
			return token;
		}

		return null;
	}

	@Override
	public boolean loginToken(String token) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

}
