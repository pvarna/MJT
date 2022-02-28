package bg.sofia.uni.fmi.mjt.boardgames.analyzer;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardGamesStatisticsAnalyzerTest {
    private static Collection<BoardGame> boardGames;
    private static StatisticsAnalyzer analyzer;
    private static StatisticsAnalyzer emptyAnalyzer;

    @BeforeAll
    public static void setUp() {
        initBoardGames();

        analyzer = new BoardGamesStatisticsAnalyzer(boardGames);
        emptyAnalyzer = new BoardGamesStatisticsAnalyzer(Collections.emptyList());
    }

    @Test
    public void testConstructorWithNullGames() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesStatisticsAnalyzer(null));
    }

    @Test
    public void testGetNMostPopularCategoriesWithNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getNMostPopularCategories(-2));
    }

    @Test
    public void testGetNMostPopularCategories() {
        String assertMessage = "The returned categories do not match the correct most popular categories from the dataset";

        List<String> expected = List.of("Card Game", "Movies / TV / Radio theme", "Number", "Bluffing");
        List<String> actual = analyzer.getNMostPopularCategories(4);

        assertEquals("Card Game", actual.get(0));
        assertTrue(expected.containsAll(actual), assertMessage);
        assertTrue(actual.containsAll(expected), assertMessage);
    }

    @Test
    public void testGetAverageMinAgeWithNoGames() {
        String assertMessage = "In case the dataset contains no games, it should return 0.0";

        assertEquals(0.0, emptyAnalyzer.getAverageMinAge(), assertMessage);
    }

    @Test
    public void testGetAverageMinAgeWithGames() {
        String assertMessage = "Wrong average min age";

        double expected = (8 + 8 + 12 + 8 + 6 + 12 + 8 + 10 + 8 + 6) / 10.0;
        double actual = analyzer.getAverageMinAge();

        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testGetAveragePlayingTimeByCategoryWithNullCategory() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getAveragePlayingTimeByCategory(null));
    }

    @Test
    public void testGetAveragePlayingTimeByCategoryWithEmptyCategory() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getAveragePlayingTimeByCategory(""));
    }

    @Test
    public void testGetAveragePlayingTimeByCategoryWithNoGamesWithSuchCategory() {
        String assertMessage = "In case the dataset contains no games in the specified category, it should return 0.0";

        assertEquals(0.0, emptyAnalyzer.getAveragePlayingTimeByCategory("wrongCategory"), assertMessage);
    }

    @Test
    public void testGetAveragePlayingTimeByCategoryWithExistingCategory() {
        String assertMessage = "Wrong average playing game for the given category";

        double expected = (20 + 60 + 60 + 30 + 20 + 30) / 6.0;
        double actual = analyzer.getAveragePlayingTimeByCategory("Card Game");
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testGetAveragePlayingTimeByCategoryWithoutArgument() {
        String assertMessage = "The returned output is not the wanted result";

        Map<String, Double> expected = Map.ofEntries(
                Map.entry("Card Game", (20 + 60 + 60 + 30 + 20 + 30) / 6.0),
                Map.entry("Economic", 180.0),
                Map.entry("Negotiation", 180.0),
                Map.entry("Bluffing", (60 + 60) / 2.0),
                Map.entry("City Building", 45.0),
                Map.entry("Medieval", 45.0),
                Map.entry("Territory Building", 45.0),
                Map.entry("Abstract Strategy", 60.0),
                Map.entry("Deduction", 60.0),
                Map.entry("Horror", 60.0),
                Map.entry("Party Game", 60.0),
                Map.entry("Math", 30.0),
                Map.entry("Number", (30 + 30) / 2.0),
                Map.entry("Word Game", 90.0),
                Map.entry("Collectible Components", 20.0),
                Map.entry("Fantasy", 20.0),
                Map.entry("Fighting", 20.0),
                Map.entry("Movies / TV / Radio theme", (20 + 30) / 2.0),
                Map.entry("Comic Book / Strip", 30.0));

        Map<String, Double> actual = analyzer.getAveragePlayingTimeByCategory();

        Set<Map.Entry<String, Double>> expectedSet = expected.entrySet();
        Set<Map.Entry<String, Double>> actualSet = actual.entrySet();

        assertTrue(expectedSet.containsAll(actualSet), assertMessage);
        assertTrue(actualSet.containsAll(expectedSet), assertMessage);
    }

    private static void initBoardGames() {
        boardGames = List.of (
                BoardGame.of("1;4;8;2;Belote;20;Card Game;Partnerships,Trick-taking;fun card game played on table and played by young and old"),
                BoardGame.of("2;8;8;2;Monopoly;180;Economic,Negotiation;Auction/Bidding,Player Elimination,Roll / Spin and Move,Set Collection,Stock Holding,Trading; great board game to play with your friends"),
                BoardGame.of("3;10;12;2;Poker;60;Bluffing,Card Game;Betting/Wagering,Player Elimination,Set Collection;Poker is played with a standard deck of 52 cards"),
                BoardGame.of("4;5;8;2;Carcassonne;45;City Building,Medieval,Territory Building;Area Control / Area Influence,Tile Placement;very fun and entertaining game to play with friends"),
                BoardGame.of("5;2;6;2;Chess;60;Abstract Strategy;Grid Movement;Chess is a two-player, abstract strategy board game"),
                BoardGame.of("6;12;12;4;Stay Away!;60;Bluffing,Card Game,Deduction,Horror,Party Game;Card Drafting,Co-operative Play,Partnerships,Player Elimination,Role Playing,Storytelling;Party game to have fun with your mates"),
                BoardGame.of("7;10;8;2;Blackjack;30;Card Game,Math,Number;Betting/Wagering,Press Your Luck;Blackjack is played with a standard deck of 52 playing cards."),
                BoardGame.of("8;4;10;2;Scrabble;90;Word Game;Hand Management,Tile Placement;In this classic word game, players use their seven drawn letter-tiles to write words"),
                BoardGame.of("9;2;8;2;Yu-Gi-Oh! Trading Card Game;20;Card Game,Collectible Components,Fantasy,Fighting,Movies / TV / Radio theme;Hand Management,Set Collection,Variable Player Powers;Yu-Gi-Oh! Trading Card Game is a collectible card game based on the hit TV series' own collectible card game."),
                BoardGame.of("10;10;6;2;UNO;30;Card Game,Comic Book / Strip,Movies / TV / Radio theme,Number;Hand Management;Players race to empty their hands and catch opposing players with cards left in theirs, which score points")
        );
    }
}