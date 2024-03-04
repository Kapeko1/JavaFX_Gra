package com.example.javafx_gra;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    private Label scoreLabel;
    @FXML
    private Pane GamePane;
    @FXML
    private Pane GameOverPane;

    private Rectangle Player;
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }
    private Direction currentDirection = Direction.NONE;


    @FXML
    /* Wywołanie metody initialize, która uruchamiana jest wraz z uruchomieniem GameController */
    public void initialize(URL location, ResourceBundle resources) {
        createPlayer();
        scoreLabel.setText("0");
        GamePane.requestFocus();
        setControl();
        gameLoop.start();
    }
    /* Tworzenie nowego gracza o określonych wymiarach */
    private void createPlayer() {
        /* Ustawienie rozmiarów gracza:
        v1 = x w GamePane
        v2 = y w GamePane
        v3 = szerokosc gracza
        v4 = wysokosc gracza */
        Player = new Rectangle(220, 135, 10, 10);
        Player.setFill(Color.BLACK); // Ustawienie koloru gracza

        GamePane.getChildren().add(Player); // Dodanie prostokąta do GamePane
    }

    /* Uruchomienie czytania naciscniec klawiszy*/
    public void setControl() {
        Platform.runLater(() -> GamePane.getScene().setOnKeyPressed(this::handleKeyPress));
    }

    /* Zmiana aktualnego kierunku gracza zaleznie od nacisnietego klawisza */
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
            case TAB: //Ustawienie pauzy na przycisk TAB
                currentDirection = Direction.NONE;
            default:
                break; // Ignoruje inne klawisze
        }
    }

    /* Uruchomienie automatycznego ruchu gracza */
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
                break; // Czeka na ruch gracza
        }
    }
};
    /* Aktualizacja polozenia gracza */
private void movePlayer(double dx, double dy) {
    double newX = Player.getX() + dx;
    double newY = Player.getY() + dy;

    //Sprawdzanie kolizji z krawedzia GamePane
    if (newX >= 0 && newX + Player.getWidth() <= GamePane.getWidth() &&
            newY >= 0 && newY + Player.getHeight() <= GamePane.getHeight()) {
        Player.setX(newX);
        Player.setY(newY);
    } else  {
        /* W przypadku kolizji ze sciana GamePane wywoluje ekran przegranej gry oraz zatrzymuje ruch*/
        GameOverPane.setOpacity(1);
        currentDirection = Direction.NONE;
    }
    }
}

