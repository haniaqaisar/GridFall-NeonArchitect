package com.gridfall.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class BuildingPalette extends VBox {
    private String selectedType = "REACTOR";
    private Consumer<String> onTypeSelected;
    private Runnable onSaveRequested;
    private Runnable onUndoRequested;
    private Runnable onHelpRequested;
    private final java.util.Map<String, Button> buttons = new java.util.HashMap<>();

    public BuildingPalette(Consumer<String> onTypeSelected, Runnable onSaveRequested, Runnable onUndoRequested, Runnable onHelpRequested) {
        this.onTypeSelected = onTypeSelected;
        this.onSaveRequested = onSaveRequested;
        this.onUndoRequested = onUndoRequested;
        this.onHelpRequested = onHelpRequested;
        this.getStyleClass().add("hud-panel");
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setPrefWidth(260);
        this.setMinWidth(260);
        this.setMaxHeight(700); // Allow much more vertical space for high resolutions, up to 700px
        this.setStyle("-fx-border-radius: 10 0 0 10; -fx-background-radius: 10 0 0 10;");

        Label title = new Label("GRID_ARCHITECT");
        title.getStyleClass().add("neon-text");
        title.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 10 0;"); // Slightly larger and spaced

        this.getChildren().add(title);

        // Scrollable container for building options
        VBox optionsBox = new VBox(8);
        optionsBox.setPadding(new Insets(2, 5, 2, 2));

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(optionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        addOption(optionsBox, "SOLAR", "Solar Farm (150 CR)");
        addOption(optionsBox, "REACTOR", "Nuclear Reactor (500 CR)");
        addOption(optionsBox, "HOUSING", "Residential Block (50 CR)");
        addOption(optionsBox, "FACTORY", "Industrial Factory (500 CR)");
        addOption(optionsBox, "BATTERY", "Battery Bank (250 CR)");
        addOption(optionsBox, "TOWER", "Mega Tower (250 CR)");
        addOption(optionsBox, "SIGN", "Neon Sign (30 CR)");
        addOption(optionsBox, "LINE", "Transmission Line (10 CR)");
        addOption(optionsBox, "REMOVE", "Deconstruct Node");

        this.getChildren().add(scrollPane);

        // Initial selection highlight
        updateSelection("REACTOR");

        // Action controls are stuck to the bottom of the architect panel
        VBox actionsBox = new VBox(8);
        actionsBox.setPadding(new Insets(10, 0, 0, 0));

        Button saveBtn = new Button("SAVE CITY");
        saveBtn.getStyleClass().add("palette-button");
        saveBtn.setStyle("-fx-border-color: #00FFAA; -fx-text-fill: #00FFAA; -fx-font-weight: bold;");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> onSaveRequested.run());
        actionsBox.getChildren().add(saveBtn);

        Button undoBtn = new Button("UNDO ACTION");
        undoBtn.getStyleClass().add("palette-button");
        undoBtn.setStyle("-fx-border-color: #FFCC00; -fx-text-fill: #FFCC00; -fx-font-weight: bold;");
        undoBtn.setMaxWidth(Double.MAX_VALUE);
        undoBtn.setOnAction(e -> onUndoRequested.run());
        actionsBox.getChildren().add(undoBtn);

        Button helpBtn = new Button("SYSTEM MANUAL");
        helpBtn.getStyleClass().add("palette-button");
        helpBtn.setStyle("-fx-border-color: #00CFFF; -fx-text-fill: #00CFFF; -fx-font-weight: bold;");
        helpBtn.setMaxWidth(Double.MAX_VALUE);
        helpBtn.setOnAction(e -> onHelpRequested.run());
        actionsBox.getChildren().add(helpBtn);

        this.getChildren().add(actionsBox);
    }

    private void addOption(VBox optionsBox, String type, String label) {
        Button btn = new Button(label);
        btn.getStyleClass().add("palette-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            selectedType = type;
            updateSelection(type);
            onTypeSelected.accept(type);
        });
        buttons.put(type, btn);
        optionsBox.getChildren().add(btn);
    }

    private void updateSelection(String type) {
        buttons.values().forEach(b -> {
            b.setStyle(null); // Reset to CSS defaults from .palette-button
            b.setEffect(null);
        });

        Button selected = buttons.get(type);
        if (selected != null) {
            String color = type.equals("REMOVE") ? "#FF3355" : "#00CFFF";
            selected.setStyle("-fx-border-color: " + color + "; -fx-background-color: #1A3A5A; -fx-text-fill: white; -fx-font-weight: bold;");

            // Neon Glow Effect
            javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
            glow.setColor(javafx.scene.paint.Color.web(color));
            glow.setRadius(20);
            glow.setSpread(0.5);
            selected.setEffect(glow);
        }
    }
}
