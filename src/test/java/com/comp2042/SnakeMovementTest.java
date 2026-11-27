package com.comp2042;

import java.awt.Point;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.comp2042.SnakeMovement.Direction.DOWN;
import static com.comp2042.SnakeMovement.Direction.LEFT;
import static com.comp2042.SnakeMovement.Direction.RIGHT;
import static com.comp2042.SnakeMovement.Direction.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeMovementTest {
    private final SnakeMovement snake = new SnakeMovement(8, 8, false);

    @Test
    void ordinaryMoveReplacesTheTailAndEatingKeepsIt() {
        assertFalse(snake.advance(LEFT, null, List.of()));
        assertEquals(new Point(3, 4), snake.head());
        assertEquals(List.of(new Point(4, 4)), snake.body());

        assertTrue(snake.advance(UP, new Point(3, 3), List.of()));
        assertEquals(new Point(3, 3), snake.head());
        assertEquals(List.of(new Point(4, 4), new Point(3, 4)), snake.body());
        assertEquals(0, snake.rotation());
    }

    @Test
    void solidBorderEndsTheGameWithoutChangingTheLastValidPosition() {
        moveToLeftEdge(snake);
        List<Point> oldBody = snake.body();
        assertFalse(snake.advance(LEFT, null, List.of()));
        assertTrue(snake.isDead());
        assertEquals(new Point(0, 4), snake.head());
        assertEquals(oldBody, snake.body());

        assertFalse(snake.advance(UP, new Point(0, 3), List.of()));
        assertEquals(new Point(0, 4), snake.head(), "A finished snake cannot move or score");
    }

    @Test
    void temporaryWrapCanCrossTheEdgeAndBeDisabledAgain() {
        moveToLeftEdge(snake);
        snake.setWrapPowerUp(true);
        assertTrue(snake.advance(LEFT, new Point(7, 4), List.of()));
        assertEquals(new Point(7, 4), snake.head());
        assertFalse(snake.isDead());

        snake.setWrapPowerUp(false);
        assertFalse(snake.advance(RIGHT, null, List.of()));
        assertTrue(snake.isDead());
        assertEquals(new Point(7, 4), snake.head());
    }

    @Test
    void wrapStillChecksTheDestinationForObstacles() {
        SnakeMovement wrapping = new SnakeMovement(8, 8, true);
        moveToLeftEdge(wrapping);
        assertFalse(wrapping.advance(LEFT, null, List.of(new Point(7, 4))));
        assertTrue(wrapping.isDead());
        assertEquals(new Point(0, 4), wrapping.head());
    }

    @Test
    void obstacleTakesPrecedenceOverCoinCollection() {
        Point blocked = new Point(3, 4);
        assertFalse(snake.advance(LEFT, blocked, List.of(blocked)));
        assertTrue(snake.isDead());
        assertEquals(new Point(4, 4), snake.head());
        assertEquals(List.of(new Point(5, 4)), snake.body());
    }

    @Test
    void enteringABodyCellThatWillRemainOccupiedEndsTheRun() {
        assertTrue(snake.advance(LEFT, new Point(3, 4), List.of()));
        assertTrue(snake.advance(UP, new Point(3, 3), List.of()));
        assertTrue(snake.advance(RIGHT, new Point(4, 3), List.of()));
        assertFalse(snake.advance(DOWN, null, List.of()));
        assertTrue(snake.isDead());
        assertEquals(new Point(4, 3), snake.head());
    }

    @Test
    void enteringAVacatingTailCellIsAllowed() {
        moveHeadNextToTail();
        assertFalse(snake.advance(DOWN, null, List.of()));
        assertFalse(snake.isDead());
        assertEquals(new Point(5, 4), snake.head());
        assertFalse(snake.body().contains(snake.head()));
    }

    @Test
    void enteringTheTailCellWhileGrowingIsACollision() {
        moveHeadNextToTail();
        assertFalse(snake.advance(DOWN, new Point(5, 4), List.of()));
        assertTrue(snake.isDead());
        assertEquals(new Point(5, 3), snake.head());
    }

    private void moveHeadNextToTail() {
        assertTrue(snake.advance(UP, new Point(4, 3), List.of()));
        assertTrue(snake.advance(RIGHT, new Point(5, 3), List.of()));
    }

    private void moveToLeftEdge(SnakeMovement movement) {
        for (int step = 0; step < 4; step++) {
            assertFalse(movement.advance(LEFT, null, List.of()));
            assertFalse(movement.isDead());
        }
        assertEquals(new Point(0, 4), movement.head());
    }
}
