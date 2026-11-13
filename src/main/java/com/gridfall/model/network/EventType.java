package com.gridfall.model.network;

public enum EventType {
    NONE("OPERATIONAL", 0, 1.0, 1.0,
            "Systems normal.",
            "Expand the grid and save credits for the next surge."),

    HEATWAVE("HEATWAVE", 30, 0.9, 1.5,
            "Massive demand spike due to cooling systems. Reactors are slightly less efficient in the heat.",
            "Build more Power Sources before the grid collapses."),

    SOLAR_FLARE("SOLAR FLARE", 20, 0.5, 1.0,
            "Atmospheric interference is cutting power generation in half!",
            "Cut non-essential buildings (MegaTowers) or build massive power redundancy."),

    AI_SURGE("AI SURGE", 15, 1.0, 2.0,
            "Quantum computing clusters in residential blocks are drawing 2x power.",
            "Prioritize supporting Residential Blocks or let them brown out."),

    CYBER_ATTACK("CYBER ATTACK", 10, 0.8, 1.2,
            "A malicious worm is draining stability and stealing budget units.",
            "Expect budget loss and rapid grid decay. Build extra Transmission Towers to bypass loops.");

    public final String label;
    public final int duration;
    public final double supplyMod;
    public final double demandMod;
    public final String description;
    public final String strategy;

    EventType(String label, int duration, double supplyMod, double demandMod, String description, String strategy) {
        this.label = label;
        this.duration = duration;
        this.supplyMod = supplyMod;
        this.demandMod = demandMod;
        this.description = description;
        this.strategy = strategy;
    }
}
