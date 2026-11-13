package com.gridfall.model.grid;

import com.gridfall.model.nodes.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityGrid {
    private final int width, height;
    private final Map<String, Node> nodes = new HashMap<>();

    public CityGrid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private String getPosKey(int x, int y) {
        return x + "," + y;
    }

    public boolean placeNode(Node node) {
        if (node.getX() < 0 || node.getX() >= width || node.getY() < 0 || node.getY() >= height) return false;
        String key = getPosKey(node.getX(), node.getY());
        if (nodes.containsKey(key)) return false;
        nodes.put(key, node);
        return true;
    }

    public void removeNode(int x, int y) {
        nodes.remove(getPosKey(x, y));
    }

    public Node getNode(int x, int y) {
        return nodes.get(getPosKey(x, y));
    }

    public List<Node> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
