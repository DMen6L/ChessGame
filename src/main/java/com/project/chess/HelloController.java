package com.project.chess;

import SocketServer.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class HelloController {
    @FXML
    private TextField userName;
    @FXML
    private ComboBox<String> colorComboBox;
    @FXML
    private ComboBox<String> timeComboBox;

    Client client;
    ChessUIController chessUIController;
    HelloApplication application;

    public void initialize() {
        colorComboBox.getItems().addAll("White", "Black");
        timeComboBox.getItems().addAll("1:00", "3:00", "5:00", "10:00", "15:00");
    }

    @FXML
    public void onConnectClick() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);

        if(Objects.equals(this.userName.getText(), "")) {
            alert.setContentText("Please enter your name");
            alert.showAndWait();
        } else if(Objects.equals(this.colorComboBox.getValue(), "")) {
            alert.setContentText("Please select a color");
            alert.showAndWait();
        } else if(Objects.equals(this.timeComboBox.getValue(), "")) {
            alert.setContentText("Please select a time");
            alert.showAndWait();
        } else {
            System.out.println("Success!");

            startClient();
        }
    }

    public void setController(ChessUIController chessUIController, HelloApplication application) {
        this.chessUIController = chessUIController;
        this.application = application;
    }

    private void startClient() throws IOException {
        this.client = new Client(this.chessUIController, this.application,
                this.userName.getText(), this.colorComboBox.getValue(), this.timeComboBox.getValue());
    }
}