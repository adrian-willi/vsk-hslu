package ch.hslu.vsk.stringpersistor.impl;

import ch.hslu.vsk.stringpersistor.api.PersistedString;

/**
 * Context class transfers objects to the right strategy.
 */
public class FileFormatStrategyContext {
    private final FileFormatStrategy fileFormatStrategy;

    /**
     * Constructor initialize {@link FileFormatStrategy}.
     *
     * @param strategy {@link FileFormatStrategy}
     */
    public FileFormatStrategyContext(final FileFormatStrategy strategy) {
        this.fileFormatStrategy = strategy;
    }

    /**
     * Call the chosen strategy.
     *
     * @param persistedString {@link PersistedString}
     * @return {@link String}
     */
    public String serialize(final PersistedString persistedString) {
        return fileFormatStrategy.serialize(persistedString);
    }

    /**
     * Call the chosen strategy.
     *
     * @param data {@link String}
     * @return {@link PersistedString}
     */
    public PersistedString deserialize(final String data) {
        return fileFormatStrategy.deserialize(data);
    }
}
