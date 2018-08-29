package uno;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pieter Vanderhaegen
 */
public class ReverseCard extends Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5745108777726812406L;

	/**
	 * @param colour
	 */
	public ReverseCard(int colour) {
		super(colour, "REVERSE");
		this.myScore = 20;
	}

	/* (non-Javadoc)
	 * @see uno.Card#canPlayOn(uno.Card)
	 */
	@Override
	public boolean canPlayOn(Card card) {
		return (card.getColour() == this.getColour() || card.getSymbol().equals(this.getSymbol()));
	}

	/* (non-Javadoc)
	 * @see uno.Card#play(uno.UnoGame)
	 */
	@Override
	public void play(UnoGame game) {
		game.reversePlayDirection();
	}
}
