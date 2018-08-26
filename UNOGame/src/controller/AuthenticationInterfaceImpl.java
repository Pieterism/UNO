package controller;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import applicationServer.serverInterfaceImpl;
import interfaces.AuthenticationInterface;
import interfaces.clientInterface;
import interfaces.dbInterface;
import interfaces.serverInterface;
import security.BCrypt;
import security.PasswordHashing;

public class AuthenticationInterfaceImpl extends UnicastRemoteObject implements AuthenticationInterface {

	private serverInterfaceImpl applicationServer;
	private dbInterface db;

	public AuthenticationInterfaceImpl(int portnumber) throws RemoteException {
		Registry registry;
		dbInterface db;
		try {
			registry = LocateRegistry.getRegistry("localhost", portnumber);
			db = (dbInterface) registry.lookup("UNOdatabase");
			this.db = db;
		} catch (Exception e) {
			// TODO: exception handling
		}
	}

	@Override
	public boolean register(String username, String password) throws RemoteException {
		if (!db.checkUsername(username)) {
			return false;
		} else {
			db.addUser(username, BCrypt.hashpw(password, BCrypt.gensalt()));
			tellClients(username + " has succesfully connected to UNO room and your id is: ");
			return true;
		}
	}

	@Override
	public boolean login(String username, String password) throws RemoteException {
		if (!db.checkUsername(username)) {
			return false;
		} else {
			if (db.loginUser(username, password)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String getLoginToken(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean tokenLogin(String token) {
		// TODO Auto-generated method stub
		return false;
	}

	public void tellClients(String msg) throws RemoteException {
		for (clientInterface client : applicationServer.getClients()) {
			client.tell(msg);
		}

	}

	public serverInterfaceImpl getApplicationServer() {
		return applicationServer;
	}

	public void setApplicationServer(serverInterfaceImpl applicationServer) {
		this.applicationServer = applicationServer;
	}

	public dbInterface getDb() {
		return db;
	}

	public void setDb(dbInterface db) {
		this.db = db;
	}

}
