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

	public Player() {
		cards = new ArrayList<>();
	}

	public Player(String name, gameControllerInterface gameController) {
		this.name = name;
		this.gameController = gameController;
		this.score = 0;
		cards = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public gameControllerInterface getGameController() {
		return gameController;
	}

	public void setGameController(gameControllerInterface gameController) {
		this.gameController = gameController;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public int getScore() {
		return score;
	}

	public void setScore() {
		for (Card card : cards) {
			score += card.myScore;
		}
	}

	public boolean removeCard(Card card) {
		boolean removable = false;
		removable = this.cards.remove(card);
		return removable;
	}

	public boolean getReady() {
		return this.ready;
	}

	public void setReady(boolean bool) {
		this.ready = bool;
	}

}
