package com.comp2042;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObstacleTest {
    @Test
    void generatedCoordinatesAlwaysStayInsideBoard() {
        Random random = new Random(42);
        for (int i = 0; i < 1_000; i++) {
            Point point = Obstacle.randomPoint(random, 20, 15);
            assertTrue(point.x >= 0 && point.x < 20);
            assertTrue(point.y >= 0 && point.y < 15);
        }
    }

    @Test
    void invalidBoardDimensionsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Obstacle.randomPoint(new Random(), 0, 10));
    }
}
