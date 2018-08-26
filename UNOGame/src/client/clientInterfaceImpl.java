package client;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import interfaces.clientInterface;
import interfaces.dispatcherInterface;
import interfaces.serverInterface;
import uno.Card;

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
    public void getCard() throws RemoteException {
    	Card temp = null;
    	for (Card card : cards) {
    		if (card.canPlayOn(topCard)) temp = card;
    	}
    	server.playCard(temp);
    }

    @Override
    public List<Card> getCards() throws RemoteException {
        return cards;
    }

    @Override
    public void updateTopCard(Card card) throws RemoteException {
        topCard = card;
    }

	@Override
	public void giveInterface(dispatcherInterface server) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
