package com.gridfall.model.nodes;

import com.gridfall.util.ColorPalette;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class IndustrialZone extends Node {
    public IndustrialZone(int x, int y) {
        super(x, y, "FACTORY", 500);
    }

    @Override
    public int getDemand() { return 80; }
    @Override
    public int getOutput() { return 0; }
    @Override
    public int getRevenue() { return 100; }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        gc.setGlobalAlpha(1.0);

        Color body = isDamaged ? Color.rgb(30, 30, 35) : (isPowered ? Color.web("#FF8800").darker() : ColorPalette.UNPOWERED_NAVY);

        // Large factory base
        gc.setFill(body);
        double[] x = { sx - 20, sx, sx + 20, sx };
        double[] y = { sy - 10, sy - 20, sy - 10, sy };
        gc.fillPolygon(x, y, 4);

        gc.setFill(body.darker());
        gc.fillRect(sx - 20, sy - 10, 20, 15);
        gc.setFill(body.darker().darker());
        gc.fillRect(sx, sy - 10, 20, 15);

        // Smokestacks
        gc.setFill(Color.rgb(60, 60, 70));
        gc.fillRect(sx - 12, sy - 30, 6, 20);
        gc.fillRect(sx + 6, sy - 25, 6, 15);

        if (isPowered && !isDamaged) {
            // Smoke particles simulation logic usually goes here, but we'll do static glow
            gc.setGlobalAlpha(0.4);
            gc.setFill(ColorPalette.NEON_GOLD);
            gc.fillOval(sx - 14, sy - 35, 10, 10);
        }
    }
}
