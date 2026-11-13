package com.gridfall.model.nodes;

import com.gridfall.util.ColorPalette;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BatteryBank extends Node {
    public BatteryBank(int x, int y) {
        super(x, y, "BATTERY BANK", 250);
    }

    @Override
    public int getDemand() { return 5; }
    @Override
    public int getOutput() { return 0; }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        gc.setGlobalAlpha(1.0);

        Color mainColor = isDamaged ? Color.rgb(40, 40, 45) : (isPowered ? Color.web("#00FFAA").darker() : ColorPalette.UNPOWERED_STEEL);

        // Draw 3 small battery units
        for (int i = -1; i <= 1; i++) {
            double ox = i * 10;
            double oy = i * 5;

            gc.setFill(mainColor.darker());
            gc.fillRect(sx + ox - 4, sy + oy - 15, 8, 15);

            gc.setFill(mainColor);
            gc.fillRect(sx + ox - 4, sy + oy - 15, 6, 13);

            // Charge lights
            if (isPowered && !isDamaged) {
                double charge = (Math.sin(time * 3 + i) + 1) / 2;
                gc.setFill(charge > 0.5 ? ColorPalette.NEON_MINT : Color.web("#003322"));
                gc.fillOval(sx + ox - 2, sy + oy - 12, 4, 4);
            }
        }
    }
}
