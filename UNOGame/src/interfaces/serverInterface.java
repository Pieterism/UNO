package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import uno.Card;

public interface serverInterface extends Remote, AuthenticationInterface {

	// lobby calls
	public void startNewGame(String name, int description, int aantalSpelers) throws RemoteException;

	public void giveLobby(lobbyInterface lobbyController) throws RemoteException;

	public void exit(lobbyInterface lobbyController) throws RemoteException;

	public void joinGame(gameControllerInterface gameController, int gameID, String username) throws RemoteException;

	// start game
	public void readyToStart(int gameId, String username, int gameTheme) throws RemoteException;

	public List<String> getGames() throws RemoteException;

	public void send(String s, String username) throws RemoteException;

	public void sendGameMsg(String msg, int gameID, String username) throws RemoteException;

	public List<Card> getCards(String username, int gameID) throws RemoteException;

	public void closeGC(gameControllerInterface gci) throws RemoteException;

}
