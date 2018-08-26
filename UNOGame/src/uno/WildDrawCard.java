package uno;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Pieter Vanderhaegen
 */
public class WildDrawCard extends Card implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7436623692293175762L;
	private int nDraw;

    public WildDrawCard(int nDraw) {
        super(COLOUR_NONE, "WILDDRAWCARD");
        this.nDraw = nDraw;
    }

    public int getNDraw() {
        return nDraw;
    }

    @Override
    public boolean canPlayOn(Card card) {
    	Random rand = new Random();
    	System.out.println("Wat is de nieuwe kleur? ");
        System.out.println("GREEN: 1");
        System.out.println("BLUE: 2");
        System.out.println("RED : 3");
        System.out.println("YELLOW : 4");
        int kleur = rand.nextInt(4) +1;
        this.setColour(kleur);
        
        System.out.println("Nieuwe kleur is: " + COLOUR_NAMES[kleur]);

        return true;
    }

    @Override
	public void play(UnoGame game) {
        try {
			game.draw(game.getNextPlayer(1).getGameController(), nDraw);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // the current player's turn will also be skipped
        game.goToNextPlayer();
    }
}
