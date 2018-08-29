package uno;

import java.io.Serializable;

import javafx.scene.image.Image;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pieter Vanderhaegen
 */
public class Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -467804728581745981L;
	// Constanten voor de kleuren
	public static final int COLOUR_NONE = 0;
	public static final int COLOUR_GREEN = 1;
	public static final int COLOUR_BLUE = 2;
	public static final int COLOUR_RED = 3;
	public static final int COLOUR_YELLOW = 4;
	public static final String[] COLOUR_NAMES = { "", "GREEN", "BLUE", "RED", "YELLOW" };

	public int myColour;
	public String mySymbol;
	public String cardName;
	public int myScore;

	// Constructor om speciale kaart te maken
	/**
	 * @param colour
	 * @param symbol
	 */
	public Card(int colour, String symbol) {
		myColour = colour;
		mySymbol = symbol;
		cardName = mySymbol + "_" + COLOUR_NAMES[colour] + ".png";
	}

	// Constructor om gewone kaart te maken
	/**
	 * @param colour
	 * @param number
	 */
	public Card(int colour, int number) {
		myColour = colour;
		mySymbol = String.valueOf(number);
		cardName = mySymbol + "_" + COLOUR_NAMES[colour] + ".png";
		myScore = number;
	}

	/**
	 * @param selectedCard
	 */
	public Card(Card selectedCard) {
		this.cardName = selectedCard.cardName;
		this.myColour = selectedCard.myColour;
		this.mySymbol = selectedCard.mySymbol;
	}

	/**
	 * @return
	 */
	public int getColour() {
		return myColour;
	}

	/**
	 * @param colour
	 */
	public void setColour(int colour) {
		myColour = colour;
	}

	/**
	 * @return
	 */
	public String getSymbol() {
		return mySymbol;
	}

	/**
	 * @param symbol
	 */
	public void setSymbol(int symbol) {
		this.mySymbol = String.valueOf(symbol);
	}

	// Controleert of kaart gespeeld kan worden op de kaart die er reeds ligt
	/**
	 * @param card
	 * @return
	 */
	public boolean canPlayOn(Card card) {
		return (card.mySymbol.equals(mySymbol) || card.myColour == myColour);
	}

	// hierin komen acties bij het spelen van een speciale kaart
	/**
	 * @param game
	 */
	public void play(UnoGame game) {
		// Default
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result;

		result = COLOUR_NAMES[myColour];
		if (!result.isEmpty()) {
			result += " ";
		}
		result += mySymbol;
		return result;
	}

	/**
	 * @return
	 */
	public Image getImage() {
		return new Image(Card.class.getResourceAsStream("SEVEN_YELLOW.png"));

	}

}
