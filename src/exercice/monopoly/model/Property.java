package exercice.monopoly.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class Property {

    //// Events ////

    /**
     *
     */
    public List<Consumer<Property>> ownerChanged;

    //// Private Members ////

    private int index;
    private int price;
    private int rent;
    private Player owner;

    //// Constructors ////

    /**
     * @param index
     * @param price
     * @param rent
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
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return
     */
    public int getRent() {
        return rent;
    }

    /**
     * @return
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * @param owner
     */
    public void setOwner(Player owner) {
        this.owner = owner;
        // Notify changes
        ownerChanged.forEach(b -> b.accept(this));
    }
}
