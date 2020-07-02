package ch.hslu.vsk.logger.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ch.hslu.vsk.logger.api.LogLevel;
import ch.hslu.vsk.logger.api.LoggerSetupFactory;

@TestInstance(Lifecycle.PER_CLASS)
class LoggerComponentTest {

    private LoggerComponentSetup setup;

    @BeforeAll
    public void initLoggerSetup() throws Exception {
        if (setup == null) {
            setup = (LoggerComponentSetup) LoggerSetupFactory
                    .getLoggerSetup("ch.hslu.vsk.logger.component.LoggerComponentSetup");
        }
    }

    @Test
    void testLoggerComponentStringLogLevel() {
        LoggerComponent logger = new LoggerComponent("Sample", LogLevel.ERROR, this.setup);
        assertEquals(logger.getName(), "Sample");
        assertEquals(logger.getLogLevel(), LogLevel.ERROR);
    }

    @Test
    void testLoggerComponentString() {
        LoggerComponent logger = new LoggerComponent("Sample", this.setup);
        assertEquals(logger.getName(), "Sample");
        assertEquals(logger.getLogLevel(), LogLevel.WARN);
    }

    @Test
    void testLoggerComponent() {
        LoggerComponent logger = new LoggerComponent(this.setup);
        assertEquals(logger.getName(), "default");
        assertEquals(logger.getLogLevel(), LogLevel.WARN);
    }

    @Test
    void testLoggerComponentSendLogs() {
        LoggerComponent logger = new LoggerComponent(this.setup);
        logger.debug("debug");
        logger.info("info");
        logger.error("error");
        logger.warn("warn");
        assertEquals(logger.getName(), "default");
        assertEquals(logger.getLogLevel(), LogLevel.WARN);
    }

}
