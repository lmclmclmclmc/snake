package com.comp2042;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HighScoresTest {
    @TempDir
    Path temp;

    @Test
    void startsAtZeroAndDoesNotCreateAFileForAnEmptyRun() {
        Path file = temp.resolve("scores.properties");
        HighScores scores = new HighScores(file);
        scores.record(Level.CLASSIC, 0);
        assertEquals(0, scores.best(Level.CLASSIC));
        assertFalse(Files.exists(file));
    }

    @Test
    void persistsBestsSeparatelyAndNeverReplacesOneWithALowerScore() {
        Path file = temp.resolve("game/scores.properties");
        HighScores scores = new HighScores(file);
        scores.record(Level.CLASSIC, 45);
        scores.record(Level.ORCHARD, 30);
        scores.record(Level.CLASSIC, 20);
        scores.record(Level.ORCHARD, 35);

        HighScores reloaded = new HighScores(file);
        assertEquals(45, reloaded.best(Level.CLASSIC));
        assertEquals(35, reloaded.best(Level.ORCHARD));
        assertEquals(0, reloaded.best(Level.MAZE));
    }

    @Test
    void ignoresInvalidScoresWithoutLosingValidEntries() throws IOException {
        Path file = temp.resolve("scores.properties");
        Files.writeString(file, "CLASSIC=invalid\nORCHARD=18\nMAZE=-2\n");

        HighScores scores = new HighScores(file);
        assertEquals(0, scores.best(Level.CLASSIC));
        assertEquals(18, scores.best(Level.ORCHARD));
        assertEquals(0, scores.best(Level.MAZE));
    }

    @Test
    void keepsTheBestInMemoryAndRetriesAfterAWriteFailure() throws IOException {
        Path blockedParent = Files.createDirectory(temp.resolve("game"));
        Path file = Files.createDirectory(blockedParent.resolve("scores.properties"));
        HighScores scores = new HighScores(file);
        scores.record(Level.MAZE, 55);
        assertEquals(55, scores.best(Level.MAZE));

        Files.move(file, temp.resolve("moved-directory"));
        scores.record(Level.MAZE, 20);
        assertEquals(55, new HighScores(file).best(Level.MAZE));
    }
}
