package com.comp2042;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Point;
import java.util.List;

/** Connects the snake's movement state to obstacles and its JavaFX sprites. */
public class Snake {
    private final SnakeMovement movement;
    private final Obstacle obstacle;
    private final ImageView imageHead = new ImageView(new Image("resources/views/res/snakehead.png"));
    private final ImageView imageBody = new ImageView(new Image("resources/views/res/snakebody.png"));
    private final ImageView imageTurn = new ImageView(new Image("resources/views/res/snaketurnleft.png"));
    private final ImageView imageTail = new ImageView(new Image("resources/views/res/snaketail.png"));

    public Snake(int width, int height, boolean noBorder, Obstacle obstacle) {
        this.obstacle = obstacle;
        this.movement = new SnakeMovement(width, height, noBorder);
    }

    boolean advance(SnakeMovement.Direction direction, Coin coin) {
        return movement.advance(direction, coin.getPos(), obstacle.getObstacles());
    }

    boolean occupies(Point cell) {
        return movement.occupies(cell);
    }

    public List<Point> getBody() {
        return movement.body();
    }

    public Point getHead() {
        return movement.head();
    }

    public boolean getDead() {
        return movement.isDead();
    }

    public void ignoreBorders(boolean active) {
        movement.setWrapPowerUp(active);
    }

    public ImageView getImageViewHead() {
        return imageHead;
    }

    public ImageView getImageViewBody() {
        return imageBody;
    }

    public ImageView getImageViewTurn() {
        return imageTurn;
    }

    public int getSnakeDir() {
        return movement.rotation();
    }

    public ImageView getImageViewTail() {
        return imageTail;
    }
}
