package exercice.monopoly.util;

import java.util.Random;

/**
 *
 */
public class Dice {

    //// Private Static Members ////

    private static final int nbFaces = 6;
    private static Random random = new Random();

    //// Public Static Functions ////

    /**
     * @return
     */
    public static int roll() {
        return random.nextInt(nbFaces) + 1;
    }
}
