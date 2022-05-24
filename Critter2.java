package assignment4;

import assignment4.Critter.TestCritter;


/**
 * Critter runs at all possible moment that it can, sometimes even to its death.
 */
public class Critter2 extends TestCritter {

    @Override
    public void doTimeStep() {
        int rand = getRandomInt(4);
        for(int i = 0; i < rand; i++) {
        	run(getRandomInt(8));
        }
    }

    @Override
    public boolean fight(String opponent) {
        run(getRandomInt(8));
        return false;	
    }

    public String toString() {
        return "2";
    }

}