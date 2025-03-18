package com.project.chess;

import com.project.chess.backend.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CheckmateEndingController {
    @FXML
    private Label winnerLabel;

    private Color winner;
    ChessUIController chessUIController = new ChessUIController();

    public void setWinner(String winner) {
        this.winnerLabel.setText(winner + " Has Won!");
    }
}
