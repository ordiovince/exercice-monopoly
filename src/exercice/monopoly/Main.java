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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger logger = Logger.getLogger("exercice.monopoly.Main");

    private Scene scene;

    private Group root;

    private GameManager gm;

    private List<PropertyView> propertyViews;

    private Timeline fiveSecondsWonder = null;

    private static int nbPlayers;

    public static void main(String[] args) throws Exception {

        if (args.length > 0) {
            nbPlayers = Integer.parseInt(args[0]);
        } else {
            nbPlayers = 4;
        }

        Handler fh = new FileHandler("events.log", false);
        fh.setFormatter(new VerySimpleFormatter());
        logger.addHandler(fh);

        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) {

        logger.log(Level.INFO, "====== NEW GAME =====");

        gm = new GameManager(nbPlayers, 1500, 20, 100, 50);

        gm.getPlayers().forEach(player -> player.gameOverOccurred.add(p -> logger.log(Level.INFO, "Player " + p.getName() + " - Game Over")));
        gm.getPlayers().forEach(player -> player.balanceChanged.add(p -> logger.log(Level.INFO, "Player " + p.getName() + " - Balance changed to " + p.getBalance())));
        gm.getPlayers().forEach(player -> player.currentPropertyIndexChanged.add(p -> logger.log(Level.INFO, "Player " + p.getName() + " - Payer moved to property n° " + p.getCurrentPropertyIndex())));
        gm.getBoard().getProperties().forEach(property -> property.ownerChanged.add(p -> logger.log(Level.INFO, "Property n° " + p.getIndex() + " - Owner changed to " + (p.getOwner() == null ? "null" : p.getOwner().getName()))));

        primaryStage.setTitle("Exercice Monopoly");
        primaryStage.setResizable(false);
        root = new Group();
        scene = new Scene(root, 800, 600, Color.LIGHTGREY);

        int nbRows = (gm.getBoard().getProperties().size() + 4) / 4;
        int remainder = (gm.getBoard().getProperties().size() + 4) % 4;
        int nbColumns = nbRows + (remainder / 2);

        double propertyWidth = scene.getWidth() / nbColumns;
        double propertyHeight = scene.getHeight() / nbRows;

        TextFlow recap = new TextFlow();
        recap.setLayoutX(propertyWidth + 20);
        recap.setLayoutY(propertyHeight + 100);

        for (int playerIndex = 0; playerIndex < gm.getPlayers().size(); playerIndex++) {
            Player player = gm.getPlayers().get(playerIndex);
            StringBuilder recapString = new StringBuilder();
            recapString.append(player.getName()).append(" - Position : ").append(player.getCurrentPropertyIndex()).append(" - Solde : ").append(player.getBalance()).append("$\n");
            Text playerRecapText = new Text(recapString.toString());
            playerRecapText.setFill(ColorList.getColorAt(playerIndex));

            recap.getChildren().add(playerRecapText);
        }

        root.getChildren().add(recap);

        Button btn = new Button();
        btn.setPrefWidth(200);
        btn.setPrefHeight(40);
        btn.setLayoutX(scene.getWidth() / 2 - btn.getPrefWidth() / 2 - 40);
        btn.setLayoutY(propertyHeight + 20);
        btn.setText("Tour Suivant : " + gm.getPlayers().get(gm.getCurrentPlayerIndex()).getName());
        btn.setOnAction(event -> {
            String lastPlayerName = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getName();
            int lastPlayerIndex = gm.getCurrentPlayerIndex();

            propertyViews.forEach(propertyView -> propertyView.hidePlayerIndicator(lastPlayerIndex));

            logger.log(Level.INFO, "== NEW TURN ==");
            GameManager.TurnResult turnResult = gm.RunTurn();
            btn.setText("Tour Suivant : " + gm.getPlayers().get(gm.getCurrentPlayerIndex()).getName());

            propertyViews.get(turnResult.propertyLandedOn.getIndex()).showPlayerIndicator(lastPlayerIndex);

            recap.getChildren().clear();
            for (int playerIndex = 0; playerIndex < gm.getPlayers().size(); playerIndex++) {
                Player player = gm.getPlayers().get(playerIndex);
                StringBuilder recapString = new StringBuilder();
                recapString.append(player.getName()).append(" - Position : ").append(player.getCurrentPropertyIndex()).append(" - Solde : ").append(player.getBalance()).append("$\n");
                Text playerRecapText = new Text(recapString.toString());
                playerRecapText.setFill(ColorList.getColorAt(playerIndex));

                recap.getChildren().add(playerRecapText);
            }

            StringBuilder recapString = new StringBuilder();
            recapString.append("\n======\n\n");
            recapString.append(lastPlayerName).append(" a tiré un ").append(turnResult.diceRoll);
            recapString.append(" et est tombé sur la case n°").append(turnResult.propertyLandedOn.getIndex());

            if (turnResult.isPlayerGameOver) {
                recapString.append("\n\n======\n\n");
                recapString.append("Game Over pour ").append(lastPlayerName);
                propertyViews.get(turnResult.propertyLandedOn.getIndex()).hidePlayerIndicator(turnResult.playerIndex);
                propertyViews.forEach(pv -> pv.removePlayer(turnResult.playerIndex));
                ColorList.removeColorAt(turnResult.playerIndex);
            }

            Text lastRecapText = new Text(recapString.toString());

            recap.getChildren().add(lastRecapText);

            if (gm.getPlayers().size() <= 1) {
                gameOver();
            }
        });
        root.getChildren().add(btn);


        CheckBox cb = new CheckBox("Jouer automatiquement");
        cb.setSelected(false);
        cb.setPrefHeight(40);
        cb.setLayoutX(scene.getWidth() / 2 + btn.getPrefWidth() / 2 - 10);
        cb.setLayoutY(propertyHeight + 20);
        cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (fiveSecondsWonder == null) {
                    fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(0.01), event -> btn.fire()));
                    fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
                }
                fiveSecondsWonder.play();
            } else {
                if (fiveSecondsWonder != null) {
                    fiveSecondsWonder.stop();
                }
            }
        });
        root.getChildren().add(cb);

        propertyViews = new ArrayList<>();

        int propertyIndex = 0;
        Point2D direction = new Point2D(1, 0);
        double previousX = -propertyWidth;
        double previousY = 0;
        for (int i = 0; i < nbColumns; i++) {
            for (int j = 0; j < nbRows; j++) {
                if (i == 0 || i == nbColumns - 1 || j == 0 || j == nbRows - 1) {

                    if (direction.getX() == 1 && previousX >= scene.getWidth() - propertyWidth) {
                        direction = new Point2D(0, 1);
                    } else if (direction.getX() == -1 && previousX <= 0) {
                        direction = new Point2D(0, -1);
                    } else if (direction.getY() == 1 && previousY >= scene.getHeight() - propertyHeight) {
                        direction = new Point2D(-1, 0);
                    } else if (direction.getY() == -1 && previousY <= 0) {
                        direction = new Point2D(1, 0);
                    }
                    double x = previousX + propertyWidth * direction.getX();
                    double y = previousY + propertyHeight * direction.getY();

                    PropertyView propertyView = new PropertyView(propertyWidth, propertyHeight, x, y, gm.getBoard().getPropertyAt(propertyIndex), gm.getPlayers().size(), propertyIndex == 0);
                    propertyViews.add(propertyView);
                    root.getChildren().add(propertyView.getGroup());

                    previousX = x;
                    previousY = y;
                    propertyIndex++;
                }
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void gameOver() {
        logger.log(Level.INFO, "====== GAME OVER =====");
        logger.log(Level.INFO, "The winner is " + gm.getPlayers().get(0).getName());

        fiveSecondsWonder.stop();

        root.getChildren().clear();

        StringBuilder gameOverString = new StringBuilder();
        gameOverString.append("======= GAME OVER =======\n\n");
        gameOverString.append("Le vainqueur est : ").append(gm.getPlayers().get(0).getName());

        Label gameOverLabel = new Label(gameOverString.toString());
        gameOverLabel.setPrefWidth(scene.getWidth());
        gameOverLabel.setPrefHeight(scene.getHeight());
        gameOverLabel.setTextAlignment(TextAlignment.CENTER);
        gameOverLabel.setAlignment(Pos.CENTER);

        root.getChildren().add(gameOverLabel);
    }
}
