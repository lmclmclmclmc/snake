package com.comp2042;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

/** Applies grid movement, growth and collision rules without JavaFX. */
final class SnakeMovement {
    enum Direction {
        UP(0, -1, 0), DOWN(0, 1, 180), LEFT(-1, 0, 270), RIGHT(1, 0, 90);

        private final int dx;
        private final int dy;
        private final int rotation;

        Direction(int dx, int dy, int rotation) {
            this.dx = dx;
            this.dy = dy;
            this.rotation = rotation;
        }
    }

    private final int width;
    private final int height;
    private final boolean wrapByDefault;
    private final Deque<Point> body = new ArrayDeque<>();
    private Point head;
    private Direction heading = Direction.LEFT;
    private boolean wrapPowerUp;
    private boolean dead;

    SnakeMovement(int width, int height, boolean wrapByDefault) {
        if (width < 3 || height < 1) {
            throw new IllegalArgumentException("Board must fit the starting snake");
        }
        this.width = width;
        this.height = height;
        this.wrapByDefault = wrapByDefault;
        head = new Point(width / 2, height / 2);
        body.addLast(new Point(head.x + 1, head.y));
    }

    /**
     * Attempts one movement tick, checking the destination after any wrap.
     *
     * @param direction direction to attempt
     * @param coin position of the current coin, or null if there is no coin
     * @param obstacles blocked grid cells
     * @return true when the move succeeds and consumes the coin
     */
    boolean advance(Direction direction, Point coin, Collection<Point> obstacles) {
        if (dead) {
            return false;
        }

        Point next = new Point(head.x + direction.dx, head.y + direction.dy);
        if (next.x < 0 || next.x >= width || next.y < 0 || next.y >= height) {
            if (!wrapByDefault && !wrapPowerUp) {
                dead = true;
                return false;
            }
            next.setLocation(Math.floorMod(next.x, width), Math.floorMod(next.y, height));
        }

        boolean grows = next.equals(coin);
        // The current tail cell is available only if the tail would move away this tick.
        boolean hitsBody = (grows || !next.equals(body.getFirst())) && body.contains(next);
        if (obstacles.contains(next) || hitsBody || next.equals(head)) {
            dead = true;
            return false;
        }

        if (!grows) {
            body.removeFirst();
        }
        body.addLast(head);
        head = next;
        heading = direction;
        return grows;
    }

    Point head() {
        return new Point(head);
    }

    List<Point> body() {
        return body.stream().map(Point::new).toList();
    }

    boolean occupies(Point cell) {
        return head.equals(cell) || body.contains(cell);
    }

    boolean isDead() {
        return dead;
    }

    void setWrapPowerUp(boolean active) {
        wrapPowerUp = active;
    }

    int rotation() {
        return heading.rotation;
    }
}
