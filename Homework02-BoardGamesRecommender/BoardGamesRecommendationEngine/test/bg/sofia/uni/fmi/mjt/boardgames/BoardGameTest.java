package bg.sofia.uni.fmi.mjt.boardgames;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardGameTest {
    @Test
    void testOf() {
        String gameStr = "734;5;8;2;Carcassonne;45;City Building,Medieval,Territory Building;Area Control / Area Influence,Tile Placement;Carcassonne is a tile-placement game in which the players draw and place a tile with a piece of southern French landscape on it. The tile might feature a city, a road, a cloister, grassland or some combination thereof, and it must be placed adjacent to tiles that have already been played, in such a way that cities are connected to cities, roads to roads, etcetera. Having placed a tile, the player can then decide to place one of his meeples on one of the areas on it: on the city as a knight, on the road as a robber, on a cloister as a monk, or on the grass as a farmer. When that area is complete, that meeple scores points for its owner...During a game of Carcassonne, players are faced with decisions like: &quot Is it really worth putting my last meeple there?&quot  or &quot Should I use this tile to expand my city, or should I place it near my opponent instead, giving him a hard time to complete his project and score points?&quot  Since players place only one tile and have the option to place one meeple on it, turns proceed quickly even if it is a game full of options and possibilities...";

        int expectedIndex = 734;
        int expectedMaxPlayers = 5;
        int expectedMinAge = 8;
        int expectedMinPlayers = 2;
        String expectedName = "Carcassonne";
        int expectedPlayingTime = 45;
        Set<String> expectedCategories = Set.of ("City Building", "Medieval", "Territory Building");
        Set<String> expectedMechanics = Set.of("Area Control / Area Influence", "Tile Placement");
        String expectedDescription = "Carcassonne is a tile-placement game in which the players draw and place a tile with a piece of southern French landscape on it. The tile might feature a city, a road, a cloister, grassland or some combination thereof, and it must be placed adjacent to tiles that have already been played, in such a way that cities are connected to cities, roads to roads, etcetera. Having placed a tile, the player can then decide to place one of his meeples on one of the areas on it: on the city as a knight, on the road as a robber, on a cloister as a monk, or on the grass as a farmer. When that area is complete, that meeple scores points for its owner...During a game of Carcassonne, players are faced with decisions like: &quot Is it really worth putting my last meeple there?&quot  or &quot Should I use this tile to expand my city, or should I place it near my opponent instead, giving him a hard time to complete his project and score points?&quot  Since players place only one tile and have the option to place one meeple on it, turns proceed quickly even if it is a game full of options and possibilities...";

        BoardGame actual = BoardGame.of(gameStr);

        assertEquals(expectedIndex, actual.id(), "Wrong id");
        assertEquals(expectedMaxPlayers, actual.maxPlayers(), "Wrong number of max players");
        assertEquals(expectedMinAge, actual.minAge(), "Wrong min age");
        assertEquals(expectedMinPlayers, actual.minPlayers(), "Wrong number of min players");
        assertEquals(expectedName, actual.name(), "Wrong name");
        assertEquals(expectedPlayingTime, actual.playingTimeMins());
        //assertTrue(expectedCategories.containsAll(actual.categories()), "Wrong categories");
        //assertTrue(actual.categories().containsAll(expectedCategories), "Wrong categories");
        assertIterableEquals(expectedCategories, actual.categories());
        assertTrue(expectedMechanics.containsAll(actual.mechanics()), "Wrong mechanics");
        assertTrue(actual.mechanics().containsAll(expectedMechanics), "Wrong mechanics");
        assertEquals(expectedDescription, actual.description());
    }

    @Test
    void testGetDistanceToWithEqualGames() {
        String gameStr = "734;5;8;2;Carcassonne;45;City Building,Medieval,Territory Building;Area Control / Area Influence,Tile Placement;Carcassonne is a tile-placement game in which the players draw and place a tile with a piece of southern French landscape on it. The tile might feature a city, a road, a cloister, grassland or some combination thereof, and it must be placed adjacent to tiles that have already been played, in such a way that cities are connected to cities, roads to roads, etcetera. Having placed a tile, the player can then decide to place one of his meeples on one of the areas on it: on the city as a knight, on the road as a robber, on a cloister as a monk, or on the grass as a farmer. When that area is complete, that meeple scores points for its owner...During a game of Carcassonne, players are faced with decisions like: &quot Is it really worth putting my last meeple there?&quot  or &quot Should I use this tile to expand my city, or should I place it near my opponent instead, giving him a hard time to complete his project and score points?&quot  Since players place only one tile and have the option to place one meeple on it, turns proceed quickly even if it is a game full of options and possibilities...";

        BoardGame game = BoardGame.of(gameStr);

        assertEquals(-1.0, game.getDistanceTo(game));
    }

    @Test
    void testGetDistanceToWithGamesWithNoCommonCategories() {
        String carcassonneStr = "734;5;8;2;Carcassonne;45;City Building,Medieval,Territory Building;Area Control / Area Influence,Tile Placement;Carcassonne is a tile-placement game in which the players draw and place a tile with a piece of southern French landscape on it. The tile might feature a city, a road, a cloister, grassland or some combination thereof, and it must be placed adjacent to tiles that have already been played, in such a way that cities are connected to cities, roads to roads, etcetera. Having placed a tile, the player can then decide to place one of his meeples on one of the areas on it: on the city as a knight, on the road as a robber, on a cloister as a monk, or on the grass as a farmer. When that area is complete, that meeple scores points for its owner...During a game of Carcassonne, players are faced with decisions like: &quot Is it really worth putting my last meeple there?&quot  or &quot Should I use this tile to expand my city, or should I place it near my opponent instead, giving him a hard time to complete his project and score points?&quot  Since players place only one tile and have the option to place one meeple on it, turns proceed quickly even if it is a game full of options and possibilities...";
        String beloteStr = "14469;4;8;2;Belote;20;Card Game;Partnerships,Trick-taking;Belote is probably the most played game in France, essentially by adults (youngsters will probably play the &quot coinche&quot  variant or Tarot during high school)...It has been known for more than 75 years in France, and is played as a family game or as a money game (even if this last thing has been prohibited in bistros)..A 32 card deck is needed ( that means a poker deck without cards 2 to 6 and jokers). .Belote is a point-trick taking game for 4 players...Players are by teams of 2. Partners sit at opposite seats..The dealer gives everybody 3 cards face down, then 2..After that, the first card of the deck is turned faced up. Its suits will serve as trumps..Then, each player announces if he takes or not. If a polayer takes, he receives the face up card. Then the dealer deals 3 cards to everyone except the taker who receives only two. That makes for 8 cards for everyone..If no players takes, we take another round were each player announces if he takes, but also the suit that will serves as trumps (instead of the one of the face up card)...After that, it is similar to Tarot: first player plays a card, then each player can play a card of the same suit or a trump. If you can't play the asked suit, you have to play a trump. If your partner wins the &quot pli&quot , you don't have to play a trump..When 8 &quot plis&quot  have been played (when no one has card in the hand), points are counted. The team with the most points win...Points values and order of the cards vary from other card games:.....    .         Trump suit ..         Point Value..         Other suits..         Point Value ..    .    .         J ..         20..         A..         11..    .    .         9..         14..         10..         10..    .    .         A..         11..         K..         4..    .    .         10..         10..         Q..         3..    .    .         K..         4..         J..         2..    .    .         Q..         3..         9..         0..    .    .         8..         0..         8..         0..    .    .         7..         0..         7..         0..    ...Variants: .- la coinche (belote coinch&eacute e, belote bridg&eacute e) were everyone is dealt all the cards and player bids the number of points they're going to make. The biggest bid plays first. This has become the &quot standard&quot  version in France these days..- la belote de comptoir: more rapid than belote, and involves heavy alcohol drinking for all players..- la belote d&eacute couverte is a variant for 2 players, used essentially to teach the game to a new player..- Klabberjass (aka Klobyash) is the two-person variation played around the world...";

        BoardGame carcassonne = BoardGame.of(carcassonneStr);
        BoardGame belote = BoardGame.of(beloteStr);

        assertEquals(-1.0, carcassonne.getDistanceTo(belote));
    }

    @Test
    void testGetDistanceToWithValidGames() {
        String beloteStr = "14469;4;8;2;Belote;20;Card Game;Partnerships,Trick-taking;Belote is probably the most played game in France, essentially by adults (youngsters will probably play the &quot coinche&quot  variant or Tarot during high school)...It has been known for more than 75 years in France, and is played as a family game or as a money game (even if this last thing has been prohibited in bistros)..A 32 card deck is needed ( that means a poker deck without cards 2 to 6 and jokers). .Belote is a point-trick taking game for 4 players...Players are by teams of 2. Partners sit at opposite seats..The dealer gives everybody 3 cards face down, then 2..After that, the first card of the deck is turned faced up. Its suits will serve as trumps..Then, each player announces if he takes or not. If a polayer takes, he receives the face up card. Then the dealer deals 3 cards to everyone except the taker who receives only two. That makes for 8 cards for everyone..If no players takes, we take another round were each player announces if he takes, but also the suit that will serves as trumps (instead of the one of the face up card)...After that, it is similar to Tarot: first player plays a card, then each player can play a card of the same suit or a trump. If you can't play the asked suit, you have to play a trump. If your partner wins the &quot pli&quot , you don't have to play a trump..When 8 &quot plis&quot  have been played (when no one has card in the hand), points are counted. The team with the most points win...Points values and order of the cards vary from other card games:.....    .         Trump suit ..         Point Value..         Other suits..         Point Value ..    .    .         J ..         20..         A..         11..    .    .         9..         14..         10..         10..    .    .         A..         11..         K..         4..    .    .         10..         10..         Q..         3..    .    .         K..         4..         J..         2..    .    .         Q..         3..         9..         0..    .    .         8..         0..         8..         0..    .    .         7..         0..         7..         0..    ...Variants: .- la coinche (belote coinch&eacute e, belote bridg&eacute e) were everyone is dealt all the cards and player bids the number of points they're going to make. The biggest bid plays first. This has become the &quot standard&quot  version in France these days..- la belote de comptoir: more rapid than belote, and involves heavy alcohol drinking for all players..- la belote d&eacute couverte is a variant for 2 players, used essentially to teach the game to a new player..- Klabberjass (aka Klobyash) is the two-person variation played around the world...";
        String stayAwayStr = "69809;12;12;4;Stay Away!;60;Bluffing,Card Game,Deduction,Horror,Party Game;Card Drafting,Co-operative Play,Partnerships,Player Elimination,Role Playing,Storytelling;A group of archeologists has mysteriously disappeared during an expedition to the risen island of R'lyeh. You are a member of a rescue team sent to aid them, but as you investigate the site, you encounter something terrible that seeks to destroy your team from within.....You'll need intuition, nerves of steel, and clever acting if you're going to survive and win the game. Who will believe you? Who is whom, and who is what? You can't trust anyone and no one will trust you, so block the doors, quarantine a suspect, reveal your identity to your &quot allies&quot , grab a flamethrower, or run away &mdash  but look carefully where you will end up because &quot The Thing&quot  might be closer than you think.....In Stay Away!, 4-12 players try to track down &quot The Thing&quot , a creature &#226 &#128 &#168 awakened from a nauseating eternal slumber on the emerged R'lyeh island that has the capability of possessing the human body, cloning it, then taking its place, so as you play you won't know who &quot The Thing&quot  is or when someone will become &quot The Thing&quot  or who is infected or not. The archaeologists are looking for The Thing  their goal is to work together to identify which player is The Thing and roast it with a &quot Flamethrower&quot  card. You remain Human until The Thing passes an &quot Infected!&quot  card to you during a card exchange, at which point you become an Infected and take on that new Role: You are now an ally of The Thing and must take care that its identity is not revealed...";

        BoardGame stayAway = BoardGame.of(stayAwayStr);
        BoardGame belote = BoardGame.of(beloteStr);

        double expected = Math.sqrt((4 - 12) * (4 - 12)
                                    + (8 - 12) * (8 - 12)
                                    + (2 - 4) * (2 - 4)
                                    + (20 - 60) * (20 - 60))
                                    + (5 - 1)
                                    + (7 - 1);

        assertEquals(expected, belote.getDistanceTo(stayAway));
    }
}