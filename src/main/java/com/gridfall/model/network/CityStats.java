package com.gridfall.model.network;

public class CityStats {
    public int totalOutput = 0;
    public int totalDemand = 0;
    public int budget = 2000;
    public int activeBuildings = 0;
    public int gridAlerts = 0;
    public int lastIncome = 0;
    public double loadRatio = 0.0;
    public double stability = 1.0;
    public EventType currentEventType = EventType.NONE;
    public int eventTimeRemaining = 0;

    public void reset() {
        totalOutput = 0;
        totalDemand = 0;
        activeBuildings = 0;
        gridAlerts = 0;
        lastIncome = 0;
    }
}
