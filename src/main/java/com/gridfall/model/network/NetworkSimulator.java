package com.gridfall.model.network;

import com.gridfall.model.grid.CityGrid;
import com.gridfall.model.nodes.*;
import javafx.scene.paint.Color;

import java.util.*;

public class NetworkSimulator {
    public List<PowerLine> activeConnections = new ArrayList<>();

    public static class PowerLine {
        public double x1, y1, x2, y2;
        public Color color;
        public PowerLine(double x1, double y1, double x2, double y2, Color color) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.color = color;
        }
    }

    public void recalculatePower(CityGrid grid, CityStats stats) {
        List<Node> allNodes = grid.getAllNodes();
        activeConnections.clear();

        // 1. Reset transient stats
        stats.totalOutput = 0;
        stats.totalDemand = 0;
        stats.activeBuildings = 0;
        stats.gridAlerts = 0;
        stats.loadRatio = 0.0;

        for (Node n : allNodes) {
            n.setPowered(false);
            n.setOverloaded(false);
        }

        // 2. BFS from reactors
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        for (Node n : allNodes) {
            if (n instanceof PowerSource && !n.isDamaged()) {
                queue.add(n);
                visited.add(n);
                n.setPowered(true);
            }
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Propagation ONLY happens through functional Reactors and Towers
            boolean propagates = (current instanceof PowerSource || current instanceof TransmissionTower) && !current.isDamaged();
            if (!propagates) continue;

            // 8-way search for better connectivity
            for (int dx8 = -1; dx8 <= 1; dx8++) {
                for (int dy8 = -1; dy8 <= 1; dy8++) {
                    if (dx8 == 0 && dy8 == 0) continue;

                    int nx = current.getX() + dx8;
                    int ny = current.getY() + dy8;
                    Node neighbor = grid.getNode(nx, ny);

                    if (neighbor != null && !neighbor.isDamaged() && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        neighbor.setPowered(true);
                        queue.add(neighbor);
                    }
                }
            }
        }

        // 3. Stats Check
        double supplyMod = stats.currentEventType.supplyMod;
        double demandMod = stats.currentEventType.demandMod;

        for (Node n : allNodes) {
            if (n.isPowered()) {
                stats.totalOutput += (int)(n.getOutput() * supplyMod);

                double nodeDemandMod = demandMod;
                if (stats.currentEventType == EventType.AI_SURGE && !(n instanceof ResidentialBlock)) {
                    nodeDemandMod = 1.0; // AI Surge only affects residences
                }

                stats.totalDemand += (int)(n.getDemand() * nodeDemandMod);
                if (n.getDemand() > 0) stats.activeBuildings++;
            }
        }

        if (stats.totalOutput > 0) {
            stats.loadRatio = (double) stats.totalDemand / stats.totalOutput;
        }

        // Stability Bonus from Batteries
        for (Node n : allNodes) {
            if (n instanceof BatteryBank && n.isPowered() && !n.isDamaged()) {
                stats.stability = Math.min(1.0, stats.stability + 0.005);
            }
        }

        if (stats.totalDemand > stats.totalOutput && stats.totalOutput > 0) {
            for (Node n : allNodes) {
                if (n.isPowered()) {
                    n.setOverloaded(true);
                    stats.gridAlerts++;
                }
            }
        }
    }
}
