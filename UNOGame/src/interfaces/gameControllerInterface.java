package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import uno.Card;

public interface gameControllerInterface extends Remote {
	// controller methods
	public void setMsg(String msg) throws RemoteException;

	public void setScoreboard(List<String> scoreboard) throws RemoteException;

	// game methods
	public void addPile(Card card) throws RemoteException;

	public void addCards(List<Card> cards) throws RemoteException;

	public Card getCard() throws RemoteException;

	public void setNextPlayer(String username) throws RemoteException;

	public void setCardAmountPlayer(String username, int amount) throws RemoteException;

	public void setReady(boolean b) throws RemoteException;

	public int askColor() throws RemoteException;

	public void sendPlayerInfo(ArrayList<String> info) throws RemoteException;
}
