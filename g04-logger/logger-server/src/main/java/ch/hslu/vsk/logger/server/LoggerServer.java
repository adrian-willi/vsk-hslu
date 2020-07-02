package ch.hslu.vsk.logger.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ch.hslu.vsk.logger.common.LogPersistor;
import ch.hslu.vsk.logger.common.StringPersistorAdapter;

/**
 * @author adiwi
 */

public final class LoggerServer implements Runnable, PropertyChangeListener {

    private static final int SERVER_PORT = 3200;
    private LogMessageHandler handler;
    private static LogViewerServer logViewerServer = new LogViewerServer();

    private LoggerServer() {
    }

    public static void main(final String[] args) {
        Thread serverThread = new Thread(new LoggerServer());
        Thread rmiServer = new Thread(logViewerServer);
        serverThread.start();
        rmiServer.start();
    }

    @Override
    public void run() {
        ServerSocket listen = null;
        try {
            listen = new ServerSocket(SERVER_PORT);
            final ExecutorService executor = Executors.newFixedThreadPool(3);
            File file = getFile(createFilePath(System.getProperty("user.home")));

            final LogPersistor stringPersistorAdapter = StringPersistorAdapter.create(file,
                    readStrategyFromProperty("loggerServer.properties"));

            while (true) {
                try {
                    System.out.println("Waiting for connection..");
                    final Socket client = listen.accept();
                    handler = new LogMessageHandler(client, stringPersistorAdapter);
                    handler.subscribe(this);
                    executor.execute(handler);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static File getFile(final String pathOfLogFile) throws IOException {
        File logFile = new File(pathOfLogFile);
        if (logFile.isFile()) {
            return logFile;
        } else {
            logFile.createNewFile();
            return logFile;
        }
    }

    public static String createFilePath(final String directory) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        new File(directory + File.separator + "vsk_group04_FS20_Server").mkdirs();
        return directory + File.separator + "vsk_group04_FS20_Server/" + timeStamp + ".txt";
    }

    /**
     * Reads strategy from property file and returns it. Used to create StringPersistorAdapter.
     *
     * @param propertyFile String of Filename to load
     * @return fileStrategy from property file or default
     * @throws IOException file not found, not enough rights
     */
    public static String readStrategyFromProperty(final String propertyFile) throws IOException {
        InputStream inputStream = null;
        String fileStrategy = "default";

        try {
            Properties properties = new Properties();
            String propertyFileName = propertyFile;
            inputStream = LoggerServer.class.getClassLoader().getResourceAsStream(propertyFileName);

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException(
                        "property file '" + propertyFileName + "' not found in the classpath");
            }
            String tempStrategy = properties.getProperty("strategy").toLowerCase();
            if (tempStrategy.equals("json") || tempStrategy.equals("csv")) {
                fileStrategy = tempStrategy;
            }
        } catch (Exception e) {
            System.err.print(e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return fileStrategy;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent notification) {
        System.out.print("Property Change Handler: " + notification.getNewValue().toString());
        try {
            logViewerServer.getLogViewerServant()
                    .notifyClients(notification.getNewValue().toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
