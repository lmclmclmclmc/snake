package com.comp2042;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectionValidatorTest {
    @Test
    void allowsPerpendicularTurns() {
        assertTrue(DirectionValidator.canTurn(KeyCode.LEFT, KeyCode.UP));
        assertTrue(DirectionValidator.canTurn(KeyCode.LEFT, KeyCode.DOWN));
    }

    @Test
    void rejectsEveryDirectReverse() {
        assertFalse(DirectionValidator.canTurn(KeyCode.LEFT, KeyCode.RIGHT));
        assertFalse(DirectionValidator.canTurn(KeyCode.RIGHT, KeyCode.LEFT));
        assertFalse(DirectionValidator.canTurn(KeyCode.UP, KeyCode.DOWN));
        assertFalse(DirectionValidator.canTurn(KeyCode.DOWN, KeyCode.UP));
    }

    @Test
    void rejectsNonArrowAndNullInput() {
        assertFalse(DirectionValidator.canTurn(KeyCode.LEFT, KeyCode.P));
        assertFalse(DirectionValidator.canTurn(null, KeyCode.LEFT));
        assertFalse(DirectionValidator.canTurn(KeyCode.LEFT, null));
    }
}
