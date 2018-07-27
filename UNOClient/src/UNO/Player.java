package UNO;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Pieter Vanderhaegen
 */
public class Player{

	private String myName;
    
	
	public Player () {
		
	}
	
	public Player (String name) {
		this.myName = name;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}
	
	
}
