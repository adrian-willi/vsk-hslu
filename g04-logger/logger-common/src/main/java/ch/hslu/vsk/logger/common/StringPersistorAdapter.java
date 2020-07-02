package ch.hslu.vsk.logger.common;

import java.io.File;
import java.time.Instant;

import ch.hslu.vsk.stringpersistor.api.StringPersistor;
import ch.hslu.vsk.stringpersistor.impl.FileFormatStrategyCsv;
import ch.hslu.vsk.stringpersistor.impl.FileFormatStrategyJson;
import ch.hslu.vsk.stringpersistor.impl.FileFormatStrategySimpleText;
import ch.hslu.vsk.stringpersistor.impl.StringPersistorFile;

/**
 * @author adiwi GoF adapter pattern for StringPersistor
 */

public final class StringPersistorAdapter implements LogPersistor {
    private final StringPersistor stringPersistor;

    private StringPersistorAdapter(final StringPersistor stringPersistor) {
        this.stringPersistor = stringPersistor;
    }

    /**
     * Creates adapter for StringPersistor and LogPersistor. Takes file and
     * strategy.
     *
     * @param filename of file where logs are persisted
     * @param strategy of fileFormat
     * @return stringPersistorAdapter
     */
    public static StringPersistorAdapter create(final File filename, final String strategy) {
        final StringPersistor persistor;
        if (strategy.equals("csv")) {
            persistor = new StringPersistorFile(new FileFormatStrategyCsv("\n", ";"));
        } else if (strategy.equals("json")) {
            persistor = new StringPersistorFile(new FileFormatStrategyJson());
        } else {
            persistor = new StringPersistorFile(new FileFormatStrategySimpleText("\n"));
        }
        persistor.setFile(new File(String.valueOf(filename)));
        return new StringPersistorAdapter(persistor);
    }

    @Override
    public void save(final LogMessage logMessage) {
        this.stringPersistor.save(Instant.now(), logMessage.toString() + "\n");
    }
}