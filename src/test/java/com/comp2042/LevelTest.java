package com.comp2042;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {
    @ParameterizedTest
    @EnumSource(Level.class)
    void wallsLeaveEveryFreeCellReachableFromTheSpawn(Level level) {
        Set<Level.Wall> walls = new HashSet<>(level.walls());
        assertEquals(level.walls().size(), walls.size(), "Duplicate wall");
        for (Level.Wall wall : walls) {
            assertTrue(onBoard(level, wall), "Wall outside the board");
        }

        int x = level.width() / 2;
        int y = level.height() / 2;
        Level.Wall head = new Level.Wall(x, y);
        assertFalse(walls.contains(head));
        assertFalse(walls.contains(new Level.Wall(x + 1, y)), "Tail must start in a free cell");
        assertFalse(walls.contains(new Level.Wall(x - 1, y)), "First left move must be safe");

        Set<Level.Wall> reached = new HashSet<>();
        ArrayDeque<Level.Wall> frontier = new ArrayDeque<>();
        reached.add(head);
        frontier.add(head);
        int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!frontier.isEmpty()) {
            Level.Wall current = frontier.remove();
            for (int[] move : moves) {
                Level.Wall next = new Level.Wall(current.x() + move[0], current.y() + move[1]);
                if (onBoard(level, next) && !walls.contains(next) && reached.add(next)) {
                    frontier.add(next);
                }
            }
        }
        assertEquals(level.width() * level.height() - walls.size(), reached.size());
    }

    @Test
    void levelsHaveDistinctCoinObjectivesAndSpeeds() {
        Game classic = new Game(Level.CLASSIC);
        Game orchard = new Game(Level.ORCHARD);
        Game maze = new Game(Level.MAZE);
        assertTrue(Level.CLASSIC.walls().isEmpty());
        assertFalse(Level.ORCHARD.walls().isEmpty());
        assertTrue(Level.MAZE.walls().size() > Level.ORCHARD.walls().size());
        assertEquals(398, classic.getCoinTarget());
        assertEquals(10, orchard.getCoinTarget());
        assertEquals(14, maze.getCoinTarget());
        assertTrue(orchard.getSpeed() < classic.getSpeed());
        assertTrue(classic.getSpeed() < maze.getSpeed());

        for (int i = 0; i < 9; i++) {
            orchard.incScore();
        }
        orchard.addPoints(150);
        assertFalse(orchard.gameWon(), "The objective counts coins, not their point value");
        orchard.incScore();
        assertTrue(orchard.gameWon());
        orchard.resetScore();
        assertEquals(0, orchard.getCollectedCoins());
        assertEquals(10, orchard.getCoinTarget());
        assertFalse(orchard.gameWon());
    }

    @Test
    void resetRestoresLevelWallsFromTheOriginalLayout() {
        Obstacle obstacles = new Obstacle(Level.MAZE);
        Level.Wall firstWall = Level.MAZE.walls().get(0);
        Point viewPoint = obstacles.getObstacles().get(0);
        viewPoint.move(0, 0);
        obstacles.reset();
        assertEquals(Level.MAZE.walls().size(), obstacles.getObstacles().size());
        assertEquals(new Point(firstWall.x(), firstWall.y()), obstacles.getObstacles().get(0));
    }

    private boolean onBoard(Level level, Level.Wall cell) {
        return cell.x() >= 0 && cell.x() < level.width() && cell.y() >= 0 && cell.y() < level.height();
    }
}
