package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface dbInterface extends Remote {

	public void addUser(String username, String password) throws RemoteException, InvalidKeyException, SignatureException;
	
	public void insertUser(String username, String password, String token, Timestamp timestamp) throws RemoteException, InvalidKeyException, SignatureException;

    public boolean checkUsername(String username) throws RemoteException;

    public boolean loginUser(String username, String password) throws RemoteException, InvalidKeyException, SignatureException;

    public String getAllUsers() throws RemoteException;

    public void insertCard(int user_id, int card_id) throws RemoteException;

    public void removeCard(int user_id, int card_id) throws RemoteException;

    public String getPlayerHand(int user_id) throws RemoteException, SQLException;

    public void addGame(int user1, int user2, int user3, int user4) throws RemoteException;

    public void removeGame(int game_id) throws RemoteException;

    public String getActiveGames() throws RemoteException, SQLException;

    public void StopGame(int game_id) throws RemoteException;

    public void playTurn(int game_id, int user_id, int card_id, int next_player) throws RemoteException;

    public String getAllTurns(int game_id) throws RemoteException, SQLException;

    public String getTurn(int turn_id, int game_id) throws RemoteException, SQLException;

    public String getCardImage(int card_id) throws RemoteException, SQLException;

    public String getCardImage(int color, int value) throws RemoteException, SQLException;

    public void insertImage(int card_color, int card_value, Blob image) throws RemoteException;

	public String getToken(String username) throws RemoteException, SQLException;

	public boolean validateToken(String username, String token) throws RemoteException;
	
	public void setDatabaseServers() throws RemoteException;

	public void ping(int portnumber)throws RemoteException;
}
