package exercice.monopoly;

import exercice.monopoly.behaviour.GameManager;
import exercice.monopoly.model.Player;
import exercice.monopoly.util.ColorList;
import exercice.monopoly.util.VerySimpleFormatter;
import exercice.monopoly.view.PropertyView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The application for the Monopoly exercice
 */
public class Main extends Application {

    private static final Logger logger = Logger.getLogger("exercice.monopoly.Main");
    private static final double timeToWaitBetweenTurns = 0.01;
    private static final int defaultNbPlayers = 2;
    private static final int defaultStartingPlayerBalance = 1500;
    private static final int defaultNbProperties = 20;
    private static final int defaultPropertyCost = 100;
    private static final int defaultPropertyRent = 50;
    private static int nbPlayers;

    private Scene scene;
    private Group root;
    private GameManager gm;
    private List<PropertyView> propertyViews;
    private Timeline playTurnAutomatically = null;

    /**
     * @param args the arguments passed to the program
     */
    public static void main(String[] args) {

        // Get the number of player as the first argument of the program
        if (args.length > 0) {
            // If an argument as been passed
            nbPlayers = Integer.parseInt(args[0]);
        } else {
            // Else use the default number of players
            nbPlayers = defaultNbPlayers;
        }

        // Init the logger
        try {
            Handler fh = new FileHandler("events.log", false);
            fh.setFormatter(new VerySimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            // If the file could not be created or accessed just log to the console (i.e. do nothing here)
        }

        // Start the JavaFX application
        Application.launch(Main.class, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {

        logger.log(Level.INFO, "====== NEW GAME =====");

        // Create the Game Manager
        gm = new GameManager(nbPlayers, defaultStartingPlayerBalance, defaultNbProperties, defaultPropertyCost, defaultPropertyRent);

        // Log what is happening on the game by subscribing to the events
        gm.getPlayers().forEach(player -> player.gameOverOccurred.add(p -> logger.log(Level.INFO, "Player " + p.getName() + " - Game Over")));
        gm.getPlayers().forEach(player -> player.balanceChanged.add(p -> logger.log(Level.INFO, "Player " + p.getName() + " - Balance changed to " + p.getBalance())));
        gm.getPlayers().forEach(player -> player.currentPropertyIndexChanged.add(p -> logger.log(Level.INFO, "Player " + p.getName() + " - Payer moved to property n° " + p.getCurrentPropertyIndex())));
        gm.getBoard().getProperties().forEach(property -> property.ownerChanged.add(p -> logger.log(Level.INFO, "Property n° " + p.getIndex() + " - Owner changed to " + (p.getOwner() == null ? "null" : p.getOwner().getName()))));

        // Setup the stage
        primaryStage.setTitle("Exercice Monopoly");
        primaryStage.setResizable(false);

        // Create the root group
        root = new Group();
        // Create the scene
        scene = new Scene(root, 800, 600, Color.LIGHTGREY);

        // Calculate the number of rows and columns depending on the number of properties
        int nbRows = (gm.getBoard().getProperties().size() + 4) / 4;
        int remainder = (gm.getBoard().getProperties().size() + 4) % 4;
        int nbColumns = nbRows + (remainder / 2);

        // Calculate the width and height of each property
        double propertyWidth = scene.getWidth() / nbColumns;
        double propertyHeight = scene.getHeight() / nbRows;

        // Create the TextFlow for the recap of the las played turn
        TextFlow recap = new TextFlow();
        recap.setLayoutX(propertyWidth + 20);
        recap.setLayoutY(propertyHeight + 100);

        // Init the recap with the default values
        for (int playerIndex = 0; playerIndex < gm.getPlayers().size(); playerIndex++) {
            // Get the player
            Player player = gm.getPlayers().get(playerIndex);
            StringBuilder recapString = new StringBuilder();
            // Display his information
            recapString.append(player.getName()).append(" - Position : ").append(player.getCurrentPropertyIndex()).append(" - Solde : ").append(player.getBalance()).append("$\n");
            Text playerRecapText = new Text(recapString.toString());
            // The the color of the text to his color
            playerRecapText.setFill(ColorList.getColorAt(playerIndex));

            recap.getChildren().add(playerRecapText);
        }

        root.getChildren().add(recap);

        // Create the button for the next turn
        Button nextTurnButton = new Button();
        nextTurnButton.setPrefWidth(200);
        nextTurnButton.setPrefHeight(40);
        nextTurnButton.setLayoutX(scene.getWidth() / 2 - nextTurnButton.getPrefWidth() / 2 - 40);
        nextTurnButton.setLayoutY(propertyHeight + 20);
        // Display the name of the next player on the button
        nextTurnButton.setText("Tour Suivant : " + gm.getPlayers().get(gm.getCurrentPlayerIndex()).getName());
        // Run the next turn and display the results when the button is clicked
        nextTurnButton.setOnAction(event -> {

            // Save the name and index of the player that is about to play
            String lastPlayerName = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getName();
            int lastPlayerIndex = gm.getCurrentPlayerIndex();

            // Hide the indicator for this player for the property he is about to leave
            propertyViews.forEach(propertyView -> propertyView.hidePlayerIndicator(lastPlayerIndex));

            logger.log(Level.INFO, "== NEW TURN ==");

            // Run the next turn
            GameManager.TurnResult turnResult = gm.RunTurn();

            // Update the button text to display the next player name
            nextTurnButton.setText("Tour Suivant : " + gm.getPlayers().get(gm.getCurrentPlayerIndex()).getName());

            // Show the indicator for the player on the new property (landed on)
            propertyViews.get(turnResult.propertyLandedOn.getIndex()).showPlayerIndicator(lastPlayerIndex);

            // Clear the recap
            recap.getChildren().clear();
            // Repopulate the recap with the new information
            for (int playerIndex = 0; playerIndex < gm.getPlayers().size(); playerIndex++) {
                // Get the player
                Player player = gm.getPlayers().get(playerIndex);
                StringBuilder recapString = new StringBuilder();
                // Display his information
                recapString.append(player.getName()).append(" - Position : ").append(player.getCurrentPropertyIndex()).append(" - Solde : ").append(player.getBalance()).append("$\n");
                Text playerRecapText = new Text(recapString.toString());
                // The the color of the text to his color
                playerRecapText.setFill(ColorList.getColorAt(playerIndex));

                recap.getChildren().add(playerRecapText);
            }

            // Add what append this turn to the recap
            StringBuilder recapString = new StringBuilder();
            recapString.append("\n======\n\n");
            // What number the player rolled
            recapString.append(lastPlayerName).append(" a tiré un ").append(turnResult.diceRoll);
            // Where did he landed
            recapString.append(" et est tombé sur la case n°").append(turnResult.propertyLandedOn.getIndex());

            if (turnResult.isPlayerGameOver) {
                // If the game is over for this player as a result of this turn
                recapString.append("\n\n======\n\n");
                // Show that it is game over for this player
                recapString.append("Game Over pour ").append(lastPlayerName);
                // Hide and and remove all his indicators and color
                propertyViews.get(turnResult.propertyLandedOn.getIndex()).hidePlayerIndicator(turnResult.playerIndex);
                propertyViews.forEach(pv -> pv.removePlayer(turnResult.playerIndex));
                ColorList.removeColorAt(turnResult.playerIndex);
            }

            Text lastRecapText = new Text(recapString.toString());

            recap.getChildren().add(lastRecapText);

            if (gm.getPlayers().size() <= 1) {
                // If only one player is remaining
                // The game is over
                gameOver();
            }
        });
        root.getChildren().add(nextTurnButton);

        // Create a checkbox to let the game run automatically
        CheckBox cb = new CheckBox("Jouer automatiquement");
        cb.setSelected(false);
        cb.setPrefHeight(40);
        cb.setLayoutX(scene.getWidth() / 2 + nextTurnButton.getPrefWidth() / 2 - 10);
        cb.setLayoutY(propertyHeight + 20);
        cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If the checkbox is checked
                if (playTurnAutomatically == null) {
                    // If the timeline is not yet initialized
                    // Create a timeline that executes the next turn button code
                    playTurnAutomatically = new Timeline(new KeyFrame(Duration.seconds(timeToWaitBetweenTurns), event -> nextTurnButton.fire()));
                    playTurnAutomatically.setCycleCount(Timeline.INDEFINITE);
                }
                // Play the timeline
                playTurnAutomatically.play();
            } else {
                // If the checkbox is not checked
                if (playTurnAutomatically != null) {
                    // If the timeline exists
                    // Stop the timeline
                    playTurnAutomatically.stop();
                }
            }
        });
        root.getChildren().add(cb);

        // Generate the views for the properties
        propertyViews = new ArrayList<>();

        int propertyIndex = 0;
        // The initial direction is to the right (we are going clockwise starting from the top left corner)
        Point2D direction = new Point2D(1, 0);
        // This value is offset to account for the first property
        double previousX = -propertyWidth;
        double previousY = 0;
        for (int i = 0; i < nbColumns; i++) {
            for (int j = 0; j < nbRows; j++) {
                if (i == 0 || i == nbColumns - 1 || j == 0 || j == nbRows - 1) {
                    // If the current tile is on the perimeter

                    // Change the direction if necessary
                    if (direction.getX() == 1 && previousX >= scene.getWidth() - propertyWidth) {
                        // If we are going to the right and we are at the last column
                        // Change direction to down
                        direction = new Point2D(0, 1);
                    } else if (direction.getX() == -1 && previousX <= 0) {
                        // If we are going to the lest and we are at the first column
                        // Change direction to up
                        direction = new Point2D(0, -1);
                    } else if (direction.getY() == 1 && previousY >= scene.getHeight() - propertyHeight) {
                        // If we are going down and we are at the last row
                        // Change direction to left
                        direction = new Point2D(-1, 0);
                    } else if (direction.getY() == -1 && previousY <= 0) {
                        // If we are going up and we are at the first row
                        // Change direction to right
                        direction = new Point2D(1, 0);
                    }

                    // Calculate the position with the last one and the direction
                    double x = previousX + propertyWidth * direction.getX();
                    double y = previousY + propertyHeight * direction.getY();

                    // Create the view for the property
                    PropertyView propertyView = new PropertyView(propertyWidth, propertyHeight, x, y, gm.getBoard().getPropertyAt(propertyIndex), gm.getPlayers().size(), propertyIndex == 0);
                    propertyViews.add(propertyView);
                    root.getChildren().add(propertyView.getGroup());

                    // Save the position
                    previousX = x;
                    previousY = y;

                    // Go to the next property
                    propertyIndex++;
                }
            }
        }

        primaryStage.setScene(scene);

        // Show the window
        primaryStage.show();
    }

    /**
     * Game Over
     */
    private void gameOver() {
        logger.log(Level.INFO, "====== GAME OVER =====");
        logger.log(Level.INFO, "The winner is " + gm.getPlayers().get(0).getName());

        if (playTurnAutomatically != null) {
            // If the timeline is initialized
            // Stop the timeline
            playTurnAutomatically.stop();
        }

        // Empty the window
        root.getChildren().clear();

        // Display the game over
        StringBuilder gameOverString = new StringBuilder();
        gameOverString.append("======= GAME OVER =======\n\n");
        // Display who wined the game
        gameOverString.append("Le vainqueur est : ").append(gm.getPlayers().get(0).getName());

        Label gameOverLabel = new Label(gameOverString.toString());
        gameOverLabel.setPrefWidth(scene.getWidth());
        gameOverLabel.setPrefHeight(scene.getHeight());
        gameOverLabel.setTextAlignment(TextAlignment.CENTER);
        gameOverLabel.setAlignment(Pos.CENTER);

        root.getChildren().add(gameOverLabel);
    }
}
