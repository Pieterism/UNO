package uno;

import java.util.ArrayList;
import java.util.List;

import interfaces.gameControllerInterface;

/**
 *
 * @author Pieter Vanderhaegen
 */
public class Player {
	private String name;
	private gameControllerInterface gameController;
	private List<Card> cards;
	private int score;
	private boolean ready;

	/**
	 * 
	 */
	public Player() {
		cards = new ArrayList<>();
	}

	/**
	 * @param name
	 * @param gameController
	 */
	public Player(String name, gameControllerInterface gameController) {
		this.name = name;
		this.gameController = gameController;
		this.score = 0;
		cards = new ArrayList<>();
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public gameControllerInterface getGameController() {
		return gameController;
	}

	/**
	 * @param gameController
	 */
	public void setGameController(gameControllerInterface gameController) {
		this.gameController = gameController;
	}

	/**
	 * @return
	 */
	public List<Card> getCards() {
		return cards;
	}

	/**
	 * @param cards
	 */
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	/**
	 * @return
	 */
	public int getScore() {
		return score;
	}

	/**
	 * 
	 */
	public void setScore() {
		for (Card card : cards) {
			score += card.myScore;
		}
	}

	/**
	 * @param card
	 * @return
	 */
	public boolean removeCard(Card card) {
		boolean removable = false;
		removable = this.cards.remove(card);
		return removable;
	}

	/**
	 * @return
	 */
	public boolean getReady() {
		return this.ready;
	}

	/**
	 * @param bool
	 */
	public void setReady(boolean bool) {
		this.ready = bool;
	}

}
