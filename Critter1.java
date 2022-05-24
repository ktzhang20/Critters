package assignment4;

import assignment4.Critter.TestCritter;


/**
 * Lazy Critter, RNG determines if it walks, and it only runs from fights.
 * However, if it doesn't have enough energy, it just takes the fight
 */
public class Critter1 extends TestCritter {

    @Override
    public void doTimeStep() {
        int rand = getRandomInt(4);
        if(rand == 3) {
        	walk(1);
        }
    }

    @Override
    public boolean fight(String opponent) {
        if(getEnergy() > 20) {
        	walk(getRandomInt(8));
        	return false;
        }
        return true;
    }

    public String toString() {
        return "1";
    }

}