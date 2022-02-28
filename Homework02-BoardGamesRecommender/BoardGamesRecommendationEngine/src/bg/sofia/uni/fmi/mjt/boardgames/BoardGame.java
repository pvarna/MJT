package bg.sofia.uni.fmi.mjt.boardgames;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public record BoardGame(int id, String name, String description, int maxPlayers, int minAge, int minPlayers,
                        int playingTimeMins, Collection<String> categories, Collection<String> mechanics) {

    private static final String MAIN_DELIMITER = ";";
    private static final String SECONDARY_DELIMITER = ",";
    private static final int ID = 0;
    private static final int MAX_PLAYERS = 1;
    private static final int MIN_AGE = 2;
    private static final int MIN_PLAYERS = 3;
    private static final int NAME = 4;
    private static final int PLAYING_TIME = 5;
    private static final int CATEGORIES = 6;
    private static final int MECHANICS = 7;
    private static final int DESCRIPTION = 8;

    public static BoardGame of(String line) {
        String[] tokens = line.split(MAIN_DELIMITER);

        return new BoardGame(Integer.parseInt(tokens[ID]),
                             tokens[NAME],
                             tokens[DESCRIPTION],
                             Integer.parseInt(tokens[MAX_PLAYERS]),
                             Integer.parseInt(tokens[MIN_AGE]),
                             Integer.parseInt(tokens[MIN_PLAYERS]),
                             Integer.parseInt(tokens[PLAYING_TIME]),
                             Set.of(tokens[CATEGORIES].split(SECONDARY_DELIMITER)),
                             Set.of(tokens[MECHANICS].split(SECONDARY_DELIMITER)));
    }

    public double getDistanceTo(BoardGame other) {
        if (this.id == other.id) {
            return -1.0;
        }

        Set<String> unionCategories = new HashSet<>(this.categories);
        unionCategories.addAll(other.categories);

        Set<String> intersectionCategories = new HashSet<>(this.categories);
        intersectionCategories.retainAll(other.categories);
        if (intersectionCategories.isEmpty()) {
            return -1.0;
        }

        Set<String> unionMechanics = new HashSet<>(this.mechanics);
        unionMechanics.addAll(other.mechanics);

        Set<String> intersectionMechanics = new HashSet<>(this.mechanics);
        intersectionMechanics.retainAll(other.mechanics);

        int playingTimeDiff = this.playingTimeMins - other.playingTimeMins;
        int maxPlayersDiff = this.maxPlayers - other.maxPlayers;
        int minAgeDiff = this.minAge - other.minAge;
        int minPlayersDiff = this.minPlayers - other.minPlayers;
        int categoriesDiff = unionCategories.size() - intersectionCategories.size();
        int mechanicsDiff = unionMechanics.size() - intersectionMechanics.size();

        return Math.sqrt(playingTimeDiff * playingTimeDiff +
                         maxPlayersDiff * maxPlayersDiff +
                         minAgeDiff * minAgeDiff +
                         minPlayersDiff * minPlayersDiff) +
                         categoriesDiff +
                         mechanicsDiff;
    }
}
