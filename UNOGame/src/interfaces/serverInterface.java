package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import uno.Card;

public interface serverInterface extends Remote, AuthenticationInterface {

	// lobby calls
	/**
	 * @param name
	 * @param description
	 * @param aantalSpelers
	 * @throws RemoteException
	 */
	public void startNewGame(String name, int description, int aantalSpelers) throws RemoteException;

	/**
	 * @param lobbyController
	 * @throws RemoteException
	 */
	public void giveLobby(lobbyInterface lobbyController) throws RemoteException;

	/**
	 * @param lobbyController
	 * @throws RemoteException
	 */
	public void exit(lobbyInterface lobbyController) throws RemoteException;

	/**
	 * @param gameController
	 * @param gameID
	 * @param username
	 * @throws RemoteException
	 */
	public void joinGame(gameControllerInterface gameController, int gameID, String username) throws RemoteException;

	// start game
	/**
	 * @param gameId
	 * @param username
	 * @param gameTheme
	 * @throws RemoteException
	 */
	public void readyToStart(int gameId, String username, int gameTheme) throws RemoteException;

	/**
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getGames() throws RemoteException;

	/**
	 * @param s
	 * @param username
	 * @throws RemoteException
	 */
	public void send(String s, String username) throws RemoteException;

	/**
	 * @param msg
	 * @param gameID
	 * @param username
	 * @throws RemoteException
	 */
	public void sendGameMsg(String msg, int gameID, String username) throws RemoteException;

	/**
	 * @param username
	 * @param gameID
	 * @return
	 * @throws RemoteException
	 */
	public List<Card> getCards(String username, int gameID) throws RemoteException;

	/**
	 * @param gci
	 * @throws RemoteException
	 */
	public void closeGC(gameControllerInterface gci) throws RemoteException;

}
