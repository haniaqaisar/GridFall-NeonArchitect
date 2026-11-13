package com.gridfall;

import com.gridfall.controller.GameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        GameController controller = new GameController(root);

        Scene scene = new Scene(root, 1024, 700);
        scene.getStylesheets().add(getClass().getResource("/css/neon-theme.css").toExternalForm());

        primaryStage.setTitle("GRIDFALL: Neon City Architect");
        primaryStage.setScene(scene);
        primaryStage.show();

        controller.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
