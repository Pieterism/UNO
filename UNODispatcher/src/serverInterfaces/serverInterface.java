package serverInterfaces;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import UNO.Card;
import clientInterfaces.clientInterface;
import clientInterfaces.gameControllerInterface;
import clientInterfaces.lobbyInterface;

public interface serverInterface extends Remote {
	//user authentication methods
    public boolean register(String username, String password) throws RemoteException;
    public boolean login(String username, String password) throws RemoteException;	

    //lobby calls
    public void startNewGame(String name, String description, int aantalSpelers) throws RemoteException;
	public void giveLobby(lobbyInterface lobbyController) throws RemoteException;
	public void exit(clientInterface client, lobbyInterface lobbyController) throws RemoteException;
	public void joinGame(gameControllerInterface gameController, int gameID, String username) throws RemoteException;


	//start game
	public void giveGameController(gameControllerInterface gcInterface) throws RemoteException;
    public void readyToStart(int gameId, String username) throws RemoteException;


    public List<String> getGames() throws RemoteException;


    public void playCard(Card card) throws RemoteException;

    
    public List<String> getUsers() throws RemoteException;

    public void send(String s, String username) throws RemoteException;

    public void giveClient(String s, clientInterface client) throws RemoteException;
	public void sendGameMsg(String msg, int gameID, String username) throws RemoteException;
	public boolean ping() throws RemoteException;
	public List<Card> getCards(String username, int gameID) throws RemoteException;

}
