package com.example.javafx_gra;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HighScoresController implements Initializable {
    public Button goBackToMainMenuButton;
    @FXML
    private AnchorPane highScoresPane;
    @FXML
    private Label h1;
    @FXML
    private Label h2;
    @FXML
    private Label h3;
    /* Utworzenie listy integerów z pliku, każdy wers to nowa wartosc do talbicy  */
    private List<Integer> readScores() {
        List<Integer> scores = new ArrayList<>();
        String filename = "HighScores.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Próba parsowania każdej linii bezpośrednio na liczbę całkowitą
                try {
                    int score = Integer.parseInt(line.trim());
                    scores.add(score);
                } catch (NumberFormatException e) {
                    System.err.println("Nieprawidłowy format liczby: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scores.sort(Collections.reverseOrder());
        return scores;
    }

    public void initialize(URL location, ResourceBundle resources) {
        fillHighScores();
    }
    public void goBackToMainMenu(){
    try {
        Parent mainMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainMenu.fxml")));
        Scene scene = new Scene(mainMenu);
        Stage window = (Stage) highScoresPane.getScene().getWindow();
        window.setScene(scene);
        window.show();
    } catch (
    IOException ignored) {}
    }
    /* Wypełnienie sceny wynikami */
    private void fillHighScores(){
        URL fontUrl = getClass().getResource("JUNGLEFE.ttf");
        if (fontUrl != null) {
            Font customFont = Font.loadFont(fontUrl.toExternalForm(), 19);
            if (customFont != null) {
                Label[] labels = new Label[]{h1, h2, h3};
                List<Integer> topScores = readScores().stream().limit(3).toList();
                for (int i = 0; i < labels.length; i++) {
                    if (i < topScores.size()) {
                        labels[i].setAlignment(Pos.CENTER);
                        labels[i].setFont(customFont);
                        labels[i].setText(topScores.get(i).toString());
                    } else {
                        labels[i].setAlignment(Pos.CENTER);
                        labels[i].setFont(customFont);
                        labels[i].setText("puste");
                    }
                }
            } else {
                System.err.println("Nie udało się załadować czcionki.");
            }
        } else {
            System.err.println("Nie znaleziono pliku czcionki.");
        }
    }

}
