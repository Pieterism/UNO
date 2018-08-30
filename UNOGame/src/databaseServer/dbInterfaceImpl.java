package databaseServer;

import java.io.IOException;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import interfaces.dbInterface;
import uno.Card;

public class dbInterfaceImpl extends UnicastRemoteObject implements dbInterface {

	private Database db;
	private List<dbInterface> databaseServers;
	private int portnumber;

	private final int dbPortnumber = 1300;
	private final int NUMBER_OF_DATABASES = 4;
	private int ApplicationServerCount;

	// constructor
	/**
	 * @param uri
	 * @param portnumber
	 * @throws SQLException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 */
	public dbInterfaceImpl(String uri, int portnumber) throws SQLException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException  {
		db = new Database(uri);
		db.createUserTable();
		db.createGameTable();
		db.createImagesTable();
		db.createGameToUserTable();
		databaseServers = new ArrayList<>();
		this.portnumber = portnumber;
		System.out.println("----------------------------------------------");

	}

	// Gebruiker toevoegen aan de databank
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#addUser(java.lang.String, java.lang.String)
	 */
	@Override
	public void addUser(String username, String password)
			throws InvalidKeyException, SignatureException, RemoteException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String token = db.createToken(username, timestamp);
		db.addUser(username, password, token, timestamp);
		for (dbInterface database : databaseServers) {
			database.duplicateAddUser(username, password, token, timestamp);
		}
	}
	
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#duplicateAddUser(java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp)
	 */
	@Override
	public void duplicateAddUser(String username, String password, String token, Timestamp timestamp)
			throws RemoteException, InvalidKeyException, SignatureException {
		this.db.addUser(username, password, token, timestamp);
		
	}

	// methode om te checken of een bepaalde username reeds bestaat in databank
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#checkUsername(java.lang.String)
	 */
	@Override
	public boolean checkUsername(String username) throws RemoteException {
		return db.checkUsername(username);
	}

	// username en password van een bepaalde user verifieren in databank
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#loginUser(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean loginUser(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException, SQLException {
		return db.loginUser(username, password);
	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#getPlayerHand(int)
	 */
	@Override
	public String getPlayerHand(int user_id) throws RemoteException, SQLException {
		return db.getPlayerHand(user_id);
	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#addUsersToGame(java.lang.String, java.util.List)
	 */
	@Override
	public void addUsersToGame(String game_name, List<String> users) throws RemoteException {
		List<String> temp = new ArrayList<>();
		temp.addAll(users);
		for (int i = 0; i < 4 - users.size(); i++) {
			temp.add(new String(""));
		}
		int game_id = db.getGameId(game_name);
		db.addUserToGame(game_id, temp.get(0), temp.get(1), temp.get(2), temp.get(3));
		for (dbInterface database : databaseServers) {
			database.duplicateAddUsersToGame(game_id, temp);
		}
	}
	
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#duplicateAddUsersToGame(int, java.util.List)
	 */
	@Override
	public void duplicateAddUsersToGame(int game_id, List<String> users) throws RemoteException {
		db.addUserToGame(game_id, users.get(0), users.get(1), users.get(2), users.get(3));
	}
	
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#addGame(int, java.lang.String, int, int, int)
	 */
	@Override
	public String addGame(int id, String name, int aantalSpelers, int serverport, int theme) {
		String dbID = id + "" + this.portnumber;
		db.addGame(dbID, name, aantalSpelers, serverport, theme);
		for (dbInterface iter : databaseServers) {
			try {
				iter.duplicateAddGame(dbID, name, aantalSpelers, serverport, theme);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return dbID;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#duplicateAddGame(java.lang.String, java.lang.String, int, int, int)
	 */
	@Override
	public void duplicateAddGame(String id, String name, int aantalSpelers, int serverport, int theme) {
		db.addGame(id, name, aantalSpelers, serverport, theme);
	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#getActiveGames()
	 */
	@Override
	public List<String> getActiveGames() throws RemoteException, SQLException {
		return db.getActiveGames();
	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#StopGame(int)
	 */
	@Override
	public void StopGame(int game_id) throws RemoteException {
		db.StopGame(game_id);
	}

	/**
	 * @return
	 */
	public Database getDb() {
		return db;
	}

	/**
	 * @param db
	 */
	public void setDb(Database db) {
		this.db = db;
	}

	/**
	 * @return
	 */
	public List<dbInterface> getDatabaseServers() {
		return databaseServers;
	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#setDatabaseServers()
	 */
	@Override
	public void setDatabaseServers() throws RemoteException {
		for (int i = dbPortnumber; i < dbPortnumber + NUMBER_OF_DATABASES; i++) {
			if (i != this.portnumber) {
				Registry registry;
				try {
					registry = LocateRegistry.getRegistry("localhost", i);
					dbInterface temp = (dbInterface) registry.lookup("UNOdatabase" + i);
					this.databaseServers.add(temp);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// check the connection
		for (dbInterface databaseInterface : this.databaseServers) {
			databaseInterface.ping(this.portnumber);
		}
	}

	/**
	 * @param databaseServer
	 */
	public void addDatabaseServer(dbInterfaceImpl databaseServer) {
		this.databaseServers.add(databaseServer);
	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#ping(int)
	 */
	@Override
	public void ping(int portnumber) throws RemoteException {
		System.out.println("server " + portnumber + " has pinged server " + this.portnumber);

	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#getPortnumber()
	 */
	@Override
	public int getPortnumber() throws RemoteException {
		return this.portnumber;
	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#updateHandPlayer(java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public void updateHandPlayer(String name, List<Card> cards, String dbID) throws RemoteException {
		db.playTurn(name, cards, dbID);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (dbInterface databaseInterface : databaseServers) {
					try {
						databaseInterface.duplicateUpdateHandPlayer(name, cards, dbID);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}				
			}
		});
		thread.start();

	}

	/* (non-Javadoc)
	 * @see interfaces.dbInterface#duplicateUpdateHandPlayer(java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public void duplicateUpdateHandPlayer(String name, List<Card> cards, String dbID) throws RemoteException {
		db.playTurn(name, cards, dbID);
	}
	
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#createPlayerHandTabel(java.lang.String)
	 */
	@Override
	public void createPlayerHandTabel(String id) throws RemoteException {
		try {
			db.createPlayerHandTable(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (dbInterface iter : databaseServers) {
			iter.duplicateCreatePlayerHand(id);
		}
	}
	
	/* (non-Javadoc)
	 * @see interfaces.dbInterface#duplicateCreatePlayerHand(java.lang.String)
	 */
	@Override
	public void duplicateCreatePlayerHand(String id) throws RemoteException {
		try {
			db.createPlayerHandTable(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	public String getLoginToken(String username) throws SQLException, RemoteException {
		return db.getToken(username);
	}
}
