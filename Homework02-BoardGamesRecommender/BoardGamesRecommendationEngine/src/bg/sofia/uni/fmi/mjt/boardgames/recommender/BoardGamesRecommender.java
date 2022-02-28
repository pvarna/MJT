package bg.sofia.uni.fmi.mjt.boardgames.recommender;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class BoardGamesRecommender implements Recommender {
    private static final String DATASET_ZIP_FILE_NAME = "Dataset ZIP File";
    private static final String DATASET_FILE_NAME = "Dataset File Name";
    private static final String STOPWORDS_FILE_NAME = "Stopwords File";
    private static final String DATASET_READER_NAME = "Dataset Reader";
    private static final String STOPWORDS_READER_NAME = "Stopwords Reader";
    private static final String WRITER_NAME = "Writer";
    private static final String GAME_PARAM_NAME = "Game";
    private static final String N_PARAM_NAME = "N";

    private static final String PATTERN = "[\\p{IsPunctuation}\\p{IsWhite_Space}]+";
    private Set<BoardGame> boardGames;
    private Set<String> stopwords;
    private Map<String, Set<BoardGame>> index;

    /**
     * Constructs an instance using the provided file names.
     *
     * @param datasetZipFile  ZIP file containing the board games dataset file
     * @param datasetFileName the name of the dataset file (inside the ZIP archive)
     * @param stopwordsFile   the stopwords file
     */
    public BoardGamesRecommender(Path datasetZipFile, String datasetFileName, Path stopwordsFile) {
        assertNonNull(datasetZipFile, DATASET_ZIP_FILE_NAME);
        assertNonEmpty(datasetZipFile.toString(), DATASET_ZIP_FILE_NAME);
        assertNonNull(datasetFileName, DATASET_FILE_NAME);
        assertNonEmpty(datasetFileName, DATASET_FILE_NAME);
        assertNonNull(stopwordsFile, STOPWORDS_FILE_NAME);
        assertNonEmpty(stopwordsFile.toString(), STOPWORDS_FILE_NAME);

        try (var zipFile = new ZipFile(datasetZipFile.toString());
             InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(datasetFileName));
             var datasetReader = new BufferedReader(new InputStreamReader(inputStream));
             var stopwordsReader = new BufferedReader(new FileReader(stopwordsFile.toString()))) {

            this.loadData(datasetReader, stopwordsReader);

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Could not find files", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not load dataset", e);
        }

        this.loadIndex();
    }

    /**
     * Constructs an instance using the provided Reader streams.
     *
     * @param dataset   Reader from which the dataset can be read
     * @param stopwords Reader from which the stopwords list can be read
     */
    public BoardGamesRecommender(Reader dataset, Reader stopwords) {
        assertNonNull(dataset, DATASET_READER_NAME);
        assertNonNull(stopwords, STOPWORDS_READER_NAME);

        try (var datasetReader = new BufferedReader(dataset);
             var stopwordsReader = new BufferedReader(stopwords)) {

            this.loadData(datasetReader, stopwordsReader);

        } catch (IOException e) {
            throw new IllegalArgumentException("Could not load dataset", e);
        }

        this.loadIndex();
    }

    @Override
    public Collection<BoardGame> getGames() {
        return Collections.unmodifiableCollection(this.boardGames);
    }

    @Override
    public List<BoardGame> getSimilarTo(BoardGame game, int n) {
        this.assertNonNull(game, GAME_PARAM_NAME);
        this.assertNonNegative(n, N_PARAM_NAME);

        return this.boardGames.stream()
                .filter(g -> g.getDistanceTo(game) != -1.0)
                .sorted((Comparator.comparingDouble(g -> g.getDistanceTo(game))))
                .limit(n)
                .toList();
    }

    @Override
    public List<BoardGame> getByDescription(String... keywords) {
        Map<BoardGame, Integer> occurrences = new HashMap<>();

        Set<String> setKeywords = Arrays.stream(keywords)
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (String currentKeyword : setKeywords) {

            if (this.index.containsKey(currentKeyword)) {
                for (BoardGame currentGame : this.index.get(currentKeyword)) {
                    occurrences.putIfAbsent(currentGame, 0);
                    occurrences.put(currentGame, occurrences.get(currentGame) + 1);
                }
            }
        }

        return occurrences.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public void storeGamesIndex(Writer writer) {
        assertNonNull(writer, WRITER_NAME);

        try (var bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(this.indexToString());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException("A problem occurred while writing to a file", e);
        }
    }

    private void loadData(BufferedReader datasetReader, BufferedReader stopwordsReader) {
        this.boardGames = datasetReader.lines().skip(1).map(BoardGame::of).collect(Collectors.toSet());
        this.stopwords = new HashSet<>(stopwordsReader.lines().toList());
    }

    private void loadIndex() {
        this.index = new HashMap<>();

        for (BoardGame game : this.boardGames) {
            String [] separatedDescription = game.description().split(PATTERN);

            for (String current : separatedDescription) {
                if (!current.isBlank() && !this.stopwords.contains(current)) {
                    index.computeIfAbsent(current.toLowerCase(), set -> new HashSet<>()).add(game);
                }
            }
        }
    }

    private String indexToString() {
        return this.index.keySet().stream()
                .map(current -> current + ": " + index.get(current).stream()
                                                    .map(BoardGame::id)
                                                    .map(String::valueOf)
                                                    .collect(Collectors.joining(", ")))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private void assertNonNull(Object object, String paramName) {
        if (object == null) {
            throw new IllegalArgumentException(paramName + " should not be null");
        }
    }

    private void assertNonEmpty(String string, String paramName) {
        if (string.isEmpty()) {
            throw new IllegalArgumentException(paramName + " should not be empty");
        }
    }

    private void assertNonNegative(int param, String paramName) {
        if (param < 0) {
            throw new IllegalArgumentException(paramName + " should not be negative");
        }
    }
}
