package exercice.monopoly.behaviour;

import exercice.monopoly.model.Board;
import exercice.monopoly.model.Player;
import exercice.monopoly.model.Property;
import exercice.monopoly.util.Dice;
import exercice.monopoly.util.NameList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GameManager {

    //// Private Members ////

    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;

    //// Constructors ////

    /**
     * @param nbPlayers
     * @param startingPlayerBalance
     * @param nbProperties
     * @param propertyCost
     * @param propertyRent
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
     * @return
     */
    public TurnResult RunTurn() {
        // Get the current player
        Player currentPlayer = getPlayers().get(currentPlayerIndex);

        // Roll the dice
        int diceRoll = Dice.roll();

        boolean isPlayerGameOver = false;

        // Save the currentPlayerIndex value for use in the TurnResult
        int savedCurrentPlayer = currentPlayerIndex;

        // Get the correct landed on property by loop through the list of properties on the board
        int propertyLandedOnIndex = (currentPlayer.getCurrentPropertyIndex() + diceRoll) % getBoard().getProperties().size();

        // Set the property the player is standing on
        currentPlayer.setCurrentPropertyIndex(propertyLandedOnIndex);
        Property propertyLandedOn = getBoard().getPropertiyAt(propertyLandedOnIndex);

        if (propertyLandedOn.getOwner() == null) {
            // If the property is not owned by anyone
            if (currentPlayer.getBalance() - propertyLandedOn.getPrice() >= 150) {
                // If the player can afford to buy it
                // Buy the property
                currentPlayer.modifyBalance(-propertyLandedOn.getPrice());
                propertyLandedOn.setOwner(currentPlayer);
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
            }
        }

        // Go to the next player (looping around)
        currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers().size();

        // Return the important values for this turn
        return new TurnResult(savedCurrentPlayer, propertyLandedOn, diceRoll, isPlayerGameOver);
    }

    /**
     * @return
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * @return
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @return
     */
    public Board getBoard() {
        return board;
    }

    /**
     *
     */
    public class TurnResult {
        /**
         *
         */
        public int playerIndex;
        /**
         *
         */
        public Property propertyLandedOn;
        /**
         *
         */
        public int diceRoll;
        /**
         *
         */
        public boolean isPlayerGameOver;

        /**
         * @param playerIndex
         * @param propertyLandedOn
         * @param diceRoll
         * @param isPlayerGameOver
         */
        public TurnResult(int playerIndex, Property propertyLandedOn, int diceRoll, boolean isPlayerGameOver) {
            this.playerIndex = playerIndex;
            this.propertyLandedOn = propertyLandedOn;
            this.diceRoll = diceRoll;
            this.isPlayerGameOver = isPlayerGameOver;
        }
    }
}
