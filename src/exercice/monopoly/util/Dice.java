package exercice.monopoly.util;

import java.util.Random;

/**
 * Dice class a helper static class representing a dice roll
 */
public class Dice {

    //// Private Static Members ////

    private static final int nbFaces = 6;
    private static final Random random = new Random();

    //// Public Static Functions ////

    /**
     * Roll a dice and returns the value
     *
     * @return the value rolled
     */
    public static int roll() {
        return random.nextInt(nbFaces) + 1;
    }
}
