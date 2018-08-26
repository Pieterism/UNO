package uno;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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

//    public boolean canPlayOn(Card card) {
//        return (card.myColour == myColour);
//    }
    //Volgende speler moet nDraw aantal kaarten trekken en beurt wordt overgeslaan. 
    @Override
	public void play(UnoGame game) {
        try {
        	List<Card> draw = game.draw(nDraw);
        	game.getNextPlayer(1).getGameController().addCards(draw);
        	game.getNextPlayer(1).getCards().addAll(draw);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        game.goToNextPlayer();
    }
}
