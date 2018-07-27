package UNO;


import java.io.Serializable;
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
        return true;
    }

}
