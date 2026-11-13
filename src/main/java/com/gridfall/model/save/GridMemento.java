package com.gridfall.model.save;

import com.gridfall.model.nodes.Node;
import com.gridfall.model.nodes.NodeFactory;
import java.util.ArrayList;
import java.util.List;

public class GridMemento {
    private final List<String> nodeData;

    public GridMemento(List<Node> nodes) {
        this.nodeData = new ArrayList<>();
        for (Node node : nodes) {
            String type = node.getClass().getSimpleName().replace("NuclearReactor", "REACTOR")
                    .replace("ResidentialBlock", "HOUSING")
                    .replace("MegaTower", "TOWER")
                    .replace("NeonSign", "SIGN")
                    .replace("TransmissionTower", "LINE");
            nodeData.add(type + "," + node.getX() + "," + node.getY());
        }
    }

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<>();
        for (String data : nodeData) {
            String[] parts = data.split(",");
            Node node = NodeFactory.create(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            if (node != null) nodes.add(node);
        }
        return nodes;
    }
}
