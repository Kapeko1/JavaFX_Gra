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

                /* Wywyłanie metody initialize, uruchamianej wraz ze startem sceny */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Dodanie słuchaczy śledzących zmiany w wysokości i szerokości okna */
        GamePane.widthProperty().addListener((obs, oldVal, newVal) -> adjustGameComponents());
        GamePane.heightProperty().addListener((obs, oldVal, newVal) -> adjustGameComponents());
        Platform.runLater(this::adjustGameComponents); // Dopasowanie elementów layoutu do nowych rozmiarów okna
    }

    /* Uniwersalna metoda do dopasowywania elementów layoutu.
   ANTYKOMBINOWANIE Za każdym razem tworzony jest nowy gracz bo zmienia się prędkość głowy  */
    private void adjustGameComponents() {
        createPlayer();
        scoreLabel.setTextFill(Color.BLACK);
        int score = 0;
        scoreLabel.setText("Wynik = " + score);
        GamePane.requestFocus();
        setControl();
        gameLoop.start();
    }

    /* Metoda służąca do utworzenia nowego gracza w centrym GamePane */
    private void createPlayer() {
        /* Trzeba usunąc starego Playera bo przy zmianie rozmiaru wywoływałem metode createPlayer i pojawiało się wielu graczy w nowym centrum okna */
        if (Player != null) {
            GamePane.getChildren().remove(Player);
        }

        /* Dostosowanie rozmiarów gracza do GamePane (responsywność) 2% i 5% */
        double playerWidth = GamePane.getWidth() * 0.02;
        double playerHeight = GamePane.getHeight() * 0.05;
        /* Ustawienie gracza na środek */
        double startX = GamePane.getWidth() / 2 - playerWidth / 2;
        double startY = GamePane.getHeight() / 2 - playerHeight / 2;
        Player = new Rectangle(startX, startY, playerWidth, playerHeight);
        Player.setFill(Color.BLACK);
        GamePane.getChildren().add(Player);
    }
        /* Uruchomienie czytania klawiatury */
    public void setControl() {
        Platform.runLater(() -> GamePane.getScene().setOnKeyPressed(this::handleKeyPress));
    }
        /* Ustawianie aktualnego kierunku na podstawie kliknietej strzalki */
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
                // Ignorowanie innych przycisków
                break;
        }
    }
        /* Uruchomienie animacji ruchu głowy */
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
                    // Oczekiwanie na ruch
                    break;
            }
        }
    };
    /* Aktualizacja pozycji Playera na podstawie animacji */
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
        /* Metoda wywołująca powrót do sceny Main Menu */
    public void showMainMenu() {
        try {
            Parent mainMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainMenu.fxml")));
            Scene scene = new Scene(mainMenu);
            Stage window = (Stage) GameSceneBase.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException ignored) {
        }
    }
        /* Metoda wywołująca pane oznaczające koniec gry, wraz z napisem i przyciskiem */
    public void ShowGameOverPane() {
        if (GameOverPane == null) {
            GameOverPane = new Pane();
            GameOverButton = new Button("Główne menu");
            GameOverLabel = new Label("Ale lipa");

            GameOverButton.setOnAction(event -> showMainMenu());

            GameOverLabel.setFont(new Font("System", 48));
            GameOverLabel.setTextFill(Color.WHITE);

            GameOverPane.setStyle("-fx-background-color: black;");
            //Ustawienie hierarchii
            GameOverPane.getChildren().addAll(GameOverButton, GameOverLabel);
            GameSceneBase.getChildren().add(GameOverPane);
        }

        GameOverPane.setVisible(true);
        Platform.runLater(this::updateGameOverPaneLayout);// Musiałem to dodać bo pierwszy label nie był wyśrodkowany i aktualizaowało się dopiero po zmianie rozmiarów okna

        // Dodajemy słuchacze do GameSceneBase, aby reagować na zmiany rozmiaru
        GameSceneBase.widthProperty().addListener((obs, oldVal, newVal) -> updateGameOverPaneLayout());
        GameSceneBase.heightProperty().addListener((obs, oldVal, newVal) -> updateGameOverPaneLayout());
    }
    /* Responsywna aktualizacja elementów GameOverPane */
    private void updateGameOverPaneLayout() {
        GameOverPane.setPrefSize(GameSceneBase.getWidth(), GameSceneBase.getHeight());
        /* Centrowanie elementów GameOverPane */
        GameOverButton.setLayoutX(GameSceneBase.getWidth() / 2 - GameOverButton.getWidth() / 2);
        GameOverButton.setLayoutY(GameSceneBase.getHeight() / 2 - GameOverButton.getHeight() / 2 + 50);
        GameOverLabel.setLayoutX(GameSceneBase.getWidth() / 2 - GameOverLabel.getWidth() / 2);
        GameOverLabel.setLayoutY(GameSceneBase.getHeight() / 2 - GameOverLabel.getHeight() / 2 - 50);
    }
}
