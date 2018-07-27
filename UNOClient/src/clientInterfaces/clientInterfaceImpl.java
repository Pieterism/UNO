package clientInterfaces;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import UNO.Card;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import serverInterfaces.serverInterface;

public class clientInterfaceImpl extends UnicastRemoteObject implements clientInterface {

    private serverInterface server;
    private List<Card> cards;
    private Card topCard;
    private String msgs;

    
    public String getMsgs() {
		return msgs;
	}

	public void setMsgs(String msgs) {
		this.msgs = msgs;
	}

	public clientInterfaceImpl() throws RemoteException {
        cards = new ArrayList<>();
        msgs = "";
    }

    @Override
    public void tell(String s) throws RemoteException {
        msgs += s + "\n";
    }

    @Override
    public void updatePile(Card card) throws RemoteException {
        System.out.println(card.getColour() + " " + card.getSymbol());

    }

    @Override
    public void drawCards(List<Card> cards) throws RemoteException {
        cards.addAll(cards);

    }

    @Override
    public void askColour() throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void giveInterface(serverInterface server) throws RemoteException {
        this.server = server;

    }

    @Override
    public void giveCards(List<Card> cards) throws RemoteException {
        this.cards.addAll(cards);

    }

    @Override
    public Card getCard() throws RemoteException {
        for (Card card : cards) {
            if (card.canPlayOn(topCard)) {
                cards.remove(card);
                return card;
            }
        }
        return null;
    }

    @Override
    public List<Card> getCards() throws RemoteException {
        return cards;
    }

    @Override
    public void updateTopCard(Card card) throws RemoteException {
        topCard = card;
    }

}
