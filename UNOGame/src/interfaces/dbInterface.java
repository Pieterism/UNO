package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import uno.Card;

public interface dbInterface extends Remote {
	public void setDatabaseServers() throws RemoteException;

	public void ping(int portnumber) throws RemoteException;

	public int getPortnumber() throws RemoteException;

	public boolean checkUsername(String username) throws RemoteException;

	public boolean loginUser(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException;

	public List<String> getActiveGames() throws RemoteException, SQLException;

	public void StopGame(int game_id) throws RemoteException;

	public void addUser(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException;

	public void duplicateAddUser(String username, String password, String token, Timestamp timestamp)
			throws RemoteException, InvalidKeyException, SignatureException;

	String addGame(int id, String name, int aantalSpelers, int serverport, int theme) throws RemoteException;

	void duplicateAddGame(String id, String name, int aantalSpelers, int serverport, int theme) throws RemoteException;

	void addUsersToGame(String game_name, List<String> users) throws RemoteException;

	void duplicateAddUsersToGame(int game_id, List<String> users) throws RemoteException;

	public void createPlayerHandTabel(String id) throws RemoteException;

	public void duplicateCreatePlayerHand(String id) throws RemoteException;

	public void updateHandPlayer(String name, List<Card> cards, String id) throws RemoteException;

	public void duplicateUpdateHandPlayer(String name, List<Card> cards, String id) throws RemoteException;

	public String getPlayerHand(int user_id) throws RemoteException, SQLException;

}
