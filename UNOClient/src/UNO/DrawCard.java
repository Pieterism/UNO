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
public class DrawCard extends Card implements Serializable{

 	/**
	 * 
	 */
	private static final long serialVersionUID = 1954659785727378897L;
	// instance variables - replace the example below with your own
    private int nDraw;

    /**
     * Constructor for objects of class DrawCard
     */
    public DrawCard(int colour, int nDraw) {
        super(colour, "PLUS2");
        this.nDraw = nDraw;
    }

    public int getNDraw() {
        return nDraw;
    }

    @Override
	public boolean canPlayOn(Card card) {
        return (card.myColour == myColour);
    }
}
