package ch.hslu.vsk.logger.component;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.hslu.vsk.logger.api.LogLevel;
import ch.hslu.vsk.logger.api.Logger;

/**
 * @author mae
 * @author adiwi
 *
 */
class LoggerComponentSetupTest {

    private LoggerComponentSetup setup;

    @BeforeEach
    void init() {
        try {
            setup = new LoggerComponentSetup("LoggerTest.properties");
        } catch (IOException ioException) {
            Assertions.fail("File not found");
        }
    }

    @AfterEach
    void cleanup() {
        setup = null;

    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogger()}.
     */
    @Test
    void testEmptyConstructor() {
        try {
            setup = new LoggerComponentSetup();
        } catch (IOException e) {
            Assertions.fail();
        }
        Assertions.assertNotNull(setup.getServerIp());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogger()}.
     */
    @Test
    void testGetLogger() {
        Logger logger = setup.getLogger();
        Assertions.assertTrue(logger instanceof LoggerComponent);
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogger(java.lang.String)}.
     */
    @Test
    void testGetLoggerString() {
        Logger logger = setup.getLogger("LoggerName");
        Assertions.assertTrue(logger instanceof LoggerComponent);
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogger(String, ch.hslu.vsk.logger.api.LogLevel)}.
     */
    @Test
    void testGetLoggerStringLogLevel() {
        Logger logger = setup.getLogger("LoggerName", LogLevel.ERROR);
        Assertions.assertTrue(logger instanceof LoggerComponent);
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setServerIp(Inet4Address)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getServerIp()}.
     */
    @Test
    void testSetAndGetServerIp() {
        try {
            setup.setServerIp((Inet4Address) Inet4Address.getByName("localhost"));
        } catch (UnknownHostException e) {
            Assertions.fail("Input IP NOK.");
        }
        System.out.println(setup.getServerIp());
        Assertions.assertEquals("/127.0.0.1", setup.getServerIp().toString());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setServerIp(Inet4Address)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getServerIp()}.
     */
    @Test
    void testSetAndGetServerIpException() {
        setup.setValueAndKey("dstIpAddress", "NotAnIp");
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> setup.getServerIp());
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Given IP address"));
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setServerPort(Integer)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getServerPort()}.
     */
    @Test
    void testsetAndGetServerPort() {
        setup.setServerPort(8080);
        Assertions.assertEquals(8080, setup.getServerPort());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setServerPort(Integer)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getServerPort()}.
     */
    @Test
    void testsetServerPortMinus() {
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> setup.setServerPort(-10));
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Selected Port [-10]"));
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setServerPort(Integer)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getServerPort()}.
     */
    @Test
    void testSetServerPortOutOfRange() {
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> setup.setServerPort(100000));
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Selected Port [100000]"));
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setServerPort(Integer)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getServerPort()}.
     */
    @Test
    void testGetServerPortMinus() {
        setup.setValueAndKey("dstPort", "-10");
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> setup.getServerPort());
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Selected Port [-10]"));
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setServerPort(Integer)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getServerPort()}.
     */
    @Test
    void testGetServerPortOutOfRange() {
        setup.setValueAndKey("dstPort", "100000");
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> setup.getServerPort());
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Selected Port [100000]"));
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setLogLevel(LogLevel)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogLevel()}.
     */
    @Test
    void testGetAndSetLogLevelDebug() {
        setup.setLogLevel(LogLevel.DEBUG);
        Assertions.assertEquals(LogLevel.DEBUG, setup.getLogLevel());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setLogLevel(LogLevel)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogLevel()}.
     */
    @Test
    void testGetAndSetLogLevelInfo() {
        setup.setLogLevel(LogLevel.INFO);
        Assertions.assertEquals(LogLevel.INFO, setup.getLogLevel());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setLogLevel(LogLevel)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogLevel()}.
     */
    @Test
    void testGetAndSetLogLevelWarn() {
        setup.setLogLevel(LogLevel.WARN);
        Assertions.assertEquals(LogLevel.WARN, setup.getLogLevel());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setLogLevel(LogLevel)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogLevel()}.
     */
    @Test
    void testGetAndSetLogLevelError() {
        setup.setLogLevel(LogLevel.ERROR);
        Assertions.assertEquals(LogLevel.ERROR, setup.getLogLevel());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setLogLevel(LogLevel)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogLevel()}.
     */
    @Test
    void testGetAndSetLogLevelOff() {
        setup.setLogLevel(LogLevel.OFF);
        Assertions.assertEquals(LogLevel.OFF, setup.getLogLevel());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setLogLevel(LogLevel)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogLevel()}.
     */
    @Test
    void testGetAndSetLogLevelDefault() {
        setup.setValueAndKey("logLevel", "NotAValidLogLevel");
        Assertions.assertEquals(LogLevel.WARN, setup.getLogLevel());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getApplicationId()}.
     */
    @Test
    void testGetApplicationId() {
        Assertions.assertTrue(setup.getApplicationId().toString().matches("([a-z0-9]+-){4}[a-z0-9]+"));
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setLogLevel(LogLevel)}.
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getLogLevel()}.
     */
    @Test
    void testLoadPropertiesNotExists() {
        NullPointerException nullPointerException = Assertions.assertThrows(NullPointerException.class,
                () -> setup = new LoggerComponentSetup("NotExists.properties"));
        Assertions.assertEquals("inStream parameter is null", nullPointerException.getMessage());
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#getValueByKey(String)}.
     */
    @Test
    void testGetValueByKey() {
        Assertions.assertEquals("8585", setup.getValueByKey("dstPort"));
    }

    /**
     * Test method for
     * {@link ch.hslu.vsk.logger.component.LoggerComponentSetup#setValueAndKey(String, String)}.
     */
    @Test
    void testSetValueAndKey() {
        setup.setValueAndKey("animal", "duck");
        Assertions.assertEquals("duck", setup.getValueByKey("animal"));
    }

    /**
     * tests if a socket can be opened.
     *
     * @throws IOException      if socket could not be opened
     * @throws TimeoutException if previous
     */
    @Test
    public void testGetSocket() throws IOException, TimeoutException {
        final Socket testSocket = mock(Socket.class);
        final LoggerComponentSetup testSetup = new LoggerComponentSetup() {
            @Override
            public void openSocket() throws IOException {
                this.setSocket(testSocket);
            }
        };
        testSetup.openSocket();
        Assertions.assertEquals(testSocket, testSetup.getSocket());
    }
}
