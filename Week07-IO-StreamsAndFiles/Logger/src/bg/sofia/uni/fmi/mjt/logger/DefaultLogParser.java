package bg.sofia.uni.fmi.mjt.logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DefaultLogParser implements LogParser {
    private static final int LEVEL = 0;
    private static final int TIMESTAMP = 1;
    private static final int PACKAGE_NAME = 2;
    private static final int MESSAGE = 3;

    private final Path logsFilePath;

    public DefaultLogParser(Path logsFilePath) {
        this.logsFilePath = logsFilePath;
    }

    private List<Log> logs()
    {
        List<Log> result = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(logsFilePath.toFile()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\|");
                result.add(new Log(this.getLevel(parts[LEVEL]), LocalDateTime.parse(parts[TIMESTAMP],
                        DateTimeFormatter.ISO_DATE_TIME), parts[PACKAGE_NAME], parts[MESSAGE]));
            }
        } catch (IOException e) {
            result.clear();
        }

        return result;
    }

    @Override
    public List<Log> getLogs(Level level) {
        if (level == null) {
            throw new IllegalArgumentException();
        }

        List<Log> allLogs = this.logs();
        List<Log> result = new ArrayList<>();

        for (Log current : allLogs) {
            if (level == current.level()) {
                result.add(current);
            }
        }

        return result;
    }

    @Override
    public List<Log> getLogs(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException();
        }

        List<Log> allLogs = this.logs();
        List<Log> result = new ArrayList<>();

        for (Log current : allLogs) {
            if (!current.timestamp().isBefore(from) && !current.timestamp().isAfter(to)) {
                result.add(current);
            }
            if (current.timestamp().isAfter(to)) {
                break;
            }
        }

        return result;
    }

    @Override
    public List<Log> getLogsTail(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        List<Log> allLogs = this.logs();

        if (n > allLogs.size()) {
            return allLogs;
        }

        List<Log> result = new ArrayList<>();

        int size = allLogs.size();
        for (int i = size - n; i < size; ++i) {
            result.add(allLogs.get(i));
        }

        return result;
    }

    private Level getLevel(String string) {
        String level = string.substring(1, string.length() - 1);
        return switch (level) {
            case "DEBUG" -> Level.DEBUG;
            case "INFO" -> Level.INFO;
            case "WARN" -> Level.WARN;
            case "ERROR" -> Level.ERROR;
            default -> null;
        };
    }
}
