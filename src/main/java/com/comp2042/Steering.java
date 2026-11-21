package com.comp2042;

import javafx.scene.input.KeyCode;

/** Holds the movement direction and one pending turn for a single game. */
final class Steering {
    private KeyCode heading = KeyCode.LEFT;
    private KeyCode pending;

    KeyCode heading() {
        return heading;
    }

    void requestTurn(KeyCode direction) {
        if (pending == null && direction != heading && DirectionValidator.canTurn(heading, direction)) {
            pending = direction;
        }
    }

    KeyCode advance() {
        if (pending != null) {
            heading = pending;
            pending = null;
        }
        return heading;
    }

    void reset() {
        heading = KeyCode.LEFT;
        pending = null;
    }
}
