package com.comp2042;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SteeringTest {
    @Test
    void appliesAtMostOneTurnBeforeEachMove() {
        Steering steering = new Steering();
        steering.requestTurn(KeyCode.UP);
        steering.requestTurn(KeyCode.RIGHT);
        assertEquals(KeyCode.LEFT, steering.heading());
        assertEquals(KeyCode.UP, steering.advance());

        steering.requestTurn(KeyCode.RIGHT);
        assertEquals(KeyCode.RIGHT, steering.advance());
    }

    @Test
    void repeatedDirectionDoesNotBlockAnActualTurn() {
        Steering steering = new Steering();
        steering.requestTurn(KeyCode.LEFT);
        steering.requestTurn(KeyCode.UP);
        assertEquals(KeyCode.UP, steering.advance());
    }

    @Test
    void resetRestoresInitialHeadingAndClearsPendingTurn() {
        Steering steering = new Steering();
        steering.requestTurn(KeyCode.UP);
        steering.advance();
        steering.requestTurn(KeyCode.RIGHT);
        steering.reset();
        assertEquals(KeyCode.LEFT, steering.advance());
    }

    @Test
    void separateGamesDoNotShareDirection() {
        Steering first = new Steering();
        Steering second = new Steering();
        first.requestTurn(KeyCode.UP);
        assertEquals(KeyCode.UP, first.advance());
        assertEquals(KeyCode.LEFT, second.advance());
    }
}
