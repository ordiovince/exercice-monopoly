package exercice.monopoly.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Player class used to represent a player
 */
public class Player {

    //// Events ////

    /**
     *
     */
    public List<Consumer<Player>> balanceChanged;
    /**
     *
     */
    public List<Consumer<Player>> currentPropertyIndexChanged;
    /**
     *
     */
    public List<Consumer<Player>> gameOverOccurred;

    //// Private Members ////

    private String name;
    private int balance;
    private int currentPropertyIndex;

    //// Constructors ////

    /**
     * @param name    the name of the player to create
     * @param balance the starting amount of movey this player will starts with
     */
    public Player(String name, int balance) {
        this.name = name;
        this.balance = balance;
        this.currentPropertyIndex = 0;
        this.balanceChanged = new ArrayList<>();
        this.currentPropertyIndexChanged = new ArrayList<>();
        this.gameOverOccurred = new ArrayList<>();
    }

    //// Public Functions ////

    /**
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * @return the current balance of the player
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Modifies the balance of the player (a positive value increments the balance, a negative value decrements it)
     *
     * @param amount the amount to modify the balance by
     */
    public void modifyBalance(int amount) {
        this.balance += amount;
        // Notify changes
        balanceChanged.forEach(b -> b.accept(this));
    }

    /**
     * @return the index of the property the player is currently standing on
     */
    public int getCurrentPropertyIndex() {
        return currentPropertyIndex;
    }

    /**
     * Changes the index of the property the player is currently standing on
     *
     * @param currentPropertyIndex the index of the property to set
     */
    public void setCurrentPropertyIndex(int currentPropertyIndex) {
        this.currentPropertyIndex = currentPropertyIndex;
        // Notify changes
        currentPropertyIndexChanged.forEach(b -> b.accept(this));
    }

    /**
     * Notfifies a game over for this player
     */
    public void gameOver() {
        // Notify game over
        gameOverOccurred.forEach(b -> b.accept(this));
    }
}
