package ch.hslu.vsk.logger.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ch.hslu.vsk.logger.api.LogLevel;

/**
 * @author adiwi
 */

final class LogMessageTest {

    @Test
    void constructorLogMessageTest() {
        final Instant time = Instant.now();
        final LogMessage logMsg = new LogMessage("ConstructorTest", LogLevel.INFO, UUID.randomUUID(), time, time);
        assertEquals("ConstructorTest", logMsg.getMessage());
        assertEquals(LogLevel.INFO, logMsg.getLoglevel());
        assertEquals(time, logMsg.getTimeOfLog());
        assertEquals(time, logMsg.getTimeServerReceivedLog());
    }

    @Test
    void testConstructorLogMessageTest() {
        final Instant time = Instant.now();
        final LogMessage logMsg = new LogMessage("ConstructorTest", LogLevel.INFO, UUID.randomUUID(), time);
        assertEquals("ConstructorTest", logMsg.getMessage());
        assertEquals(LogLevel.INFO, logMsg.getLoglevel());
        assertEquals(time, logMsg.getTimeOfLog());
    }

    @Test
    void getMessageTest() {
        final LogMessage logMsg = new LogMessage("messageTest", LogLevel.INFO, UUID.randomUUID());
        assertEquals("messageTest", logMsg.getMessage());
    }

    @Test
    void getLogLevelTest() {
        final LogMessage logMsg = new LogMessage("logLevelTest", LogLevel.WARN, UUID.randomUUID());
        assertEquals(LogLevel.WARN, logMsg.getLoglevel());
    }

    @Test
    void testGetApplicationId() {
        UUID uuid = UUID.randomUUID();
        final LogMessage logMsg = new LogMessage("logLevelTest", LogLevel.WARN, uuid);
        assertEquals(uuid, logMsg.getApplicationId());
    }

    @Test
    void getTimeOfLogTest() {
        final Instant timeOfLog = Instant.now();
        final LogMessage logMsg = new LogMessage("timeOfLogTest", LogLevel.INFO, UUID.randomUUID(), timeOfLog,
                timeOfLog);
        assertEquals(timeOfLog, logMsg.getTimeOfLog());
    }

    @Test
    void getTimeServerReceivedLogTest() {
        final Instant time = Instant.now();
        final LogMessage logMsg = new LogMessage("timeServerReceivedLogTest", LogLevel.INFO, UUID.randomUUID(), time,
                time);
        assertEquals(time, logMsg.getTimeServerReceivedLog());
    }

    @Test
    void toStringTest() {
        final Instant time = Instant.now();
        final LogMessage logMsg = new LogMessage("toStringTest", LogLevel.INFO, UUID.randomUUID(), time, time);
        assertTrue(logMsg.toString().contains("toStringTest"));
    }
}
