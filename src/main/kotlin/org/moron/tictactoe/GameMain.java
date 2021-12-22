package org.moron.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GameMain extends Application implements Runnable {
    private static String[] ARGS;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameMain.class.getResource("game-view.fxml"));
        VBox rootVBox = fxmlLoader.load();

        Scene scene = new Scene(rootVBox, rootVBox.getPrefHeight(), rootVBox.getPrefWidth());

        ((Controller) fxmlLoader.getController()).createEmptyBoard();

        scene.getStylesheets().add(Objects.requireNonNull(GameMain.class.getResource("FX-CSS.css")).toExternalForm());

        stage.setMinHeight(scene.getHeight());
        stage.setMinWidth(scene.getWidth());
        stage.setResizable(false);

        stage.setTitle("TicTacToe");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        ARGS = args;
        new Thread(new GameMain(), "Game Thread").start();
    }

    @Override
    public void run() {
        System.out.println("Starting " + Thread.currentThread().getName());
        launch(ARGS);
    }
}
