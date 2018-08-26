package uno;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
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
        return true;
    }

    @Override
	public void play(UnoGame game) {
        try {
        	List<Card> draw = game.draw(nDraw);
        	game.getNextPlayer(1).getGameController().addCards(draw);
        	game.getNextPlayer(1).getCards().addAll(draw);
			this.myColour = game.getNextPlayer(0).getGameController().askColor();
			game.goToNextPlayer();

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // the current player's turn will also be skipped
        game.goToNextPlayer();
    }
}
