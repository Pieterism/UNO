package uno;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Pieter Vanderhaegen
 */
public class WildDrawCard extends Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7436623692293175762L;
	private int nDraw;

	/**
	 * @param nDraw
	 */
	public WildDrawCard(int nDraw) {
		super(COLOUR_NONE, "WILDDRAWCARD");
		this.nDraw = nDraw;
		this.myScore = 50;
	}

	/**
	 * @return
	 */
	public int getNDraw() {
		return nDraw;
	}

	/* (non-Javadoc)
	 * @see uno.Card#canPlayOn(uno.Card)
	 */
	@Override
	public boolean canPlayOn(Card card) {
		return true;
	}

	/* (non-Javadoc)
	 * @see uno.Card#play(uno.UnoGame)
	 */
	@Override
	public void play(UnoGame game) {
		try {
			this.myColour = game.getNextPlayer(0).getGameController().askColor();
			List<Card> draw = game.draw(nDraw);
			game.getNextPlayer(1).getGameController().addCards(draw);
			game.getNextPlayer(1).getCards().addAll(draw);
			game.sendMsg("Chosen colour is " + Card.COLOUR_NAMES[myColour]);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		game.goToNextPlayer();
	}
}
