package UNO;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clientInterfaces.gameControllerInterface;
import serverInterfaces.serverInterface;

public class UnoGame {
	
    private ArrayList<Card> myCards;
    private Card topCard;
	private ArrayList<String> myPlayers;
    private int myCurrentPlayer;
    private serverInterface server;
    
    public void UNOGame(serverInterface server, ArrayList<Card> cards, ArrayList<String> players, int currentPlayer) {
    	this.server = server;
    	this.setMyCards(cards);
    	this.setMyPlayers(players);
    	this.setMyCurrentPlayer(currentPlayer);
    }
    
	public ArrayList<Card> getMyCards() {
		return myCards;
	}
	
	public void setMyCards(ArrayList<Card> myCards) {
		this.myCards = myCards;
	}
	
	public Card getTopCard() {
		return topCard;
	}
	
	public void setTopCard(Card topCard) {
		this.topCard = topCard;
	}
	
	public ArrayList<String> getMyPlayers() {
		return myPlayers;
	}
	
	public void setMyPlayers(ArrayList<String> myPlayers) {
		this.myPlayers = myPlayers;
	}
	
	public int getMyCurrentPlayer() {
		return myCurrentPlayer;
	}
	
	public void setMyCurrentPlayer(int myCurrentPlayer) {
		this.myCurrentPlayer = myCurrentPlayer;
	}

	public void addCards(ArrayList<Card> cards) {
		myCards.addAll(cards);
	}
    
}
