package com.gridfall.ui;

import com.gridfall.util.ColorPalette;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HelpOverlay extends VBox {
    private final Runnable onClose;

    public HelpOverlay(Runnable onClose) {
        this.onClose = onClose;
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: rgba(5, 10, 20, 0.95);");

        VBox dialogBox = new VBox(20);
        dialogBox.setMaxWidth(650);
        dialogBox.setMaxHeight(550);
        dialogBox.setPadding(new Insets(30));
        dialogBox.setAlignment(Pos.TOP_CENTER);
        dialogBox.setStyle("-fx-border-color: #00CFFF; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 12; " +
                "-fx-background-color: #081220; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,207,255,0.4), 15, 0, 0, 0);");

        Label titleLabel = new Label("GRID_OPERATIONS_MANUAL");
        titleLabel.setFont(Font.font("Consolas", FontWeight.BLACK, 32));
        titleLabel.setTextFill(Color.web("#00CFFF"));

        Label subTitle = new Label("EMERGENCY RESPONSE PROTOCOLS & SYSTEMS GUIDE");
        subTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        subTitle.setTextFill(Color.web("#80A0C0"));

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(10));

        // Let's list each event
        contentBox.getChildren().addAll(
                createSectionTitle("DYNAMIC ENVIRONMENTAL EMERGENCIES"),
                createEventItem("☀️ HEATWAVE",
                        "System Demand scales to 150%. Solar & nuclear output drops slightly (90%).\n" +
                                "STRATEGY: Build quick backup power (Solar Farms) and storage Batteries before the grid strains.",
                        "#FF8800"),
                createEventItem("☄️ SOLAR FLARE",
                        "Electromagnetic storm spikes. All Power Generation drops to 50%!\n" +
                                "STRATEGY: Disconnect massive consumers (Factories/MegaTowers) or build redundancy in advance.",
                        "#FF4444"),
                createEventItem("🧬 AI COMPUTE SURGE",
                        "Residential areas experience 200% computational load spikes.\n" +
                                "STRATEGY: Allocate dedicated power channels to housing blocks or maintain battery stability buffers.",
                        "#B05CFF"),
                createEventItem("👾 CYBER ATTACK",
                        "Malicious breach drains stability at double speed and steals credits of 10-50 CR periodically.\n" +
                                "STRATEGY: Rapidly deploy passive Battery Banks to counteract stability drain until threat is eliminated.",
                        "#00FFAA")
        );

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(320);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Button closeBtn = new Button("CLOSE TERMINAL");
        closeBtn.getStyleClass().add("palette-button");
        closeBtn.setStyle("-fx-border-color: #00CFFF; -fx-text-fill: #00CFFF; -fx-font-weight: bold; -fx-padding: 8 20 8 20;");
        closeBtn.setOnAction(e -> onClose.run());

        dialogBox.getChildren().addAll(titleLabel, subTitle, scroll, closeBtn);
        this.getChildren().add(dialogBox);
    }

    private Label createSectionTitle(String title) {
        Label lbl = new Label("[ " + title + " ]");
        lbl.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        lbl.setTextFill(Color.web("#FFCC00"));
        VBox.setMargin(lbl, new Insets(10, 0, 5, 0));
        return lbl;
    }

    private VBox createEventItem(String name, String desc, String colorHex) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1; -fx-border-radius: 6;");

        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Consolas", FontWeight.BLACK, 16));
        nameLbl.setTextFill(Color.web(colorHex));

        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Consolas", FontWeight.NORMAL, 13));
        descLbl.setTextFill(Color.web("#BDD4EC"));
        descLbl.setWrapText(true);

        box.getChildren().addAll(nameLbl, descLbl);
        return box;
    }
}
