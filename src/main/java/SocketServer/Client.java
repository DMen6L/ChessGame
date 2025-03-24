package SocketServer;

import com.google.gson.Gson;
import com.project.chess.ChessUIController;
import com.project.chess.HelloApplication;
import com.project.chess.backend.Color;
import javafx.application.Platform;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class Client {
    private Gson gson = new Gson();

    private Socket socket = new Socket("localhost", 5000);
    private BufferedReader in;
    private PrintWriter out;

    @Getter
    private String userName;
    @Getter
    private Color userColor;
    @Getter
    private String userTime;

    ChessUIController controller;
    HelloApplication application;

    public Client(ChessUIController chessUIController, HelloApplication application,
                  String userName, String userColor, String userTime) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);

        this.application = application;

        this.userName = userName;
        this.userColor = Objects.equals(userColor, "White") ? Color.WHITE : Color.BLACK;
        this.userTime = userTime;

        this.controller = chessUIController;
        this.controller.setClient(this);
        this.controller.updateUserInfo();

        startReceiver();
    }

    public void sendMove(String move) {
        GameMessage send = new GameMessage("MOVE", this.userName, move);

        out.println(gson.toJson(send));
    }

    public void sendStalemateRequest() {
        GameMessage request = new GameMessage("REQUEST", this.userName, "Stalemate");

        out.println(gson.toJson(request));
    }

    private void startReceiver() {
        Thread receiver = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    GameMessage receive = gson.fromJson(message, GameMessage.class);

                    switch (receive.getType()) {
                        case "START_GAME":
                            Platform.runLater(() -> this.application.startChess());
                            break;
                        case "MOVE":
                            Platform.runLater(() -> this.controller.parseMove(receive.getContent()));
                            break;
                        case "REQUEST":
                            switch (receive.getContent()) {
                                case "Stalemate":
                                    Platform.runLater(() -> this.controller.stalemateRequest());
                                    break;
                            }
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiver.setDaemon(true);
        receiver.start();
    }
}
