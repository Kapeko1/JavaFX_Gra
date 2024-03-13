package com.example.javafx_gra;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
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


    URL fontUrl = getClass().getResource("JUNGLEFE.ttf");
    Font customFont = Font.loadFont(Objects.requireNonNull(fontUrl).toExternalForm(), 19);



    Image snakeBodyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/javafx_gra/snake_body.png")));
    ImagePattern bodyPattern = new ImagePattern(snakeBodyImage);
    Image appleImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("appleFood.png")));
    ImagePattern appleImagePattern = new ImagePattern(appleImage);
    Image cheeseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("chesseFood.png")));
    ImagePattern cheeseImagePattern = new ImagePattern(cheeseImage);
    Image chickenImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("chickenFood.png")));
    ImagePattern chickenImagePattern = new ImagePattern(chickenImage);


    int gameLoopCounter = 0;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE}

    private Direction currentDirection = Direction.NONE;

    @FXML
    /* Wywołanie metody initialize, która uruchamiana jest wraz z uruchomieniem GameController */
    public void initialize(URL location, ResourceBundle resources) {
        // Ustawienie labeli legendy
        l1.setFont(customFont);
        l2.setFont(customFont);
        l3.setFont(customFont);
        scoreLabel.setFont(customFont);
        scoreLabel.setText("Wynik = " + score);
        // Uruchomienie wstepnych ustawien i petli gry
        createPlayer();
        gamePane.requestFocus();
        setControl();
        gameLoop.start();
    }

    /* Tworzenie nowego gracza o określonych wymiarach i załadowanie obrazu głowy */
    private void createPlayer() {
        Image snakeHead = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/javafx_gra/head_snake.png")));
        ImagePattern pattern = new ImagePattern(snakeHead);

        /* Ustawienie rozmiarów gracza:
        v1 = x w gamePane
        v2 = y w gamePane
        v3 = szerokosc gracza
        v4 = wysokosc gracza */
        head = new Rectangle(200, 130, 10, 10);
        head.setFill(pattern);
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
                if (currentDirection != Direction.DOWN) {
                    currentDirection = Direction.UP;
                    head.rotateProperty().setValue(180) ;
                }
                break;
            case DOWN:
                if (currentDirection != Direction.UP) {
                    currentDirection = Direction.DOWN;
                    head.rotateProperty().setValue(0);
                }
                break;
            case LEFT:
                if (currentDirection != Direction.RIGHT) {
                    currentDirection = Direction.LEFT;
                    head.rotateProperty().setValue(90);
                }
                break;
            case RIGHT:
                if (currentDirection !=Direction.LEFT) {
                    currentDirection = Direction.RIGHT;
                    head.rotateProperty().setValue(-90);
                }
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
            gameLoopCounter++;

            // Usuniecie falszywego jedzenia co 20 cykli petli gry
            if(gameLoopCounter == 20){
                checkGamePaneForBadFood();
                gameLoopCounter = 0;
            }
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
    private void showMainMenu() {
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
    private void showgameOverPane() {
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
            retryGameButton.setLayoutX(253.0);
            retryGameButton.setLayoutY(295.0);
            retryGameButton.setPrefHeight(42.0);
            retryGameButton.setPrefWidth(64.0);
            retryGameButton.setStyle("-fx-background-color: TRANSPARENT;");
            retryGameButton.setOnAction(e -> retryGame());

            Button goBackToMainMenuButton = new Button();
            goBackToMainMenuButton.setLayoutX(335.0);
            goBackToMainMenuButton.setLayoutY(295.0);
            goBackToMainMenuButton.setPrefHeight(42.0);
            goBackToMainMenuButton.setPrefWidth(64.0);
            goBackToMainMenuButton.setStyle("-fx-background-color: TRANSPARENT;");
            goBackToMainMenuButton.setOnAction(e -> showMainMenu());

            Label finalScoreLabel = new Label();
            finalScoreLabel.setPrefSize(130.0, 23.0);
            finalScoreLabel.setLayoutX(255);
            finalScoreLabel.setLayoutY(266);
            finalScoreLabel.setAlignment(Pos.CENTER);
            finalScoreLabel.setFont(customFont);
            finalScoreLabel.setText("Wynik = " + score);


            // Dodanie elementow do gameOverPane a nastepnie do glownego Pane
            gameOverPane.getChildren().add(gameOverImage);
            gameOverPane.getChildren().add(goBackToMainMenuButton);
            gameOverPane.getChildren().add(retryGameButton);
            gameOverPane.getChildren().add(finalScoreLabel);
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
                food.setFill(appleImagePattern);
            } else if (color >= 5 && color < 8) {
                food.setFill(cheeseImagePattern);
            } else if (color >= 8 && color < 10) {
                food.setFill(chickenImagePattern);
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
                Paint fill = foodsTable[i].getFill();
                int pointsToAdd = 0;
                int sizeToAdd = 0;

                // Sprawdzenie jaki fill ma konkretne jedzenie
                if (fill instanceof ImagePattern foodPattern) {
                    if (foodPattern.equals(appleImagePattern)) {
                        pointsToAdd = 1;
                        sizeToAdd = 3;
                    } else if (foodPattern.equals(cheeseImagePattern)) {
                        pointsToAdd = 3;
                        sizeToAdd = 9;
                    } else if (foodPattern.equals(chickenImagePattern)) {
                        pointsToAdd = 5;
                        sizeToAdd = 14;
                    }
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

    /* Zwiekszenie rozmiarów węża na podstawie rodzaju zjedzoneog jedzenia oraz
    * zmiana niewidocznego ogona weza */
    private void incSnakeSize(int size) {
        for (int i = 0; i < size; i++) {
            if(snakeSegments.get(3).getFill() == Color.TRANSPARENT){
                for (int z = 10 ; z < 23; z++ ){
                    snakeSegments.get(z).setFill(bodyPattern);
                }
            }
            // Pobierz pozycję ostatniego widocznego segmentu
            Rectangle tail = snakeSegments.getLast();

            // Tworzenie nowego segmentu na koncu
            Rectangle newSegment = new Rectangle(tail.getX(), tail.getY(), tail.getWidth(), tail.getHeight());
            newSegment.setFill(bodyPattern);

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
    private void retryGame() {
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
        gameLoopCounter = 0;

        // Ponowne uruchomienie pętli gry
        gameLoop.start();
    }

    /* Naprawa błedu powodującego, że czasem na gamePane pojawiały się obiekty
    * typu Rectangle, z ktorymi waz nie mogl wchodzic w interakcje.  */
    private void checkGamePaneForBadFood(){
        // Tworzymy nowy set zawierający wszystkie "legalne" Rectangles w gamePane
        Set<Rectangle> validRectangles = new HashSet<>();
        validRectangles.addAll(Arrays.asList(foodsTable));
        validRectangles.addAll(snakeSegments);
        // Usuniecie tych, które nie naleza do validRectangles
        gamePane.getChildren().removeIf(node -> node instanceof Rectangle && !validRectangles.contains(node));
        }

}


