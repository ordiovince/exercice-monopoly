package exercice.monopoly.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Property class representing a square on the board as a property
 */
public class Property {

    //// Events ////

    /**
     * Event raised when the owner of the property has changed
     */
    public final List<Consumer<Property>> ownerChanged;

    //// Private Members ////

    private final int index;
    private final int price;
    private final int rent;
    private Player owner;

    //// Constructors ////

    /**
     * @param index the index of the property on the board
     * @param price the price of the property
     * @param rent  the price of the rent for the property
     */
    public Property(int index, int price, int rent) {
        this.index = index;
        this.price = price;
        this.rent = rent;
        this.owner = null;
        this.ownerChanged = new ArrayList<>();
    }

    //// Public Functions ////

    /**
     * @return the index of the property on the board
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the price of the property
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return the price of the rent for the property
     */
    public int getRent() {
        return rent;
    }

    /**
     * @return the owner of the property
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Changes the owner of the property
     *
     * @param owner the new owner of the property to set
     */
    public void setOwner(Player owner) {
        this.owner = owner;
        // Notify changes
        ownerChanged.forEach(b -> b.accept(this));
    }
}
