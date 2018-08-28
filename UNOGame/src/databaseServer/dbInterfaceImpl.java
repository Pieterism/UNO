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
import interfaces.serverInterface;

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
		db.createPlayerHandTable();
		db.createGameTable();
		db.createGameTurnTable();
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
	public void insertCard(int user_id, int card_id) throws RemoteException {
		db.insertCard(user_id, card_id);
	}

	@Override
	public void removeCard(int user_id, int card_id) throws RemoteException {
		db.removeCard(user_id, card_id);
	}

	@Override
	public String getPlayerHand(int user_id) throws RemoteException, SQLException {
		return db.getPlayerHand(user_id);
	}

	@Override
	public void addGame(int user1, int user2, int user3, int user4) throws RemoteException {
		db.addGame(user1, user2, user3, user4);
	}

	@Override
	public void removeGame(int game_id) throws RemoteException {
		db.removeGame(game_id);
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
	public void playTurn(int game_id, int user_id, int card_id, int next_player) throws RemoteException {
		db.playTurn(game_id, user_id, card_id, next_player);
	}

	@Override
	public String getAllTurns(int game_id) throws RemoteException, SQLException {
		return db.getAllTurns(game_id);
	}

	@Override
	public String getTurn(int turn_id, int game_id) throws RemoteException, SQLException {
		return db.getTurn(turn_id, game_id);
	}

	@Override
	public String getCardImage(int card_id) throws RemoteException, SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public String getCardImage(int color, int value) throws RemoteException, SQLException {
		return db.getCardImage(color, value);
	}

	@Override
	public void insertImage(int card_color, int card_value, Blob image) throws RemoteException {
		db.insertImage(card_color, card_value, image);
	}

	@Override
	public String getToken(String username) throws RemoteException, SQLException {
		return db.getToken(username);

	}

	@Override
	public boolean validateToken(String username, String token) {
		return db.validateToken(username, token);
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
}
