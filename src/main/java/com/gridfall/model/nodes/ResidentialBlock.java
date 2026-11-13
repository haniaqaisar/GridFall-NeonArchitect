package com.gridfall.model.nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.gridfall.util.ColorPalette;

public class ResidentialBlock extends Consumer {
    public ResidentialBlock(int x, int y) {
        super(x, y, "Residential Block", 50, 50, 10);
    }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        gc.save();
        sy -= 5;

        // Smaller footprint building
        double w = 18;
        double h = 35;
        double dx = sx - w/2;
        double dy = sy - h + 5;

        // 1. Structural Frame (3-Tone Shading)
        gc.setGlobalAlpha(1.0);

        Color baseNeon = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_CYAN;
        Color mainColor = isDamaged ? Color.rgb(40, 40, 45) : (isPowered ? baseNeon.darker().darker() : ColorPalette.UNPOWERED_STEEL);
        Color sideColor = isDamaged ? Color.rgb(30, 30, 35) : (isPowered ? mainColor.darker() : ColorPalette.UNPOWERED_NAVY);
        Color roofColor = isDamaged ? Color.rgb(50, 50, 55) : (isPowered ? mainColor.brighter() : ColorPalette.UNPOWERED_GRAPHITE);

        // Main Face
        gc.setFill(mainColor);
        gc.fillRect(dx, dy, w, h);

        // Depth/Side Face
        gc.setFill(sideColor);
        gc.fillPolygon(new double[]{dx+w, dx+w+4, dx+w+4, dx+w}, new double[]{dy, dy+3, dy+h+3, dy+h}, 4);

        // Roof/Top highlight
        gc.setFill(roofColor);
        gc.fillRect(dx, dy, w, 2);
        gc.fillPolygon(new double[]{dx, dx+w, dx+w+4, dx+4}, new double[]{dy, dy, dy+3, dy+3}, 4);

        // Windows (Unpowered / Damaged)
        gc.setFill(isDamaged ? Color.BLACK : ColorPalette.UNPOWERED_WINDOW);
        for(int i=0; i<5; i++) {
            // Unpowered windows: only a few might flicker or be slightly lit
            if (!isPowered && !isDamaged && (i + x) % 4 == 0) {
                gc.setFill(Color.rgb(100, 110, 130, 0.4));
            } else {
                gc.setFill(isDamaged ? Color.BLACK : ColorPalette.UNPOWERED_WINDOW);
            }

            gc.fillRect(dx + 3, dy + 4 + (i*6), 4, 3);
            gc.fillRect(dx + 11, dy + 4 + (i*6), 4, 3);
        }

        if (isDamaged) {
            // Smoke particles for damaged buildings
            gc.setGlobalAlpha(0.6);
            gc.setFill(Color.gray(0.2));
            for(int i=0; i<3; i++) {
                double smokeX = sx + Math.sin(time + i) * 5;
                double smokeY = dy - 5 - (time * 10 + i * 15) % 30;
                gc.fillOval(smokeX - 4, smokeY - 4, 8, 8);
            }

            // Cracks
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeLine(dx + 2, dy + 5, dx + 10, dy + 15);
            gc.strokeLine(dx + 15, dy + 20, dx + 5, dy + 30);
        } else if (isPowered) {
            Color neonColor = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_CYAN;
            double flicker = (isOverloaded && Math.random() > 0.8) ? 0.3 : 1.0;
            double pulse = (0.7 + 0.3 * Math.sin(time * 3)) * flicker;

            // Neon Bloom Aura (The "Previous" Glow)
            gc.setGlobalAlpha(0.25 * pulse);
            gc.setFill(neonColor);
            gc.fillOval(sx - 20, dy - 10, 40, 50);

            // Glowing Windows
            gc.setGlobalAlpha(1.0 * flicker);
            gc.setFill(isOverloaded ? Color.WHITE : ColorPalette.NEON_CYAN);
            for(int i=0; i<5; i++) {
                if ((i + (int)(time * 2)) % 2 == 0 || isOverloaded) {
                    gc.fillRect(dx + 3, dy + 4 + (i*6), 4, 3);
                    gc.fillRect(dx + 11, dy + 4 + (i*6), 4, 3);
                }
            }

            // Neon Trim
            gc.setStroke(neonColor);
            gc.setLineWidth(1.5);
            gc.strokeRect(dx, dy, w, h);
        }
        gc.restore();
    }

    @Override public int getDemand() { return 50; }
    @Override public int getOutput() { return 0; }
}
