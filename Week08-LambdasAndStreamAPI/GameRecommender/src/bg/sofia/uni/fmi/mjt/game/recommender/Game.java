package bg.sofia.uni.fmi.mjt.game.recommender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record Game(String title, String platform, LocalDate releaseDate,
                   String summary, int metaScore, double userReview) {

    private static final String MAIN_DELIMITER = ",";
    private static final int TITLE_INDEX = 0;
    private static final int PLATFORM_INDEX = 1;
    private static final int RELEASE_DATE_INDEX = 2;
    private static final int SUMMARY_INDEX = 3;
    private static final int META_SCORE_INDEX = 4;
    private static final int USER_REVIEW_INDEX = 5;

    public static Game of(String line) {
        String[] tokens = line.split(MAIN_DELIMITER);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.US);

        return new Game(tokens[TITLE_INDEX], tokens[PLATFORM_INDEX],
                LocalDate.parse(tokens[RELEASE_DATE_INDEX], formatter), tokens[SUMMARY_INDEX],
                Integer.parseInt(tokens[META_SCORE_INDEX]), Double.parseDouble(tokens[USER_REVIEW_INDEX]));
    }

}
