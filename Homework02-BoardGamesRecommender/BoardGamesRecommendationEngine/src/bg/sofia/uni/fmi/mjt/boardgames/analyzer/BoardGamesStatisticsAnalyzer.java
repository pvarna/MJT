package bg.sofia.uni.fmi.mjt.boardgames.analyzer;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardGamesStatisticsAnalyzer implements StatisticsAnalyzer {
    private static final String GAME_PARAM_NAME = "Game";
    private static final String CATEGORY_PARAM_NAME = "Category";
    private static final String N_PARAM_NAME = "N";

    Collection<BoardGame> boardGames;

    public BoardGamesStatisticsAnalyzer(Collection<BoardGame> games) {
        assertNonNull(games, GAME_PARAM_NAME);

        this.boardGames = games;
    }

    @Override
    public List<String> getNMostPopularCategories(int n) {
        assertNonNegative(n, N_PARAM_NAME);

        Map<String, Long> categories = this.getNumberOfGamesPerCategory();

        return categories.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public double getAverageMinAge() {
        return this.boardGames.stream()
                .mapToInt(BoardGame::minAge)
                .average()
                .orElse(0.0);
    }

    @Override
    public double getAveragePlayingTimeByCategory(String category) {
        assertNonNull(category, CATEGORY_PARAM_NAME);
        assertNonEmpty(category, CATEGORY_PARAM_NAME);

        return this.boardGames.stream()
                .filter(game -> game.categories().contains(category))
                .mapToInt(BoardGame::playingTimeMins)
                .average()
                .orElse(0.0);
    }

    @Override
    public Map<String, Double> getAveragePlayingTimeByCategory() {
        Set<String> categories = this.getNumberOfGamesPerCategory().keySet();

        Map<String, Double> result = new HashMap<>();
        for (String current : categories) {
            result.put(current, this.getAveragePlayingTimeByCategory(current));
        }

        return result;
    }

    private Map<String, Long> getNumberOfGamesPerCategory() {
        return this.boardGames.stream()
                .flatMap(game -> game.categories().stream()
                                    .map(category -> Map.entry(category, game)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.counting()));
    }

    private void assertNonNull(Object object, String paramName) {
        if (object == null) {
            throw new IllegalArgumentException(paramName + " should not be null");
        }
    }

    private void assertNonEmpty(String string, String paramName) {
        if (string.isEmpty())
        {
            throw new IllegalArgumentException(paramName + " should not be empty");
        }
    }

    private void assertNonNegative(int param, String paramName) {
        if (param < 0) {
            throw new IllegalArgumentException(paramName + " should not be negative");
        }
    }
}
