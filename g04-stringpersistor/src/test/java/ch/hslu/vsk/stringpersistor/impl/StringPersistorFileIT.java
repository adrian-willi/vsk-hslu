package ch.hslu.vsk.stringpersistor.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.hslu.vsk.stringpersistor.api.PersistedString;

class StringPersistorFileIT {

    @Test
    void testSetEmptyFile() {
        StringPersistorFile spf = new StringPersistorFile();
        File nullFile = null;
        assertThrows(IllegalArgumentException.class, () -> spf.setFile(nullFile));
    }

    @Test
    void testFileDoesNotExist() {
        final File homedir = new File(System.getProperty("user.home"));
        final File notExistant = new File(homedir.getAbsolutePath() + File.separator + System.currentTimeMillis());
        final StringPersistorFile spf = new StringPersistorFile();
        assertThrows(IllegalArgumentException.class, () -> spf.setFile(notExistant));
    }

    @Test
    void testDirectoryDoesNotExist() {
        File tmpDir = new File("target/notExisting.txt");
        final StringPersistorFile spf = new StringPersistorFile();
        assertThrows(IllegalArgumentException.class, () -> spf.setFile(tmpDir));
    }

    @Test
    void testWriteWithoutFile() {
        StringPersistorFile spf = new StringPersistorFile();
        assertThrows(IllegalStateException.class, () -> spf.save(Instant.now(), "Can you save me?"));
    }

    @Test
    void testReadWithoutFile() {
        StringPersistorFile spf = new StringPersistorFile();
        assertThrows(IllegalStateException.class, () -> spf.get(1));
    }

    @Test
    void testSaveWithoutTimestamp() {
        StringPersistorFile spf = this.createPersistorFile();
        assertThrows(IllegalArgumentException.class,
                () -> spf.save(null, "Should throw IllegalArgumentException without Timestamp"));
    }

    @Test
    void testSaveWithoutPayload() {
        StringPersistorFile spf = this.createPersistorFile();
        assertThrows(IllegalArgumentException.class, () -> spf.save(Instant.now(), null));
    }

    @Test
    void testZeroString() throws IOException {
        StringPersistorFile spf = this.createPersistorFile();
        final File homedir = File.createTempFile("emptyLog", ".txt");
        spf.setFile(homedir);

        final List<PersistedString> persistedStrings = spf.get(0);
        assertTrue(persistedStrings.isEmpty());
    }

    @Test
    void testOnePersistedString() {
        StringPersistorFile spf = this.createPersistorFile();
        final List<PersistedString> stringsToPersist = this.persistedStringGenerator(1, 10);
        spf.save(stringsToPersist.get(0).getTimestamp(), stringsToPersist.get(0).getPayload());

        final List<PersistedString> persistedStrings = spf.get(1);
        assertEquals(stringsToPersist, persistedStrings);
    }

    @Test
    void testOnePersistedStringCsv() {
        StringPersistorFile spf = this.createPersistorFileCsv();
        final List<PersistedString> stringsToPersist = this.persistedStringGenerator(1, 10);
        spf.save(stringsToPersist.get(0).getTimestamp(), stringsToPersist.get(0).getPayload());

        final List<PersistedString> persistedStrings = spf.get(1);
        assertEquals(stringsToPersist, persistedStrings);
    }

    @Test
    void testOnePersistedStringJson() {
        StringPersistorFile spf = this.createPersistorFileJson();
        final List<PersistedString> stringsToPersist = this.persistedStringGenerator(1, 10);
        spf.save(stringsToPersist.get(0).getTimestamp(), stringsToPersist.get(0).getPayload());

        final List<PersistedString> persistedStrings = spf.get(1);
        assertEquals(stringsToPersist, persistedStrings);
    }

    @Test
    void testMultiplePersistedStrings() {
        StringPersistorFile spf = this.createPersistorFile();
        final List<PersistedString> stringsToPersist = this.persistedStringGenerator(1000, 20);
        for (final PersistedString ps : stringsToPersist) {
            spf.save(ps.getTimestamp(), ps.getPayload());
        }

        final List<PersistedString> persistedStrings = new ArrayList<>(spf.get(1000));
        assertEquals(stringsToPersist, persistedStrings);
    }

    @Test
    void testGet100ObjectsWith1000Chars() {
        long startTime;
        long finishTime;

        StringPersistorFile spf = this.createPersistorFile();
        final List<PersistedString> stringsToPersist = this.persistedStringGenerator(100, 1000);
        for (final PersistedString ps : stringsToPersist) {
            spf.save(ps.getTimestamp(), ps.getPayload());
        }

        startTime = System.currentTimeMillis();
        final List<PersistedString> persistedStrings = new ArrayList<>(spf.get(100));
        finishTime = System.currentTimeMillis();

        boolean lengthSatisfied = true;
        for (final PersistedString ps : persistedStrings) {
            if (ps.getPayload().length() < 1000) {
                lengthSatisfied = false;
                break;
            }
        }

        assertTrue(finishTime - startTime <= 200);
        assertEquals(100, persistedStrings.size());
        assertTrue(lengthSatisfied);
    }

    /**
     * Return a list with {@link PersistedString}. Payload is randomly created by
     * {@link StringPersistorFileIT#createRandomString(int)}.
     *
     * @param number       number of {@link PersistedString}
     * @param stringLength length of Payload
     * @return {@link List<>} of {@link PersistedString}
     */
    private List<PersistedString> persistedStringGenerator(final int number, final int stringLength) {
        final List<PersistedString> persistedStringList = new ArrayList<>();
        if (number == 0) {
            return persistedStringList;
        }
        for (int i = 1; i <= number; i++) {
            persistedStringList
                    .add(new PersistedString(Instant.now(), "Nr. " + i + " – " + createRandomString(stringLength)));
        }
        return persistedStringList;
    }

    /**
     * Create instance of StringPersitorFile with temporary file to store logs that
     * are created during test cases. Uses {@link FileFormatStrategySimpleText}.
     *
     * @return {@link StringPersistorFile} with default constructor.
     */
    private StringPersistorFile createPersistorFile() {
        final StringPersistorFile spf = new StringPersistorFile();
        try {
            spf.setFile(File.createTempFile("testMessages", ".txt"));
        } catch (final IOException e) {
            Assertions.fail(e.getMessage());
        }
        return spf;
    }

    /**
     * Create instance of StringPersitorFile with temporary file to store logs that
     * are created during test cases. Uses {@link FileFormatStrategyCsv}
     *
     * @return {@link StringPersistorFile} for a csv file.
     */
    private StringPersistorFile createPersistorFileCsv() {
        final StringPersistorFile spf = new StringPersistorFile(new FileFormatStrategyCsv("\n", ";"));
        try {
            spf.setFile(File.createTempFile("testMessages", ".csv"));
        } catch (final IOException e) {
            Assertions.fail(e.getMessage());
        }
        return spf;
    }

    /**
     * Create instance of StringPersitorFile with temporary file to store logs that
     * are created during test cases. Uses {@link FileFormatStrategyJson}
     *
     * @return {@link StringPersistorFile} for a csv file.
     */
    private StringPersistorFile createPersistorFileJson() {
        final StringPersistorFile spf = new StringPersistorFile(new FileFormatStrategyJson());
        try {
            spf.setFile(File.createTempFile("testMessages", ".json"));
        } catch (final IOException e) {
            Assertions.fail(e.getMessage());
        }
        return spf;
    }

    /**
     * Creates random String of given length. code copied from
     * https://www.baeldung.com/java-random-string and modified it.
     *
     * @param length Length of string
     * @return {@link String} with given length
     */
    private String createRandomString(final int length) {
        final int leftLimit = 97; // letter 'a'
        final int rightLimit = 122; // letter 'z'
        final Random random = new Random();
        final StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }
}