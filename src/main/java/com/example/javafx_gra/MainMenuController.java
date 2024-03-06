package com.example.javafx_gra;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.Objects;


public class MainMenuController  {
    @FXML
    private Button StartGameButton;
    @FXML
    private Button CloseGameButton;
    @FXML
    private Button HighScoresButton;
    @FXML
    private void OnCloseButtonClick() {
        Stage stage = (Stage) CloseGameButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void OnStartGameButtonClick(){
        try {
            // Załaduj widok gry z pliku FXML
            Parent gameView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("GameScene.fxml")));
            Scene gameScene = new Scene(gameView);

            // Pobierz Stage, na którym aktualnie znajduje się menu
            Stage window = (Stage) StartGameButton.getScene().getWindow();

            // Ustaw scenę gry na aktualnym Stage
            window.setScene(gameScene);
            window.show();
            window.setResizable(false);
        } catch (Exception ignored) {
        }
    }
    @FXML
    private void onHighScoresButtonCLick(){
        try {
            // Załaduj widok gry z pliku FXML
            Parent gameView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("HighScores.fxml")));
            Scene gameScene = new Scene(gameView);

            // Pobierz Stage, na którym aktualnie znajduje się menu
            Stage window = (Stage) HighScoresButton.getScene().getWindow();

            // Ustaw scenę gry na aktualnym Stage
            window.setScene(gameScene);
            window.show();
            window.setResizable(false);
        } catch (Exception ignored) {
        }
    }
}


