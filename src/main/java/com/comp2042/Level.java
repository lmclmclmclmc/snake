package com.comp2042;

import java.util.ArrayList;
import java.util.List;

/** The selectable boards, starting speeds, and win objectives. */
public enum Level {
    CLASSIC("Classic", 20, 20, 8.5, Integer.MAX_VALUE, "Fill the original open board."),
    ORCHARD("Orchard", 18, 16, 6.5, 10, "Collect 10 coins and steer through two rows of hedges."),
    MAZE("Maze", 22, 18, 10.5, 14, "Collect 14 coins on a faster board with narrow passages.");

    private final String title;
    private final int width;
    private final int height;
    private final double speed;
    private final int coinGoal;
    private final String description;

    Level(String title, int width, int height, double speed, int coinGoal, String description) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.coinGoal = coinGoal;
        this.description = description;
    }

    int width() {
        return width;
    }

    int height() {
        return height;
    }

    double speed() {
        return speed;
    }

    /** Classic leaves the goal capped only by free board capacity. */
    int coinGoal() {
        return coinGoal;
    }

    String description() {
        return description;
    }

    record Wall(int x, int y) {}

    List<Wall> walls() {
        List<Wall> walls = new ArrayList<>();
        switch (this) {
            case CLASSIC -> {
                return List.of();
            }
            case ORCHARD -> {
                verticalWall(walls, 4, 3, 12, 7);
                verticalWall(walls, 13, 3, 12, 7);
            }
            case MAZE -> {
                horizontalWall(walls, 4, 4, 17, 9);
                horizontalWall(walls, 13, 4, 17, 11);
                verticalWall(walls, 6, 5, 12, 8);
                verticalWall(walls, 15, 5, 12, 9);
            }
        }
        return List.copyOf(walls);
    }

    private static void verticalWall(List<Wall> walls, int x, int start, int end, int gap) {
        for (int y = start; y <= end; y++) {
            if (y != gap && y != gap + 1) {
                walls.add(new Wall(x, y));
            }
        }
    }

    private static void horizontalWall(List<Wall> walls, int y, int start, int end, int gap) {
        for (int x = start; x <= end; x++) {
            if (x != gap && x != gap + 1) {
                walls.add(new Wall(x, y));
            }
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
