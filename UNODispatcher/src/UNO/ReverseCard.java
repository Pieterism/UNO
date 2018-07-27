package UNO;

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
public class ReverseCard extends Card implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5745108777726812406L;

	public ReverseCard(int colour) {
        super(colour, "REVERSE");
    }

    public boolean canPlayOn(Card card) {
        return (card.getColour() == this.getColour());
    }

    public void play(UnoGame game) {
        game.setPlayDirection(-1);
    }
}
