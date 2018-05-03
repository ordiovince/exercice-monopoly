package exercice.monopoly.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Board class representing the game board
 */
public class Board {

    //// Private Members ////

    private final List<Property> properties;

    //// Constructors ////

    /**
     * @param nbProperties the number of properties to add to the board
     * @param price        the price of each property
     * @param rent         the price of the rent of each property
     */
    public Board(int nbProperties, int price, int rent) {
        properties = new ArrayList<>();

        for (int i = 0; i < nbProperties; i++) {
            properties.add(new Property(i, price, rent));
        }
    }

    //// Public Functions ////

    /**
     * @return the list of properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param index the index of the property to get
     * @return the property found at the index
     */
    public Property getPropertyAt(int index) {

        if (index >= properties.size()) {
            throw new IllegalArgumentException("index parameter must be within the bounds of the list of properties");
        }

        return properties.get(index);
    }
}
