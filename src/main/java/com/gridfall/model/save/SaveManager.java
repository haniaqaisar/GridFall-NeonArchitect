package com.gridfall.model.save;

import com.gridfall.model.grid.CityGrid;
import com.gridfall.model.nodes.Node;
import com.gridfall.model.nodes.NodeFactory;
import java.io.*;

public class SaveManager {
    public static void saveCity(CityGrid grid, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Node node : grid.getAllNodes()) {
                String type = node.getClass().getSimpleName().replace("NuclearReactor", "REACTOR")
                        .replace("ResidentialBlock", "HOUSING")
                        .replace("MegaTower", "TOWER")
                        .replace("NeonSign", "SIGN")
                        .replace("TransmissionTower", "LINE");
                writer.println(type + "," + node.getX() + "," + node.getY());
            }
            System.out.println("City saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCity(CityGrid grid, String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Node node = NodeFactory.create(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    if (node != null) grid.placeNode(node);
                }
            }
            System.out.println("City loaded from " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
