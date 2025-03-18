package com.project.chess;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private Stage currStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("chess.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Chess game!");
        stage.setScene(scene);

        currStage = stage;

        stage.show();
    }

    public void endingCheckmateThread(String winner) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("checkMateEnding.fxml"));
        Stage endingStage = new Stage();
        Parent root = fxmlLoader.load();

        CheckmateEndingController controller = fxmlLoader.getController();
        controller.setWinner(winner);

        Platform.runLater(() -> {
            endingStage.setScene(new Scene(root));
            endingStage.initOwner(currStage);
            endingStage.showAndWait();
        });
    }

    public void endingStalemateThread() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("staleMateEnding.fxml"));
        Stage endingStage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);

        Platform.runLater(() -> {
            endingStage.setScene(scene);
            endingStage.initOwner(currStage);
            endingStage.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}