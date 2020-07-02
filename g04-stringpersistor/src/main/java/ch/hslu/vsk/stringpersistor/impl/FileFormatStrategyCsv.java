package ch.hslu.vsk.stringpersistor.impl;

import ch.hslu.vsk.stringpersistor.api.PersistedString;

import java.time.Instant;

/**
 * Serialize a {@link PersistedString} to persist it in an csv-formatted file.
 */
public class FileFormatStrategyCsv implements FileFormatStrategy {
    private final String lineSeparator;
    private final String delimiter;

    public FileFormatStrategyCsv() {
        this.lineSeparator = "\n";
        this.delimiter = ",";
    }

    public FileFormatStrategyCsv(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
        this.delimiter = ",";
    }

    public FileFormatStrategyCsv(final String lineSeparator, final String delimiter) {
        this.lineSeparator = lineSeparator;
        this.delimiter = delimiter;
    }

    /**
     * {@inheritDoc}
     *
     * @param persistedString {@link PersistedString}
     * @return
     */
    @Override
    public String serialize(final PersistedString persistedString) {
        return persistedString.getTimestamp().toString().trim()
                + delimiter
                + persistedString.getPayload().trim()
                + lineSeparator;
    }

    /**
     * {@inheritDoc}
     *
     * @param line {@link String} contains a line of text, read in a {@link java.io.File}
     * @return
     */
    @Override
    public PersistedString deserialize(final String line) {
        Instant date = Instant.parse(line.substring(0, line.indexOf(delimiter)));
        String msg = line.substring(line.indexOf(delimiter) + 1);

        return new PersistedString(date, msg);
    }
}
