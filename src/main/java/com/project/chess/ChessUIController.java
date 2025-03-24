package com.project.chess;

import SocketServer.Client;
import com.project.chess.backend.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import lombok.Setter;

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
    @FXML
    private Label playerName;
    @FXML
    private Button resignButton;
    @FXML
    private Button allowButton;
    @FXML
    private Button cancelButton;

    private ChessClock whiteClockController;
    private ChessClock blackClockController;

    private final ChessBoard board = new ChessBoard();
    private Color turn = Color.WHITE;

    HelloApplication application = new HelloApplication();
    @Setter
    Client client;

    public void initialize() {
        Platform.runLater(this::boardInit);
    }

    // On resign buttons
    @FXML
    public void onResignWhite() throws IOException {
        this.application.endingCheckmateThread("Black");
    }
    @FXML
    public void onResignBlack() throws IOException {
        this.application.endingCheckmateThread("White");
    }
    @FXML
    public void onStalemate() {
        this.client.sendStalemateRequest();

        this.allowButton.setDisable(false);
        this.cancelButton.setDisable(false);
    }
    @FXML
    public void onAllow() throws IOException {
        this.application.endingStalemateThread();
        this.allowButton.setDisable(true);
        this.cancelButton.setDisable(true);
    }
    @FXML
    public void onCancel() {
        this.allowButton.setDisable(true);
        this.cancelButton.setDisable(true);
    }

    private void boardInit() {
        double pieceSize = chessBoard.getWidth() / 8;

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board.getPiece(i, j) != null) {
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

    // Needed to choose piece, as a button
    private void choosePiece(int row, int col) {
        if(this.client.getUserColor() != this.turn) return ;

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

    // When moveButton is pressed the piece moves to the indicated square
    private void makePieceMove(int sRow, int sCol, int eRow, int eCol) {
        if(this.board.movePiece(sRow, sCol, eRow, eCol, this.turn)) {
            if(this.turn == this.client.getUserColor()) {
                String move = sRow + "," + sCol + "->" + eRow + "," + eCol;
                this.client.sendMove(move);
            }

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

    // clearing off the grid
    private void clearGrid() {
        List<Node> piecesToRemove = new ArrayList<>();
        for(Node node : this.chessBoard.getChildren()) {
            if(node instanceof Button) {
                piecesToRemove.add(node);
            }
        }

        this.chessBoard.getChildren().removeAll(piecesToRemove);
    }

    // Getting piece images
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

    // Needed to read updates from socket server
    public void parseMove(String move) {
        String[] parts = move.split("->");

        String[] start = parts[0].split(",");
        String[] end = parts[1].split(",");

        int sRow = Integer.parseInt(start[0]);
        int sCol = Integer.parseInt(start[1]);

        int eRow = Integer.parseInt(end[0]);
        int eCol = Integer.parseInt(end[1]);

        makePieceMove(sRow, sCol, eRow, eCol);
    }

    // Getting USERNAME, COLOR AND TIME
    public void updateUserInfo() {
        this.playerName.setText(this.client.getUserName());

        String[] parts = this.client.getUserTime().split(":");
        int time = Integer.parseInt(parts[0]);

        if(this.client.getUserColor() == Color.WHITE) {
            this.whiteClockController = new ChessClock(0, time, 0, whiteClock);
            this.blackClockController = new ChessClock(0, time, 0, blackClock);

            resignButton.setOnAction((ActionEvent e) -> {
                try {
                    onResignWhite();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } else {
            this.whiteClockController = new ChessClock(0, time, 0, blackClock);
            this.blackClockController = new ChessClock(0, time, 0, whiteClock);

            resignButton.setOnAction((ActionEvent e) -> {
                try {
                    onResignBlack();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        this.whiteClockController.start();
    }

    // When stalemate request is received showcase the buttons
    public void stalemateRequest() {
        this.allowButton.setDisable(false);
        this.cancelButton.setDisable(false);
    }
}