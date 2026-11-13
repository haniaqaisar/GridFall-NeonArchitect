package com.gridfall.ui;

import com.gridfall.model.network.EventType;
import com.gridfall.util.ColorPalette;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class EventNotification extends VBox {
    private final Label titleLabel;
    private final Label descLabel;
    private final Label adviceLabel;

    public EventNotification() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(30));
        this.setMaxWidth(600);
        this.setMaxHeight(300);
        this.setMouseTransparent(true);
        this.setOpacity(0);

        // Use inline styling for a cyberpunk popup look
        this.setStyle("-fx-background-color: rgba(5, 10, 18, 0.95); " +
                "-fx-border-color: #FF3355; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, #FF3355, 20, 0, 0, 0);");

        titleLabel = new Label();
        titleLabel.setFont(Font.font("Consolas", FontWeight.BLACK, 40));
        titleLabel.setTextFill(Color.web("#FF3355"));

        descLabel = new Label();
        descLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        descLabel.setTextFill(Color.WHITE);
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);

        adviceLabel = new Label();
        adviceLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
        adviceLabel.setTextFill(Color.web("#00FFAA"));
        adviceLabel.setWrapText(true);
        adviceLabel.setAlignment(Pos.CENTER);

        this.getChildren().addAll(titleLabel, descLabel, adviceLabel);
    }

    public void show(EventType event) {
        if (event == EventType.NONE) return;

        titleLabel.setText("!!! " + event.label + " !!!");
        descLabel.setText(event.description);
        adviceLabel.setText("ADVICE: " + event.strategy);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(4));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition seq = new SequentialTransition(fadeIn, pause, fadeOut);
        seq.play();
    }
}
