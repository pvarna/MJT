package bg.sofia.uni.fmi.mjt.logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLogParserTest {
    private static final String PATH_STR = "localTesting";
    private static LogParser parser;
    private final Log log1 = new Log(Level.DEBUG, LocalDateTime.of(1969, 7, 29, 0, 0, 0, 0), this.getClass().getPackageName(), "Mom was born");
    private final Log log2 = new Log(Level.ERROR, LocalDateTime.of(1981, 8, 8, 0, 0, 0, 0), this.getClass().getPackageName(), "Federer was born");
    private final Log log3 = new Log(Level.WARN, LocalDateTime.of(1991, 5, 16, 0, 0, 0, 0), this.getClass().getPackageName(), "Grisho was born");
    private final Log log4 = new Log(Level.INFO, LocalDateTime.of(2001, 6, 11, 11, 50, 0, 0), this.getClass().getPackageName(), "Pesho was born");
    private final Log log5 = new Log(Level.INFO, LocalDateTime.of(2022,1,1,0,0,0,0), this.getClass().getPackageName(), "Happy New 2022!");

    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectory(Path.of(PATH_STR));
        Logger logger = new DefaultLogger(new LoggerOptions(this.getClass(), PATH_STR));
        logger.log(log1.level(), log1.timestamp(), log1.message());
        logger.log(log2.level(), log2.timestamp(), log2.message());
        logger.log(log3.level(), log3.timestamp(), log3.message());
        logger.log(log4.level(), log4.timestamp(), log4.message());
        logger.log(log5.level(), log5.timestamp(), log5.message());

        parser = new DefaultLogParser(Path.of(PATH_STR, "logs-0.txt"));
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
    void testGetLogsWithLevelWithNull() {
        assertThrows(IllegalArgumentException.class, () -> parser.getLogs(null));
    }

    @Test
    void testGetLogsWithLevel() {
        List<Log> result = parser.getLogs(Level.INFO);

        assertEquals(2, result.size());

        assertEquals(log4, result.get(0));
        assertEquals(log5, result.get(1));
    }

    @Test
    void testGetLogsWithTimeWithNull() {
        assertThrows(IllegalArgumentException.class, () -> parser.getLogs(null, LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class, () -> parser.getLogs(LocalDateTime.now(), null));
    }

    @Test
    void testGetLogsWithTime() {
        List<Log> result = parser.getLogs(LocalDateTime.of(1969, 7, 29, 0, 0, 0, 0),
                                          LocalDateTime.of(2001, 6, 11, 11, 50, 0, 0));

        assertEquals(3, result.size());

        assertEquals(log2, result.get(0));
        assertEquals(log3, result.get(1));
        assertEquals(log4, result.get(2));
    }

    @Test
    void testGetLogsTailWithNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> parser.getLogsTail(-1));
    }

    @Test
    void testGetLogsTailWithZero() {
        List<Log> result = parser.getLogsTail(0);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLogsTailWithNumberBiggerThanTheSize() {
        List<Log> result = parser.getLogsTail(10);

        assertEquals(4, result.size());

        assertEquals(log2, result.get(0));
        assertEquals(log3, result.get(1));
        assertEquals(log4, result.get(2));
        assertEquals(log5, result.get(3));
    }

    @Test
    void testGetLogsTailWithNumberSmallerThanTheSize() {
        List<Log> result = parser.getLogsTail(2);

        assertEquals(2, result.size());

        assertEquals(log4, result.get(0));
        assertEquals(log5, result.get(1));
    }
}