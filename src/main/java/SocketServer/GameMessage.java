package SocketServer;

import lombok.Data;

@Data
public class GameMessage {
    private String type;
    private String from;
    private String content;

    public GameMessage() {}
    public GameMessage(String type, String from, String content) {
        this.type = type;
        this.from = from;
        this.content = content;
    }
}
