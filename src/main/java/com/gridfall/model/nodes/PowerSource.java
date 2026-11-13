package com.gridfall.model.nodes;

public abstract class PowerSource extends Node {
    protected int powerOutput;

    public PowerSource(int x, int y, String name, int cost, int powerOutput) {
        super(x, y, name, cost);
        this.powerOutput = powerOutput;
        this.isPowered = true; // Sources are always self-powered
    }

    @Override public int getOutput() { return powerOutput; }
    @Override public int getDemand() { return 0; }
}
