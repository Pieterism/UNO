package Interfaces;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.smartcardio.Card;

import clientInterfaces.gameControllerInterface;
import clientInterfaces.lobbyInterface;


public interface serverInterface extends Remote {

    public boolean register(String username, String password) throws RemoteException;

    public boolean login(String username, String password ) throws RemoteException;

    public String JWT() throws RemoteException;

    public void startNewGame(String name, String description, int aantalSpelers) throws RemoteException;

    public List<String> getGames() throws RemoteException;

    public void joinGame(int id, String username) throws RemoteException;

    public void playCard(Card card) throws RemoteException;

    public void startGame(int id) throws RemoteException;

    public boolean checkName(String username) throws RemoteException;

    public List<String> getUsers() throws RemoteException;

    public void send(String s, String username) throws RemoteException;

    public void giveClient(String s, clientInterface client) throws RemoteException;
    
	public void giveLobby(lobbyInterface lobbyController) throws RemoteException;

	public void exit(clientInterface client, lobbyInterface lobbyController) throws RemoteException;

	public void addPlayer(gameControllerInterface gameController, int gameID, String username) throws RemoteException;

	public void sendGameMsg(String msg, int gameID, String username) throws RemoteException;
}

