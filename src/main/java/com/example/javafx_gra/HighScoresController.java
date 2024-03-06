package com.example.javafx_gra;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HighScoresController implements Initializable {
    public Button goBackToMainMenuButton;
    public VBox scoresVbox;
    @FXML
    private AnchorPane highScoresPane;
    @FXML
    private Label h1;
    @FXML
    private Label h2;
    @FXML
    private Label h3;
    @FXML
    private Label h4;
    @FXML
    private Label h5;
    @FXML
    private Label h6;
    @FXML
    private Label h7;
    @FXML
    private Label h8;
    @FXML
    private Label h9;
    @FXML
    private Label h10;

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
   private void fillHighScores(){
       List<Integer> topScores = readScores().stream().limit(10).toList();
       Label[] labels = new Label[]{h1, h2, h3, h4, h5, h6, h7, h8, h9, h10};
       for (int i = 0; i < labels.length; i++) {
           if (i < topScores.size()) {

               labels[i].setText( (i+1)+".    " + topScores.get(i) + " punkty");
           } else {
               labels[i].setText((i+1)+". Puste");
           }
       }
    }
}
