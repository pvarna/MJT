package bg.sofia.uni.fmi.mjt.logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLoggerTest {
    private static final String PATH_STR = "localTesting";
    private static Logger logger;
    private static final long DEFAULT_MAX_FILE_SIZE_BYTES = 1024;
    private static final int NUMBER_OF_BYTES_PER_WARNING_WITH_MESSAGE_TEST = 70;

    private List<String> getfiles()
    {
        List<String> result = new ArrayList<>();
        Path path = Path.of(PATH_STR);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

            for (Path file : stream) {
                result.add(file.getFileName().toString());
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.out.println("Problem");
        }

        return result;
    }

    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectory(Path.of(PATH_STR));
        LoggerOptions options = new LoggerOptions(this.getClass(), PATH_STR);
        logger = new DefaultLogger(options);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Path path = Path.of(PATH_STR);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

            for (Path file : stream) {
                Files.delete(file);
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.out.println("Problem");
        }

        Files.delete(Path.of(PATH_STR));
    }

    @Test
    public void testLogWithNullLevel() {
        assertThrows(IllegalArgumentException.class, () -> logger.log(null, LocalDateTime.now(), ""));
    }

    @Test
    public void testLogWithNullDate() {
        assertThrows(IllegalArgumentException.class, () -> logger.log(Level.INFO, null, ""));
    }

    @Test
    public void testLogWithNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> logger.log(Level.INFO, LocalDateTime.now(), null));
    }

    @Test
    public void testLogWithEmptyMessage() {
        assertThrows(IllegalArgumentException.class, () -> logger.log(Level.INFO, LocalDateTime.now(), ""));
    }

    @Test
    public void testLogWithLowLevel() {
        logger.log(Level.DEBUG, LocalDateTime.now(), "test");
        assertEquals(0, this.getfiles().size());
    }

    @Test
    public void testLogWithOneLog() {
        logger.log(Level.WARN, LocalDateTime.now(), "test");

        List<String> files = this.getfiles();
        assertEquals(1, files.size());
        assertEquals("logs-0.txt", files.get(0));
    }

    @Test
    public void testLogWithLogsForTwoFiles() {
        int n = (int) (DEFAULT_MAX_FILE_SIZE_BYTES / NUMBER_OF_BYTES_PER_WARNING_WITH_MESSAGE_TEST) + 1;
        for (int i = 0; i < n + 1; ++i) {
            logger.log(Level.WARN, LocalDateTime.now(), "test");
        }

        List<String> files = this.getfiles();

        assertEquals(2, files.size());
        assertTrue(files.contains("logs-0.txt"));
        assertTrue(files.contains("logs-1.txt"));
    }
}