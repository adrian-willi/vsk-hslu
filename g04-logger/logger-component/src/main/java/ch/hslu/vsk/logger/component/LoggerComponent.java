package ch.hslu.vsk.logger.component;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import ch.hslu.vsk.logger.api.LogLevel;
import ch.hslu.vsk.logger.api.Logger;
import ch.hslu.vsk.logger.common.LogMessage;
import ch.hslu.vsk.stringpersistor.api.PersistedString;

public class LoggerComponent implements Logger {

    private final String name;
    private final LogLevel level;
    private LoggerComponentSetup setup;
    private Boolean localLoggerFlag = false;
    private int logsSinceDisconnect = 0;

    /**
     * Constructs a Logger with a name and a fixed LogLevel.
     *
     * @param name  of logger
     * @param level for logging
     * @param setup Object work with
     */
    public LoggerComponent(final String name, final LogLevel level, final LoggerComponentSetup setup) {
        this.name = name;
        this.level = level;
        this.setup = setup;
    }

    /**
     * Constructs a Logger with a name and default LogLevel = WARN.
     *
     * @param name  of logger
     * @param setup Object work with
     */
    public LoggerComponent(final String name, final LoggerComponentSetup setup) {
        this(name, LogLevel.WARN, setup);
    }

    /**
     * Constructs a Logger with a default name.
     *
     * @param setup Object work with
     */
    public LoggerComponent(final LoggerComponentSetup setup) {
        this("default", LogLevel.WARN, setup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final String message) {
        sendLog(LogLevel.DEBUG, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final String message) {
        sendLog(LogLevel.INFO, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final String message) {
        sendLog(LogLevel.WARN, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final String message) {
        sendLog(LogLevel.ERROR, message);
    }

    /**
     * Creates and sends a logMessage Object to the configured Server on the
     * configured Port.
     *
     * @param logLevel logLevel of game of life
     * @param message  log of game of life
     */
    private void sendLog(final LogLevel logLevel, final String message) {
        if (logLevel.getValue() <= this.level.getValue()) {
            LogMessage logMessage = new LogMessage(message, logLevel, this.setup.getApplicationId());
            ObjectOutputStream outStream;
            if (this.localLoggerFlag) {
                this.logLocally(logMessage);
                try {
                    this.setup.openSocket();
                    outStream = new ObjectOutputStream(this.setup.getSocket().getOutputStream());
                    final List<PersistedString> locallyLogged = this.setup.getLocalLogger().get(Integer.MAX_VALUE);
                    for (PersistedString ps : locallyLogged) {
                        System.out.println(ps.toString());
                        outStream.writeObject(MessageFormatter.parse(ps));
                        outStream.flush();
                    }
                    File file = this.setup.getLocalFile();
                    PrintWriter writer = new PrintWriter(file);
                    writer.print("");
                    writer.close();
                    this.setup.closeSocket();
                    this.setLocalLoggerFlag(false);
                    System.out.println("All " + logsSinceDisconnect + " local saved messages from [ " + this.getName()
                            + " ] where sent now");
                    logsSinceDisconnect = 0;
                } catch (TimeoutException ex) {
                    logsSinceDisconnect++;
                } catch (SocketException ex) {
                    System.err.println("Connection to logger server still not possible");
                    System.err.println("Messages will be to continued logged locally ");
                } catch (IOException ex) {
                    logsSinceDisconnect++;
                    System.out.println(this.getName() + " - could not send logs");
                    // ex.printStackTrace();
                }
            } else {
                try {
                    this.setup.openSocket();
                    outStream = new ObjectOutputStream(this.setup.getSocket().getOutputStream());
                    outStream.writeObject(logMessage);
                    outStream.flush();
                    this.setup.closeSocket();
                } catch (TimeoutException ex) {
                    System.out.println(this.getName() + " - could not send logs");
                    this.setLocalLoggerFlag(true);
                    logsSinceDisconnect++;
                } catch (IOException e) {
                    this.setLocalLoggerFlag(true);
                    this.logLocally(logMessage);
                    System.err.println("Connection to logger server failed...\nMessages will be logged locally now");
                }
            }
        }
    }

    /**
     * Returns the name of the Logger.
     *
     * @return loggerName
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the LogLevel of the Logger or Null if not set.
     *
     * @return logLevel
     */
    public LogLevel getLogLevel() {
        return this.level;
    }

    private void setLocalLoggerFlag(final Boolean flag) {
        this.localLoggerFlag = flag;
    }

    private void logLocally(final LogMessage message) {
        this.setup.getLocalLogger().save(message.getTimeOfLog(), MessageFormatter.format(message).getPayload());
    }
}
