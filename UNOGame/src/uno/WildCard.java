package uno;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Random;

/**
 *
 * @author Pieter Vanderhaegen
 */
public class WildCard extends Card implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -1204492106528466734L;

	public WildCard() {
        super(COLOUR_NONE, "WILDCARD");
    }

    @Override
    public boolean canPlayOn(Card card) {
        // wild card: it should always return true
        return true;
    }
    
    @Override public void play(UnoGame game) {
    	try {
			this.myColour = game.getNextPlayer(0).getGameController().askColor();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	
    }

}
