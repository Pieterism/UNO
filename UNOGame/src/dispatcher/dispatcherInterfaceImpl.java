package dispatcher;


import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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
import interfaces.dispatcherInterface;

public class dispatcherInterfaceImpl extends UnicastRemoteObject implements dispatcherInterface {

	private Map<Integer, Integer> serverStatus;
	private List<Integer> unfilledServers;
	private List<Integer> fullServers;
	private List<dbInterfaceImpl> databaseServers;

	private String uri ="C:\\Users\\wouter\\Documents\\School\\geavanceerde\\UNO\\uno.db";
	//private String uri = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\uno";

	private Map<Integer, Integer> serverToDB;

	private final int MAXLOAD = 20;
	private final int dbPortnumber = 1300;
	private final int NUMBER_OF_DATABASES = 4;

	public dispatcherInterfaceImpl() throws AlreadyBoundException, SQLException, UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		serverStatus = new HashMap<>();
		unfilledServers = new ArrayList<>();
		fullServers = new ArrayList<>();
		serverToDB = new HashMap<>();
		databaseServers = new ArrayList<>();

		// auto create dbserver
		createDbServers(1300);

		// auto create 1 server
		createServer(1200, 1300);

	}

	private Registry createServer(int portnumber, int dbPortnumber) {
		Registry registry;
		try {
			registry = LocateRegistry.createRegistry(portnumber);
	        registry.bind("UNOserver", new serverInterfaceImpl(dbPortnumber, portnumber));
	        unfilledServers.add(portnumber);
	        serverStatus.put(portnumber, 0);

			return registry;
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("Remote Exception");
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
			System.out.println("AlreadyBoundException");
		}
		return null;
	}

	// give uri => location on disk
	private Registry createDbServers(int portnumber) throws SQLException, UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		Registry registry = null;
		for (int i = 0; i < NUMBER_OF_DATABASES; i++) {

			try {
				registry = LocateRegistry.createRegistry(portnumber + i);
				dbInterfaceImpl db = new dbInterfaceImpl(uri + (portnumber + i) + ".db");
				registry.bind("UNOdatabase" + (portnumber + i), db);
				databaseServers.add(db);

				System.out.println("check databaseServersList");

			} catch (RemoteException e) {
				e.printStackTrace();
				System.out.println("Remote Exception");
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
				System.out.println("AlreadyBoundException");
			}
		}

		for (dbInterfaceImpl db : databaseServers) {
			db.setDatabaseServers(databaseServers);
		}

		return registry;
	}


	// TODO
	private int getLeastLoadedDB() {
		return 0;
	}

	@Override
	public int getPort() throws RemoteException {
		if (unfilledServers.size() == 0) {
			// new server met zo laag mogelijk portnumber
			for (int i = 1200; i < 1300; i++) {
				if (!serverStatus.containsKey(i)) {
					Registry server = createServer(i, dbPortnumber);
					System.out.println("Server " + i + " has been created");
					if (server != null) {
						return i;
					} else {
						System.out.println("something went wrong error (creating server failed)");
						return -1;
					}
				}
			}
		}
		else {
			//allocate user to server
			System.out.println("already made server");
			return unfilledServers.iterator().next();

		}
		return -2; // alle poorten zijn in gebruik => te veel spellen 0
	}

	@Override
	public void updateInfo(int serverPort, int load) throws RemoteException {
		serverStatus.put(serverPort, load);
		if (load>=MAXLOAD) {
			try {
				unfilledServers.remove(load);
				fullServers.add(load);
			} catch (Exception e) {
			}
		}
		else {
			try {
				unfilledServers.add(load);
				fullServers.remove(load);
			} catch (IndexOutOfBoundsException e) {
				
			}


		}

	}

}
