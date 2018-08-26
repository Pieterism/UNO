package controller;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import applicationServer.serverInterfaceImpl;
import databaseServer.dbInterfaceImpl;
import interfaces.AuthenticationInterface;
import interfaces.dbInterface;

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
			// TODO: handle exception
		}
	}

	public AuthenticationInterfaceImpl(serverInterfaceImpl appServer) throws RemoteException {
		this.applicationServer = appServer;
	}

	@Override
	public boolean register(String username, String password) {
		// TODO Auto-generated method stub
		return false;
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

}
