/*
 * CRITTERS Main.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * <Student1 Name> Vivek Mahapatra
 * <Student1 EID> vsm485
 * <Student1 5-digit Unique No.> 17805
 * <Student2 Name> Ken Zhang
 * <Student2 EID> ktz85
 * <Student2 5-digit Unique No.> 17805
 * Slip days used: <0>
 * Fall 2021
 */

package assignment4;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
//import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Usage: java <pkg name>.Main <input file> test input file is
 * optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */

public class Main {

    /* Scanner connected to keyboard input, or input file */
    static Scanner kb;

    /* Input file, used instead of keyboard input if specified */
    private static String inputFile;

    /* If test specified, holds all console output */
    static ByteArrayOutputStream testOutputString;

    /* Use it or not, as you wish! */
    private static boolean DEBUG = false;

    /* if you want to restore output to console */
    static PrintStream old = System.out;

    /* Gets the package name.  The usage assumes that Critter and its
       subclasses are all in the same package. */
    private static String myPackage; // package of Critter file.

    /* Critter cannot be in default pkg. */
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     *
     * @param args args can be empty.  If not empty, provide two
     *             parameters -- the first is a file name, and the
     *             second is test (for test output, where all output
     *             to be directed to a String), or nothing.
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
            }
            if (args.length >= 2) {
                /* If the word "test" is the second argument to java */
                if (args[1].equals("test")) {
                    /* Create a stream to hold the output */
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    /* Save the old System.out. */
                    old = System.out;
                    /* Tell Java to use the special stream; all
                     * console output will be redirected here from
                     * now */
                    System.setOut(ps);
                }
            }
        } else { // If no arguments to main
            kb = new Scanner(System.in); // Use keyboard and console
        }
        commandInterpreter(kb);

        System.out.flush();
    }

    /* Do not alter the code above for your submission. */

    private static void commandInterpreter(Scanner kb) {		//This loops the entire process of the world
    	while(true) {
    	String s = "";
        System.out.print("critters>");
        s = kb.nextLine();
        String[] parameters = s.split(" ");
        //String string = Arrays.toString(parameters);
        //System.out.println(string);
        switch(parameters[0]) {									//switch case figures out what to do with each input
        	case "quit":										//quit returns to main and ends the program
        		if(parameters.length > 1) {
        			System.out.println("error processing: "+s);
        		}
        		else
        			kb.close();
        		return;
        	case "show":										//show displays the Critter world
        		if(parameters.length > 1) {
        			System.out.println("error processing: "+s);	//error displayed if more words are input
        		}
        		else
        			Critter.displayWorld();
        		continue;
        		
        	case "step":										//step causes the Critters to move a specified amount of times
        		if(parameters.length > 2) {
        			System.out.println("error processing: "+s);
        		}
        		else if(parameters.length == 1)					//if no specified number, step only once
        			Critter.worldTimeStep();
        		else {
        			try {
        				int i = 0;
        				i = Integer.parseInt(parameters[1]);
        				while(i > 0) {							//if number is specified, step that many times
        					Critter.worldTimeStep();
        					i--;
        				}
        			}
        			catch(NumberFormatException e) {
        				System.out.println("error processing: "+s);
        			}
        		}
        		continue;
        	case "seed":										//seed creates seed in Critter world
        		if(parameters.length > 2) {
        			System.out.println("error processing: "+s);
        		}
        		else {
        			try {
        				long seed = Long.parseLong(parameters[1]);
        				Critter.setSeed(seed);
        			}
        			catch(NumberFormatException e) {
        				System.out.println("error processing: "+s);
        			}
        						
        		}
        		continue;
        	case "create":													//create creates a specified Critter in world
        		if(parameters.length == 1 || parameters.length > 3) {
        			System.out.println("error processing: "+s);
        		}
        		else if(parameters.length == 2) {							//must have create <Critter>
        			String critterName = parameters[1];
        			try {
						Critter.createCritter(critterName);
					} catch (InvalidCritterException e) {
						System.out.println("error processing: "+s);
					}
        		}
        		else {
        			String critterName = parameters[1];
        			try {
        				int i = Integer.parseInt(parameters[2]);			//creates multiple Critters according to specified number
        				while(i > 0) {
        					Critter.createCritter(critterName);
        					i--;
        				}
        			}
        			catch(InvalidCritterException e) {
        				System.out.println("error processing: "+s);
        			}
        			catch(NumberFormatException e) {
        				System.out.println("error processing: "+s);
        			}
        		}
        		continue;
        	case "stats":											//reveals the stats of all critters of specified class
        		if(parameters.length != 2 || parameters[1].equals("Critters")) {
        			System.out.println("error processing: "+s);
        		}
        		else {
        			String critterName = parameters[1];

    				try {
    					List<Critter> list = Critter.getInstances(critterName);				//gathers all critters of specified critterName
    					Class<?> critterClass = null;
            			Class<?>[] types = {List.class};
    					critterClass = Class.forName(myPackage + "." + critterName);		//critterClass is now specified Critter class

    					
    					java.lang.reflect.Method runStats = critterClass.getMethod("runStats", types);
    					runStats.invoke(critterClass, list);
    				}
    				catch (InvalidCritterException|NoClassDefFoundError | ClassNotFoundException |
    						NoSuchMethodException | SecurityException | IllegalAccessException | 
    						IllegalArgumentException | InvocationTargetException e) {
    					System.out.println("error processing: "+s);
    				}
					
				}
        		continue;
        			
        	
        case "clear" : 																//clears Critter world
        	if(parameters.length != 1) {
        		System.out.println("error processing: "+s);
        	}
        	else
        		Critter.clearWorld();
        	continue;
        default:
        	System.out.println("invalid command: "+s);
        }
    }
  }
        
}
