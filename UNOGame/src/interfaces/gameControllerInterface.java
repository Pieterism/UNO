package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import uno.Card;

public interface gameControllerInterface extends Remote {
	// controller methods
	/**
	 * @param msg
	 * @throws RemoteException
	 */
	public void setMsg(String msg) throws RemoteException;

	/**
	 * @param scoreboard
	 * @throws RemoteException
	 */
	public void setScoreboard(List<String> scoreboard) throws RemoteException;

	// game methods
	/**
	 * @param card
	 * @throws RemoteException
	 */
	public void addPile(Card card) throws RemoteException;

	/**
	 * @param cards
	 * @throws RemoteException
	 */
	public void addCards(List<Card> cards) throws RemoteException;

	/**
	 * @return
	 * @throws RemoteException
	 */
	public Card getCard() throws RemoteException;

	/**
	 * @param username
	 * @throws RemoteException
	 */
	public void setNextPlayer(String username) throws RemoteException;

	/**
	 * @param username
	 * @param amount
	 * @throws RemoteException
	 */
	public void setCardAmountPlayer(String username, int amount) throws RemoteException;

	/**
	 * @param b
	 * @throws RemoteException
	 */
	public void setReady(boolean b) throws RemoteException;

	/**
	 * @return
	 * @throws RemoteException
	 */
	public int askColor() throws RemoteException;

	/**
	 * @param info
	 * @throws RemoteException
	 */
	public void sendPlayerInfo(ArrayList<String> info) throws RemoteException;
}
