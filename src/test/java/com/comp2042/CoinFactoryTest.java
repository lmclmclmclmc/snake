package com.comp2042;

import org.junit.jupiter.api.Test;

import static com.comp2042.CoinFactory.CoinType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoinFactoryTest {
    @Test
    void everyPowerUpHasAReachableRoll() {
        assertEquals(GOLD, CoinFactory.selectType(1, true));
        assertEquals(RAINBOW, CoinFactory.selectType(2, true));
        assertEquals(BLUE, CoinFactory.selectType(3, true));
        assertEquals(SILVER, CoinFactory.selectType(4, true));
    }

    @Test
    void silverCoinRequiresAnEmptyObstacleField() {
        assertEquals(NORMAL, CoinFactory.selectType(4, false));
    }

    @Test
    void otherRollsCreateNormalCoins() {
        assertEquals(NORMAL, CoinFactory.selectType(0, true));
        assertEquals(NORMAL, CoinFactory.selectType(11, true));
    }
}
