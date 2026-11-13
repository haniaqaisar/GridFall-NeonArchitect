package com.gridfall.model.nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.gridfall.util.ColorPalette;

public class MegaTower extends Consumer {
    public MegaTower(int x, int y) {
        super(x, y, "Mega Tower", 250, 400, 100);
    }

    @Override
    public void draw(GraphicsContext gc, double sx, double sy, double time) {
        gc.save();
        sy -= 5;

        // Rectangular Skyscraper Design
        double w = 15;
        double h = 55;
        double dx = sx - w/2;
        double dy = sy - h;

        // 3-Tone Architectural Design
        gc.setGlobalAlpha(1.0);

        Color baseNeon = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_PURPLE;
        Color mainColor = isDamaged ? Color.rgb(40, 40, 45) : (isPowered ? baseNeon.darker().darker() : ColorPalette.UNPOWERED_STEEL);
        Color sideColor = isDamaged ? Color.rgb(30, 30, 35) : (isPowered ? mainColor.darker() : ColorPalette.UNPOWERED_NAVY);
        Color roofColor = isDamaged ? Color.rgb(50, 50, 55) : (isPowered ? mainColor.brighter() : ColorPalette.UNPOWERED_GRAPHITE);

        // Core Structure
        gc.setFill(mainColor);
        gc.fillRect(dx, dy, w, h);

        // Depth/Side
        gc.setFill(sideColor);
        gc.fillPolygon(new double[]{dx+w, dx+w+2, dx+w+2, dx+w}, new double[]{dy, dy+2, dy+h+2, dy+h}, 4);

        // Roof Top
        gc.setFill(roofColor);
        gc.fillRect(dx, dy, w, 2);

        // Window Details (Grid)
        gc.setFill(isDamaged ? Color.BLACK : ColorPalette.UNPOWERED_WINDOW);
        for (int i = 0; i < 12; i++) {
            // Subtle window light for unpowered buildings
            if (!isPowered && !isDamaged && (i + x) % 6 == 0) {
                gc.setGlobalAlpha(0.3);
                gc.setFill(Color.rgb(120, 130, 150));
            } else {
                gc.setGlobalAlpha(1.0);
                gc.setFill(isDamaged ? Color.BLACK : ColorPalette.UNPOWERED_WINDOW);
            }

            double wy = dy + 4 + (i * 5.5);
            gc.fillRect(dx + 2, wy, w - 4, 2);
        }
        gc.setGlobalAlpha(1.0);

        if (isDamaged) {
            gc.setGlobalAlpha(0.6);
            gc.setFill(Color.gray(0.1));
            for(int i=0; i<4; i++) {
                double smokeX = sx + Math.cos(time * 0.4 + i) * 6;
                double smokeY = dy + 10 - (time * 15 + i * 20) % 50;
                gc.fillOval(smokeX - 5, smokeY - 5, 10, 10);
            }
        } else if (isPowered) {
            Color towerColor = isOverloaded ? ColorPalette.NEON_RED : ColorPalette.NEON_PURPLE;
            double pulse = (0.7 + 0.3 * Math.sin(time * 3));

            // Neon strips (Professional architect look)
            gc.setStroke(towerColor);
            gc.setLineWidth(1.5);
            gc.strokeLine(dx, dy, dx, sy);
            gc.strokeLine(dx + w, dy, dx + w, sy);

            // Internal Glow
            gc.setGlobalAlpha(0.3 * pulse);
            gc.setFill(towerColor);
            gc.fillRect(dx + 2, dy, w - 4, h);

            // Random lit windows
            gc.setGlobalAlpha(0.8);
            gc.setFill(isOverloaded ? Color.WHITE : towerColor);
            for (int i = 0; i < 12; i++) {
                if ((i + (int)(time * 1.5)) % 5 == 0) {
                    double wy = dy + 4 + (i * 5.5);
                    gc.fillRect(dx + 2, wy, w - 4, 1.5);
                }
            }

            // Roof Beacon
            gc.setGlobalAlpha(0.8 + 0.2 * Math.sin(time * 8));
            gc.setFill(Color.WHITE);
            gc.fillOval(sx - 3, dy - 6, 6, 6);
        }
        gc.restore();
    }
}
