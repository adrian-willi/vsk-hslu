package ch.hslu.vsk.logger.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

/**
 * @author adiwi
 */

public class LoggerServerIT {

    @Test
    public void connectToServer() throws UnknownHostException, IOException {
        LoggerServer.main(new String[] {});
        Socket client = new Socket("localhost", 3200);
        assertTrue(client.isConnected());
        client.close();
    }

    @Test
    public void testGetFileThatNotExists() throws IOException {
        File file = new File("target/notExisting.txt");
        assertEquals(file, LoggerServer.getFile("target/notExisting.txt"));
    }

    @Test
    public void testCreatePath() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        String directory = System.getProperty("user.home");
        String path = directory + File.separator + "vsk_group04_FS20_Server/" + timeStamp + ".txt";
        assertEquals(path, LoggerServer.createFilePath(directory));
    }

    /**
     * Test must pass in any case, because in case of no property file value is set
     * to default strategy.
     *
     * @throws IOException no such file or not enough rights
     */
    @Test
    void testReadStrategyFromProperty() throws IOException {
        String strategy;
        strategy = LoggerServer.readStrategyFromProperty("csv.properties");
        assertEquals("csv", strategy);
        strategy = LoggerServer.readStrategyFromProperty("json.properties");
        assertEquals("json", strategy);
        strategy = LoggerServer.readStrategyFromProperty("default.properties");
        assertEquals("default", strategy);
    }

}
