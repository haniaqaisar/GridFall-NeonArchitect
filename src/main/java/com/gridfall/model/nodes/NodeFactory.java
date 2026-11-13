package com.gridfall.model.nodes;

public class NodeFactory {
    public static Node create(String type, int x, int y) {
        switch (type.toUpperCase()) {
            case "REACTOR":
                return new NuclearReactor(x, y);
            case "SOLAR":
                return new SolarFarm(x, y);
            case "BATTERY":
                return new BatteryBank(x, y);
            case "FACTORY":
                return new IndustrialZone(x, y);
            case "HOUSING":
                return new ResidentialBlock(x, y);
            case "TOWER":
                return new MegaTower(x, y);
            case "SIGN":
                return new NeonSign(x, y);
            case "LINE":
                return new TransmissionTower(x, y);
            default:
                return null;
        }
    }
}
