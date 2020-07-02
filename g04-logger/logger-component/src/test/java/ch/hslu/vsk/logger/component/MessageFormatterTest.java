package ch.hslu.vsk.logger.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ch.hslu.vsk.logger.api.LogLevel;
import ch.hslu.vsk.logger.common.LogMessage;
import ch.hslu.vsk.stringpersistor.api.PersistedString;

/**
 * @author adiwi
 */

public class MessageFormatterTest {

    @Test
    void testFormatInfo() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.INFO, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        assertEquals("[INFO] IchBinEinTest " + uuid, ps.getPayload());
        assertEquals(ps.getTimestamp(), timestampTest);
    }

    @Test
    void testFormatDebug() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.DEBUG, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        assertEquals("[DEBUG] IchBinEinTest " + uuid, ps.getPayload());
        assertEquals(ps.getTimestamp(), timestampTest);
    }

    @Test
    void testFormatWarn() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.WARN, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        assertEquals("[WARN] IchBinEinTest " + uuid, ps.getPayload());
        assertEquals(ps.getTimestamp(), timestampTest);
    }

    @Test
    void testFormatError() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.ERROR, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        assertEquals("[ERROR] IchBinEinTest " + uuid, ps.getPayload());
        assertEquals(ps.getTimestamp(), timestampTest);
    }

    @Test
    void testParseInfo() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.INFO, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        final LogMessage result = MessageFormatter.parse(ps);
        assertNotNull(result);
        assertEquals(LogLevel.INFO, result.getLoglevel());
        assertEquals("IchBinEinTest", result.getMessage());
        assertEquals(timestampTest, result.getTimeOfLog());
        assertEquals(uuid, result.getApplicationId());
    }

    @Test
    void testParseDebug() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.DEBUG, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        final LogMessage result = MessageFormatter.parse(ps);
        assertNotNull(result);
        assertEquals(LogLevel.DEBUG, result.getLoglevel());
        assertEquals("IchBinEinTest", result.getMessage());
        assertEquals(timestampTest, result.getTimeOfLog());
        assertEquals(uuid, result.getApplicationId());
    }

    @Test
    void testParseError() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.ERROR, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        final LogMessage result = MessageFormatter.parse(ps);
        assertNotNull(result);
        assertEquals(LogLevel.ERROR, result.getLoglevel());
        assertEquals("IchBinEinTest", result.getMessage());
        assertEquals(timestampTest, result.getTimeOfLog());
        assertEquals(uuid, result.getApplicationId());
    }

    @Test
    void testParseWarn() {
        final Instant timestampTest = Instant.now();
        final UUID uuid = UUID.randomUUID();
        final LogMessage msg = new LogMessage("IchBinEinTest", LogLevel.WARN, uuid, timestampTest);
        final PersistedString ps = MessageFormatter.format(msg);
        final LogMessage result = MessageFormatter.parse(ps);
        assertNotNull(result);
        assertEquals(LogLevel.WARN, result.getLoglevel());
        assertEquals("IchBinEinTest", result.getMessage());
        assertEquals(timestampTest, result.getTimeOfLog());
        assertEquals(uuid, result.getApplicationId());
    }
}
