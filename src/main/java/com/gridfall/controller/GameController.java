package com.gridfall.controller;

import com.gridfall.core.GameState;
import com.gridfall.model.grid.CityGrid;
import com.gridfall.model.network.CityStats;
import com.gridfall.model.network.EventType;
import com.gridfall.model.network.NetworkSimulator;
import com.gridfall.model.nodes.*;
import com.gridfall.model.save.GridMemento;
import com.gridfall.ui.*;
import com.gridfall.view.IsometricRenderer;
import com.gridfall.util.ColorPalette;
import com.gridfall.util.IsoMath;
import com.gridfall.model.save.SaveManager;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Stack;

public class GameController {
    private final CityGrid grid;
    private final NetworkSimulator simulator;
    private final IsometricRenderer renderer;
    private final Canvas canvas;
    private final HUD hud;
    private final BuildingPalette palette;
    private final AlertLog alerts;
    private final EventNotification eventPopup;
    private CityStats currentStats;
    private GameState currentState = GameState.MENU;
    private final Stack<GridMemento> undoStack = new Stack<>();
    private Point2D hoverPos = null;
    private String selectedType = "REACTOR";
    private double eventCycleTimer = 0;
    private double economyTimer = 0;
    private HelpOverlay helpOverlay = null;

    public GameController(StackPane root) {
        this.grid = new CityGrid(15, 15);
        this.simulator = new NetworkSimulator();
        this.renderer = new IsometricRenderer();
        this.canvas = new Canvas(1000, 700);

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
        });
        this.hud = new HUD();
        this.alerts = new AlertLog();
        this.eventPopup = new EventNotification();
        this.palette = new BuildingPalette(
                type -> this.selectedType = type,
                () -> {
                    SaveManager.saveCity(grid, "my_city.txt");
                    alerts.addMessage("CITY SAVED TO DISK", "#00FFAA");
                },
                () -> undo(),
                () -> toggleHelp(root)
        );
        this.currentStats = new CityStats();

        BorderPane uiLayout = new BorderPane();
        uiLayout.setTop(hud);
        uiLayout.setRight(palette);
        uiLayout.setLeft(alerts);
        uiLayout.setPickOnBounds(false);

        root.getChildren().addAll(canvas, uiLayout, eventPopup);
        StackPane.setAlignment(hud, Pos.TOP_CENTER);
        StackPane.setAlignment(palette, Pos.CENTER_RIGHT);
        StackPane.setAlignment(alerts, Pos.BOTTOM_LEFT);

        setupEvents();
        updateSimulation();
    }

    private void toggleHelp(StackPane root) {
        if (helpOverlay == null) {
            helpOverlay = new HelpOverlay(() -> toggleHelp(root));
            root.getChildren().add(helpOverlay);
        } else {
            root.getChildren().remove(helpOverlay);
            helpOverlay = null;
        }
    }

    private void saveState() {
        undoStack.push(new GridMemento(grid.getAllNodes()));
        if (undoStack.size() > 50) undoStack.remove(0);
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            GridMemento memento = undoStack.pop();
            // Clear current grid
            for (Node n : grid.getAllNodes()) {
                grid.removeNode(n.getX(), n.getY());
            }
            // Restore nodes
            for (Node n : memento.getNodes()) {
                grid.placeNode(n);
            }
            updateSimulation();
            alerts.addMessage("UNDO: ACTION REVERSED", "#FFCC00");
        }
    }

    private void setupEvents() {
        canvas.setOnMouseMoved(e -> {
            double offsetX = canvas.getWidth() / 2;
            double offsetY = canvas.getHeight() / 4;
            int[] pos = IsoMath.screenToGrid(e.getX(), e.getY(), offsetX, offsetY);
            hoverPos = new Point2D(pos[0], pos[1]);
        });

        canvas.setOnMouseClicked(e -> {
            if (currentState == GameState.MENU) {
                currentState = GameState.PLAYING;
                alerts.addMessage("GRID CONTROL ACTIVE", "#00CFFF");
                return;
            }

            if (hoverPos != null) {
                int x = (int) hoverPos.getX();
                int y = (int) hoverPos.getY();
                Node existingNode = grid.getNode(x, y);

                if (existingNode != null && existingNode.isDamaged()) {
                    int repairCost = existingNode.getCost() / 2;
                    if (currentStats.budget >= repairCost) {
                        saveState();
                        existingNode.setDamaged(false);
                        existingNode.setOverloadTimer(1.0);
                        currentStats.budget -= repairCost;
                        alerts.addMessage("REPAIRED: " + existingNode.getName(), "#00FFAA");
                    } else {
                        alerts.addMessage("REPAIR COST: " + repairCost + " CR", "#FF3355");
                    }
                } else if (selectedType.equals("REMOVE")) {
                    if (existingNode != null) {
                        saveState();
                        grid.removeNode(x, y);
                        alerts.addMessage("DECONSTRUCTED: " + existingNode.getName(), "#FF3355");
                    }
                } else {
                    Node newNode = NodeFactory.create(selectedType, x, y);
                    if (newNode != null && currentStats.budget >= newNode.getCost()) {
                        saveState();
                        if (grid.placeNode(newNode)) {
                            currentStats.budget -= newNode.getCost();
                            alerts.addMessage("PLACED: " + newNode.getName(), "#00CFFF");
                        } else {
                            alerts.addMessage("POSITION OCCUPIED", "#FFCC00");
                            undoStack.pop(); // Remove the state we just saved
                        }
                    } else if (newNode != null) {
                        alerts.addMessage("INSUFFICIENT CREDITS", "#FF3355");
                    }
                }
                updateSimulation();
            }
        });
    }

    private void updateSimulation() {
        int oldAlerts = currentStats.gridAlerts;
        simulator.recalculatePower(grid, currentStats);
        hud.update(currentStats);

        if (currentStats.gridAlerts > oldAlerts) {
            alerts.addMessage("GRID OVERLOAD DETECTED!", "#FF3355");
        }
    }

    private void processCascade(double deltaTime) {
        boolean changed = false;
        double loadSeverity = Math.max(1.0, currentStats.loadRatio);

        // Stability Logic: Grid wears down under stress
        if (currentStats.loadRatio > 1.0) {
            currentStats.stability -= deltaTime * 0.05 * (currentStats.loadRatio - 1.0);
        } else if (currentStats.stability < 1.0) {
            currentStats.stability = Math.min(1.0, currentStats.stability + deltaTime * 0.01);
        }

        if (currentStats.stability <= 0) {
            currentState = GameState.GAMEOVER;
            alerts.addMessage("TOTAL GRID COLLAPSE!", "#FF3355");
        }

        var allNodes = new java.util.ArrayList<>(grid.getAllNodes());
        java.util.Collections.shuffle(allNodes);

        for (Node node : allNodes) {
            if (node.isOverloaded()) {
                // Decay rate scales with load severity
                double baseDecay = (node instanceof PowerSource) ? 0.15 : 0.4;
                double decayRate = baseDecay * loadSeverity;

                node.setOverloadTimer(node.getOverloadTimer() - deltaTime * decayRate);

                // Visual flicker
                if (node.getOverloadTimer() < 0.4) {
                    node.setPowered(Math.random() > (0.1 * loadSeverity));
                }

                if (node.getOverloadTimer() <= 0) {
                    node.setDamaged(true);
                    node.setPowered(false);
                    node.setOverloaded(false);
                    alerts.addMessage("CRITICAL DAMAGE: " + node.getName(), "#FF3355");
                    changed = true;
                }
            } else if (!node.isDamaged()) {
                // Recovery is slow
                if (node.getOverloadTimer() < 1.0) {
                    node.setOverloadTimer(Math.min(1.0, node.getOverloadTimer() + deltaTime * 0.05));
                }
            }
        }
        if (changed) updateSimulation();
    }

    private void processEvents(double deltaTime) {
        if (currentStats.currentEventType != EventType.NONE) {
            // Passive stability drain during events
            currentStats.stability -= deltaTime * 0.02;

            // Direct Event Logic
            if (currentStats.currentEventType == EventType.CYBER_ATTACK) {
                currentStats.stability -= deltaTime * 0.05; // Extra drain
                if (Math.random() < 0.1 * deltaTime) { // Random credit theft
                    int theft = 10 + (int)(Math.random() * 40);
                    currentStats.budget = Math.max(0, currentStats.budget - theft);
                    alerts.addMessage("CYBER THEFT: -" + theft + " CR", "#FF3355");
                }
            } else if (currentStats.currentEventType == EventType.SOLAR_FLARE) {
                // Occasional EMP brownouts (flickering power levels)
                if (Math.random() < 0.05 * deltaTime) {
                    alerts.addMessage("EMP INTERFERENCE: OUTPUT UNSTABLE", "#FFCC00");
                    updateSimulation();
                }
            }

            eventCycleTimer += deltaTime;
            if (eventCycleTimer >= 1.0) {
                eventCycleTimer = 0;
                currentStats.eventTimeRemaining--;
                if (currentStats.eventTimeRemaining <= 0) {
                    alerts.addMessage("GRID STABILIZED: " + currentStats.currentEventType.name(), "#00FFAA");
                    currentStats.currentEventType = EventType.NONE;
                    updateSimulation();
                }
            }
        } else {
            eventCycleTimer += deltaTime;
            if (eventCycleTimer >= 60.0) { // Check for event every 60s (was 30)
                eventCycleTimer = 0;
                if (Math.random() < 0.2) { // Lower chance (was 0.35)
                    EventType[] events = EventType.values();
                    currentStats.currentEventType = events[1 + (int)(Math.random() * (events.length - 1))];
                    currentStats.eventTimeRemaining = currentStats.currentEventType.duration;
                    alerts.addMessage("CRITICAL EVENT: " + currentStats.currentEventType.label, "#FF3355");
                    eventPopup.show(currentStats.currentEventType);
                    updateSimulation();
                }
            }
        }
    }

    private void processEconomy(double deltaTime) {
        economyTimer += deltaTime;
        if (economyTimer >= 1.0) { // Every 1 second
            economyTimer = 0;

            int income = 0;
            for (Node node : grid.getAllNodes()) {
                if (node.isPowered() && !node.isOverloaded()) {
                    income += node.getRevenue();
                }
            }

            // Energy Surplus Bonus (Selling excess power)
            if (currentStats.totalOutput > currentStats.totalDemand) {
                int surplus = currentStats.totalOutput - currentStats.totalDemand;
                // $1 per 10 units of surplus
                income += (surplus / 10);
            }

            if (income > 0) {
                currentStats.budget += income;
            }
            currentStats.lastIncome = income;
            hud.update(currentStats);
        }
    }

    public void startGame() {
        new AnimationTimer() {
            private long lastTime = -1;

            @Override
            public void handle(long now) {
                if (lastTime == -1) lastTime = now;
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (currentState == GameState.PLAYING) {
                    processCascade(deltaTime);
                    processEconomy(deltaTime);
                    processEvents(deltaTime);
                }

                renderer.render(canvas, grid, now / 1_000_000.0, hoverPos, currentStats, simulator);

                if (currentState == GameState.MENU) {
                    drawMenu();
                } else if (currentState == GameState.GAMEOVER) {
                    drawGameOver();
                }
            }
        }.start();
    }

    private void drawMenu() {
        var gc = canvas.getGraphicsContext2D();
        gc.save();
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(ColorPalette.NEON_CYAN);
        gc.setFont(Font.font("Consolas", FontWeight.BLACK, 80));
        gc.fillText("GRIDFALL", canvas.getWidth()/2 - 180, canvas.getHeight()/2 - 50);

        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        gc.fillText("CLICK ANYWHERE TO START ARCHITECTURE", canvas.getWidth()/2 - 200, canvas.getHeight()/2 + 50);
        gc.restore();
    }

    private void drawGameOver() {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.color(1, 0, 0, 0.5));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 60));
        gc.fillText("TOTAL BLACKOUT", canvas.getWidth()/2 - 200, canvas.getHeight()/2);
    }
}
