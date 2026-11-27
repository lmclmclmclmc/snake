package com.comp2042;

import javafx.scene.paint.Color;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Obstacle {
    private static final int MAX_OBSTACLES = 50;
    private final Color color = Color.BLACK;
    private final ArrayList<Point> obstacles = new ArrayList<>();
    private final List<Level.Wall> startingWalls;
    private final int x;
    private final int y;

    public Obstacle(int x, int y) {
        this(x, y, List.of());
    }

    /** Creates the obstacle field for the selected level. */
    public Obstacle(Level level) {
        this(level.width(), level.height(), level.walls());
    }

    private Obstacle(int x, int y, List<Level.Wall> startingWalls) {
        this.x = x;
        this.y = y;
        this.startingWalls = startingWalls;
        reset();
    }

    public void newObstacles(Snake snek, Coin coin, int amount) {
        if (amount <= 0 || obstacles.size() >= MAX_OBSTACLES) {
            return;
        }
        Random rnd = new Random();
        int targetSize = Math.min(MAX_OBSTACLES, obstacles.size() + amount);
        int remainingAttempts = x * y * 2;

        while (obstacles.size() < targetSize && remainingAttempts-- > 0) {
            Point candidate = randomPoint(rnd, x, y);
            boolean occupied = coin.getPos().equals(candidate)
                    || snek.occupies(candidate)
                    || obstacles.contains(candidate);
            if (!occupied) {
                obstacles.add(candidate);
            }
        }
    }

    static Point randomPoint(Random random, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }
        return new Point(random.nextInt(width), random.nextInt(height));
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Point> getObstacles() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
        for (Level.Wall wall : startingWalls) {
            obstacles.add(new Point(wall.x(), wall.y()));
        }
    }
}
