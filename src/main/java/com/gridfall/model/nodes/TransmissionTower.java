package com.gridfall.model.nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.gridfall.util.ColorPalette;

public class TransmissionTower extends Node {
    public TransmissionTower(int x, int y) {
        super(x, y, "Power Line", 10);
    }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        gc.save();
        sy -= 8;

        // 1. Structure (Always visible)
        gc.setGlobalAlpha(1.0);
        Color baseColor = isDamaged ? Color.rgb(40, 40, 45) : (isPowered ? (isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_MINT) : ColorPalette.UNPOWERED_STEEL);
        gc.setStroke(baseColor);
        gc.setLineWidth(1.5);

        // Slimmer lattice body
        double[] xBody = {sx - 6, sx + 6, sx + 2, sx - 2};
        double[] yBody = {sy + 8, sy + 8, sy - 28, sy - 28};
        gc.strokePolygon(xBody, yBody, 4);

        // Support arms
        gc.strokeLine(sx - 12, sy - 18, sx + 12, sy - 18); // Top arm
        gc.strokeLine(sx - 9, sy - 6, sx + 9, sy - 6);     // Lower arm

        if (isDamaged) {
            // Broken power lines / Arcing sparks
            gc.setStroke(Color.rgb(100, 100, 110));
            gc.setLineWidth(1);
            gc.strokeLine(sx - 12, sy - 18, sx - 15, sy - 10); // Hanging wire

            if (Math.random() > 0.8) {
                gc.setStroke(Color.WHITE);
                double sparkX = sx - 15 + Math.random() * 5;
                double sparkY = sy - 10 + Math.random() * 5;
                gc.strokeLine(sparkX, sparkY, sparkX + 3, sparkY + 3);
            }
        } else if (isPowered) {
            double flicker = (isOverloaded && Math.random() > 0.9) ? 0.3 : 1.0;

            // Neon Glow
            gc.setGlobalAlpha(0.3 * flicker);
            gc.setStroke(isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_MINT);
            gc.setLineWidth(6);
            gc.strokeLine(sx - 12, sy - 18, sx + 12, sy - 18);

            // Energy surge pulse
            double pulse = (time * 2.0) % 1.0;
            gc.setGlobalAlpha(1.0 * flicker);
            gc.setFill(Color.WHITE);
            gc.fillOval(sx - 1.5, sy - 28 + (pulse * 36), 3, 3);

            // Tip locator light
            double heartbeat = (0.5 + 0.5 * Math.sin(time * 8)) * flicker;
            gc.setGlobalAlpha(heartbeat);
            gc.setFill(isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_MINT);
            gc.fillOval(sx - 3, sy - 34, 6, 6);
        } else {
            gc.setGlobalAlpha(0.6);
            gc.setFill(ColorPalette.UNPOWERED_WINDOW);
            gc.fillOval(sx - 2, sy - 32, 4, 4);
        }
        gc.restore();
    }

    @Override public int getDemand() { return 0; }
    @Override public int getOutput() { return 0; }
}
