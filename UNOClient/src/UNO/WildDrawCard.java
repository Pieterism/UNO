package UNO;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Scanner;



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

}
