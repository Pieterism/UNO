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
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import interfaces.dbInterface;
import interfaces.gameControllerInterface;
import interfaces.serverInterface;
import uno.Card;

public class dbInterfaceImpl extends UnicastRemoteObject implements dbInterface {

	private Database db;
	private List<dbInterface> databaseServers;
	private int portnumber;
	
	private final int dbPortnumber = 1300;
	private final int NUMBER_OF_DATABASES = 4;
	private int ApplicationServerCount;

	// constructor
	public dbInterfaceImpl(String uri, int portnumber) throws SQLException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException {
		db = new Database(uri);
		db.createUserTable();
		db.createGameTable();
		db.createImagesTable();
		databaseServers = new ArrayList<>();
		this.portnumber = portnumber;
		System.out.println("----------------------------------------------");

	}

	// Gebruiker toevoegen aan de databank
	@Override
	public void addUser(String username, String password) throws InvalidKeyException, SignatureException, RemoteException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String token = 	db.insertUser(username, password, timestamp);
		for (dbInterface database : databaseServers) {
			database.insertUser(username, password, token, timestamp);
		}
	}

	// methode om te checken of een bepaalde username reeds bestaat in databank
	@Override
	public boolean checkUsername(String username) throws RemoteException {
		return db.checkUsername(username);
	}

	// username en password van een bepaalde user verifieren in databank
	@Override
	public boolean loginUser(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException {
		return db.loginUser(username, password);
	}

	@Override
	public String getAllUsers() {
		return db.getAllUsers();
	}

	@Override
	public String getPlayerHand(int user_id) throws RemoteException, SQLException {
		return db.getPlayerHand(user_id);
	}

	@Override
	public void addGame(List<String> users, int gameTheme) throws RemoteException {
		List<String> temp = new ArrayList<>();
		temp.addAll(users);
		for (int i=0; i<NUMBER_OF_DATABASES-users.size(); i++) {
			temp.add(new String(""));
		}
		
		db.addGame(temp.get(0), temp.get(1), temp.get(2), temp.get(3), gameTheme);
		for (dbInterface database : databaseServers) {
			database.duplicateGame(temp, gameTheme);
		}
	}
	
	public void duplicateGame(List<String> users, int gameTheme) {
		db.addGame(users.get(0), users.get(1), users.get(2), users.get(3), gameTheme);
	}

	@Override
	public String getActiveGames() throws RemoteException, SQLException {
		return db.getActiveGames();
	}

	@Override
	public void StopGame(int game_id) throws RemoteException {
		db.StopGame(game_id);
	}

	@Override
	public String getCardImage(String card_name) throws RemoteException, SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public String getToken(String username) throws RemoteException, SQLException {
		return this.db.getToken(username);

	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public List<dbInterface> getDatabaseServers() {
		return databaseServers;
	}

	@Override
	public void setDatabaseServers() throws RemoteException {
		for (int i = dbPortnumber; i<dbPortnumber+NUMBER_OF_DATABASES; i++) {
			if (i != this.portnumber) {
				Registry registry;
				try {
					registry = LocateRegistry.getRegistry("localhost", i);
					dbInterface temp = (dbInterface) registry.lookup("UNOdatabase"+ i);
					this.databaseServers.add(temp);
					

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//check the connection
		for (dbInterface databaseInterface : this.databaseServers) {
			databaseInterface.ping(this.portnumber);
		}
	}

	public void addDatabaseServer(dbInterfaceImpl databaseServer) {
		this.databaseServers.add(databaseServer);
	}

	@Override
	public void insertUser(String username, String password, String token, Timestamp timestamp)
			throws RemoteException, InvalidKeyException, SignatureException {
		db.duplicateUser(username, password, token, timestamp);
	}

	@Override
	public void ping(int portnumber) throws RemoteException {
		System.out.println("server " + portnumber + " has pinged server " + this.portnumber);
		
	}

	@Override
	public int getPortnumber() throws RemoteException {
		return this.portnumber;
	}

	@Override
	public void updateHandPlayer(String name, List<Card> cards, int gameId) throws RemoteException {
		db.playTurn(name, cards, gameId);
		for (dbInterface databaseInterface : databaseServers) {
			databaseInterface.duplicateCards(name, cards, gameId);
		}
	}
	
	@Override
	public void duplicateCards(String name, List<Card> cards, int gameId) throws RemoteException {
		db.playTurn(name, cards, gameId);
	}

}
