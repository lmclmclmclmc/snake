package com.comp2042;

import javafx.scene.paint.Color;

public class Game {
    private Color color = Color.web("feffe4");
    private final int width;
    private final int height;
    private int score;
    private int maxScore;
    private final int coinGoal;
    private final int startingObstacleCount;
    private final double multiplier;
    private int scorePoints;
    private final GamePace pace;
    private final boolean powerUp;
    private final boolean noBorder;

    public Game(int x, int y, boolean powerUp, char difficulty) {
        this(x, y, powerUp, difficulty == 'E', new GamePace(switch (difficulty) {
            case 'E', 'N' -> 8.5;
            case 'H' -> 13;
            default -> 0;
        }, false), Integer.MAX_VALUE, 0);
    }

    /** Creates a game with the selected level's dimensions, speed and coin objective. */
    public Game(Level level) {
        this(level, false);
    }

    /**
     * Creates a game with the selected level and movement pace option.
     *
     * @param level selected board and coin objective
     * @param adaptiveSpeed whether the pace increases as coins are collected
     */
    public Game(Level level, boolean adaptiveSpeed) {
        this(level.width(), level.height(), true, false, new GamePace(level.speed(), adaptiveSpeed), level.coinGoal(),
                level.walls().size());
    }

    private Game(int width, int height, boolean powerUp, boolean noBorder, GamePace pace,
                 int coinGoal, int startingObstacleCount) {
        this.width = width;
        this.height = height;
        this.powerUp = powerUp;
        this.noBorder = noBorder;
        this.pace = pace;
        this.coinGoal = coinGoal;
        this.startingObstacleCount = startingObstacleCount;
        multiplier = height > 50 ? 10 : 20;
        setObstacleCount(startingObstacleCount);
    }

    public boolean gameWon() {
        return score == maxScore;
    }

    public void incScore() {
        score++;
    }

    /**
     * Caps the level's coin objective at the capacity of the current board.
     *
     * @param count number of cells occupied by obstacles
     */
    public void setObstacleCount(int count) {
        maxScore = Math.min(coinGoal, width * height - 2 - count);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetScore() {
        score = 0;
        scorePoints = 0;
        setObstacleCount(startingObstacleCount);
    }

    public Color getColor() {
        return color;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void addPoints(int n) {
        scorePoints += n;
    }

    public int getScorePoints() {
        return scorePoints;
    }

    /** Returns the coins collected in the current game. */
    public int getCollectedCoins() {
        return score;
    }

    /** Returns the current coin target for winning. */
    public int getCoinTarget() {
        return maxScore;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    /**
     * Calculates the current movement rate from the run's coin count.
     *
     * @param coinBoost temporary extra speed from a collected coin, or zero
     * @return current moves per second
     */
    public double tickRate(double coinBoost) {
        return pace.rateAt(score, coinBoost);
    }

    public boolean isNoBorder() {
        return noBorder;
    }
}
