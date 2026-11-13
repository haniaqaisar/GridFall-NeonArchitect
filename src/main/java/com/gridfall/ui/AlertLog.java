package com.gridfall.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AlertLog extends ScrollPane {
    private final VBox container;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public AlertLog() {
        this.container = new VBox(2);
        this.container.setPadding(new Insets(5));
        this.setContent(container);
        this.setFitToWidth(true);
        this.setPrefSize(280, 250);
        this.getStyleClass().add("hud-panel");
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);

        addMessage("NIGHT-CITY GRID OS", "#00CFFF");
        addMessage("SYSTEM: ONLINE", "#00FFAA");
    }

    public void addMessage(String message, String colorHex) {
        String time = LocalTime.now().format(formatter);
        Text text = new Text("[" + time + "] " + message);
        text.setStyle("-fx-fill: " + colorHex + "; -fx-font-family: 'Consolas'; -fx-font-size: 11px;");
        container.getChildren().add(text);
        this.setVvalue(1.0);
    }
}
