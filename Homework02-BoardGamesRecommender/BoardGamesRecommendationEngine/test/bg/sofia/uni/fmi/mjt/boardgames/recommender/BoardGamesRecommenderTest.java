package bg.sofia.uni.fmi.mjt.boardgames.recommender;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BoardGamesRecommenderTest {
    private static Recommender recommenderFromZip;
    private static Recommender recommenderFromReader;
    private static List<BoardGame> boardGames;

    @BeforeAll
    public static void setUp() throws IOException {
        Reader boardGamesStream = initBoardGamesStream();

        try (var reader = new BufferedReader(boardGamesStream)) {
            boardGames = reader.lines().skip(1).map(BoardGame::of).toList();
        }

        recommenderFromReader = new BoardGamesRecommender(initBoardGamesStream(), initStopwordsStream());
        recommenderFromZip = new BoardGamesRecommender(Path.of("data.zip"), "data.csv", Path.of("stopwords.txt"));
    }

    @Test
    public void testZipConstructorWithNullDatasetZipFile() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(null, "file.csv", Path.of("file.txt")));
    }

    @Test
    public void testZipConstructorWithEmptyDatasetZipFile() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(Path.of(""), "file.csv", Path.of("file.txt")));
    }

    @Test
    public void testZipConstructorWithNullDatasetFileName() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(Path.of("data.zip"), null, Path.of("file.txt")));
    }

    @Test
    public void testZipConstructorWithEmptyDatasetFileName() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(Path.of("data.zip"), "", Path.of("file.txt")));
    }

    @Test
    public void testZipConstructorWithNullStopwordsFile() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(Path.of("data.zip"), "file.csv", null));
    }

    @Test
    public void testZipConstructorWithEmptyStopwordsFile() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(Path.of("data.zip"), "file.csv", Path.of("")));
    }

    @Test
    public void testReadersConstructorWithNullDatasetReader() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(null, new StringReader("test")));
    }

    @Test
    public void testReadersConstructorWithNullStopwordsReader() {
        assertThrows(IllegalArgumentException.class, () -> new BoardGamesRecommender(new StringReader("test"), null));
    }

    @Test
    public void testIfExistingDatasetIsLoadedCorrectlyFromZip() {
        String assertMessage = "Number of games in the BoardGamesRecommender loaded from ZIP does not match the number of games in the dataset.";

        int expected = boardGames.size();
        int actual = recommenderFromZip.getGames().size();

        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testIfExistingDatasetIsLoadedCorrectlyFromReader() {
        String assertMessage = "Number of games in the BoardGamesRecommender loaded from Reader does not match the number of games in the dataset.";

        int expected = boardGames.size();
        int actual = recommenderFromReader.getGames().size();

        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testGetGamesUnmodifiable() {
        String assertMessage = "Returned collection is not unmodifiable";
        Collection<BoardGame> actual = recommenderFromReader.getGames();

        try {
            actual.clear();
        } catch (UnsupportedOperationException e) {
            return;
        }

        fail(assertMessage);
    }

    @Test
    public void testGetSimilarToWithNullGame() {
        assertThrows(IllegalArgumentException.class, () -> recommenderFromReader.getSimilarTo(null, 5));
    }

    @Test
    public void testGetSimilarToWithNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> recommenderFromReader.getSimilarTo(BoardGame.of("test"), -2));
    }

    @Test
    public void testGetSimilarTo() {
        String assertMessage = "The returned games do not match the correct games from the dataset";

        BoardGame gameToSearchSimilarGamesOf = BoardGame.of("1;4;8;2;Belote;20;Card Game;Partnerships,Trick-taking;fun card game played on table and played by young and old");

        List<BoardGame> expected = List.of (
                BoardGame.of("9;2;8;2;Yu-Gi-Oh! Trading Card Game;20;Card Game,Collectible Components,Fantasy,Fighting,Movies / TV / Radio theme;Hand Management,Set Collection,Variable Player Powers;Yu-Gi-Oh! Trading Card Game is a collectible card game based on the hit TV series' own collectible card game."),
                BoardGame.of("7;10;8;2;Blackjack;30;Card Game,Math,Number;Betting/Wagering,Press Your Luck;Blackjack is played with a standard deck of 52 playing cards."),
                BoardGame.of("10;10;6;2;UNO;30;Card Game,Comic Book / Strip,Movies / TV / Radio theme,Number;Hand Management;Players race to empty their hands and catch opposing players with cards left in theirs, which score points")
        );

        List<BoardGame> actual = recommenderFromReader.getSimilarTo(gameToSearchSimilarGamesOf, 3);

        assertIterableEquals(expected, actual, assertMessage);
    }

    @Test
    public void testGetByDescription() {
        String assertMessage = "The returned games do not match the correct games from the dataset";

        List<BoardGame> expected = List.of (
                BoardGame.of("4;5;8;2;Carcassonne;45;City Building,Medieval,Territory Building;Area Control / Area Influence,Tile Placement;very fun and entertaining game to play with friends"),
                BoardGame.of("2;8;8;2;Monopoly;180;Economic,Negotiation;Auction/Bidding,Player Elimination,Roll / Spin and Move,Set Collection,Stock Holding,Trading; great board game to play with your friends"),
                BoardGame.of("1;4;8;2;Belote;20;Card Game;Partnerships,Trick-taking;fun card game played on table and played by young and old"),
                BoardGame.of("6;12;12;4;Stay Away!;60;Bluffing,Card Game,Deduction,Horror,Party Game;Card Drafting,Co-operative Play,Partnerships,Player Elimination,Role Playing,Storytelling;Party game to have fun with your mates")
        );

        List<BoardGame> actual = recommenderFromReader.getByDescription("fun", "friends");

        assertEquals(4, actual.get(0).id(), assertMessage);
        assertTrue(expected.containsAll(actual), assertMessage);
        assertTrue(actual.containsAll(expected), assertMessage);
    }

    @Test
    public void testStoreGamesIndexWithNullWriter() {
        assertThrows(IllegalArgumentException.class, () -> recommenderFromReader.storeGamesIndex(null));
    }

    @Test
    public void testStoreGamesIndex() {
        String assertMessage = "The output does not match the correct index";

        Writer myWriter = new StringWriter();

        String[] expected = """
                        standard: 3, 7
                        play: 4, 2
                        tv: 9
                        hands: 10
                        collectible: 9
                        use: 8
                        deck: 3, 7
                        seven: 8
                        points: 10
                        empty: 10
                        score: 10
                        tiles: 8
                        hit: 9
                        catch: 10
                        yu: 9
                        drawn: 8
                        write: 8
                        52: 3, 7
                        based: 9
                        mates: 6
                        chess: 5
                        in: 8
                        players: 8, 10
                        old: 1
                        poker: 3
                        blackjack: 7
                        friends: 4, 2
                        left: 10
                        letter: 8
                        playing: 7
                        word: 8
                        party: 6
                        card: 9, 1
                        trading: 9
                        game: 4, 2, 9, 8, 1, 5, 6
                        cards: 3, 7, 10
                        entertaining: 4
                        young: 1
                        two: 5
                        oh: 9
                        table: 1
                        player: 5
                        opposing: 10
                        gi: 9
                        race: 10
                        words: 8
                        abstract: 5
                        great: 2
                        played: 3, 7, 1
                        classic: 8
                        series: 9
                        strategy: 5
                        board: 2, 5
                        fun: 4, 1, 6""".split(System.lineSeparator());

        Set<String> expectedSet = Set.of(expected);

        recommenderFromReader.storeGamesIndex(myWriter);

        String[] actual = myWriter.toString().split(System.lineSeparator());
        Set<String> actualSet = Set.of(actual);

        assertTrue(expectedSet.containsAll(actualSet), assertMessage);
        assertTrue(actualSet.containsAll(expectedSet), assertMessage);
    }

    private static Reader initBoardGamesStream() {
        String[] boardGames = {
                "",
                "1;4;8;2;Belote;20;Card Game;Partnerships,Trick-taking;fun card game played on table and played by young and old",
                "2;8;8;2;Monopoly;180;Economic,Negotiation;Auction/Bidding,Player Elimination,Roll / Spin and Move,Set Collection,Stock Holding,Trading; great board game to play with your friends",
                "3;10;12;2;Poker;60;Bluffing,Card Game;Betting/Wagering,Player Elimination,Set Collection;Poker is played with a standard deck of 52 cards",
                "4;5;8;2;Carcassonne;45;City Building,Medieval,Territory Building;Area Control / Area Influence,Tile Placement;very fun and entertaining game to play with friends",
                "5;2;6;2;Chess;60;Abstract Strategy;Grid Movement;Chess is a two-player, abstract strategy board game",
                "6;12;12;4;Stay Away!;60;Bluffing,Card Game,Deduction,Horror,Party Game;Card Drafting,Co-operative Play,Partnerships,Player Elimination,Role Playing,Storytelling;Party game to have fun with your mates",
                "7;10;8;2;Blackjack;30;Card Game,Math,Number;Betting/Wagering,Press Your Luck;Blackjack is played with a standard deck of 52 playing cards.",
                "8;4;10;2;Scrabble;90;Word Game;Hand Management,Tile Placement;In this classic word game, players use their seven drawn letter-tiles to write words",
                "9;2;8;2;Yu-Gi-Oh! Trading Card Game;20;Card Game,Collectible Components,Fantasy,Fighting,Movies / TV / Radio theme;Hand Management,Set Collection,Variable Player Powers;Yu-Gi-Oh! Trading Card Game is a collectible card game based on the hit TV series' own collectible card game.",
                "10;10;6;2;UNO;30;Card Game,Comic Book / Strip,Movies / TV / Radio theme,Number;Hand Management;Players race to empty their hands and catch opposing players with cards left in theirs, which score points"
        };

        return new StringReader(Arrays.stream(boardGames).collect(Collectors.joining(System.lineSeparator())));
    }

    private static Reader initStopwordsStream() {
        String[] stopwords = ("""
                        a
                        about
                        above
                        after
                        again
                        against
                        all
                        am
                        an
                        and
                        any
                        are
                        aren't
                        as
                        at
                        be
                        because
                        been
                        before
                        being
                        below
                        between
                        both
                        but
                        by
                        can't
                        cannot
                        could
                        couldn't
                        did
                        didn't
                        do
                        does
                        doesn't
                        doing
                        don't
                        down
                        during
                        each
                        few
                        for
                        from
                        further
                        had
                        hadn't
                        has
                        hasn't
                        have
                        haven't
                        having
                        he
                        he'd
                        he'll
                        he's
                        her
                        here
                        here's
                        hers
                        herself
                        him
                        himself
                        his
                        how
                        how's
                        i
                        i'd
                        i'll
                        i'm
                        i've
                        if
                        in
                        into
                        is
                        isn't
                        it
                        it's
                        its
                        itself
                        let's
                        me
                        more
                        most
                        mustn't
                        my
                        myself
                        no
                        nor
                        not
                        of
                        off
                        on
                        once
                        only
                        or
                        other
                        ought
                        our
                        ours
                        ourselves
                        out
                        over
                        own
                        same
                        shan't
                        she
                        she'd
                        she'll
                        she's
                        should
                        shouldn't
                        so
                        some
                        such
                        than
                        that
                        that's
                        the
                        their
                        theirs
                        them
                        themselves
                        then
                        there
                        there's
                        these
                        they
                        they'd
                        they'll
                        they're
                        they've
                        this
                        those
                        through
                        to
                        too
                        under
                        until
                        up
                        very
                        was
                        wasn't
                        we
                        we'd
                        we'll
                        we're
                        we've
                        were
                        weren't
                        what
                        what's
                        when
                        when's
                        where
                        where's
                        which
                        while
                        who
                        who's
                        whom
                        why
                        why's
                        with
                        won't
                        would
                        wouldn't
                        you
                        you'd
                        you'll
                        you're
                        you've
                        your
                        yours
                        yourself
                        yourselves""").split(System.lineSeparator());

        return new StringReader(Arrays.stream(stopwords).collect(Collectors.joining(System.lineSeparator())));
    }
}