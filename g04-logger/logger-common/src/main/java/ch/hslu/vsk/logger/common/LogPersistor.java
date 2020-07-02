package ch.hslu.vsk.logger.common;

/**
 * Interface for StringPersistorAdapter.
 */
public interface LogPersistor {

    /**
     * saves LogMessage using the save method of StringPersistor through
     * theStringPersistorAdapter.
     *
     * @param logMessage log
     */
    void save(LogMessage logMessage);

}
