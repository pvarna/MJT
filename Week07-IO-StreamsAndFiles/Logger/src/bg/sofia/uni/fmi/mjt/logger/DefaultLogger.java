package bg.sofia.uni.fmi.mjt.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DefaultLogger implements Logger {
    private final LoggerOptions options;
    private Path currentPath;
    private int currentIndex;

    public DefaultLogger(LoggerOptions options) {
        this.options = options;
        this.currentPath = Paths.get(options.getDirectory(), "logs-0.txt");
    }

    @Override
    public void log(Level level, LocalDateTime timestamp, String message) {
        if (level == null || timestamp == null || message == null || message.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (level.getLevel() < this.options.getMinLogLevel().getLevel()) {
            if (this.options.shouldThrowErrors())
            {
                throw new LogException("The currently configured minimum log level " +
                                        "is higher than the provided log level");
            }
            return;
        }

        try {
            if (Files.exists(this.currentPath) && Files.size(this.currentPath) >= this.options.getMaxFileSizeBytes()) {
                this.updateCurrentPath();
            }
        } catch (IOException e) {
            throw new LogException("Problem while getting the size of the file", e);
        }

        String textToAdd = "[" + level + "]|" + timestamp.format(DateTimeFormatter.ISO_DATE_TIME) + "|"
                            + this.options.getClazz().getPackageName() + "|" + message + System.lineSeparator();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.currentPath.toFile(), true))) {
            bufferedWriter.write(textToAdd);
            bufferedWriter.flush();
        } catch (IOException e) {
            if (this.options.shouldThrowErrors()) {
                throw new LogException("Problem while writing to the file");
            }
        }
    }

    @Override
    public LoggerOptions getOptions() {
        return this.options;
    }

    @Override
    public Path getCurrentFilePath() {
        return this.currentPath;
    }

    private void updateCurrentPath() {
        String newFileName = ("logs-" + (++this.currentIndex) + ".txt");
        this.currentPath = Paths.get(options.getDirectory(), newFileName);
    }
}
