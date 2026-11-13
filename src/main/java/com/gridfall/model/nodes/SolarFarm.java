package com.gridfall.model.nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SolarFarm extends Node {
    public SolarFarm(int x, int y) {
        super(x, y, "SOLAR FARM", 150);
    }

    @Override
    public int getDemand() { return 0; }
    @Override
    public int getOutput() { return 40; }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        double w = 24;
        double h = 12;

        // Base Panels
        gc.setGlobalAlpha(1.0);
        Color panelColor = isDamaged ? Color.rgb(40, 40, 45) : (isPowered ? Color.web("#0066FF") : Color.web("#112244"));

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                double px = sx + (i * 10);
                double py = sy + (j * 6) - 5;

                gc.setFill(panelColor);
                gc.setStroke(panelColor.brighter());
                gc.setLineWidth(1);

                double[] ptsX = { px, px + 8, px, px - 8 };
                double[] ptsY = { py - 4, py, py + 4, py };
                gc.fillPolygon(ptsX, ptsY, 4);
                gc.strokePolygon(ptsX, ptsY, 4);

                // Reflection glare
                if (isPowered && !isDamaged) {
                    gc.setGlobalAlpha(0.3 * (0.5 + 0.5 * Math.sin(time * 2 + i + j)));
                    gc.setStroke(Color.WHITE);
                    gc.strokeLine(px - 2, py - 1, px + 2, py + 1);
                }
            }
        }
    }
}
