package exercice.monopoly.view;

import exercice.monopoly.model.Property;
import exercice.monopoly.util.ColorList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * PropertyView class the view representing a property
 */
public class PropertyView {

    //// Private Members ////

    private final Group group;
    private final Label ownerLabel;
    private final List<Circle> playersIndicators;

    //// Constructors ////

    /**
     * @param width                      the width of the rectangle to display
     * @param height                     the height of the rectangle to display
     * @param x                          the x position
     * @param y                          the y position
     * @param property                   the Property that is being represented
     * @param nbPlayers                  the number of players playing the game
     * @param arePlayerIndicatorsVisible the flag dictating the visibility of the players indicators
     */
    public PropertyView(double width, double height, double x, double y, Property property, int nbPlayers, boolean arePlayerIndicatorsVisible) {
        group = new Group();

        // Generate the rectangle representing the property
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.DARKGREY);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);
        group.getChildren().add(rectangle);

        // Generate the label representing the index of the property
        Label indexLabel = new Label("" + property.getIndex());
        indexLabel.setTextAlignment(TextAlignment.CENTER);
        indexLabel.setAlignment(Pos.CENTER);
        indexLabel.setPrefWidth(width);
        group.getChildren().add(indexLabel);

        // Generate the label showing the owner of the property (default "Disponible")
        ownerLabel = new Label("Disponible");
        ownerLabel.setTextAlignment(TextAlignment.CENTER);
        ownerLabel.setAlignment(Pos.CENTER);
        ownerLabel.setPrefWidth(width);
        ownerLabel.setLayoutY(height - 40);
        // Subscribe to the ownerChanged event to show the correct owner name as it changes
        property.ownerChanged.add(p -> ownerLabel.setText(p.getOwner() == null ? "Disponible" : "Propriétaire :\n" + p.getOwner().getName()));
        group.getChildren().add(ownerLabel);

        // Generate the indicators for the players standing on this property

        FlowPane playersIndicatorsPane = new FlowPane(Orientation.HORIZONTAL);
        playersIndicatorsPane.setVgap(2);
        playersIndicatorsPane.setHgap(2);
        playersIndicatorsPane.setLayoutY(20);
        playersIndicatorsPane.setPrefWidth(width);
        playersIndicatorsPane.setAlignment(Pos.CENTER);

        playersIndicators = new ArrayList<>();
        for (int i = 0; i < nbPlayers; i++) {
            // Get a different color for each player
            Circle playerIndicator = new Circle(8, ColorList.getColorAt(i));
            playerIndicator.setVisible(arePlayerIndicatorsVisible);
            playersIndicatorsPane.getChildren().add(playerIndicator);
            playersIndicators.add(playerIndicator);
        }

        group.getChildren().add(playersIndicatorsPane);

        group.setLayoutX(x);
        group.setLayoutY(y);
    }

    //// Public Functions ////

    /**
     * @return the group storing the visual representation of the property
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Shows the indicator of the player represented by the index
     *
     * @param playerIndex the index of the player
     */
    public void showPlayerIndicator(int playerIndex) {
        playersIndicators.get(playerIndex).setVisible(true);
    }

    /**
     * Hides the indicator of the player represented by the index
     *
     * @param playerIndex the index of the player
     */
    public void hidePlayerIndicator(int playerIndex) {
        playersIndicators.get(playerIndex).setVisible(false);
    }

    /**
     * Removes the indicator of the player represented by the index
     *
     * @param playerIndex the index of the player
     */
    public void removePlayer(int playerIndex) {
        playersIndicators.remove(playerIndex);
    }
}
