package com.gridfall.util;

import javafx.geometry.Point2D;

public class IsoMath {
    public static final double TILE_WIDTH = 100;
    public static final double TILE_HEIGHT = 50;

    public static Point2D gridToScreen(int x, int y, double offsetX, double offsetY) {
        double sx = (x - y) * (TILE_WIDTH / 2) + offsetX;
        double sy = (x + y) * (TILE_HEIGHT / 2) + offsetY;
        return new Point2D(sx, sy);
    }

    public static int[] screenToGrid(double sx, double sy, double offsetX, double offsetY) {
        double relX = sx - offsetX;
        double relY = sy - offsetY;
        int x = (int) Math.floor((relX / (TILE_WIDTH / 2) + relY / (TILE_HEIGHT / 2)) / 2);
        int y = (int) Math.floor((relY / (TILE_HEIGHT / 2) - relX / (TILE_WIDTH / 2)) / 2);
        return new int[]{x, y};
    }
}
