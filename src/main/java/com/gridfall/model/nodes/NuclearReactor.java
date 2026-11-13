package com.gridfall.model.nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.gridfall.util.ColorPalette;

public class NuclearReactor extends PowerSource {
    public NuclearReactor(int x, int y) {
        super(x, y, "Fusion Reactor", 500, 1500);
    }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        gc.save();
        sy -= 5;

        double radius = 16;
        double h = 22;

        // 1. Basic Structure (Always visible)
        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.rgb(45, 50, 60));
        gc.fillOval(sx - radius, sy - 4, radius * 2, 8); // Shadow base

        // Structural Body
        Color baseNeon = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_GOLD;
        Color mainColor = isDamaged ? Color.rgb(40, 40, 45) : (isPowered ? baseNeon.darker() : ColorPalette.UNPOWERED_STEEL);
        Color sideColor = isDamaged ? Color.rgb(30, 30, 35) : (isPowered ? mainColor.darker() : ColorPalette.UNPOWERED_NAVY);
        Color topColor = isDamaged ? Color.rgb(20, 20, 25) : (isPowered ? baseNeon : ColorPalette.UNPOWERED_GRAPHITE);

        gc.setFill(mainColor);
        gc.fillRect(sx - radius, sy - h, radius * 2, h); // Cylinder body

        gc.setFill(topColor);
        gc.fillOval(sx - radius, sy - h - 4, radius * 2, 8); // Top cap

        if (isDamaged) {
            // Radioactive / Dirty Smoke
            gc.setGlobalAlpha(0.6);
            gc.setFill(Color.rgb(100, 120, 100)); // Sickly green-grey smoke
            for(int i=0; i<4; i++) {
                double smokeX = sx + Math.sin(time + i) * 10;
                double smokeY = sy - h - 10 - (time * 12 + i * 15) % 40;
                gc.fillOval(smokeX - 6, smokeY - 6, 12, 12);
            }
            // Lava/Meltdown glow at base
            gc.setGlobalAlpha(0.4 + 0.2 * Math.sin(time * 8));
            gc.setFill(Color.ORANGERED);
            gc.fillOval(sx - radius - 2, sy - 6, (radius + 2) * 2, 12);
        } else if (isPowered) {
            Color energyColor = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_GOLD;
            double flicker = (isOverloaded && Math.random() > 0.9) ? 0.3 : 1.0;
            double pulse = (0.7 + 0.3 * Math.sin(time * 5)) * flicker;

            // Intense core glow (The "Previous" Glow style)
            gc.setGlobalAlpha(0.25 * pulse);
            gc.setFill(energyColor);
            gc.fillOval(sx - 30, sy - 30, 60, 60);

            // Glowing interior
            gc.setGlobalAlpha(1.0 * flicker);
            gc.setFill(energyColor);
            gc.fillOval(sx - 8, sy - h/2 - 4, 16, 8);

            // Neon Trim
            gc.setStroke(energyColor);
            gc.setLineWidth(2);
            gc.strokeRect(sx - radius, sy - h, radius * 2, h);
        } else {
            gc.setGlobalAlpha(0.6);
            gc.setFill(Color.DARKRED);
            gc.fillOval(sx - 2, sy - h - 6, 4, 4);
        }
        gc.restore();
    }

    @Override public int getDemand() { return 0; }
    @Override public int getOutput() { return 1500; }
}
