package com.example.javafx_gra;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    private AnchorPane GameSceneBase;
    @FXML
    private Label scoreLabel;
    @FXML
    private Pane GamePane;
    private Pane GameOverPane;
    private Rectangle Player;
    private Button GameOverButton;
    private Label GameOverLabel;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    private Direction currentDirection = Direction.NONE;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GamePane.widthProperty().addListener((obs, oldVal, newVal) -> adjustGameComponents());
        GamePane.heightProperty().addListener((obs, oldVal, newVal) -> adjustGameComponents());
        Platform.runLater(this::adjustGameComponents); // Initial adjustment
    }

    private void adjustGameComponents() {
        createPlayer();
        scoreLabel.setTextFill(Color.BLACK);
        int score = 0;
        scoreLabel.setText("Wynik = " + score);
        GamePane.requestFocus();
        setControl();
        gameLoop.start();
    }

    private void createPlayer() {
        if (Player != null) {
            GamePane.getChildren().remove(Player); // Remove the old player if it exists
        }
        double playerWidth = GamePane.getWidth() * 0.02; // 2% of GamePane's width
        double playerHeight = GamePane.getHeight() * 0.05; // 5% of GamePane's height
        double startX = GamePane.getWidth() / 2 - playerWidth / 2;
        double startY = GamePane.getHeight() / 2 - playerHeight / 2;

        Player = new Rectangle(startX, startY, playerWidth, playerHeight);
        Player.setFill(Color.BLACK);
        GamePane.getChildren().add(Player);
    }

    public void setControl() {
        Platform.runLater(() -> GamePane.getScene().setOnKeyPressed(this::handleKeyPress));
    }

    private void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case UP:
                currentDirection = Direction.UP;
                break;
            case DOWN:
                currentDirection = Direction.DOWN;
                break;
            case LEFT:
                currentDirection = Direction.LEFT;
                break;
            case RIGHT:
                currentDirection = Direction.RIGHT;
                break;
            case TAB:
                currentDirection = Direction.NONE;
                break;
            default:
                // Ignore other keys
                break;
        }
    }

    private final AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            switch (currentDirection) {
                case UP:
                    movePlayer(0, -1);
                    break;
                case DOWN:
                    movePlayer(0, 1);
                    break;
                case LEFT:
                    movePlayer(-1, 0);
                    break;
                case RIGHT:
                    movePlayer(1, 0);
                    break;
                case NONE:
                    // Wait for player to move
                    break;
            }
        }
    };

    private void movePlayer(double dx, double dy) {
        double newX = Player.getX() + dx;
        double newY = Player.getY() + dy;

        if (newX >= 0 && newX + Player.getWidth() <= GamePane.getWidth() && newY >= 0 && newY + Player.getHeight() <= GamePane.getHeight()) {
            Player.setX(newX);
            Player.setY(newY);
        } else {
            ShowGameOverPane();
            currentDirection = Direction.NONE;
            gameLoop.stop();
        }
    }

    public void ShowMainMenu() {
        try {
            Parent mainMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainMenu.fxml")));
            Scene scene = new Scene(mainMenu);
            Stage window = (Stage) GameSceneBase.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException ignored) {
        }
    }

    public void ShowGameOverPane() {
        if (GameOverPane == null) {
            GameOverPane = new Pane();
            GameOverButton = new Button("Główne menu");
            GameOverLabel = new Label("Ale lipa");

            GameOverButton.setOnAction(event -> ShowMainMenu());

            // Ustawienia dla GameOverLabel
            GameOverLabel.setFont(new Font("System", 48)); // Ustawienie rozmiaru fontu
            GameOverLabel.setTextFill(Color.WHITE); // Ustawienie koloru tekstu

            // Ustawienie tła GameOverPane na czarne
            GameOverPane.setStyle("-fx-background-color: black;");

            GameOverPane.getChildren().addAll(GameOverButton, GameOverLabel);
            GameSceneBase.getChildren().add(GameOverPane);
        }

        GameOverPane.setVisible(true); // Pokaż GameOverPane
        Platform.runLater(this::updateGameOverPaneLayout);

        // Dodajemy słuchacze do GameSceneBase, aby reagować na zmiany rozmiaru
        GameSceneBase.widthProperty().addListener((obs, oldVal, newVal) -> updateGameOverPaneLayout());
        GameSceneBase.heightProperty().addListener((obs, oldVal, newVal) -> updateGameOverPaneLayout());
    }

    private void updateGameOverPaneLayout() {
        GameOverPane.setPrefSize(GameSceneBase.getWidth(), GameSceneBase.getHeight());

        // Centrujemy GameOverButton i GameOverLabel w GameOverPane
        GameOverButton.setLayoutX(GameSceneBase.getWidth() / 2 - GameOverButton.getWidth() / 2);
        GameOverButton.setLayoutY(GameSceneBase.getHeight() / 2 - GameOverButton.getHeight() / 2 + 50);
        GameOverLabel.setLayoutX(GameSceneBase.getWidth() / 2 - GameOverLabel.getLayoutBounds().getWidth() / 2); // Używamy getLayoutBounds dla dokładnego środkowania
        GameOverLabel.setLayoutY(GameSceneBase.getHeight() / 2 - GameOverLabel.getLayoutBounds().getHeight() / 2 - 50);
    }

}
