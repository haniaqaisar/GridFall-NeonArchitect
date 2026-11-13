package com.gridfall.view;

import com.gridfall.model.grid.CityGrid;
import com.gridfall.model.network.CityStats;
import com.gridfall.model.network.EventType;
import com.gridfall.model.network.NetworkSimulator;
import com.gridfall.model.nodes.*;
import com.gridfall.util.ColorPalette;
import com.gridfall.util.IsoMath;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class IsometricRenderer {
    public void render(Canvas canvas, CityGrid grid, double time, Point2D hover, CityStats stats, NetworkSimulator simulator) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double offsetX = width / 2;
        double offsetY = height / 4;

        // Background
        gc.setFill(ColorPalette.BACKGROUND);
        gc.fillRect(0, 0, width, height);

        // Event Overlays - Much more dramatic effects
        if (stats.currentEventType == EventType.HEATWAVE) {
            gc.setGlobalAlpha(0.25);
            gc.setFill(Color.ORANGERED);
            gc.fillRect(0, 0, width, height);

            // Heat ripples
            gc.setGlobalAlpha(0.1);
            gc.setStroke(Color.YELLOW);
            for(int i=0; i<5; i++) {
                double speed = time / 20.0 + i;
                double y = (Math.sin(speed) + 1) * height / 2;
                gc.strokeLine(0, y, width, y + Math.sin(time/50.0) * 100);
            }
        } else if (stats.currentEventType == EventType.SOLAR_FLARE) {
            // Blinding white pulses
            double flareStrength = 0.5 + 0.5 * Math.sin(time / 20.0);
            gc.setGlobalAlpha(0.15 * flareStrength);
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, width, height);

            // "White out" static
            gc.setGlobalAlpha(0.08);
            for(int i=0; i<30; i++) {
                gc.setFill(Color.WHITE);
                gc.fillRect(Math.random() * width, Math.random() * height, 100, 2);
            }
        } else if (stats.currentEventType == EventType.CYBER_ATTACK) {
            // Radical glitching
            if (time % 10 < 3) {
                if (Math.random() > 0.8) {
                    gc.setGlobalAlpha(0.3);
                    gc.setFill(Color.LIME);
                    gc.fillRect(0, Math.random() * height, width, 5);

                    // Ghosting effect
                    gc.setGlobalAlpha(0.2);
                    gc.setFont(Font.font("Consolas", 20));
                    gc.fillText("ACCESS_DENIED", Math.random()*width, Math.random()*height);
                }
            }
            gc.setGlobalAlpha(0.1);
            gc.setFill(Color.web("#051205")); // Dark green tint
            gc.fillRect(0, 0, width, height);
        } else if (stats.currentEventType == EventType.AI_SURGE) {
            // Purple digital aura
            gc.setGlobalAlpha(0.1);
            gc.setFill(Color.PURPLE);
            gc.fillRect(0, 0, width, height);

            // Floating hex bits
            gc.setFill(Color.VIOLET);
            gc.setFont(Font.font("Consolas", 12));
            for(int i=0; i<10; i++) {
                double tx = (Math.sin(time/200.0 + i) + 1) * width/2;
                double ty = (Math.cos(time/150.0 + i) + 1) * height/2;
                gc.fillText("0x" + Integer.toHexString((int)(time+i)%255), tx, ty);
            }
        }
        gc.setGlobalAlpha(1.0);

        // Draw Ambient "Cyber Fog" (Beneath the grid)
        gc.setGlobalAlpha(0.06);
        for(int i=0; i<2; i++) {
            gc.setFill(javafx.scene.paint.Color.web("#16324A"));
            double fogX = (time * 15 + i * 500) % (width + 500) - 250;
            double fogY = height * 0.5 + i * 40;
            gc.fillOval(fogX, fogY, 500, 80);
        }
        gc.setGlobalAlpha(1.0);

        // Draw Base Grid Tiles
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Point2D p = IsoMath.gridToScreen(x, y, offsetX, offsetY);
                boolean isHovered = hover != null && (int)hover.getX() == x && (int)hover.getY() == y;

                gc.setFill(isHovered ? ColorPalette.GRID_HOVER : ColorPalette.GRID_BASE);
                gc.setStroke(ColorPalette.GRID_HOVER.brighter());

                double[] px = { p.getX(), p.getX() + IsoMath.TILE_WIDTH/2, p.getX(), p.getX() - IsoMath.TILE_WIDTH/2 };
                double[] py = { p.getY(), p.getY() + IsoMath.TILE_HEIGHT/2, p.getY() + IsoMath.TILE_HEIGHT, p.getY() + IsoMath.TILE_HEIGHT/2 };

                gc.fillPolygon(px, py, 4);
                gc.strokePolygon(px, py, 4);
            }
        }

        // Draw Connections First (Under buildings)
        gc.save();

        for (Node node : grid.getAllNodes()) {
            if (node.isPowered() && (node instanceof TransmissionTower || node instanceof NuclearReactor)) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;

                        Node neighbor = grid.getNode(node.getX() + dx, node.getY() + dy);
                        if (neighbor != null && neighbor.isPowered()) {
                            Point2D p1 = IsoMath.gridToScreen(node.getX(), node.getY(), offsetX, offsetY);
                            Point2D p2 = IsoMath.gridToScreen(neighbor.getX(), neighbor.getY(), offsetX, offsetY);

                            double h1 = (node instanceof TransmissionTower) ? -20 : (node instanceof NuclearReactor ? -10 : -15);
                            double h2 = (neighbor instanceof TransmissionTower) ? -20 : (neighbor instanceof NuclearReactor ? -10 : -15);

                            // Layer 1: Outer Neon Glow (Thick)
                            boolean isOverloaded = node.isOverloaded() || neighbor.isOverloaded();
                            gc.setStroke(isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_MINT);
                            gc.setLineWidth(6);
                            gc.setGlobalAlpha(0.2);
                            gc.setLineDashes(null);
                            gc.strokeLine(p1.getX(), p1.getY() + h1, p2.getX(), p2.getY() + h2);

                            // Layer 2: Main Neon Wire
                            gc.setLineWidth(2);
                            gc.setGlobalAlpha(0.8);
                            gc.strokeLine(p1.getX(), p1.getY() + h1, p2.getX(), p2.getY() + h2);

                            // Layer 3: Moving Energy Core (Dashed white)
                            gc.setStroke(javafx.scene.paint.Color.WHITE);
                            gc.setLineWidth(1);
                            gc.setGlobalAlpha(1.0);
                            gc.setLineDashes(8, 12);
                            gc.setLineDashOffset(time * 30);
                            gc.strokeLine(p1.getX(), p1.getY() + h1, p2.getX(), p2.getY() + h2);

                            // Layer 4: Occasional Data Packets (Fast moving bright spots)
                            if (time % 2000 < 1000) {
                                double packetPos = (time % 1000) / 1000.0;
                                double px = p1.getX() + (p2.getX() - p1.getX()) * packetPos;
                                double py = (p1.getY() + h1) + ((p2.getY() + h2) - (p1.getY() + h1)) * packetPos;
                                gc.setFill(javafx.scene.paint.Color.WHITE);
                                gc.fillOval(px - 2, py - 2, 4, 4);
                            }
                        }
                    }
                }
            }
        }
        gc.restore();

        // Draw Buildings sorted by distance (z-order)
        grid.getAllNodes().stream()
                .sorted((a, b) -> (a.getX() + a.getY()) - (b.getX() + b.getY()))
                .forEach(node -> {
                    Point2D p = IsoMath.gridToScreen(node.getX(), node.getY(), offsetX, offsetY);

                    // Extra Ground Glow for visibility
                    if (node.isPowered()) {
                        double pulse = 0.05 + 0.02 * Math.sin(time / 200.0);
                        gc.setGlobalAlpha(pulse);

                        Color glowColor = ColorPalette.NEON_MINT;
                        if (node.isOverloaded()) glowColor = ColorPalette.NEON_RED;
                        else if (node instanceof ResidentialBlock) glowColor = ColorPalette.NEON_CYAN;
                        else if (node instanceof MegaTower) glowColor = ColorPalette.NEON_PURPLE;
                        else if (node instanceof NuclearReactor) glowColor = ColorPalette.NEON_GOLD;
                        else if (node instanceof NeonSign) glowColor = ColorPalette.NEON_MAGENTA;

                        gc.setFill(glowColor);
                        gc.fillOval(p.getX() - 12, p.getY() + 10, 24, 6);
                    }

                    node.draw(gc, p.getX(), p.getY(), time / 1000.0);

                    // Add Overload Sparks
                    if (node.isOverloaded() && Math.random() > 0.8) {
                        gc.setStroke(javafx.scene.paint.Color.WHITE);
                        gc.setLineWidth(1);
                        double ox = p.getX() + (Math.random() - 0.5) * 20;
                        double oy = p.getY() - 20 + (Math.random() - 0.5) * 20;
                        gc.strokeLine(ox, oy, ox + (Math.random()-0.5) * 10, oy + (Math.random()-0.5) * 10);
                    }
                });

        // Post-processing: Scanlines
        gc.setGlobalAlpha(0.05);
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(1);
        for(int i=0; i<height; i+=4) {
            gc.strokeLine(0, i, width, i);
        }

        // Dark Vignette
        javafx.scene.paint.RadialGradient vignette = new javafx.scene.paint.RadialGradient(
                0, 0, width/2, height/2, width, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.TRANSPARENT),
                new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.rgb(0, 0, 0, 0.4))
        );
        gc.setFill(vignette);
        gc.fillRect(0, 0, width, height);

        // World-Space Hover Tooltip
        if (hover != null) {
            Node n = grid.getNode((int)hover.getX(), (int)hover.getY());
            if (n != null) {
                Point2D p = IsoMath.gridToScreen(n.getX(), n.getY(), offsetX, offsetY);
                double tx = p.getX() + 30;
                double ty = p.getY() - 40;

                gc.setGlobalAlpha(0.95);
                gc.setFill(javafx.scene.paint.Color.rgb(10, 15, 25));
                gc.setStroke(n.isPowered() ? ColorPalette.NEON_CYAN : javafx.scene.paint.Color.GRAY);
                gc.setLineWidth(1);
                gc.fillRoundRect(tx, ty, 130, 45, 2, 2);
                gc.strokeRoundRect(tx, ty, 130, 45, 2, 2);

                gc.setFill(javafx.scene.paint.Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font("Consolas", 11));
                gc.fillText(n.getName().toUpperCase(), tx + 10, ty + 18);

                if (n.isDamaged()) {
                    gc.setFill(javafx.scene.paint.Color.rgb(255, 80, 80));
                    gc.fillText("STATUS: CRITICAL", tx + 10, ty + 34);
                } else {
                    gc.setFill(n.isPowered() ? ColorPalette.NEON_CYAN : javafx.scene.paint.Color.LIGHTGRAY);
                    gc.fillText("POWER: " + (n.isPowered() ? "ONLINE" : "OFFLINE"), tx + 10, ty + 34);
                }
            }
        }

        gc.setGlobalAlpha(1.0);
    }
}
