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
    private Parent chess;

    @Override
    public void start(Stage stage) throws IOException {
        // Loading starting stage
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        HelloController controller = fxmlLoader.getController();

        FXMLLoader chessLoader = new FXMLLoader(HelloApplication.class.getResource("chess.fxml"));
        this.chess = chessLoader.load();
        ChessUIController chessUIController = chessLoader.getController();

        controller.setController(chessUIController, this);

        // Starting the starting stage
        stage.setTitle("Connection...");
        stage.setScene(new Scene(root));
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

    public void startChess() {
        currStage.setTitle("Chess");
        currStage.setScene(new Scene(this.chess));
        currStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}