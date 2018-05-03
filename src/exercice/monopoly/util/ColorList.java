package exercice.monopoly.util;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * ColorList class a helper static class providing a list of random colors
 */
public class ColorList {

    //// Private Static Members ////

    private static List<Color> colors;

    //// Public Static Functions ////

    /**
     * Return the color stored at this index (generate one if not accessed before)
     *
     * @param index the index of the color to get
     * @return the color at the specified index
     */
    public static Color getColorAt(int index) {
        if (colors == null) {
            colors = new ArrayList<>();
        }

        while (index > colors.size() - 1) {
            // while the list of colors isn't big enough for the index specified, generate a nex random color and add it to the list
            colors.add(Color.color(Math.random(), Math.random(), Math.random()));
        }

        return colors.get(index);
    }

    /**
     * Removes a color from the list (for when a player is game over)
     *
     * @param index the index of the color to remove
     */
    public static void removeColorAt(int index) {
        colors.remove(index);
    }
}
