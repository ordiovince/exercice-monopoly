package exercice.monopoly.util;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ColorList {

    //// Private Static Members ////

    private static List<Color> colors;

    //// Public Static Functions ////

    /**
     * @param index
     * @return
     */
    public static Color getColorAt(int index) {
        if (colors == null) {
            colors = new ArrayList<>();
        }

        while (index > colors.size() - 1) {
            colors.add(Color.color(Math.random(), Math.random(), Math.random()));
        }

        return colors.get(index);
    }

    /**
     * @param index
     */
    public static void removeColorAt(int index) {
        colors.remove(index);
    }
}
