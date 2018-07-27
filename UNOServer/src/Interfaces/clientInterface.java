package Interfaces;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.smartcardio.Card;

public interface clientInterface extends Remote {

    public void tell(String s) throws RemoteException;

    public void updatePile(Card card) throws RemoteException;

    public void drawCards(List<Card> cards) throws RemoteException;

    public void askColour() throws RemoteException;

    public void giveInterface(serverInterface server) throws RemoteException;

    public void giveCards(List<Card> cards) throws RemoteException;

    public Card getCard() throws RemoteException;

    public List<Card> getCards() throws RemoteException;

    public void updateTopCard(Card card) throws RemoteException;

}
