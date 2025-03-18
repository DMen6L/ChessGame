package com.project.chess.backend;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class ChessClock {
    private int seconds;
    private boolean isRunning = false;
    private Label clockLabel;
    private Thread timerThread;

    public ChessClock(int hours, int minutes, int seconds, Label clockLabel) {
        this.seconds = hours * 3600 + minutes * 60 + seconds;
        this.clockLabel = clockLabel;

        this.clockLabel.setText(String.format("%02d:%02d", seconds/60, seconds%60));
    }

    public void start() {
        isRunning = true;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (isRunning && seconds > 0) {
                    try {
                        Thread.sleep(1000);
                        seconds--;
                        Platform.runLater(() -> {
                            clockLabel.setText(String.format("%02d:%02d", seconds/60, seconds%60));
                        });
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                return null;
            }
        };

        timerThread = new Thread(task);
        timerThread.setDaemon(true); // Allows thread to exit when the app closes
        timerThread.start();
    }

    public void stop() {
        isRunning = false;
        if (timerThread != null) {
            timerThread.interrupt(); // Interrupt the thread safely
            try {
                timerThread.join(); // Ensure it stops before starting a new one
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
            }
        }
    }

    public int getTimeLeft() {
        return seconds;
    }
}
