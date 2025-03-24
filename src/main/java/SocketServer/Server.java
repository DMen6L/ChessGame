package SocketServer;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Gson gson = new Gson();

    private static final int PORT = 5000;
    private static final int MAX_PLAYERS = 2;

    private static final List<Socket> players = new ArrayList<Socket>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(MAX_PLAYERS);

    public static void main(String[] args) throws IOException {
        // Creating server on PORT
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        // Adding players until max number is reached
        while (players.size() < MAX_PLAYERS) {
            Socket socket = serverSocket.accept();
            players.add(socket);

            System.out.println("New connection from " + socket.getInetAddress().getHostAddress());
        }

        // Notifying players to start game
        for (Socket socket : players) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            GameMessage gameMessage = new GameMessage("START_GAME", "Server", "");

            out.println(gson.toJson(gameMessage));
        }

        executor.execute(new ClientHandler(players.get(0), players.get(1)));
        executor.execute(new ClientHandler(players.get(1), players.get(0)));
        executor.shutdown();
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private Socket opponentSocket;

        public ClientHandler(Socket socket, Socket opponentSocket) {
            this.socket = socket;
            this.opponentSocket = opponentSocket;
        }
        public void run() {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter oppOut = new PrintWriter(this.opponentSocket.getOutputStream(), true)) {

                String message;
                while((message = in.readLine()) != null) {
                    GameMessage received = gson.fromJson(message, GameMessage.class);
                    System.out.println(received.getType() + " from " + received.getFrom() + ": " + received.getContent());

                    oppOut.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
