package com.comp2042;

import com.comp2042.SnakeMovement.Direction;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;
import java.util.function.IntConsumer;

public class GameController {
    private Snake snek;
    private Coin coin;
    private final GameView gameView;
    private final Game game;
    private final Stage stage;
    private final Obstacle obstacle;
    private final Runnable returnToMenu;
    private final IntConsumer recordScore;
    private final CoinFactory coinFactory = new CoinFactory();
    private final Steering steering = new Steering();
    private final Timeline animation = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> tick()));

    public GameController(Snake snek, Coin coin, Game game, GameView gameView,
                          Obstacle obstacle, Stage stage, Runnable returnToMenu, IntConsumer recordScore) {
        this.snek = snek;
        this.coin = coin;
        this.gameView = gameView;
        this.game = game;
        this.stage = stage;
        this.obstacle = obstacle;
        this.returnToMenu = returnToMenu;
        this.recordScore = recordScore;
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.tickRate(0));
    }

    private void tick() {
        if (hasEnded()) {
            finishRun();
            return;
        }

        KeyCode previous = steering.heading();
        KeyCode direction = steering.advance();
        if (previous != direction) {
            gameView.recordTurn(snek.getHead(), previous, direction);
        }
        Direction movement = switch (direction) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
            default -> throw new IllegalStateException("Expected an arrow direction");
        };

        if (snek.advance(movement, coin)) {
            collectCoin();
            if (!game.gameWon()) {
                newCoin();
            }
        }

        refreshView();
        if (hasEnded()) {
            finishRun();
        }
    }

    public void handle(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code.isArrowKey()) {
            if (!hasEnded()) {
                steering.requestTurn(code);
                animation.play();
            }
            event.consume();
            return;
        }

        if (code == KeyCode.P) {
            if (animation.getStatus() == Animation.Status.RUNNING) {
                animation.pause();
            } else if (animation.getStatus() == Animation.Status.PAUSED) {
                animation.play();
            }
        }

        if (code == KeyCode.TAB) {
            reset();
            event.consume();
        }

        if (code == KeyCode.PLUS) {
            gameView.scaleToFullscreen(true, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 50;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 50;");
            gameView.scoreLabel.setStyle("-fx-font-size: 35;");
        }

        if (code == KeyCode.MINUS) {
            gameView.scaleToFullscreen(false, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 15;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 15;");
            gameView.scoreLabel.setStyle("-fx-font-size: 15;");
        }

        if (code == KeyCode.ESCAPE) {
            finishRun();
            returnToMenu.run();
            event.consume();
        }
    }

    /** Stops the clock and records the score before switching away from the run. */
    public void finishRun() {
        animation.stop();
        recordScore.accept(game.getScorePoints());
    }

    private boolean hasEnded() {
        return snek.getDead() || game.gameWon();
    }

    private void reset() {
        finishRun();
        obstacle.reset();
        game.resetScore();
        steering.reset();
        gameView.clearTurns();
        snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);
        animation.setRate(game.tickRate(0));
        refreshView();
    }

    private void refreshView() {
        gameView.drawGrid(coin, snek, obstacle);
        gameView.gameWon();
        gameView.gameOver(snek);
        gameView.updateScoreLabel();
    }

    private void collectCoin() {
        snek.ignoreBorders(coin instanceof BlueCoin);
        if (coin instanceof SilverCoin) {
            int obstacleCount = new Random().nextInt(game.getWidth() * game.getHeight() / 20) + 3;
            obstacle.newObstacles(snek, coin, obstacleCount);
            game.setObstacleCount(obstacle.getObstacles().size());
        }
        game.addPoints(coin.getPoints());
        game.incScore();
        double coinBoost = coin instanceof RainbowCoin rainbow ? rainbow.getSpeedBonus() : 0;
        animation.setRate(game.tickRate(coinBoost));
    }

    private void newCoin() {
        coin = coinFactory.create(game, snek, obstacle);
    }
}
