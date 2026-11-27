package com.comp2042;

import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class GameView {

    public Canvas gridCanvas;
    private GraphicsContext gc;

    public Label gameOverLabel;
    public Label gameWonLabel;
    public Label scoreLabel;
    private boolean gameOverVis = false;
    private boolean gameWonVis = false;
    private int bestScore;

    private final Game game;
    private double initX;
    private double initY;
    private double fullScale = 1;
    private boolean fullsize = false;
    private final int[][] turnArray;

    public GameView(Game game, int bestScore) {
        this.game = game;
        this.bestScore = bestScore;
        this.turnArray = new int[game.getWidth()][game.getHeight()];
    }

    public void makeScene(Scene scene, Snake snek, Coin coin, Obstacle obstacle) {
        Scene gameScene = scene;
        gridCanvas = new Canvas(gameScene.getWidth(), gameScene.getHeight());
        initX = scene.getWidth();
        initY = scene.getHeight();
        gc = gridCanvas.getGraphicsContext2D();
        drawGrid(coin, snek, obstacle);

        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setId("gameOverLabel");
        String css = "resources/views/styling.css";
        gameOverLabel.getStylesheets().add(css);
        gameOverLabel.setVisible(false);

        gameWonLabel = new Label("YOU WON!");
        gameWonLabel.setId("gameWonLabel");
        gameWonLabel.getStylesheets().add(css);
        gameWonLabel.setVisible(false);

        scoreLabel = new Label();
        scoreLabel.setId("scoreLabel");
        scoreLabel.getStylesheets().add(css);
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setVisible(true);
        updateScoreLabel();
    }

    public void scaleToFullscreen(boolean fullScreen, Stage primaryStage, Obstacle obstacle, Snake snek, Coin coin) {
        primaryStage.setFullScreen(fullScreen);

        double xScaling = Screen.getPrimary().getVisualBounds().getHeight() / initY;
        double yScaling = Screen.getPrimary().getVisualBounds().getWidth() / initX;

        if (fullScreen && !fullsize) {
            if (xScaling < yScaling) {
                fullScale = xScaling;
            } else {
                fullScale = yScaling;
            }
            gridCanvas.setWidth(gridCanvas.getWidth() * fullScale);
            gridCanvas.setHeight(gridCanvas.getHeight() * fullScale);
            fullsize = true;
        } else if (!fullScreen) {
            fullScale = 1;
            gridCanvas.setWidth(initX);
            gridCanvas.setHeight(initY);
            fullsize = false;
        }

        gameOverLabel.setFont(new Font("Arial", 25 * fullScale));
        gameWonLabel.setFont(new Font("Arial", 25 * fullScale));
        drawGrid(coin, snek, obstacle);
    }

    public void drawGrid(Coin coin, Snake snek, Obstacle obstacle) {
        gc.setFill(game.getColor());
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        gc.drawImage(coin.getImage(), (coin.getPos().x * game.getMultiplier()) * fullScale, (coin.getPos().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale - 3 * fullScale, game.getMultiplier() * fullScale - 3 * fullScale);

        List<Point> body = snek.getBody();
        for (int index = 0; index < body.size(); index++) {
            drawBody(snek, body, index);
        }
        Point tail = body.get(0);
        turnArray[tail.x][tail.y] = 0;
        drawHead(snek);

        gc.setFill(obstacle.getColor());
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * game.getMultiplier() * fullScale, point.y * game.getMultiplier() * fullScale, game.getMultiplier() * fullScale, game.getMultiplier() * fullScale);
        }
    }

    private void drawHead(Snake snek) {
        drawSprite(snek.getImageViewHead(), snek.getHead(), snek.getSnakeDir());
    }

    private void drawBody(Snake snek, List<Point> body, int index) {
        Point point = body.get(index);
        int turn = turnArray[point.x][point.y];
        if (body.size() == 1) {
            drawSprite(snek.getImageViewTail(), point, snek.getSnakeDir());
        } else if (index == 0) {
            drawSprite(snek.getImageViewTail(), point, tailRotation(point, body.get(1)));
        } else if (turn != 0) {
            drawSprite(snek.getImageViewTurn(), point, 90 * turn);
        } else {
            int rotation = point.y == body.get(index - 1).y ? 270 : 0;
            drawSprite(snek.getImageViewBody(), point, rotation);
        }
    }

    private int tailRotation(Point tail, Point adjacent) {
        if (tail.x == adjacent.x) {
            return Math.floorMod(adjacent.y - tail.y, game.getHeight()) == 1 ? 180 : 0;
        }
        return Math.floorMod(adjacent.x - tail.x, game.getWidth()) == 1 ? 90 : 270;
    }

    private void drawSprite(ImageView sprite, Point point, int rotation) {
        sprite.setRotate(rotation);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = sprite.snapshot(params, null);
        double cellSize = game.getMultiplier() * fullScale;
        double spriteSize = cellSize * fullScale;
        gc.drawImage(rotatedImage, point.x * cellSize, point.y * cellSize, spriteSize, spriteSize);
    }

    public void recordTurn(Point head, KeyCode previous, KeyCode direction) {
        turnArray[head.x][head.y] = switch (previous) {
            case LEFT -> direction == KeyCode.UP ? 2 : 3;
            case DOWN -> direction == KeyCode.RIGHT ? 2 : 1;
            case RIGHT -> direction == KeyCode.UP ? 1 : 4;
            case UP -> direction == KeyCode.RIGHT ? 3 : 4;
            default -> throw new IllegalArgumentException("Expected an arrow direction");
        };
    }

    /** Clears sprite turn markers from the previous run. */
    public void clearTurns() {
        for (int[] column : turnArray) {
            Arrays.fill(column, 0);
        }
    }

    public void gameOver(Snake snek) {
        if (snek.getDead() && !gameOverVis) {
            gameOverVis = true;
            gameOverLabel.setVisible(true);
        } else if (!snek.getDead() && gameOverVis) {
            gameOverLabel.setVisible(false);
            gameOverVis = false;
        }
    }

    public void gameWon() {
        if (game.gameWon() && !gameWonVis) {
            gameWonVis = true;
            gameWonLabel.setVisible(true);
        } else if (!game.gameWon() && gameWonVis) {
            gameWonVis = false;
            gameWonLabel.setVisible(false);
        }
    }

    public void updateScoreLabel() {
        int score = game.getScorePoints();
        bestScore = Math.max(bestScore, score);
        scoreLabel.setText("Score " + score + "  Best " + bestScore + "  "
                + game.getCollectedCoins() + "/" + game.getCoinTarget() + " coins");
    }

}
