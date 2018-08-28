package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import uno.Card;

public interface dbInterface extends Remote {

	public void addUser(String username, String password) throws RemoteException, InvalidKeyException, SignatureException;
	
	public void insertUser(String username, String password, String token, Timestamp timestamp) throws RemoteException, InvalidKeyException, SignatureException;

    public boolean checkUsername(String username) throws RemoteException;

    public boolean loginUser(String username, String password) throws RemoteException, InvalidKeyException, SignatureException;

    public String getAllUsers() throws RemoteException;

    public String getPlayerHand(int user_id) throws RemoteException, SQLException;

    public void addGame(List<String> users, int gameTheme) throws RemoteException;

    public String getActiveGames() throws RemoteException, SQLException;

    public void StopGame(int game_id) throws RemoteException;

    public String getCardImage(String card_name) throws RemoteException, SQLException;

	public void setDatabaseServers() throws RemoteException;

	public void ping(int portnumber)throws RemoteException;
	
	public int getPortnumber() throws RemoteException;

	public void duplicateGame(List<String> temp, int gameTheme)throws RemoteException;

	public void updateHandPlayer(String name, List<Card> cards, int gameId) throws RemoteException;

	public void duplicateCards(String name, List<Card> cards, int gameId) throws RemoteException;

	String getToken(String username) throws RemoteException, SQLException;
}
