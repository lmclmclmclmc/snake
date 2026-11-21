package com.comp2042;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class GameController {
    private Snake snek;
    private Coin coin;
    private final GameView gameView;
    private final Game game;
    private final Stage stage;
    private final Obstacle obstacle;
    private final Runnable returnToMenu;
    private final CoinFactory coinFactory = new CoinFactory();
    private final Steering steering = new Steering();
    private final Timeline animation = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> tick()));

    public GameController(Snake snek, Coin coin, Game game, GameView gameView,
                          Obstacle obstacle, Stage stage, Runnable returnToMenu) {
        this.snek = snek;
        this.coin = coin;
        this.gameView = gameView;
        this.game = game;
        this.stage = stage;
        this.obstacle = obstacle;
        this.returnToMenu = returnToMenu;
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.getSpeed());
    }

    private void tick() {
        if (hasEnded()) {
            stop();
            return;
        }

        KeyCode previous = steering.heading();
        KeyCode direction = steering.advance();
        if (previous != direction) {
            gameView.recordTurn(snek.getHead(), previous, direction);
        }
        snek.move(switch (direction) {
            case UP -> 'U';
            case DOWN -> 'D';
            case LEFT -> 'L';
            case RIGHT -> 'R';
            default -> throw new IllegalStateException("Expected an arrow direction");
        });

        if (!snek.getDead() && snek.ateCoin(coin)) {
            powerCoin();
            game.incScore();
            if (!game.gameWon()) {
                newCoin();
            }
        }

        refreshView();
        if (hasEnded()) {
            stop();
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
            stop();
            returnToMenu.run();
            event.consume();
        }
    }

    /** Stops updates when gameplay ends or the game window closes. */
    public void stop() {
        animation.stop();
    }

    private boolean hasEnded() {
        return snek.getDead() || game.gameWon();
    }

    private void reset() {
        stop();
        obstacle.reset();
        game.resetScore();
        steering.reset();
        gameView.clearTurns();
        snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);
        animation.setRate(game.getSpeed());
        refreshView();
    }

    private void refreshView() {
        gameView.drawGrid(coin, snek, obstacle);
        gameView.gameWon();
        gameView.gameOver(snek);
        gameView.updateScoreLabel();
    }

    private void powerCoin() {
        animation.setRate(game.getSpeed());
        snek.ignoreBorders(false);
        if (coin.getClass().equals(RainbowCoin.class)) {
            RainbowCoin tempCoin = (RainbowCoin) coin;
            animation.setRate(game.getSpeed() + tempCoin.getSpeedBonus());
        } else if (coin.getClass().equals(SilverCoin.class)){
            Random rnd = new Random();
            obstacle.newObstacles(snek, coin, rnd.nextInt(game.getWidth() * game.getHeight()/ 20) +3);
            game.setObstacleCount(obstacle.getObstacles().size());
        } else if (coin.getClass().equals(BlueCoin.class)) {
            snek.ignoreBorders(true);
        }
        game.addPoints(coin.getPoints());
    }

    private void newCoin() {
        coin = coinFactory.create(game, snek, obstacle);
    }
}
