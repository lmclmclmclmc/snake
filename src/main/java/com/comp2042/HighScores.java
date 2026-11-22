package com.comp2042;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/** Tracks and stores a local personal best for each level. */
final class HighScores {
    private static final System.Logger LOG = System.getLogger(HighScores.class.getName());

    private final Path file;
    private final Map<Level, Integer> scores = new EnumMap<>(Level.class);
    private boolean dirty;

    /**
     * Loads available scores from the given file. Missing entries start at zero.
     *
     * @param file path to the score file
     */
    HighScores(Path file) {
        this.file = file.toAbsolutePath();
        load();
    }

    /**
     * Returns the best score available for a level.
     *
     * @param level selected level
     * @return highest score, or zero for a level with no stored score
     */
    int best(Level level) {
        return scores.getOrDefault(level, 0);
    }

    /**
     * Records a completed run and attempts to save any new personal best.
     *
     * @param level level played
     * @param points score earned in that run
     */
    void record(Level level, int points) {
        if (points > best(level)) {
            scores.put(level, points);
            dirty = true;
        }
        if (!dirty) {
            return;
        }
        try {
            save();
            dirty = false;
        } catch (IOException | SecurityException failure) {
            LOG.log(System.Logger.Level.WARNING, "Could not save personal bests: " + failure.getMessage());
        }
    }

    private void load() {
        Properties saved = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            saved.load(reader);
        } catch (NoSuchFileException missing) {
            return;
        } catch (IOException | IllegalArgumentException | SecurityException failure) {
            LOG.log(System.Logger.Level.WARNING, "Could not read personal bests: " + failure.getMessage());
            return;
        }

        for (Level level : Level.values()) {
            String value = saved.getProperty(level.name());
            if (value == null) {
                continue;
            }
            try {
                int points = Integer.parseInt(value.trim());
                if (points > 0) {
                    scores.put(level, points);
                }
            } catch (NumberFormatException invalidValue) {
                LOG.log(System.Logger.Level.WARNING, "Ignoring invalid score for " + level);
            }
        }
    }

    private void save() throws IOException {
        Properties saved = new Properties();
        scores.forEach((level, points) -> saved.setProperty(level.name(), points.toString()));
        Path directory = file.getParent();
        Files.createDirectories(directory);
        Path staged = Files.createTempFile(directory, "scores-", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(staged)) {
                saved.store(writer, "Snake Game personal bests");
            }
            try {
                Files.move(staged, file, ATOMIC_MOVE, REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(staged, file, REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(staged);
        }
    }
}
