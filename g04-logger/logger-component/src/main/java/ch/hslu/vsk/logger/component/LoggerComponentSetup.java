package ch.hslu.vsk.logger.component;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import ch.hslu.vsk.logger.api.LogLevel;
import ch.hslu.vsk.logger.api.Logger;
import ch.hslu.vsk.logger.api.LoggerSetup;
import ch.hslu.vsk.stringpersistor.api.StringPersistor;
import ch.hslu.vsk.stringpersistor.impl.StringPersistorFile;

/**
 * Implementation of the LoggerComponentSetup.
 *
 * @author G04-Team VSK-FS20
 */
public class LoggerComponentSetup implements LoggerSetup {
    private Properties properties;
    private Socket socket;
    private File localFile;
    private StringPersistor localLogger;
    private final UUID applicationId;
    private long openSocketFailedTime = 0;
    private int socketTimeoutSeconds = 15; // 30 seconds

    /**
     * Constructor with specific properties file.
     *
     * @param propertiesFile to properties file
     * @throws IOException for problems handling the properties file
     */
    public LoggerComponentSetup(final String propertiesFile) throws IOException {
        load(propertiesFile);
        this.initializeLocalLogger();
        this.applicationId = UUID.randomUUID();
    }

    /**
     * Constructor with default properties file.
     *
     * @throws IOException for problems handling the properties file
     *
     */
    public LoggerComponentSetup() throws IOException {
        this("logger.properties");
        this.initializeLocalLogger();
    }

    @Override
    public final Logger getLogger() {
        return new LoggerComponent(this);
    }

    @Override
    public final Logger getLogger(final String name) {
        return new LoggerComponent(name, getLogLevel(), this);
    }

    @Override
    public final Logger getLogger(final String name, final LogLevel level) {
        return new LoggerComponent(name, level, this);
    }

    @Override
    public final void setServerIp(final Inet4Address address) {
        this.properties.setProperty("dstIpAddress", address.getHostAddress());
    }

    /**
     * Set a server port in runtime only.
     *
     * @throws IllegalArgumentException if the port ist not in the following range
     *                                  [0-65535].
     */
    @Override
    public final void setServerPort(final Integer port) {
        if (port >= 0 && port <= 65535) {
            this.properties.setProperty("dstPort", Integer.toString(port));
        } else {
            throw new IllegalArgumentException(
                    "Selected Port [" + port + "] not in range (0 - 65535). Change to local Logger.");
        }
    }

    @Override
    public final void setLogLevel(final LogLevel level) {
        this.properties.setProperty("logLevel", level.name());

    }

    @Override
    public final LogLevel getLogLevel() {
        String level = properties.getProperty("logLevel").toLowerCase();
        switch (level) {
        case "off":
            return LogLevel.OFF;
        case "error":
            return LogLevel.ERROR;
        case "info":
            return LogLevel.INFO;
        case "debug":
            return LogLevel.DEBUG;
        default:
            return LogLevel.WARN;
        }
    }

    /**
     * Returns the server port from the properties. If the port is invalid, the
     *
     * @throws IllegalArgumentException if the port ist not in the following range
     *                                  [0-65535].
     */
    @Override
    public final Integer getServerPort() {
        int port = Integer.parseInt(properties.getProperty("dstPort"));
        if (port >= 0 && port <= 65535) {
            return port;
        } else {
            throw new IllegalArgumentException("Selected Port [" + port + "] not in range (0 - 65535)");
        }
    }

    /**
     * Returns the server IP address from the properties. If the address is invalid,
     * the {@link LoggerComponentSetup} initialize a
     *
     * @throws IllegalArgumentException if the given IP address from the properties
     *                                  file is invalid.
     */
    @Override
    public final Inet4Address getServerIp() {
        Inet4Address inet4Address;
        try {
            inet4Address = (Inet4Address) Inet4Address.getByName(this.properties.getProperty("dstIpAddress"));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Given IP address from properties is invalid: " + e);
        }
        return inet4Address;
    }

    /**
     * Get the value of a key-value pair.
     *
     * @param key value
     * @return The value of the key-value pair.
     */
    protected final String getValueByKey(final String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Set key-value pair in the local properties.
     *
     * @param key   pointer
     * @param value content
     */
    protected final void setValueAndKey(final String key, final String value) {
        this.properties.setProperty(key, value);
    }

    /**
     * Read properties ( key - value ) from File.
     *
     * @param propertiesFile to load data from
     * @throws IOException when no such file or other problems
     */
    private void load(final String propertiesFile) throws IOException {
        this.properties = new Properties();
        this.properties.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
    }

    /**
     * Open a TCP Socket to the Log server.
     *
     * @throws IOException      If Log server is not reachable.
     * @throws TimeoutException if multiple tries are made to open a socke.
     */
    protected void openSocket() throws IOException, TimeoutException {
        if (System.currentTimeMillis() - openSocketFailedTime > socketTimeoutSeconds * 1000) {
            try {
                this.socket = new Socket(this.getServerIp(), this.getServerPort());
            } catch (IOException e) {
                openSocketFailedTime = System.currentTimeMillis();
                throw new IOException();
            }
        } else {
            throw new TimeoutException();
        }
    }

    /**
     * Forces socket to close.
     */
    protected final void closeSocket() {
        try {
            this.socket.close();
        } catch (IOException ex) {
            System.err.println("Closing of Socket failed");
        }
    }

    /**
     * Returns the open socket.
     *
     * @return open socket
     */
    protected final Socket getSocket() {
        return this.socket;
    }

    /**
     * Configures the logger to log locally.
     */
    private void initializeLocalLogger() {
        try {
            this.localFile = getFile(createFilePath(System.getProperty("user.home")));
            this.localLogger = new StringPersistorFile();
            this.localLogger.setFile(this.localFile);
        } catch (final IOException ex) {
            System.err.println("Error creating local logger, because: " + ex.getMessage());
        }
    }

    /**
     * Gets the File to write Logs to.
     *
     * @param pathOfLogFile local log file in user home
     * @return FileObject of the log file
     * @throws IOException if it's not possible to open or create file.
     */
    private static File getFile(final String pathOfLogFile) throws IOException {
        File logFile = new File(pathOfLogFile);
        logFile.createNewFile();
        return logFile;
    }

    /**
     * Creates a file path to work as local logging directory.
     *
     * @param directory base where the file path is created
     * @return the file path to the log file as String
     */
    private static String createFilePath(final String directory) {
        new File(directory + File.separator + "vsk_group04_FS20").mkdirs();
        return directory + File.separator + "vsk_group04_FS20/" + "localLogFile" + ".txt";
    }

    /**
     * Returns the local logger or null in no local logger exists.
     *
     * @return logger as StringPersistor Object
     */
    protected final StringPersistor getLocalLogger() {
        return this.localLogger;
    }

    /**
     * Sets the socket which has been opened.
     *
     * @param socket tcp socket
     */
    protected final void setSocket(final Socket socket) {
        this.socket = socket;
    }

    /**
     * Returns the local file Object or null if it does not exist.
     *
     * @return local file Object
     */
    protected File getLocalFile() {
        return this.localFile;
    }

    /**
     * Returns the local ApplicationID as UUID.
     *
     * @return ApplicationID as UUID
     */
    protected final UUID getApplicationId() {
        return applicationId;
    }
}