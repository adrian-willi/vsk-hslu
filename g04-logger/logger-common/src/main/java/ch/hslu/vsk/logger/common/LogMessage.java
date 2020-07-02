package ch.hslu.vsk.logger.common;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import ch.hslu.vsk.logger.api.LogLevel;

/**
 * The Class LogMessage. Defines the structure of the LogMessage.
 *
 * @author adiwi
 */

public class LogMessage implements Serializable {

    private static final long serialVersionUID = -4424332095430088026L;
    //LogLevel is a enum from the api
    private LogLevel loglevel;
    private String message;
    private UUID applicationId;
    private Instant timeOfLog;
    private Instant timeServerReceivedLog;

    /**
     * Instantiates a new log message with a logLevel.
     *
     * @param message  the message
     * @param loglevel the loglevel
     * @param id       the application ID
     */
    public LogMessage(final String message, final LogLevel loglevel, final UUID id) {
        this.message = message;
        this.loglevel = loglevel;
        this.applicationId = id;
        this.timeOfLog = Instant.now();
        this.timeServerReceivedLog = Instant.now();
    }

    /**
     * Instantiates a new log message with a logLevel and timeOfLog.
     *
     * @param message   the message
     * @param loglevel  the loglevel
     * @param id        the application ID
     * @param timeOfLog the time of log
     */
    public LogMessage(final String message, final LogLevel loglevel, final UUID id,
            final Instant timeOfLog) {
        this.message = message;
        this.loglevel = loglevel;
        this.applicationId = id;
        this.timeOfLog = timeOfLog;
        this.timeServerReceivedLog = Instant.now();
    }

    /**
     * Instantiates a new log message with a logLevel, timeOfLog and timeServerReceivedLog.
     *
     * @param message               the message
     * @param loglevel              the loglevel
     * @param id                    the application ID
     * @param timeOfLog             the time of log
     * @param timeServerReceivedLog the time server received log
     */
    public LogMessage(final String message, final LogLevel loglevel, final UUID id,
            final Instant timeOfLog, final Instant timeServerReceivedLog) {
        this.message = message;
        this.loglevel = loglevel;
        this.applicationId = id;
        this.timeOfLog = timeOfLog;
        this.timeServerReceivedLog = timeServerReceivedLog;
    }

    /**
     * Gets the loglevel.
     *
     * @return the loglevel
     */
    public LogLevel getLoglevel() {
        return this.loglevel;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the applicationId.
     *
     * @return the applicationId
     */
    public UUID getApplicationId() {
        return this.applicationId;
    }

    /**
     * Gets the time of log.
     *
     * @return the time of log
     */
    public Instant getTimeOfLog() {
        return this.timeOfLog;
    }

    /**
     * Gets the time server received log.
     *
     * @return the time server received log
     */
    public Instant getTimeServerReceivedLog() {
        return this.timeServerReceivedLog;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Log[ message: " + this.message + ", logLevel: " + this.loglevel
                + ", applicationId: " + this.applicationId + ", timeOfLog: " + this.timeOfLog
                + ", timeServerReceivedLog: " + this.timeServerReceivedLog + " ]";
    }
}
