module com.project.chess {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.graphics;
    requires java.desktop;
    requires com.google.gson;
    requires static lombok;

    opens com.project.chess to javafx.fxml;
    opens SocketServer to com.google.gson;
    exports com.project.chess;
    exports com.project.chess.backend;
    opens com.project.chess.backend to javafx.fxml;
    exports SocketServer to com.google.gson;
}