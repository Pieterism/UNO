package uno;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import interfaces.dbInterface;
import interfaces.gameControllerInterface;

/**
 *
 * @author Pieter Vanderhaegen
 */
public class UnoGame {
	private ArrayList<Player> players;
	private ArrayList<Card> pile;
	private ArrayList<Card> deck;

	private int myPlayDirection, currentPlayer;
	private int playerCount, gameTheme;
	private dbInterface db;

	private String name, gameId;

	/**
	 * @return
	 */
	public String getGameId() {
		return gameId;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param gameId
	 */
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	/**
	 * @param i
	 */
	public void setPlayDirection(int i) {
		this.myPlayDirection = i;
	}

	/**
	 * 
	 */
	public void reversePlayDirection() {
		this.myPlayDirection = this.myPlayDirection * (-1);
	}

	/**
	 * @return
	 */
	public int connectedPlayers() {
		return players.size();
	}

	/**
	 * @return
	 */
	public int getPlayerCount() {
		return playerCount;
	}

	// Nieuw spel starten met aantal spelers met standaard UNO-deck.
	/**
	 * @param nPlayers
	 * @param name
	 * @param gameTheme
	 * @param db
	 */
	public UnoGame(int nPlayers, String name, int gameTheme, dbInterface db) {
		deck = new ArrayList<Card>();
		this.name = "UNO game";
		this.name = name;
		this.gameTheme = gameTheme;
		this.players = new ArrayList<>();
		this.db = db;

		// Deck vullen met alle kaarten.
		newDeck();

		// Pile is initially empty;
		pile = new ArrayList<Card>();
		players = new ArrayList<Player>();

		this.playerCount = nPlayers;

	}

	/**
	 * @param skip
	 * @return
	 */
	public Player getNextPlayer(int skip) {
		int i = currentPlayer + (skip * myPlayDirection);
		i = i % players.size();
		if (i < 0) {
			i += players.size();
		}

		return players.get(i);
	}

	/**
	 * 
	 */
	public void goToNextPlayer() {
		currentPlayer = (currentPlayer + myPlayDirection + players.size()) % players.size();
	}

	/**
	 * @param nCards
	 * @return
	 */
	public List<Card> draw(int nCards) {
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < nCards; i++) {
			if (deck.size() == 0 && pile.size() > 0) {
				Card keep = pile.get(0);
				deck.addAll(pile.subList(1, pile.size()));
				pile.clear();
				pile.add(keep);
				Collections.shuffle(deck);
			}
			cards.add(deck.remove(0));
		}
		return cards;
	}

	/**
	 * @throws RemoteException
	 */
	public void dealCards() throws RemoteException {

		for (Player player : players) {
			player.getCards().addAll(draw(7));
			player.getGameController().addCards(player.getCards());
		}

		// turn over the top card
		Card card = deck.remove(0);
		pile.add(0, card);

		for (Player player : players) {
			player.getGameController().addPile(card);
		}

		updateToDBServerInit();

		currentPlayer = 0;
		myPlayDirection = 1;

		if (card.getClass() == WildCard.class || card.getClass() == WildDrawCard.class) {
			card.myColour = Card.COLOUR_BLUE;
		}

	}

	/**
	 * @throws RemoteException
	 */
	private void updateToDBServerInit() throws RemoteException {
		for (Player player : players) {
			db.updateHandPlayer(player.getName(), player.getCards(), gameId);
		}
	}

	/**
	 * @param username
	 * @param cards
	 * @throws RemoteException
	 */
	private void updateToDBServer(String username, List<Card> cards) throws RemoteException {
		db.updateHandPlayer(username, cards, gameId);
	}

	// speel 1 beurt van het spel
	/**
	 * @return
	 * @throws RemoteException
	 */
	public String playTurn() throws RemoteException {
		Player player = players.get(currentPlayer);

		print(player);

		// the current player chooses a card to play
		Card card = player.getGameController().getCard();
		if (card == null) {
			// they cannot play, so draw a card
			List<Card> draw = draw(1);
			getNextPlayer(0).getGameController().addCards(draw);
			getNextPlayer(0).getCards().addAll(draw);
			updateCardAmountPlayer(player);
			goToNextPlayer();
			System.out.println();
			updateToDBServer(player.getName(), player.getCards());
			return null;
		}
		if (!card.canPlayOn(pile.get(0))) {
			return playTurn();
		}

		// play the card
		pile.add(0, card);
		card.play(this);
		playCard(player, card);
		updateToDBServer(player.getName(), player.getCards());

		// If that was their last card, then they win
		if (player.getCards().size() == 0) {
			return player.getName();
		}

		// Go on to the next player
		goToNextPlayer();
		System.out.println();
		return null;
	}

	/**
	 * @param player
	 */
	private void print(Player player) {
		System.out.println(player.getName());
		System.out.println(player.getCards());

	}

	/**
	 * @param player
	 * @throws RemoteException
	 */
	public void updateCardAmountPlayer(Player player) throws RemoteException {
		for (Player iter : players) {
			iter.getGameController().setCardAmountPlayer(player.getName(), player.getCards().size());
			;
		}
	}

	/**
	 * @param player
	 * @param card
	 * @throws RemoteException
	 */
	public void playCard(Player player, Card card) throws RemoteException {
		boolean valid = false;
		Iterator<Card> newIterator = player.getCards().iterator();
		while (newIterator.hasNext()) {
			Card c = newIterator.next();
			if (c.cardName.equals(card.cardName)) {
				valid = true;
				player.getCards().remove(c);
				break;
			}
		}
		if (valid) {
			for (Player iter : players) {
				iter.getGameController().addPile(card);
				iter.getGameController().setCardAmountPlayer(player.getName(), player.getCards().size());
				;
			}
		} else {
			player.getGameController().addCards(draw(1));
			System.out.println("An error occured (a card was played when it was not part of the player's hand)");
		}

	}

	// speel een volledig spel en geef identiteit van winnaar terug
	/**
	 * @return
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public String play() throws RemoteException, InterruptedException {
		dealCards();
		for (Player player : players) {
			for (Player player2 : players) {
				player.getGameController().setCardAmountPlayer(player2.getName(), player2.getCards().size());
			}
		}
		String winner = playTurn();
		while (winner == null) {
			winner = playTurn();
		}

		List<String> score = new ArrayList<>();
		// count the score
		for (Player player : players) {
			StringBuilder sBuilder = new StringBuilder();
			player.setScore();
			sBuilder.append(player.getName() + ": \t " + player.getScore() + "\n");
			score.add(sBuilder.toString());
		}

		// send the score to the players
		for (Player player : players) {
			player.getGameController().setScoreboard(score);
		}
		for (Player player : players) {
			player.getCards().clear();
		}
		return winner;
	}

	/**
	 * @return
	 */
	public String getId() {
		return this.gameId;
	}

	/**
	 * @param nextPlayer
	 * @param nDraw
	 * @throws RemoteException
	 */
	public void draw(gameControllerInterface nextPlayer, int nDraw) throws RemoteException {
		nextPlayer.addCards(draw(nDraw));
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param username
	 * @param gameController
	 * @throws RemoteException
	 * @throws NumberFormatException
	 * @throws InterruptedException
	 */
	public void addPlayer(String username, gameControllerInterface gameController)
			throws RemoteException, NumberFormatException, InterruptedException {
		Player player = new Player(username, gameController);

		players.add(player);
		if (players.size() == playerCount) {
			System.out.println("Let the games begin!");
			for (Player iter : players) {
				iter.getGameController()
						.setMsg("The game is full, press the top card in the middle to start the game!");
			}
		}
	}

	/**
	 * @param msg
	 * @throws RemoteException
	 */
	public void sendMsg(String msg) throws RemoteException {
		for (Player player : players) {
			player.getGameController().setMsg(msg);
		}

	}

	/**
	 * 
	 */
	public void newDeck() {
		for (int c = 1; c <= 4; c++) {
			deck.add(new Card(c, 0));
			deck.add(new SkipCard(c, 1));
			deck.add(new SkipCard(c, 1));
			deck.add(new ReverseCard(c));
			deck.add(new ReverseCard(c));
			deck.add(new DrawCard(c, 2));
			deck.add(new DrawCard(c, 2));
			deck.add(new WildDrawCard(4));
			deck.add(new WildCard());

			for (int i = 1; i <= 9; i++) {
				deck.add(new Card(c, i));
				deck.add(new Card(c, i));
			}
		}
		Collections.shuffle(deck);
	}

	/**
	 * @return
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}

	/**
	 * @param players
	 */
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	/**
	 * @return
	 */
	public int getTheme() {
		return this.gameTheme;
	}

	/**
	 * 
	 */
	public void endGame() {
		try {
			sendMsg("Someone has left the Game, the game has ended");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
