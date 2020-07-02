package ch.hslu.vsk.logger.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ch.hslu.vsk.logger.api.LogLevel;

class StringPersistorAdapterTest {

    @Test
    void testCreateCSVPersistorAndSaveData() throws IOException {
        String dir = System.getProperty("java.io.tmpdir");

        File csv = new File(dir + "/tmp.csv");
        Path csvPath = Paths.get(csv.getPath());

        csv.createNewFile();

        StringPersistorAdapter csvPersistor = StringPersistorAdapter.create(csv, "csv");
        LogMessage log = new LogMessage("csvTest", LogLevel.INFO, UUID.randomUUID());
        csvPersistor.save(log);

        assertTrue(Files.readString(csvPath).contains(";Log[ message: csvTest"));
        csv.delete();
    }

    @Test
    void testCreateJSONPersistorAndSaveData() throws IOException {
        String dir = System.getProperty("java.io.tmpdir");

        File json = new File(dir + "/tmp.json");
        Path jsonPath = Paths.get(json.getPath());

        json.createNewFile();

        StringPersistorAdapter csvPersistor = StringPersistorAdapter.create(json, "json");
        LogMessage log = new LogMessage("jsonTest", LogLevel.INFO, UUID.randomUUID());
        csvPersistor.save(log);

        assertTrue(Files.readString(jsonPath).contains("},\"payload\":\"Log[ message: jsonTest"));
        json.delete();
    }

    @Test
    void testCreatetxtPersistorAndSaveData() throws IOException {
        String dir = System.getProperty("java.io.tmpdir");

        File txt = new File(dir + "/tmp.txt");
        Path txtPath = Paths.get(txt.getPath());

        txt.createNewFile();

        StringPersistorAdapter csvPersistor = StringPersistorAdapter.create(txt, "txt");
        LogMessage log = new LogMessage("txtTest", LogLevel.INFO, UUID.randomUUID());
        csvPersistor.save(log);

        assertTrue(Files.readString(txtPath).contains("Log[ message: txtTest"));
        txt.delete();
    }

    /**
     * Old test
     *
     * @Test public void stringPersistorAdapterTest() throws IOException { final
     *       File tmpLogFile = File.createTempFile("StringPersistorAdpterTest",
     *       ".txt"); final LogPersistor logPersistor =
     *       StringPersistorAdapter.create(tmpLogFile, "default"); final LogMessage
     *       msg = new LogMessage("Ich test den Herr Adapter", LogLevel.DEBUG,
     *       UUID.randomUUID()); logPersistor.save(msg);
     *
     *       final BufferedReader reader = new BufferedReader(new
     *       FileReader(tmpLogFile)); final String expected = reader.readLine();
     *       final String actual = msg.getTimeOfLog() + " | " + msg.toString();
     *
     *       // substring to ignore the actual writetime which can differ on jenkins
     *       // testbuild assertEquals(expected.substring(27),
     *       actual.substring(27)); reader.close(); }
     */

}
