package com.example.javafx_gra;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.ArrayList;


public class GameController implements Initializable {
    @FXML
    private Label scoreLabel;
    @FXML
    private Pane GamePane;
    @FXML
    private Pane GameOverPane;
    @FXML
    private AnchorPane GameSceneBase;
    private int score = 0;
    private final List<Rectangle> snakeSegments = new ArrayList<>(); // Lista segmentów węża
    private Rectangle head;
    private Rectangle Food;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    private Direction currentDirection = Direction.NONE;

    @FXML
    /* Wywołanie metody initialize, która uruchamiana jest wraz z uruchomieniem GameController */
    public void initialize(URL location, ResourceBundle resources) {
        createPlayer();
        scoreLabel.setText("Wynik = " + score);
        GamePane.requestFocus();
        setControl();
        gameLoop.start();
        createFood();
    }

    /* Tworzenie nowego gracza o określonych wymiarach */
    private void createPlayer() {
        /* Ustawienie rozmiarów gracza:
        v1 = x w GamePane
        v2 = y w GamePane
        v3 = szerokosc gracza
        v4 = wysokosc gracza */
        head = new Rectangle(220, 135, 10, 10);
        head.setFill(Color.BLACK);
        snakeSegments.add(head);
        GamePane.getChildren().add(head);
        for (int i = 0; i < 22; i++) {
            Rectangle transparentSegment = new Rectangle(220 - (i + 1) * 10, 135, 10, 10); // Pozycjonowanie za głową
            transparentSegment.setFill(Color.TRANSPARENT);
            snakeSegments.add(transparentSegment);
            GamePane.getChildren().add(transparentSegment);
        }
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
            checkCollisionWithFood();
            checkCollisionWithSelf();
        }
    };

    /* Aktualizacja polozenia gracza */
    private void movePlayer(double dx, double dy) {
        Rectangle head = snakeSegments.getFirst();
        double newX = head.getX() + dx;
        double newY = head.getY() + dy;
        // Sprawdzanie kolizji z krawędzią GamePane i aktualizacja pozycji głowy
        if (newX >= 0 && newX + head.getWidth() <= GamePane.getWidth() && newY >= 0 && newY + head.getHeight() <= GamePane.getHeight()) {
            head.setX(newX);
            head.setY(newY);
        } else {
            gameLoop.stop();
            ShowGameOverPane();
        }

        for (int i = snakeSegments.size() - 1; i > 0; i--) {
            // Przesunięcie każdego segmentu do pozycji poprzedniego
            Rectangle prevSegment = snakeSegments.get(i - 1);
            Rectangle segment = snakeSegments.get(i);
            segment.setX(prevSegment.getX());
            segment.setY(prevSegment.getY());
        }
    }

    /* Wywołanie głownego menu */
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
            VBox gameOverVBox = new VBox(10);
            gameOverVBox.setAlignment(Pos.CENTER);

            Button gameOverButton = new Button("Główne menu");
            Label gameOverLabel = new Label("Ale lipa");

            // Ustawienie akcji dla GameOverButton
            gameOverButton.setOnAction(event -> ShowMainMenu());

            // Ustawienia dla GameOverLabel
            gameOverLabel.setFont(new Font("System", 48)); // Ustawienie rozmiaru fontu
            gameOverLabel.setTextFill(Color.WHITE); // Ustawienie koloru tekstu

            // Dodanie GameOverLabel i GameOverButton do gameOverVBox
            gameOverVBox.getChildren().addAll(gameOverLabel, gameOverButton);

            GameOverPane = new Pane();
            GameOverPane.getChildren().add(gameOverVBox);
            GameOverPane.setPrefSize(600, 400);

            // Ustawienie gameOverVBox na środku GameOverPane
            gameOverVBox.setLayoutX(230);
            gameOverVBox.setLayoutY(130);

            GameOverPane.setStyle("-fx-background-color: black;");
            GameSceneBase.getChildren().add(GameOverPane);
        }

        GameOverPane.setVisible(true);
        saveScore(score);
    }

    private void createFood() {
        Random rand = new Random();

        // Ustawienie wymiarów jedzenia
        double foodWidth = 10;
        double foodHeight = 10;

        // Generacja losowych współrzędnych w ramach GamePane
        double x = rand.nextDouble() * (440 - foodWidth);
        double y = rand.nextDouble() * (270 - foodHeight);

        int color = rand.nextInt(10)+1;

        //Tworzenie jedzenia roznej jakosci
        if(color >= 1 && color<5) {
            Food = new Rectangle(x, y, foodWidth, foodHeight);
            Food.setFill(Color.GREEN);
        } else if (color>=5 && color<8) {
            Food = new Rectangle(x, y, foodWidth, foodHeight);
            Food.setFill(Color.RED);
        } else if (color>=8 && color<10) {
            Food = new Rectangle(x, y, foodWidth, foodHeight);
            Food.setFill(Color.BLUE);
        }
        // Dodanie jedzenie do GamePane
        GamePane.getChildren().add(Food);
    }

    /* Zwiekszanie wyniku */
    private void incScore(int value) {
        score += value;
        scoreLabel.setText("Wynik = " + score);
    }

    /* Sprawdzanie kolizji glowy węża z granicami jedzenia i zwiekszanie wyniku */
    private void checkCollisionWithFood() {
        if (head.intersects(Food.getBoundsInLocal())) {
            GamePane.getChildren().remove(this.Food);
            if (Food.getFill() == Color.GREEN) {
                incSnakeSize(3);
                incScore(1);
            }else if (Food.getFill() == Color.RED){
                incSnakeSize(9);
                incScore(3);
            } else if (Food.getFill() == Color.BLUE) {
                incSnakeSize(14);
                incScore(5);
            }
            createFood();
        }
    }

    /* Zwiekszenie rozmiarów węża na podstawie rodzaju zjedzoneog jedzenia */
    private void incSnakeSize(int size) {
        for (int i = 0; i < size; i++) {
            // Pobierz pozycję ostatniego widocznego segmentu
            Rectangle tail = snakeSegments.getLast();

            // Tworzenie nowego segmentu na koncu
            Rectangle newSegment = new Rectangle(tail.getX(), tail.getY(), tail.getWidth(), tail.getHeight());
            newSegment.setFill(Color.BLACK); // Ustaw kolor nowego segmentu na czarny (widoczny)

            // Dodawanie nowego segmentu do listy i GamePane
            snakeSegments.add(newSegment);
            Platform.runLater(() -> GamePane.getChildren().add(newSegment));
        }
    }
    private void checkCollisionWithSelf() {
        // Sprawdzanie kolizji tylko jeśli wąż ma więcej niż 22 segmenty
        if (snakeSegments.size() > 22) {
            for (int i = 23; i < snakeSegments.size(); i++) {
                Rectangle segment = snakeSegments.get(i);
                if (head.getBoundsInParent().intersects(segment.getBoundsInParent())) {
                    // Kolizja wykryta, zatrzymaj grę i pokaż ekran końca gry
                    gameLoop.stop();
                    ShowGameOverPane();
                    break; // Wyjdź z pętli po wykryciu pierwszej kolizji
                }
            }
        }
    }

        /* Zapisanie każdego wyniku do pliku */
    private void saveScore(int score) {
        String filename = "HighScores.txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println(score); // Dopisuje score do pliku z nową linią
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

