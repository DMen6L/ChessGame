package com.project.chess;

import com.project.chess.backend.ChessBoard;
import com.project.chess.backend.ChessPiece;
import com.project.chess.backend.Color;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChessUIController {
    @FXML
    private GridPane chessBoard;

    private final ChessBoard board = new ChessBoard();
    private Color turn = Color.WHITE;

    public void initialize() {
        boardInit();
    }

    private void boardInit() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(this.board.getPiece(i, j) != null) {
                    Label label = new Label();
                    label.setText(this.board.getPiece(i, j).toString());

                    int finalI = i;
                    int finalJ = j;
                    label.setOnMouseClicked(mouseEvent -> choosePiece(finalI, finalJ));

                    this.chessBoard.add(label, j, i);
                }
            }
        }
    }

    private void choosePiece(int row, int col) {
        if(this.board.getPiece(row, col).getColor() != this.turn) {
            return ;
        }

        // clearing possible moves
        List<Node> labelsToRemove = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Node node = getGridCell(i, j);

                if(node instanceof Label) {
                    if(Objects.equals(((Label) node).getText(), "...")) {
                        labelsToRemove.add(node);
                    }
                }
            }
        }

        this.chessBoard.getChildren().removeAll(labelsToRemove);

        // Getting legal moves from board
        List<int[]> legalMoves = this.board.getLegalMoves(row, col);

        for(int[] move : legalMoves) {
            for(int i : move) {
                System.out.print(i + " ");
            }
            System.out.println();

            Label label = new Label();
            label.setText("...");
            label.setOnMouseClicked(mouseEvent -> makePieceMove(row, col, move[0], move[1]));

            this.chessBoard.add(label, move[1], move[0]);
        }
    }

    private void makePieceMove(int sRow, int sCol, int eRow, int eCol) {
        if(this.board.movePiece(sRow, sCol, eRow, eCol, this.turn)) {
            // Clearing previous board and creating new one
            clearGrid();
            boardInit();

            this.turn = this.turn == Color.WHITE ? Color.BLACK : Color.WHITE;
        }
    }

    private Node getGridCell(int row, int col) {
        for(Node node : this.chessBoard.getChildren()) {
            if(node instanceof Label) {
                Integer nodeRow = GridPane.getRowIndex(node);
                Integer nodeCol = GridPane.getColumnIndex(node);

                nodeRow = (nodeRow == null)? 0 : nodeRow;
                nodeCol = (nodeCol == null)? 0 : nodeCol;

                if(row == nodeRow && col == nodeCol) {
                    return node;
                }
            }
        }

        return null;
    }

    private void clearGrid() {
        this.chessBoard.getChildren().clear();
    }
}
