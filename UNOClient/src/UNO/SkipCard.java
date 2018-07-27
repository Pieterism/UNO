package UNO;

import java.io.Serializable;

/*
package application;
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pieter Vanderhaegen
 */
public class SkipCard extends Card implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1682533977395640829L;
	private int nSkip;

    public SkipCard(int colour, int nSkip) {
        super(colour, "SKIP");
        this.nSkip = nSkip;
    }

    public int getNSkip() {
        return nSkip;
    }
}
