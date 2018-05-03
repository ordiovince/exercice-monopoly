package exercice.monopoly.behaviour;

import exercice.monopoly.model.Board;
import exercice.monopoly.model.Player;
import exercice.monopoly.model.Property;
import exercice.monopoly.util.Dice;
import exercice.monopoly.util.NameList;

import java.util.ArrayList;
import java.util.List;

/**
 * GameManager class responsible for managing the game
 */
public class GameManager {

    //// Private Members ////

    private final List<Player> players;
    private final Board board;
    private int currentPlayerIndex;

    //// Constructors ////

    /**
     * @param nbPlayers             the number of players for the game
     * @param startingPlayerBalance the amount of money each player starts with
     * @param nbProperties          the number of properties on the board
     * @param propertyCost          the price of each property
     * @param propertyRent          the price of the rent for each property
     */
    public GameManager(int nbPlayers, int startingPlayerBalance, int nbProperties, int propertyCost, int propertyRent) {
        board = new Board(nbProperties, propertyCost, propertyRent);
        players = new ArrayList<>();

        for (int i = 0; i < nbPlayers; i++) {
            getPlayers().add(new Player(NameList.getNameAt(i), startingPlayerBalance));
        }
        currentPlayerIndex = 0;
    }

    //// Public Functions ////

    /**
     * Run a turn for the game and pass to the next player
     *
     * @return the important values of the turn
     */
    public TurnResult RunTurn() {
        // Get the current player
        Player currentPlayer = getPlayers().get(currentPlayerIndex);

        // Roll the dice
        int diceRoll = Dice.roll();

        boolean isPlayerGameOver = false;

        // Save the currentPlayerIndex value for use in the TurnResult
        int savedCurrentPlayer = currentPlayerIndex;

        int rentPayed = 0;
        String rentPayedTo = "";
        int propertyBougth = 0;

        // Get the correct landed on property by loop through the list of properties on the board
        int propertyLandedOnIndex = (currentPlayer.getCurrentPropertyIndex() + diceRoll) % getBoard().getProperties().size();

        // Set the property the player is standing on
        currentPlayer.setCurrentPropertyIndex(propertyLandedOnIndex);
        Property propertyLandedOn = getBoard().getPropertyAt(propertyLandedOnIndex);

        if (propertyLandedOn.getOwner() == null) {
            // If the property is not owned by anyone
            if (currentPlayer.getBalance() - propertyLandedOn.getPrice() >= 150) {
                // If the player can afford to buy it
                // Buy the property
                currentPlayer.modifyBalance(-propertyLandedOn.getPrice());
                propertyLandedOn.setOwner(currentPlayer);
                propertyBougth = propertyLandedOn.getPrice();
            }
        } else if (propertyLandedOn.getOwner() != currentPlayer) {
            // If the property is already owned by another player
            if (currentPlayer.getBalance() < propertyLandedOn.getRent()) {
                // If the player can't afford to pay the rent
                // The game is over for this player
                isPlayerGameOver = true;
                // Notify that the game is over for this player
                currentPlayer.gameOver();
                // Free the properties owned by this player in order to make them available for purchase
                getBoard().getProperties().stream().filter(property -> property.getOwner() == currentPlayer).forEach(property -> property.setOwner(null));
                // Remove the player
                getPlayers().remove(currentPlayerIndex);
                currentPlayerIndex--;
            } else {
                // If the player can afford to pay the rent
                // Pay the rent to the owner of the property
                propertyLandedOn.getOwner().modifyBalance(propertyLandedOn.getRent());
                currentPlayer.modifyBalance(-propertyLandedOn.getRent());
                rentPayed = propertyLandedOn.getRent();
                rentPayedTo = propertyLandedOn.getOwner().getName();
            }
        }

        // Go to the next player (looping around)
        currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers().size();

        // Return the important values for this turn
        return new TurnResult(savedCurrentPlayer, propertyLandedOn, diceRoll, rentPayed, rentPayedTo, propertyBougth, isPlayerGameOver);
    }

    /**
     * @return the index of the current player
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @return the game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * TurnResult class representing the most important values of the turn
     */
    public class TurnResult {

        /**
         * The index of the player that played this turn
         */
        public final int playerIndex;

        /**
         * The property the player as landed on
         */
        public final Property propertyLandedOn;

        /**
         * The score rolled by the player
         */
        public final int diceRoll;

        /**
         * The amount of money spent on rent
         */
        public final int rentPayed;

        /**
         * The player the rent has been payed to (if applicable)
         */
        public final String rentPayedTo;

        /**
         * The amount of money spent buying a property
         */
        public final int propertyBought;

        /**
         * The state of the player gameOver (true is the game is over for the player as a result of this turn)
         */
        public final boolean isPlayerGameOver;

        /**
         * @param playerIndex      the index of the player that played this turn
         * @param propertyLandedOn the property the player as landed on
         * @param diceRoll         the score rolled by the player
         * @param rentPayed        the amount of money spent on rent
         * @param rentPayedTo      the player the rent has been payed to (if applicable)
         * @param propertyBought   the amount of money spent buying a property
         * @param isPlayerGameOver the state of the player gameOver (true is the game is over for the player as a result of this turn)
         */
        private TurnResult(int playerIndex, Property propertyLandedOn, int diceRoll, int rentPayed, String rentPayedTo, int propertyBought, boolean isPlayerGameOver) {
            this.playerIndex = playerIndex;
            this.propertyLandedOn = propertyLandedOn;
            this.diceRoll = diceRoll;
            this.rentPayed = rentPayed;
            this.rentPayedTo = rentPayedTo;
            this.propertyBought = propertyBought;
            this.isPlayerGameOver = isPlayerGameOver;
        }
    }
}
