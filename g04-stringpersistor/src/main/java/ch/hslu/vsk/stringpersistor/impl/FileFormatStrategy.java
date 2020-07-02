package ch.hslu.vsk.stringpersistor.impl;

import ch.hslu.vsk.stringpersistor.api.PersistedString;

/**
 * Interface to implement Strategy Pattern.
 */
public interface FileFormatStrategy {
    /**
     * Serialize a {@link PersistedString} object as it is needed.
     *
     * @param persistedString {@link PersistedString}
     * @return {@link String} in the manner how the implementation is designed for.
     */
    String serialize(PersistedString persistedString);

    /**
     * Deserialize a String read out of a {@link java.io.File}. Has to extract timestamp and payload
     * and create a {@link PersistedString}.
     *
     * @param line {@link String} contains a line of text, read in a {@link java.io.File}
     * @return {@link PersistedString}
     */
    PersistedString deserialize(String line);
}
