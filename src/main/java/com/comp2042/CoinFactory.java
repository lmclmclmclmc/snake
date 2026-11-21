package com.comp2042;

import java.util.Objects;
import java.util.Random;

/** Creates normal and power-up coins without exposing selection logic to the controller. */
public class CoinFactory {
    enum CoinType {
        NORMAL, GOLD, RAINBOW, BLUE, SILVER
    }

    private final Random random;

    public CoinFactory() {
        this(new Random());
    }

    CoinFactory(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Creates a coin suitable for the current game configuration.
     *
     * @param game current game configuration
     * @param snake current snake
     * @param obstacle current obstacle collection
     * @return a newly positioned coin
     */
    public Coin create(Game game, Snake snake, Obstacle obstacle) {
        if (!game.isPowerUp()) {
            return normal(game, snake, obstacle);
        }

        CoinType type = selectType(random.nextInt(12), obstacle.getObstacles().isEmpty());
        return switch (type) {
            case GOLD -> new GoldCoin(game.getWidth(), game.getHeight(), snake, obstacle);
            case RAINBOW -> new RainbowCoin(game.getWidth(), game.getHeight(), snake, obstacle);
            case BLUE -> new BlueCoin(game.getWidth(), game.getHeight(), snake, obstacle);
            case SILVER -> new SilverCoin(game.getWidth(), game.getHeight(), snake, obstacle);
            case NORMAL -> normal(game, snake, obstacle);
        };
    }

    static CoinType selectType(int roll, boolean obstacleFieldEmpty) {
        return switch (roll) {
            case 1 -> CoinType.GOLD;
            case 2 -> CoinType.RAINBOW;
            case 3 -> CoinType.BLUE;
            case 4 -> obstacleFieldEmpty ? CoinType.SILVER : CoinType.NORMAL;
            default -> CoinType.NORMAL;
        };
    }

    private Coin normal(Game game, Snake snake, Obstacle obstacle) {
        return new Coin(game.getWidth(), game.getHeight(), snake, obstacle);
    }
}
