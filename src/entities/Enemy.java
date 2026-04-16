package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Enemy extends Entity {

    public int health = 100;

    public int currentFrame = 0;
    private double animTimer = 0;
    public double attackCooldown = 0.0;
    private final double TIME_PER_FRAME = 0.2; 
    private final int TOTAL_FRAMES = 4;
    
    private double moveSpeed = 2.0;
    private List<Node> currentPath = new ArrayList<>();

    public Enemy(double startX, double startY) {
        super(startX, startY);
    }

    public void updatePath(int[][] worldMap, int targetX, int targetY) {
        int startX = Math.max(0, Math.min(worldMap.length - 1, (int) this.posX));
        int startY = Math.max(0, Math.min(worldMap[0].length - 1, (int) this.posY));

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        boolean[][] closedSet = new boolean[worldMap.length][worldMap[0].length];

        Node startNode = new Node(startX, startY, null, 0, getHeuristic(startX, startY, targetX, targetY));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            closedSet[currentNode.x][currentNode.y] = true;

            if (currentNode.x == targetX && currentNode.y == targetY) {
                currentPath = retracePath(startNode, currentNode);
                return;
            }

            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            
            for (int[] dir : directions) {
                int neighborX = currentNode.x + dir[0];
                int neighborY = currentNode.y + dir[1];

                if (neighborX < 0 || neighborX >= worldMap.length || neighborY < 0 || neighborY >= worldMap[0].length) continue;
                if (worldMap[neighborX][neighborY] > 0 || closedSet[neighborX][neighborY]) continue;

                double newMovementCostToNeighbor = currentNode.gCost + 1;
                Node neighbor = new Node(neighborX, neighborY, currentNode, newMovementCostToNeighbor, getHeuristic(neighborX, neighborY, targetX, targetY));

                openSet.add(neighbor);
            }
        }
    }

    public void move(double deltaTime) {
        if (currentPath.isEmpty()) return;

        Node targetNode = currentPath.get(0);
        double targetX = targetNode.x + 0.5; 
        double targetY = targetNode.y + 0.5;

        double dirX = targetX - this.posX;
        double dirY = targetY - this.posY;
        double distance = Math.sqrt(dirX * dirX + dirY * dirY);

        if (distance > 0.1) {
            this.posX += (dirX / distance) * moveSpeed * deltaTime;
            this.posY += (dirY / distance) * moveSpeed * deltaTime;
        } else {
            currentPath.remove(0); 
        }

        animTimer += deltaTime;
        if (animTimer >= TIME_PER_FRAME) {
            animTimer = 0;
            currentFrame++;
            if (currentFrame >= TOTAL_FRAMES) {
                currentFrame = 0;
            }
        }
        
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
    }

    private List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;
        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private double getHeuristic(int nodeA_X, int nodeA_Y, int nodeB_X, int nodeB_Y) {
        return Math.abs(nodeA_X - nodeB_X) + Math.abs(nodeA_Y - nodeB_Y);
    }

    private class Node implements Comparable<Node> {
        public int x, y;
        public Node parent;
        public double gCost, hCost;

        public Node(int x, int y, Node parent, double gCost, double hCost) {
            this.x = x; this.y = y; this.parent = parent;
            this.gCost = gCost; this.hCost = hCost;
        }

        public double fCost() { return gCost + hCost; }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost(), other.fCost());
        }
    }

    public void clearPath() {
        currentPath.clear();
    }

}