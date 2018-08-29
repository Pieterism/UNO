package uno;

import java.io.Serializable;

/**
 *
 * @author Pieter Vanderhaegen
 */
public class SkipCard extends Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1682533977395640829L;
	private int nSkip;

	/**
	 * @param colour
	 * @param nSkip
	 */
	public SkipCard(int colour, int nSkip) {
		super(colour, "SKIP");
		this.nSkip = nSkip;
		this.myScore = 20;
	}

	/**
	 * @return
	 */
	public int getNSkip() {
		return nSkip;
	}

	/* (non-Javadoc)
	 * @see uno.Card#play(uno.UnoGame)
	 */
	@Override
	public void play(UnoGame game) {
		// skip the nSkip player
		for (int i = 0; i < this.nSkip; i++) {
			game.goToNextPlayer();
		}
	}
}
