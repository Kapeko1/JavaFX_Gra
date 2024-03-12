package com.example.javafx_gra;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
import javafx.scene.image.ImageView;
import java.io.*;
import java.net.URL;
import java.util.*;


public class GameController implements Initializable {
    @FXML
    private Label scoreLabel;
    @FXML
    private Pane gamePane;
    @FXML
    private AnchorPane gameSceneBase;
    @FXML
    private Pane borderPane;
    @FXML
    private Label l1;
    @FXML
    private Label l2;
    @FXML
    private Label l3;

    private final Button retryGameButton = new Button();
    private int score = 0;
    private final List<Rectangle> snakeSegments = new ArrayList<>(); // Lista segmentów węża
    private Rectangle head;
    private final Rectangle[] foodsTable = new Rectangle[5];

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE}

    private Direction currentDirection = Direction.NONE;

    @FXML
    /* Wywołanie metody initialize, która uruchamiana jest wraz z uruchomieniem GameController */
    public void initialize(URL location, ResourceBundle resources) {
        // Ustawienie niestandardowych czcionek i ich rozmiaru
        URL fontUrl = getClass().getResource("JUNGLEFE.ttf");
        Font customFont = Font.loadFont(Objects.requireNonNull(fontUrl).toExternalForm(), 19);
        l1.setFont(customFont);
        l2.setFont(customFont);
        l3.setFont(customFont);
        scoreLabel.setFont(customFont);
        scoreLabel.setText("Wynik = " + score);


        createPlayer();
        gamePane.requestFocus();
        setControl();
        gameLoop.start();
    }

    /* Tworzenie nowego gracza o określonych wymiarach */
    private void createPlayer() {
        /* Ustawienie rozmiarów gracza:
        v1 = x w gamePane
        v2 = y w gamePane
        v3 = szerokosc gracza
        v4 = wysokosc gracza */
        head = new Rectangle(200, 130, 10, 10);
        head.setFill(Color.BLACK);
        snakeSegments.add(head);
        gamePane.getChildren().add(head);

        /* Obowiazkowy fragment tworzący swojego rodzaju transparentny ogon za glowa weza
        * Jego rozmiary ustalilem metoda prob i bledow tak zeby gra nie przerywala sie
        * od razu po zjedzeniu Food oraz zeby umozliwic dynamiczne skrecanie */
        for (int i = 0; i < 22; i++) {
            Rectangle transparentSegment = new Rectangle(220 - (i + 1) * 10, 135, 10, 10); // Pozycjonowanie za głową
            transparentSegment.setFill(Color.TRANSPARENT);
            snakeSegments.add(transparentSegment);
            gamePane.getChildren().add(transparentSegment);
        }
    }

    /* Uruchomienie czytania naciscniec klawiszy*/
    public void setControl() {
        Platform.runLater(() -> gamePane.getScene().setOnKeyPressed(this::handleKeyPress));
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

    /* GŁÓWNA PĘTLA GRY! */
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
            checkCollisionWithSelf();
            refreshFoodInTable();
            checkCollisionWithFood();
        }
    };

    /* Aktualizacja polozenia gracza */
    private void movePlayer(double dx, double dy) {
        Rectangle head = snakeSegments.getFirst();
        double newX = head.getX() + dx;
        double newY = head.getY() + dy;

        // Sprawdzanie kolizji z krawędzią gamePane i aktualizacja pozycji głowy
        if (newX >= 0 && newX + head.getWidth() <= gamePane.getWidth() && newY >= 0 && newY + head.getHeight() <= gamePane.getHeight()) {
            head.setX(newX);
            head.setY(newY);
        } else {
            showgameOverPane();
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
    public void showMainMenu() {
        try {
            Parent mainMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainMenu.fxml")));
            Scene scene = new Scene(mainMenu);
            Stage window = (Stage) gameSceneBase.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException ignored) {
        }
    }

    /* Wywołanie ekranu przegranej gry */
    public void showgameOverPane() {
            gameLoop.stop();
            Pane gameOverPane = new Pane();
            gameOverPane.setPrefSize(635, 420);

            // Konfiguracja tła
            ImageView gameOverImage = new ImageView();
            gameOverImage.setFitHeight(420.0);
            gameOverImage.setFitWidth(635.0);
            gameOverImage.setPickOnBounds(true);
            gameOverImage.setPreserveRatio(true);
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("gameOver_snake.png")));
            gameOverImage.setImage(image);

            //Konfiguracja przyciskow retry i mainMenu
            Button retryGameButton = new Button();
            retryGameButton.setLayoutX(229.0);
            retryGameButton.setLayoutY(296.0);
            retryGameButton.setPrefHeight(35.0);
            retryGameButton.setPrefWidth(206.0);
            retryGameButton.setStyle("-fx-background-color: TRANSPARENT;");
            retryGameButton.setOnAction(e -> retryGame());

            Button goBackToMainMenuButton = new Button();
            goBackToMainMenuButton.setLayoutX(229.0);
            goBackToMainMenuButton.setLayoutY(353.0);
            goBackToMainMenuButton.setPrefHeight(35.0);
            goBackToMainMenuButton.setPrefWidth(206.0);
            goBackToMainMenuButton.setStyle("-fx-background-color: TRANSPARENT;");
            goBackToMainMenuButton.setOnAction(e -> showMainMenu());

            // Dodanie elementow do gameOverPane a nastepnie do glownego Pane
            gameOverPane.getChildren().add(gameOverImage);
            gameOverPane.getChildren().add(goBackToMainMenuButton);
            gameOverPane.getChildren().add(retryGameButton);
            gameSceneBase.getChildren().add(gameOverPane);

            saveScore(score);
    }

    /* Odswiezanie tablicy z jedzeniem */
    private void refreshFoodInTable() {
        Random rand = new Random();

        // Ustawienie wymiarów jedzenia
        double foodWidth = 10;
        double foodHeight = 10;

        for (int i = 0; i<foodsTable.length; i++) {
            if (foodsTable[i] == null){

            // Generacja losowych współrzędnych w ramach gamePane
            double x = rand.nextDouble() * (417 - foodWidth);
            double y = rand.nextDouble() * (249 - foodHeight);

            Rectangle food = new Rectangle(x, y, foodWidth, foodHeight);

            int color = rand.nextInt(9) + 1;

            //Tworzenie jedzenia roznej jakosci
            if (color >= 1 && color < 5) {
                food.setFill(Color.ANTIQUEWHITE);
            } else if (color >= 5 && color < 8) {
                food.setFill(Color.GREEN);
            } else if (color >= 8 && color < 10) {
                food.setFill(Color.DARKBLUE);
            }
            foodsTable[i] = food;
            final Rectangle finalFood = food;
            Platform.runLater(() -> gamePane.getChildren().add(finalFood));
            }
        }
    }

    /* Sprawdzanie kolizji glowy węża z granicami jedzenia i zwiekszanie wyniku */
    private void checkCollisionWithFood() {
        for (int i = 0; i < foodsTable.length; i++) {
            if (foodsTable[i] != null && head.getBoundsInParent().intersects(foodsTable[i].getBoundsInParent())) {
                Color foodColor = (Color) foodsTable[i].getFill();
                int pointsToAdd = 0;
                int sizeToAdd = 0;
                if (foodColor.equals(Color.ANTIQUEWHITE)) {
                    pointsToAdd = 1;
                    sizeToAdd = 3;
                } else if (foodColor.equals(Color.GREEN)) {
                    pointsToAdd = 3;
                    sizeToAdd = 9;
                } else if (foodColor.equals(Color.DARKBLUE)) {
                    pointsToAdd = 5;
                    sizeToAdd = 14;
                }
                incScore(pointsToAdd);
                incSnakeSize(sizeToAdd);
                // Usuniecie zjedzonego jedzenie z gamePane oraz tablicy
                gamePane.getChildren().remove(foodsTable[i]);
                foodsTable[i] = null;
            }
        }
    }

    /* Zwiekszanie wyniku */
    private void incScore(int value) {
        score += value;
        scoreLabel.setText("Wynik = " + score);
    }

    /* Zwiekszenie rozmiarów węża na podstawie rodzaju zjedzoneog jedzenia */
    private void incSnakeSize(int size) {
        for (int i = 0; i < size; i++) {
            // Pobierz pozycję ostatniego widocznego segmentu
            Rectangle tail = snakeSegments.getLast();

            // Tworzenie nowego segmentu na koncu
            Rectangle newSegment = new Rectangle(tail.getX(), tail.getY(), tail.getWidth(), tail.getHeight());
            newSegment.setFill(Color.BLACK); // Ustaw kolor nowego segmentu na czarny (widoczny)

            // Dodawanie nowego segmentu do listy i gamePane
            snakeSegments.add(newSegment);
            Platform.runLater(() -> gamePane.getChildren().add(newSegment));
        }
    }

    /* Sprawdzenie kolizji z segmentami */
    private void checkCollisionWithSelf() {
        /* Sprawdzanie kolizji tylko jeśli wąż ma więcej niż 22 segmenty
        *  Musiałem to zastosować z powodu nietykalnych elementów za głowa weza */
        if (snakeSegments.size() > 22) {
            for (int i = 23; i < snakeSegments.size(); i++) {
                Rectangle segment = snakeSegments.get(i);
                if (head.getBoundsInParent().intersects(segment.getBoundsInParent())) {
                    showgameOverPane();
                    break; // Wyjście z pętli
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

    /* Zresetowanie gry do stanu wyjsciowego */
    public void retryGame() {
        //Usuniecie gameOverPane
        gameSceneBase.getChildren().removeIf(node -> node instanceof Pane && node != gamePane && node != borderPane);

        // Czyszczenie istniejących segmentów węża i jedzenia
        gamePane.getChildren().clear();
        snakeSegments.clear();
        Arrays.fill(foodsTable, null);

        // Resetowanie zmiennych gry do stanu początkowego
        score = 0;
        currentDirection = Direction.NONE;

        // Ponowne inicjowanie gracza i elementów gry
        createPlayer();
        scoreLabel.setText("Wynik = " + score);

        // Ponowne uruchomienie pętli gry
        gameLoop.start();
    }

}

