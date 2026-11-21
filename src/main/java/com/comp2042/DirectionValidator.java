package com.comp2042;

import javafx.scene.input.KeyCode;

/** Validates snake direction changes independently from the UI controller. */
public final class DirectionValidator {
    private DirectionValidator() {
    }

    /**
     * Returns whether a requested direction is an allowed turn.
     * A snake cannot reverse directly into its own body.
     *
     * @param current current movement direction
     * @param requested requested direction
     * @return true when the requested arrow direction is allowed
     */
    public static boolean canTurn(KeyCode current, KeyCode requested) {
        if (current == null || requested == null || !requested.isArrowKey()) {
            return false;
        }
        return switch (requested) {
            case LEFT -> current != KeyCode.RIGHT;
            case RIGHT -> current != KeyCode.LEFT;
            case UP -> current != KeyCode.DOWN;
            case DOWN -> current != KeyCode.UP;
            default -> false;
        };
    }
}
