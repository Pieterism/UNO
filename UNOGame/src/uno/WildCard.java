package uno;


import java.io.Serializable;
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

        //kleur vragen 
        Random rand = new Random();
        System.out.println("Wat is de nieuwe kleur? ");
        System.out.println("GREEN: 1");
        System.out.println("BLUE: 2");
        System.out.println("RED : 3");
        System.out.println("YELLOW : 4");

        //speler mag toch zelf kleur kiezen?    
        int kleur = rand.nextInt(4) + 1;

        this.setColour(kleur);

        System.out.println("Nieuwe kleur is: " + COLOUR_NAMES[kleur]);

        // wild card: it should always return true
        return true;
    }

}
