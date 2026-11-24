package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GamePaceTest {
    @ParameterizedTest
    @CsvSource({"0, 8.5", "2, 8.5", "3, 9.25", "6, 10.0", "11, 10.75", "12, 11.5", "60, 11.5"})
    void increasesOnlyAtThresholdsAndStopsAtTheCap(int coinsCollected, double rate) {
        assertEquals(rate, new GamePace(8.5, true).rateAt(coinsCollected, 0));
    }

    @Test
    void fixedSpeedRemainsTheSameAsCoinsAreCollected() {
        GamePace pace = new GamePace(6.5, false);
        assertEquals(6.5, pace.rateAt(20, 0));
        assertEquals(11.5, pace.rateAt(20, 5), "Rainbow coins still boost fixed-speed games");
    }

    @Test
    void temporaryBoostStacksWithProgressAndDoesNotChangeTheBaseRate() {
        GamePace pace = new GamePace(10.5, true);
        assertEquals(16.25, pace.rateAt(3, 5));
        assertEquals(11.25, pace.rateAt(4, 0));
    }

    @Test
    void paceUsesCoinCountRatherThanPointsAndResetsBetweenRuns() {
        Game game = new Game(Level.ORCHARD, true);
        game.addPoints(30);
        assertEquals(6.5, game.tickRate(0));
        game.incScore();
        game.incScore();
        game.incScore();
        assertEquals(7.25, game.tickRate(0));
        game.resetScore();
        assertEquals(6.5, game.tickRate(0));
    }
}
