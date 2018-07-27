package services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import dbInterfaces.LoginInterface;
import serverInterfaces.serverInterfaceImpl;

public class LoginService extends UnicastRemoteObject implements LoginInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private final timeToLive = 24*60*60*1000 -- hoelang moet token geldig blijven? 
	
	private serverInterfaceImpl server;
	
	public LoginService(serverInterfaceImpl server) throws RemoteException{
		this.server = server;
	}

	
	//controleert of er geldige username-password combinatie is en geeft Token terug indien 
	//dit het geval is. 
	@Override
	public String getToken(String username, String password) throws RemoteException {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public boolean loginToken(String token) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}


}
