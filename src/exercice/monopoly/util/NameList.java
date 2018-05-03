package exercice.monopoly.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class NameList {

    //// Private Static Members ////

    private static List<String> names = Arrays.asList(
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
     * @param index
     * @return
     */
    public static String getNameAt(int index) {
        if (!hasBeenShuffled) {
            Collections.shuffle(names);
        }

        return names.get(index);
    }
}
