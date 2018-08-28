package dispatcher;


import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import applicationServer.serverInterfaceImpl;
import databaseServer.dbInterfaceImpl;
import interfaces.dbInterface;

import interfaces.dispatcherInterface;

public class dispatcherInterfaceImpl extends UnicastRemoteObject implements dispatcherInterface {

	private Map<Integer, Integer> serverStatus, dbServerStatus;
	private Set<Integer> unfilledServers;
	private Set<Integer> fullServers;
	private List<dbInterface> databaseServers;
	private int serverPort;

	//private String uri ="C:\\Users\\wouter\\Documents\\School\\geavanceerde\\UNO\\uno.db";
	private String uri = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\uno";

	private Map<Integer, Integer> serverToDB;

	private final int MAXLOAD = 20;
	private final int dbPortnumber = 1300;
	private final int NUMBER_OF_DATABASES = 4;

	public dispatcherInterfaceImpl() throws AlreadyBoundException, SQLException, UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		serverStatus = new HashMap<>();
		dbServerStatus = new HashMap<>();
		unfilledServers = new HashSet<>();
		fullServers = new HashSet<>();
		serverToDB = new HashMap<>();
		databaseServers = new ArrayList<>();
		serverPort = 1200;

		// auto create dbserver
		createDbServers(1300);

		//connect to all the db
		connectToDb();
		
		//make All dbservers to connect to eachother
		makeConnect();
		
		// auto create 1 server
		createServer(serverPort);

	}

	private void makeConnect() {
		for (dbInterface iter : databaseServers) {
			try {
				iter.setDatabaseServers();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void connectToDb() {
		for (int i = dbPortnumber; i<dbPortnumber+NUMBER_OF_DATABASES; i++) {
			try {
				Registry registry = LocateRegistry.getRegistry("localhost", i);
				dbInterface tempDB = (dbInterface) registry.lookup("UNOdatabase"+ i);
				databaseServers.add(tempDB);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//check connection
		for (dbInterface databaseInterface : this.databaseServers) {
			try {
				System.out.println("check connection!");
				databaseInterface.ping(1099);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void createServer(int portnumber) {
		Registry registry;
		try {
			int dbPortnumber = this.getLeastLoadedDB();
			System.out.println(dbPortnumber);
			registry = LocateRegistry.createRegistry(portnumber);
	        registry.bind("UNOserver", new serverInterfaceImpl(dbPortnumber, portnumber));
	        
	        //update class variables
	        unfilledServers.add(portnumber);
	        serverStatus.put(portnumber, 0);
	        serverPort ++;
	        
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("Remote Exception");
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
			System.out.println("AlreadyBoundException");
		}
	}

	// give uri => location on disk
	private void createDbServers(int portnumber) throws SQLException, UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		for (int i = 0; i < NUMBER_OF_DATABASES; i++) {

			try {
				Registry registry = LocateRegistry.createRegistry(portnumber + i);
				dbInterfaceImpl db = new dbInterfaceImpl(uri + (portnumber + i) + ".db", portnumber+i);
				registry.bind("UNOdatabase" + (portnumber + i), db);

				dbServerStatus.put(portnumber+i, 0);
				System.out.println("check databaseServersList");

			} catch (RemoteException e) {
				e.printStackTrace();
				System.out.println("Remote Exception");
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
				System.out.println("AlreadyBoundException");
			}
		}
	}


	private int getLeastLoadedDB() throws RemoteException {
		int highestLoad = Integer.MAX_VALUE;
		int portnumber = 1300;
		for (int i = dbPortnumber; i < dbPortnumber + dbServerStatus.size(); i++) {
			if (dbServerStatus.get(i) < highestLoad) {
				highestLoad = dbServerStatus.get(i);
				portnumber = i;
			}
		}
		dbServerStatus.put(portnumber, highestLoad+1);
		return portnumber;
	}

	@Override
	public int getPort() throws RemoteException {
		if (unfilledServers.size() == 0) {
			// new server met zo laag mogelijk portnumber
			createServer(serverPort);
		}
		//allocate user to server
		System.out.println("already made server");
		return unfilledServers.iterator().next();

	}

	@Override
	public void updateInfo(int serverPort, int load) throws RemoteException {
		serverStatus.put(serverPort, load);
		if (load>=MAXLOAD) {
			try {
				unfilledServers.remove(load);
				fullServers.add(load);
			} catch (IndexOutOfBoundsException e) {
			}
		}
		else {
			try {
				unfilledServers.add(load);
				fullServers.remove(load);
			} catch (IndexOutOfBoundsException e) {
				
			}
		}
		if (unfilledServers.isEmpty()) {
			createServer(fullServers.size());
		}
	}

}
