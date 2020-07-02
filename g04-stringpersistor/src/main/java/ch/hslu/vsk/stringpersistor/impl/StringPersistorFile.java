package ch.hslu.vsk.stringpersistor.impl;

import ch.hslu.vsk.stringpersistor.api.PersistedString;
import ch.hslu.vsk.stringpersistor.api.StringPersistor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link StringPersistor}.
 *
 * @author g04
 */
public final class StringPersistorFile implements StringPersistor {

    private static final String MSG_DIR_DOESNT_EXISTS = "Directory does not exists or could not created.";
    private static final String MSG_FILE_DOESNT_EXISTS = "Log file does not exists.";
    private static final String MSG_FILE_ERROR_CREATE = "Could not create new log file.";
    private static final String MSG_FILE_ERROR_READ = "Could not read given log file.";
    private static final String MSG_FILE_ERROR_WRITE = "Could not write to log file.";
    private static final String MSG_PAYLOAD_ERROR = "String is null or invalid.";
    private static final String MSG_TIME_ERROR = "Timestamp is null or invalid.";

    private final FileFormatStrategyContext strategyContext;

    private BufferedWriter writer;
    private BufferedReader reader;

    /**
     * Constructor receives a strategy to serialize/deserialize in the right way.
     */
    public StringPersistorFile() {
        this.strategyContext = new FileFormatStrategyContext(new FileFormatStrategySimpleText("\n"));
    }

    public StringPersistorFile(final FileFormatStrategyCsv fileFormatStrategy) {
        this.strategyContext = new FileFormatStrategyContext(fileFormatStrategy);
    }

    public StringPersistorFile(final FileFormatStrategyJson fileFormatStrategy) {
        this.strategyContext = new FileFormatStrategyContext(fileFormatStrategy);
    }

    public StringPersistorFile(final FileFormatStrategySimpleText fileFormatStrategy) {
        this.strategyContext = new FileFormatStrategyContext(fileFormatStrategy);
    }

    /**
     * Sets the given file to be used for saving and retrieving messages.
     *
     * @param file a {@link File} that must either exist (and be both readable and writable) or that
     *             can be created based on its path.
     * @throws IllegalArgumentException if the directory or file cannot be created or found.
     * @throws SecurityException        if the file is not is not readable or not writable.
     */
    @Override
    public void setFile(final File file) {
        if (file == null) {
            throw new IllegalArgumentException(MSG_FILE_DOESNT_EXISTS);
        }
        if (!file.exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IllegalArgumentException(MSG_DIR_DOESNT_EXISTS);
            }
            try {
                if (!file.createNewFile()) {
                    throw new IllegalArgumentException(MSG_FILE_ERROR_CREATE);
                }
            } catch (final IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (!file.canRead()) {
            throw new SecurityException(MSG_FILE_ERROR_READ);
        }
        if (!file.canWrite()) {
            throw new SecurityException(MSG_FILE_ERROR_WRITE);
        }
        try {
            this.writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            this.reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } catch (final FileNotFoundException ex) {
            throw new IllegalArgumentException(MSG_FILE_DOESNT_EXISTS + file, ex);
        }
    }

    /**
     * Saves the given instant with the given payload to the file specified with {@link #setFile(File)}.
     * The message is stored in the format defined by {@link PersistedString#toString()}.
     *
     * @param timestamp an {@link Instant}
     * @param payload   the actual message {@link String}
     * @throws IllegalStateException    if no proper file was set {@link #setFile(File)}
     *                                  or an error occur during writing.
     * @throws IllegalArgumentException if no proper timestamp or payload was set.
     */
    @Override
    public void save(final Instant timestamp, final String payload) {
        if (this.writer == null) {
            throw new IllegalStateException(MSG_FILE_DOESNT_EXISTS);
        }
        if (timestamp == null) {
            throw new IllegalArgumentException(MSG_TIME_ERROR);
        }
        if (payload == null) {
            throw new IllegalArgumentException(MSG_PAYLOAD_ERROR);
        }

        final PersistedString persistedString = new PersistedString(timestamp, payload);
        try {
            this.writer.write(strategyContext.serialize(persistedString));
            this.writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException(MSG_FILE_ERROR_WRITE, e);
        }
    }

    /**
     * Retrieves a number of messages. A message can either consist of a single line or encompass multiple lines.
     * A line which holds a {@link Instant} and is delimited by «|» is considered to be the start of a new message.
     *
     * @param count the number of messages that should be retrieved.
     * @return a {@link List} containing the parsed messages as {@link PersistedString}.
     * An empty {@link List} will be returned for {@code count < 1}.
     * @throws IllegalStateException if no proper file was set {@link #setFile(File)}.
     */
    @Override
    public List<PersistedString> get(final int count) {
        if (count < 1) {
            return new ArrayList<>();
        }
        if (this.reader == null) {
            throw new IllegalStateException(MSG_FILE_DOESNT_EXISTS);
        }
        final List<PersistedString> persistedStringList = new ArrayList<>();
        String line;

        int linesRead = 0;

        try {
            while ((line = this.reader.readLine()) != null && linesRead < count) {
                persistedStringList.add(strategyContext.deserialize(line));
                linesRead++;
            }
        } catch (IOException e) {
            throw new IllegalStateException(MSG_FILE_ERROR_READ, e);
        }

        return persistedStringList;
    }
}
