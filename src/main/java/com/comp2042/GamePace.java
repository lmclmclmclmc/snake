package com.comp2042;

/** Calculates the movement rate for a run without depending on JavaFX. */
record GamePace(double baseRate, boolean adaptive) {
    private static final int COINS_PER_STEP = 3;
    private static final int MAX_STEPS = 4;
    private static final double RATE_PER_STEP = 0.75;

    /**
     * Combines the level's base pace, the progress bonus and the active coin boost.
     *
     * @param coinsCollected total coins collected in the current run
     * @param coinBoost additional moves per second from the most recent coin
     * @return moves per second for the game clock
     */
    double rateAt(int coinsCollected, double coinBoost) {
        int steps = adaptive ? Math.min(coinsCollected / COINS_PER_STEP, MAX_STEPS) : 0;
        return baseRate + steps * RATE_PER_STEP + coinBoost;
    }
}
