package com.gridfall.ui;

import com.gridfall.model.network.CityStats;
import com.gridfall.model.network.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HUD extends HBox {
    private Label budgetLabel;
    private Label incomeLabel;
    private Label loadLabel;
    private Label powerLabel;
    private Label stabilityLabel;
    private Label eventLabel;

    public HUD() {
        this.getStyleClass().add("hud-panel");
        this.setSpacing(15);
        this.setPadding(new Insets(12, 20, 10, 20)); // Safer spacing to avoid top-of-screen clipping
        this.setAlignment(Pos.CENTER);
        this.setMaxHeight(65);
        this.setMinWidth(750);
        this.setStyle("-fx-background-radius: 0 0 15 15; -fx-border-radius: 0 0 15 15;");

        budgetLabel = createStatLabel("BUDGET", "0 CR", "#00FFAA");
        incomeLabel = createStatLabel("INCOME", "+0/s", "#00FFAA");
        loadLabel = createStatLabel("GRID LOAD", "0%", "#FFEE00");
        powerLabel = createStatLabel("POWER (O/D)", "0W / 0W", "#FFCC00");
        stabilityLabel = createStatLabel("STABILITY", "100%", "#00FFAA");
        eventLabel = createStatLabel("EVENT", "OPERATIONAL", "#B05CFF");

        this.getChildren().addAll(
                budgetLabel.getParent(),
                incomeLabel.getParent(),
                loadLabel.getParent(),
                powerLabel.getParent(),
                stabilityLabel.getParent(),
                eventLabel.getParent()
        );
    }

    private Label createStatLabel(String title, String initialValue, String colorHex) {
        VBox container = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #607D8B; -fx-font-size: 10px; -fx-font-family: 'Consolas';");

        Label valueLbl = new Label(initialValue);
        valueLbl.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 16px; -fx-font-family: 'Consolas'; -fx-font-weight: bold;");

        container.getChildren().addAll(titleLbl, valueLbl);
        // We return the value label so we can update it later, but the parent is what's added to HBox
        return valueLbl;
    }

    public void update(CityStats stats) {
        budgetLabel.setText(stats.budget + " CR");
        incomeLabel.setText("+" + stats.lastIncome + "/s");

        int loadPercent = (int)(stats.loadRatio * 100);
        loadLabel.setText(loadPercent + "%");
        loadLabel.setStyle(loadLabel.getStyle().split("-fx-text-fill:")[0] + "-fx-text-fill: " + (loadPercent > 90 ? "#FF3355" : "#FFEE00") + ";");

        powerLabel.setText(stats.totalOutput + "W / " + stats.totalDemand + "W");

        int stabPercent = (int)(stats.stability * 100);
        stabilityLabel.setText(stabPercent + "%");
        stabilityLabel.setStyle(stabilityLabel.getStyle().split("-fx-text-fill:")[0] + "-fx-text-fill: " + (stabPercent < 30 ? "#FF3355" : "#00FFAA") + ";");

        eventLabel.setText(stats.currentEventType.label);
        if (stats.currentEventType != EventType.NONE) {
            String timer = " (" + stats.eventTimeRemaining + "s)";
            eventLabel.setText(stats.currentEventType.label + timer);
        }
    }
}
