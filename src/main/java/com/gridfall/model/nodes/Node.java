package com.gridfall.model.nodes;

import javafx.scene.canvas.GraphicsContext;

public abstract class Node {
    protected int x, y;
    protected String name;
    protected int cost;
    protected boolean isPowered = false;
    protected boolean isOverloaded = false;
    protected boolean isDamaged = false;
    protected double overloadTimer = 1.0; // 1.0 = healthy, 0.0 = destroyed

    public Node(int x, int y, String name, int cost) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.cost = cost;
    }

    public abstract void draw(GraphicsContext gc, double sx, double sy, double time);
    public abstract int getDemand();
    public abstract int getOutput();
    public int getRevenue() { return 0; } // Default: no revenue

    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public String getName() { return name; }
    public int getCost() { return cost; }
    public boolean isPowered() { return isPowered; }
    public void setPowered(boolean powered) { isPowered = powered; }
    public boolean isOverloaded() { return isOverloaded; }
    public void setOverloaded(boolean overloaded) { isOverloaded = overloaded; }
    public boolean isDamaged() { return isDamaged; }
    public void setDamaged(boolean damaged) { isDamaged = damaged; }
    public double getOverloadTimer() { return overloadTimer; }
    public void setOverloadTimer(double overloadTimer) { this.overloadTimer = overloadTimer; }
}
