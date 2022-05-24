/*
 * CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Ken Zhang
 * ktz85
 * 17805
 * Vivek Mahapatra
 * vsm485
 * 17805
 * Slip days used: <0>
 * Fall 2021
 */

package assignment4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/* 
 * See the PDF for descriptions of the methods and fields in this
 * class. 
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {

    private int energy = 0;

    private int x_coord;
    private int y_coord;
    private boolean alive = true;
    
    private static List<Critter> population = new ArrayList<Critter>();
    private static List<Critter> babies = new ArrayList<Critter>();

    private static boolean getStatus(Critter c) {
    	return c.alive;
    }
    /* Gets the package name.  This assumes that Critter and its
     * subclasses are all in the same package. */
    private static String myPackage;

    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    private static Random rand = new Random();

    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    public static void setSeed(long new_seed) {
        rand = new Random(new_seed);
    }

    /**
     * create and initialize a Critter subclass.
     * critter_class_name must be the unqualified name of a concrete
     * subclass of Critter, if not, an InvalidCritterException must be
     * thrown.
     *
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name)
            throws InvalidCritterException {
        try {
			@SuppressWarnings("deprecation")
			Critter c = (Critter) Class.forName(myPackage + "." + critter_class_name).newInstance();
			Critter.population.add(c);// add critter to population
			
			//get start energy + random coords
			c.energy = Params.START_ENERGY;
			c.x_coord = Critter.getRandomInt(Params.WORLD_WIDTH);
			c.y_coord = Critter.getRandomInt(Params.WORLD_HEIGHT);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoClassDefFoundError e) {
			throw new InvalidCritterException(critter_class_name);
		}
        
    }

    /**
     * Gets a list of critters of a specific type.
     *
     * @param critter_class_name What kind of Critter is to be listed.
     *        Unqualified class name.
     * @return List of Critters.
     * @throws InvalidCritterException
     */
    public static List<Critter> getInstances(String critter_class_name)
            throws InvalidCritterException {
    	try {
    		Class<?> cClass = Class.forName(myPackage + "." + critter_class_name); // object of same name as string passed in
    		List<Critter> list = new ArrayList<Critter>();
            for(Critter c : population) { // 
            	if(cClass.isInstance(c)) // if critter is instance of input string class, add
            	list.add(c);
            }
            return list;
    	}
    	catch(ClassNotFoundException | NoClassDefFoundError e) { // catch if name input is not a class
    		throw new InvalidCritterException(critter_class_name);
    	}
        
    }

    /**
     * Clear the world of all critters, dead and alive
     */
    public static void clearWorld() {
        population.clear();
        babies.clear();
    }

    public static void worldTimeStep() {
    	java.util.ArrayList<Critter> dead = new java.util.ArrayList<Critter>(); // initialize an arraylist containing dead critters
    	// goes through each critter in population, does their time step, and checks if dead.
    	// if dead, add to arraylist.
        for(Critter c : population) { 
        	c.doTimeStep();
        	if(c.energy <= 0) {
        		c.alive = false;
        	}
        	c.energy -= Params.REST_ENERGY_COST;
        	if(c.energy <= 0 || !getStatus(c)) {
        		dead.add(c);
        	}
        }
        
        // this checks for if 2 critters are on the same position, and if so, how the fight goes.
        for(Critter c1 : population) {
        	for(Critter c2 : population) {
        		if(c1 == c2) // if they're equal, ignore.
        			continue;
        		if((c1.x_coord == c2.x_coord ) && (c1.y_coord == c2.y_coord)) { // same coords
        			
        			if(getStatus(c1) && getStatus(c2)) { // if they're both alive
        				// calls each critters fight function first, so if they change positions, they don't still fight
        				// (can run before fight)
        				boolean c1_fight = c1.fight(c2.toString());
            			boolean c2_fight = c2.fight(c1.toString());
            			
            			//if they run and die before a fight, set alive to false.
        				if(c1.energy <= 0) { 
        					c1.alive = false;
        				}
        				if(c2.energy <= 0)
        					c2.alive = false;
        				// checks if they're both still on top of each other
        				if ((c1.x_coord == c2.x_coord ) && (c1.y_coord == c2.y_coord)) {
							// ignore if one is dead, it will be removed later
							if (getStatus(c1) && getStatus(c2)){ // if they're both still alive (didn't run out of energy running)
        					int c1fight = 0; int c2fight = 0;
        					// if critter fights, get a random int and put it into new ints 
        					if (c1_fight)
								c1fight = Critter.getRandomInt(c1.energy);
							if (c2_fight)
								c2fight = Critter.getRandomInt(c2.energy);
							
							// if c1 wins, c1 takes half of c2's energy, and c2 dies
        					if(c1fight > c2fight) {
        						c1.energy += c2.energy/2;
        						c2.alive = false;
        						c2.energy = 0;
        					}
        					// vice versa
        					else if(c2fight > c1fight) {
        						c2.energy += c1.energy/2;
        						c1.alive = false;
        						c1.energy = 0;
        					}
        					// if they have the same random roll, then whoever wins is random too
        					else {
        						int rand = Critter.getRandomInt(2); // 2 to get either 0 or 1
        						if(rand == 0) {
        							c1.energy += c2.energy/2;
            						c2.alive = false;
            						c2.energy = 0;
        						}
        						else {
        							c2.energy += c1.energy/2;
            						c1.alive = false;
            						c1.energy = 0;
        						}
        						
        					}
							}
        				}
        				}
        			}
        		}
        	}
        
        	// create Clovers, with random positions using createCritter
        	try {
        		for(int i = 0; i < Params.REFRESH_CLOVER_COUNT; i++) {
				Critter.createCritter("Clover");
        		}
			} catch (InvalidCritterException e) {
				System.out.println("error processing");
			}
        // if critter has a reproduce, add babies to population list
        for(Critter c : babies) {
        	c.alive = true;
        	population.add(c);
        }
        
        // if any critters die, remove them from population
        for(Critter c : dead) {
        	population.remove(c);
        }
        
    }
    

    public static void displayWorld() {
    	// create 2D array of strings with 2 extra width/height to account for sides
        String[][] world = new String[Params.WORLD_HEIGHT+2][Params.WORLD_WIDTH+2];
        for(int i = 0; i < Params.WORLD_HEIGHT+2; i++) {
        	for(int j = 0; j < Params.WORLD_WIDTH+2; j++) {
        		if(i == 0 || i == Params.WORLD_HEIGHT+1) {
        			if(j == 0 || j == Params.WORLD_WIDTH+1) { // if corners
        				world[i][j] = "+";
        				continue;
        			}
        			else { // if top and bottom borders
        				world[i][j] = "-";
        			}
        		}
        		else if(j == 0 || j == Params.WORLD_WIDTH+1) {
        			if(i != 0 || i != Params.WORLD_HEIGHT+1) {
        				world[i][j] = "|"; // if left and right borders
        			}
        		}
        		else {
        			world[i][j] = " "; // else
        		}
        	}
        }
        for(Critter c : population) { // add critter to world offset by 1 in x and y so it isn't printed on borders
        	world[c.y_coord+1][c.x_coord+1] = c.toString();
        }
        // print out world
        for(int i = 0; i < Params.WORLD_HEIGHT+2; i++) {
        	for(int j = 0; j < Params.WORLD_WIDTH+2; j++){
        		System.out.print(world[i][j]); 
        	}
        	System.out.println();
        }
        
    }

    /**
     * Prints out how many Critters of each type there are on the
     * board.
     *
     * @param critters List of Critters.
     */
    public static void runStats(List<Critter> critters) {
        System.out.print("" + critters.size() + " critters as follows -- ");
        Map<String, Integer> critter_count = new HashMap<String, Integer>();
        for (Critter crit : critters) {
            String crit_string = crit.toString();
            critter_count.put(crit_string,
                    critter_count.getOrDefault(crit_string, 0) + 1);
        }
        String prefix = "";
        for (String s : critter_count.keySet()) {
            System.out.print(prefix + s + ":" + critter_count.get(s));
            prefix = ", ";
        }
        System.out.println();
    }

    // move method based on direction, and distance
    private final void move(int direction, int distance) {
    	switch(direction) {
    	
    		case 0: // right
    			this.x_coord += distance;
    			break;
    		case 1: // right + up
    			this.x_coord += distance;
    			this.y_coord += distance;
    			break;
    		case 2: // up
    			this.y_coord += distance;
    			break;
    		case 3: // left + up
    			this.y_coord += distance;
    			this.x_coord -= distance;
    			break;
    		case 4: // left
    			this.x_coord -= distance;
    			break;
    		case 5: // left+down
    			this.x_coord -= distance;
    			this.y_coord -= distance;
    			break;
    		case 6: // down
    			this.y_coord -= distance;
    			break;
    		case 7: // right+down
    			this.y_coord -= distance;
    			break;
    			
    	}
    }
    
    public abstract void doTimeStep();

    public abstract boolean fight(String oponent);

    /* a one-character long string that visually depicts your critter
     * in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }

    // walk with direction
    protected final void walk(int direction) {
        this.energy -= Params.WALK_ENERGY_COST; // subtracts energy
        this.move(direction, 1); // moves 1 square
        
        
        // rest accounts for if new location is at border/past border (for wrap around)
        if(this.x_coord > Params.WORLD_WIDTH-1) {
        	this.x_coord -= Params.WORLD_WIDTH;
        }
    	else if(this.x_coord < 0) {
        	this.x_coord = Params.WORLD_WIDTH+this.x_coord;
        }
        if(this.y_coord > Params.WORLD_HEIGHT-1) {
        	this.y_coord -= Params.WORLD_HEIGHT;
        }
        else if(this.y_coord < 0) {
        	this.y_coord = Params.WORLD_HEIGHT+this.y_coord;
        }
    }
    //run with direction (move 2 instead of 1)
    protected final void run(int direction) {
    	this.energy -= Params.RUN_ENERGY_COST;
    	this.move(direction,  2);
    	
    	// acount for new location if past border/land on border
    	if(this.x_coord > Params.WORLD_WIDTH-1) {
        	this.x_coord -= Params.WORLD_WIDTH;
        }
    	else if(this.x_coord < 0) {
        	this.x_coord = Params.WORLD_WIDTH+this.x_coord;
        }
        if(this.y_coord > Params.WORLD_HEIGHT-1) {
        	this.y_coord -= Params.WORLD_HEIGHT;
        }
        else if(this.y_coord < 0) {
        	this.y_coord = Params.WORLD_HEIGHT+this.y_coord;
        }
        
    }

    // reproduce function, offspring takes parent's energy/2, parent loses half of their energy
    protected final void reproduce(Critter offspring, int direction) {
        if(this.energy < Params.MIN_REPRODUCE_ENERGY)
        	return;
        offspring.energy = this.energy /2;
        this.energy = (int) Math.ceil( this.energy / 2);
        
        //put 1 square away from parent
        offspring.x_coord = this.x_coord;
        offspring.y_coord = this.y_coord;
        offspring.move(direction, 1);
        babies.add(offspring); // add to array of babies
        
    }

    /**
     * The TestCritter class allows some critters to "cheat". If you
     * want to create tests of your Critter model, you can create
     * subclasses of this class and then use the setter functions
     * contained here.
     * <p>
     * NOTE: you must make sure that the setter functions work with
     * your implementation of Critter. That means, if you're recording
     * the positions of your critters using some sort of external grid
     * or some other data structure in addition to the x_coord and
     * y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
     */
    static abstract class TestCritter extends Critter {

        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
        }

        protected void setX_coord(int new_x_coord) {
            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            super.y_coord = new_y_coord;
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        /**
         * This method getPopulation has to be modified by you if you
         * are not using the population ArrayList that has been
         * provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are
         * not using the babies ArrayList that has been provided in
         * the starter code.  In any case, it has to be implemented
         * for grading tests to work.  Babies should be added to the
         * general population at either the beginning OR the end of
         * every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }
    }
}
