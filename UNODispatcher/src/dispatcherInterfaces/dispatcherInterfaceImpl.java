package dispatcherInterfaces;


import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xml.internal.ws.wsdl.writer.document.Port;

import clientInterfaces.clientInterface;
import dbInterfaces.dbInterfaceImpl;
import dispatcherInterfaces.dispatcherInterface;
import serverInterfaces.serverInterface;
import serverInterfaces.serverInterfaceImpl;
import sun.font.CreatedFontTracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class dispatcherInterfaceImpl extends UnicastRemoteObject implements dispatcherInterface {

    private Map<Integer, Integer> serverStatus;
    private List<Integer> unfilledServers;
    private List<Integer> fullServers;
    private String uri = "C:\\Users\\wouter\\Documents\\school\\GedistribueerdeSystemen\\UNO-game\\uno.db";
    //private String uri = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\uno.db";
    
    private Map<Integer, Integer> serverToDB;
    
    private final int MAXLOAD = 20;
    private final int dbPortnumber = 1300;
    
    public dispatcherInterfaceImpl() throws RemoteException, AlreadyBoundException, SQLException{
    	serverStatus = new HashMap<>();
    	unfilledServers = new ArrayList<>();
    	fullServers = new ArrayList<>();
    	serverToDB = new HashMap<>();
    	

    	//auto create dbserver
    	createdbserver(1300);
    	
    	//auto create 1 server 
    	createServer(1200, 1300);
    	

    }
    
//    @Override
//    public int register(String username, String password) throws RemoteException {
//        serverInterface temp = emptyServers.get(0);
//        if ( temp.register(username, password)) {
//        	return serverPorts.get(temp);
//        }
//        else {
//        	return -1;					// error code -1
//        }
//    }
//
//    @Override
//    public int login(String username, String password) throws RemoteException {
//        serverInterface temp = emptyServers.get(0);
//        if  (temp.login(username, password)) {
//        	return serverPorts.get(temp);
//        }
//        else {
//        	return -1;					// error code -1
//        }
//    }
//    
//	@Override
//	public boolean checkName(String name) throws RemoteException {
//        serverInterface temp = emptyServers.get(0);
//        return temp.checkName(name);
//	}
	
	private Registry createServer(int portnumber, int dbPortnumber) {
        Registry registry;
		try {
			registry = LocateRegistry.createRegistry(portnumber);
	        registry.bind("UNOserver", (Remote) new serverInterfaceImpl(dbPortnumber));
	        unfilledServers.add(portnumber);
	        serverStatus.put(portnumber, 0);
			return registry;
		} 
		catch (RemoteException e) {	
			e.printStackTrace();
			System.out.println("Remote Exception");
		} 
		catch (AlreadyBoundException e) {
			e.printStackTrace();
			System.out.println("AlreadyBoundException");
		}
		return null;
	}
	
	//give uri => location on disk
	private Registry createdbserver(int portnumber) throws SQLException {
		Registry registry;
		try {
			registry = LocateRegistry.createRegistry(portnumber);
	        registry.bind("UNOdatabase", (Remote) new dbInterfaceImpl(uri));
			return registry;
		} 
		catch (RemoteException e) {	
			e.printStackTrace();
			System.out.println("Remote Exception");
		} 
		catch (AlreadyBoundException e) {
			e.printStackTrace();
			System.out.println("AlreadyBoundException");
		}
		return null;
	}
	
	//TODO
	private int getLeastLoadedDB() {
		return 0;
	}

	@Override
	public int getPort() throws RemoteException {
		if (unfilledServers.size()==0) {
			//new server met zo laag mogelijk portnumber
			for(int i = 1200; i<1300; i++) {
				if(!serverStatus.containsKey(i)) {
					Registry server = createServer(i, dbPortnumber);
					System.out.println("Server " + i + " has been created");
					if (server != null) {
						return i;
					}
					else {
						System.out.println("something went wrong error (creating server failed)");
						return -1;
					}
				}
			}
		}
		else {
			//allocate user to server
			return unfilledServers.get(0);
		}
		return -2;	// alle poorten zijn in gebruik => te veel spellen 0
	}

	@Override
	public void updateInfo(int serverPort, int load) throws RemoteException {
		serverStatus.put(serverPort, load);
		if (load==MAXLOAD) {
			fullServers.add(serverPort);
		}
		
	}
	
	
}
