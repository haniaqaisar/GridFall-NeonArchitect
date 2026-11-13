package com.gridfall.model.nodes;

public abstract class Consumer extends Node {
    protected int powerDemand;
    protected  int revenue;

    public Consumer(int x, int y, String name, int cost, int powerDemand, int revenue) {
        super(x, y, name, cost);
        this.powerDemand = powerDemand;
        this.revenue = revenue;
    }

    @Override public int getOutput() { return 0; }
    @Override public int getDemand() { return powerDemand; }
    @Override public int getRevenue() { return revenue; }
}
