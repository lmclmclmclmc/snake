package com.comp2042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {
    @Test
    void everyObstacleRemovesOneCellFromTheWinTarget() {
        Game game = new Game(4, 3, true, 'N');
        game.setObstacleCount(2);
        collectCoins(game, 7);
        assertFalse(game.gameWon());
        game.incScore();
        assertTrue(game.gameWon());
    }

    @Test
    void resetRestoresTheWinTargetForAnEmptyBoard() {
        Game game = new Game(4, 3, true, 'N');
        game.setObstacleCount(2);
        collectCoins(game, 8);
        game.addPoints(40);
        assertTrue(game.gameWon());

        game.resetScore();
        assertEquals(0, game.getScorePoints());
        assertFalse(game.gameWon());
        collectCoins(game, 9);
        assertFalse(game.gameWon());
        game.incScore();
        assertTrue(game.gameWon());
    }

    private void collectCoins(Game game, int count) {
        for (int i = 0; i < count; i++) {
            game.incScore();
        }
    }
}
