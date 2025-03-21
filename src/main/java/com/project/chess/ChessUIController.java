package com.project.chess;

import com.project.chess.backend.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChessUIController {
    @FXML
    private GridPane chessBoard;

    @FXML
    private Label whiteClock;
    @FXML
    private Label blackClock;

    private ChessClock whiteClockController;
    private ChessClock blackClockController;

    private final ChessBoard board = new ChessBoard();
    private Color turn = Color.WHITE;

    HelloApplication application = new HelloApplication();

    public void initialize() {
        Platform.runLater(this::boardInit);

        this.whiteClockController = new ChessClock(0, 5, 0, whiteClock);
        this.blackClockController = new ChessClock(0, 5, 0, blackClock);

        this.whiteClockController.start();
    }

    private void boardInit() {
        double pieceSize = chessBoard.getWidth() / 8;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(this.board.getPiece(i, j) != null) {
                    ChessPiece p = this.board.getPiece(i, j);
                    String pathToPiece = getPieceImage(p.getType(), p.getColor());

                    Image pieceImage = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(pathToPiece)));
                    ImageView piece = new ImageView(pieceImage);
                    piece.setFitHeight(pieceSize);
                    piece.setFitWidth(pieceSize);
                    piece.setPreserveRatio(true);

                    Button pieceButton = new Button();
                    pieceButton.setGraphic(piece);
                    pieceButton.setMinWidth(pieceSize);
                    pieceButton.setMinHeight(pieceSize);
                    pieceButton.setMaxWidth(pieceSize);
                    pieceButton.setMaxHeight(pieceSize);
                    pieceButton.getStyleClass().add("pieceButton");

                    int finalJ = j;
                    int finalI = i;
                    pieceButton.setOnMouseClicked(event -> choosePiece(finalI, finalJ));

                    this.chessBoard.add(pieceButton, j, i);
                }
            }
        }
    }

    private void choosePiece(int row, int col) {
        if(this.board.getPiece(row, col).getColor() != this.turn) {
            return ;
        }

        // clearing possible moves
        List<Node> moveIndicatorsToRemove = new ArrayList<>();
        for(Node node : this.chessBoard.getChildren()) {
            if(node instanceof Button) {
                for(String c : node.getStyleClass()) {
                    if(c.equals("moveButton")) moveIndicatorsToRemove.add(node);
                }
            }
        }
        this.chessBoard.getChildren().removeAll(moveIndicatorsToRemove);

        double pieceSize = chessBoard.getWidth() / 8;

        // Getting legal moves from board
        List<int[]> legalMoves = this.board.getLegalMoves(row, col);

        for(int[] move : legalMoves) {
            Circle moveIndicaton = new Circle(move[0], move[1], pieceSize / 7);
            // Different move indication depending on if a piece can take or cannot
            if(this.board.getPiece(move[0], move[1]) == null) {
                moveIndicaton.setFill(javafx.scene.paint.Color.rgb(49, 51, 53, 0.5));
            } else {
                moveIndicaton.setRadius(pieceSize / 3);
                moveIndicaton.setFill(javafx.scene.paint.Color.TRANSPARENT);
                moveIndicaton.setStroke(javafx.scene.paint.Color.rgb(49, 51, 53, 0.5));
                moveIndicaton.setStrokeWidth(pieceSize / 7);
            }

            Button moveButton = new Button();
            moveButton.setMinWidth(pieceSize);
            moveButton.setMinHeight(pieceSize);
            moveButton.setMaxWidth(pieceSize);
            moveButton.setMaxHeight(pieceSize);
            moveButton.getStyleClass().add("moveButton");

            moveButton.setOnMouseClicked(event -> makePieceMove(row, col, move[0], move[1]));

            moveButton.setGraphic(moveIndicaton);

            this.chessBoard.add(moveButton, move[1], move[0]);
        }
    }

    private void makePieceMove(int sRow, int sCol, int eRow, int eCol) {
        if(this.board.movePiece(sRow, sCol, eRow, eCol, this.turn)) {
            // Clearing previous board and creating new one
            clearGrid();
            boardInit();

            this.turn = this.turn == Color.WHITE ? Color.BLACK : Color.WHITE;

            if(this.turn == Color.BLACK) {
                this.whiteClockController.stop();
                this.blackClockController.start();
            } else {
                this.blackClockController.stop();
                this.whiteClockController.start();
            }

            try {
                if(this.board.isCheckmate(this.turn)) {
                    String winner = this.turn == Color.WHITE ? "Black" : "White";

                    this.application.endingCheckmateThread(winner);
                    this.blackClockController.stop();
                    this.whiteClockController.stop();
                }
                else if(this.board.isStalemate(this.turn)) {
                    this.application.endingStalemateThread();
                    this.blackClockController.stop();
                    this.whiteClockController.stop();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        List<Node> piecesToRemove = new ArrayList<>();
        for(Node node : this.chessBoard.getChildren()) {
            if(node instanceof Button) {
                piecesToRemove.add(node);
            }
        }

        this.chessBoard.getChildren().removeAll(piecesToRemove);
    }

    private String getPieceImage(PieceType pieceType, Color color) {
        if(color == Color.BLACK) {
            switch (pieceType) {
                case PieceType.PAWN -> {
                    return "/com/project/chess/ChessSources/pawnB.png";
                }
                case PieceType.KNIGHT -> {
                    return "/com/project/chess/ChessSources/knightB.png";
                }
                case PieceType.BISHOP -> {
                    return "/com/project/chess/ChessSources/bishopB.png";
                }
                case PieceType.ROOK -> {
                    return "/com/project/chess/ChessSources/rookB.png";
                }
                case PieceType.QUEEN -> {
                    return "/com/project/chess/ChessSources/queenB.png";
                }
                case PieceType.KING -> {
                    return "/com/project/chess/ChessSources/kingB.png";
                }
            }
        } else {
            switch (pieceType) {
                case PieceType.PAWN -> {
                    return "/com/project/chess/ChessSources/pawn.png";
                }
                case PieceType.KNIGHT -> {
                    return "/com/project/chess/ChessSources/knight.png";
                }
                case PieceType.BISHOP -> {
                    return "/com/project/chess/ChessSources/bishop.png";
                }
                case PieceType.ROOK -> {
                    return "/com/project/chess/ChessSources/rook.png";
                }
                case PieceType.QUEEN -> {
                    return "/com/project/chess/ChessSources/queen.png";
                }
                case PieceType.KING -> {
                    return "/com/project/chess/ChessSources/king.png";
                }
            }
        }

        return "";
    }
}