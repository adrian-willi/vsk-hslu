package ch.hslu.vsk.stringpersistor.impl;

import ch.hslu.vsk.stringpersistor.api.PersistedString;
import com.google.gson.Gson;

/**
 * Serialize a {@link PersistedString} to persist it in an json-formatted file.
 * Uses {@link Gson} dependency.
 */
public class FileFormatStrategyJson implements FileFormatStrategy {

    private final Gson gson = new Gson();

    public FileFormatStrategyJson() {
    }

    /**
     * {@inheritDoc}
     *
     * @param persistedString {@link PersistedString}
     * @return
     */
    @Override
    public String serialize(final PersistedString persistedString) {
        return this.gson.toJson(persistedString);
    }

    /**
     * {@inheritDoc}
     *
     * @param line {@link String} contains a line of text, read in a {@link java.io.File}
     * @return
     */
    @Override
    public PersistedString deserialize(final String line) {
        return this.gson.fromJson(line, PersistedString.class);
    }
}
