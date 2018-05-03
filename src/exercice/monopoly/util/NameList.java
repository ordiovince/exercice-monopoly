package exercice.monopoly.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * NameList class a helper static class providing a list of names
 */
public class NameList {

    //// Private Static Members ////

    private static final List<String> names = Arrays.asList(
            "Jacqualine",
            "Erin",
            "Angelika",
            "Zula",
            "Bea",
            "Bess",
            "Ira",
            "Maris",
            "Kris",
            "Valentine",
            "Tamesha",
            "Royal",
            "Bobbi",
            "Sheba",
            "Cherryl",
            "Lissa",
            "Ozie",
            "Nena",
            "Manie",
            "Kristie",
            "Fredric",
            "Coletta",
            "Suzie",
            "Steve",
            "Ariana",
            "Trinh",
            "Margarito",
            "Deonna",
            "Tomasa",
            "Ginny",
            "Milo",
            "Santo",
            "Bradley",
            "Camelia",
            "Allegra",
            "Taren",
            "Ayako",
            "Loida",
            "Alice",
            "Joie",
            "Quincy",
            "Yukiko",
            "Gilbert",
            "Zonia",
            "Oma",
            "Kaci",
            "Georgia",
            "Kathline",
            "Mahalia",
            "Ivy"
    );

    private static boolean hasBeenShuffled = false;

    //// Public Static Functions ////

    /**
     * Return the name stored at this index in the list (shuffle the list first if it hasn't been done)
     *
     * @param index the index of the name to get
     * @return the name stored at this index
     */
    public static String getNameAt(int index) {
        if (!hasBeenShuffled) {
            // If the list hasn't been shuffled yet
            hasBeenShuffled = true;
            // Shuffle the list
            Collections.shuffle(names);
        }

        return names.get(index);
    }
}
