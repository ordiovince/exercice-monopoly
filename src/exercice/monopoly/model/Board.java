package exercice.monopoly.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Board {

    //// Private Members ////

    private List<Property> properties;

    //// Constructors ////

    /**
     * @param nbProperties
     * @param price
     * @param rent
     */
    public Board(int nbProperties, int price, int rent) {
        properties = new ArrayList<>();

        for (int i = 0; i < nbProperties; i++) {
            properties.add(new Property(i, price, rent));
        }
    }

    //// Public Functions ////

    /**
     * @return
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param index
     * @return
     */
    public Property getPropertiyAt(int index) {
        return properties.get(index);
    }
}
