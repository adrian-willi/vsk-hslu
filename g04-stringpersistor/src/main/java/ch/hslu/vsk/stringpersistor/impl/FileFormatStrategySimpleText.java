package ch.hslu.vsk.stringpersistor.impl;

import ch.hslu.vsk.stringpersistor.api.PersistedString;

import java.time.Instant;

/**
 * Serialize a {@link PersistedString} to persist it in an simple text file.
 */
public class FileFormatStrategySimpleText implements FileFormatStrategy {

    private final String lineSeparator;

    public FileFormatStrategySimpleText() {
        this.lineSeparator = "\n";
    }

    public FileFormatStrategySimpleText(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * {@inheritDoc}
     *
     * @param persistedString {@link PersistedString}
     * @return
     */
    @Override
    public String serialize(final PersistedString persistedString) {
        return persistedString.toString().trim() + lineSeparator;
    }

    /**
     * {@inheritDoc}
     *
     * @param line {@link String} contains a line of text, read in a {@link java.io.File}
     * @return
     */
    @Override
    public PersistedString deserialize(final String line) {
        Instant date = Instant.parse(line.substring(0, line.indexOf(" | ")));
        String msg = line.substring(line.indexOf(" | ") + 3);

        return new PersistedString(date, msg);
    }
}
