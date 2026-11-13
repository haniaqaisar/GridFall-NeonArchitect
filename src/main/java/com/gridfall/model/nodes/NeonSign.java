package com.gridfall.model.nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.gridfall.util.ColorPalette;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class NeonSign extends Consumer {
    public NeonSign(int x, int y) {
        super(x, y, "Neon Sign", 30, 20, 15);
    }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        gc.save();
        sy -= 5;

        // 1. Support Structure (Always visible)
        gc.setGlobalAlpha(1.0);

        Color signNeon = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_MAGENTA;
        Color poleColor = isDamaged ? Color.rgb(40, 40, 45) : (isPowered ? signNeon.darker() : ColorPalette.UNPOWERED_STEEL);
        Color boardColor = isDamaged ? Color.rgb(20, 20, 25) : (isPowered ? signNeon.darker().darker().darker() : ColorPalette.UNPOWERED_NAVY);
        Color rimColor = isDamaged ? Color.BLACK : (isPowered ? signNeon.darker().darker() : ColorPalette.UNPOWERED_GRAPHITE);

        gc.setStroke(poleColor);
        gc.setLineWidth(2);
        gc.strokeLine(sx, sy + 12, sx, sy - 2); // Pole

        gc.setFill(boardColor);
        gc.fillRect(sx - 15, sy - 12, 30, 16);
        gc.setStroke(rimColor);
        gc.strokeRect(sx - 15, sy - 12, 30, 16);

        if (isDamaged) {
            // Shattered glass effect
            gc.setStroke(Color.rgb(150, 150, 160, 0.5));
            gc.setLineWidth(1);
            gc.strokeLine(sx - 10, sy - 8, sx - 2, sy - 2);
            gc.strokeLine(sx + 5, sy - 10, sx - 5, sy + 2);
        } else if (isPowered) {
            double flicker = (Math.sin(time * 15) > 0.0) ? 1.0 : 0.4;
            if (isOverloaded) flicker *= (Math.random() > 0.4 ? 1 : 0);

            Color neonColor = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_MAGENTA;

            // Bloom Layers
            gc.setGlobalAlpha(0.25 * flicker);
            gc.setFill(neonColor);
            gc.fillOval(sx - 20, sy - 18, 40, 28);

            // Neon Tubes (Inner Glow)
            gc.setGlobalAlpha(0.6 * flicker);
            gc.setFont(Font.font("Consolas", FontWeight.BLACK, 13));
            gc.setStroke(neonColor);
            gc.setLineWidth(4);
            gc.strokeText("VOX", sx - 9, sy);

            // White core
            gc.setGlobalAlpha(1.0 * flicker);
            gc.setFill(Color.WHITE);
            gc.fillText("VOX", sx - 9, sy);
        } else {
            gc.setGlobalAlpha(0.8);
            gc.setFill(ColorPalette.UNPOWERED_WINDOW);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
            gc.fillText("VOX", sx - 9, sy);
        }
        gc.restore();
    }
}
