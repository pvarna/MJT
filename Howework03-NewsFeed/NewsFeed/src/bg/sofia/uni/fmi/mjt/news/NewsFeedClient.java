package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.dto.ApiResponse;
import bg.sofia.uni.fmi.mjt.news.dto.Article;
import bg.sofia.uni.fmi.mjt.news.exceptions.BadAPIKeyException;
import bg.sofia.uni.fmi.mjt.news.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsFeedClientException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewsFeedClient {

    private static final String ARTICLES_PER_PAGE = "Articles per page";
    private static final String MAX_ARTICLES = "Max articles";

    private static final String API_KEY = "3b4649d2fcf040368230a533f0b073c7";

    private static final String API_ENDPOINT_SCHEME = "http";
    private static final String API_ENDPOINT_HOST = "newsapi.org";
    private static final String API_ENDPOINT_PATH = "/v2/top-headlines";
    private static final String API_ENDPOINT_QUERY = "q=%s%s&apiKey=%s";
    private static final int DEFAULT_ARTICLES_PER_PAGE = 20;
    private static final int FIRST_PAGE = 1;
    private static final int DEFAULT_MAX_ARTICLES = 100;

    private static final Map<String, String> availableCountries;
    private static final Set<String> availableCategories;

    private final HttpClient newsHttpClient;
    private final String apiKey;

    public NewsFeedClient(HttpClient newsHttpClient, String apiKey) {
        this.newsHttpClient = newsHttpClient;
        this.apiKey = apiKey;
    }

    public NewsFeedClient(HttpClient newsHttpClient) {
        this.newsHttpClient = newsHttpClient;
        this.apiKey = API_KEY;
    }

    public List<Article> getArticles(String[] keywords, String category, String country) throws NewsFeedClientException {
        return this.getArticles(keywords, category, country, DEFAULT_ARTICLES_PER_PAGE, DEFAULT_MAX_ARTICLES);
    }

    /**
     *
     * @param keywords the keywords we want the articles to be related to
     * @param category the category we want the articles to be related to
     *                 (if a specific category is not desired, a null ot empty string should be passed)
     * @param country the country we want the articles to be related to
     *                (if a specific country is not desired, a null ot empty string should be passed)
     *                (you should type the full name of the country, e.g. Bulgaria, The United Kingdom, etc.)
     * @param articlesPerPage the number of articles per page
     * @param maxArticles the max number of returned articles
     * @return a list of the articles that meet the specified parameters, empty list is returned when no articles are found
     * @throws IllegalArgumentException if the keywords are either null or 0 or if either the category
     *                  or the country in not supported or if either articlesPerPage or maxArticles are non-positive
     * @throws BadRequestException if the status code returned from the server is 400
     * @throws BadAPIKeyException if the status code returned from the server is 401
     * @throws NewsFeedClientException if there was a communication problem or
     *                  if the status code returned from the sever is not 200, 400 or 401
     */
    public List<Article> getArticles(String[] keywords, String category, String country, int articlesPerPage, int maxArticles) throws NewsFeedClientException {
        if (keywords == null || keywords.length == 0) {
            throw new IllegalArgumentException("There must be at least one keyword");
        }

        if (category != null && !category.isEmpty() && !availableCategories.contains(category)) {
            throw new IllegalArgumentException("The given category is not supported");
        }

        if (country != null && !country.isEmpty() && !availableCountries.containsKey(country)) {
            throw new IllegalArgumentException("The given country is not supported");
        }

        assertNonNegativeOrZero(articlesPerPage, ARTICLES_PER_PAGE);
        assertNonNegativeOrZero(maxArticles, MAX_ARTICLES);

        HttpResponse<String> response = getSingleResponse(keywords, category, country, articlesPerPage, FIRST_PAGE);
        this.checkForBadStatusCodes(response);

        ApiResponse apiResponse = ApiResponse.of(response.body());

        if (apiResponse.getTotalResults() == 0) {
            return Collections.emptyList();
        }

        int neededArticles = Math.min(apiResponse.getTotalResults(), maxArticles);
        Article[] currentArticles = apiResponse.getArticles();

        List<Article> result = new ArrayList<>(Arrays.asList(currentArticles).subList(0, Math.min(currentArticles.length, neededArticles)));
        int currentPage = FIRST_PAGE;

        while (result.size() < neededArticles) {
            response = getSingleResponse(keywords, category, country, articlesPerPage, ++currentPage);
            this.checkForBadStatusCodes(response);

            apiResponse = ApiResponse.of(response.body());
            currentArticles = apiResponse.getArticles();

            if (currentArticles.length > maxArticles - result.size()) {
                int remaining = maxArticles - result.size();

                result.addAll(Arrays.asList(currentArticles).subList(0, remaining));
                break;

            } else {
                result.addAll(Arrays.asList(currentArticles));
            }
        }

        return result;
    }

    private String getApiEndpointQuery(String[] keywords, String category, String country, int articlesPerPage, int pageNumber) {
        String keywordsJoined = String.join("+", keywords);

        StringBuilder additionalFilters = new StringBuilder();
        if (category != null && !category.isEmpty()) {
            additionalFilters.append("&category=").append(category);
        }

        if (country != null && !country.isEmpty()) {
            additionalFilters.append("&country=").append(availableCountries.get(country));
        }

        additionalFilters.append("&pageSize=").append(articlesPerPage);
        additionalFilters.append("&page=").append(pageNumber);

        return API_ENDPOINT_QUERY.formatted(keywordsJoined, additionalFilters.toString(), this.apiKey);
    }

    private HttpResponse<String> getSingleResponse(String[] keywords, String category, String country, int articlesPerPage, int pageNumber) throws NewsFeedClientException {
        HttpResponse<String> response;

        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH,
                    getApiEndpointQuery(keywords, category, country, articlesPerPage, pageNumber), null);

            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

            response = this.newsHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new NewsFeedClientException("Could not receive news feed", e);
        }

        return response;
    }

    private void checkForBadStatusCodes(HttpResponse<String> response) throws NewsFeedClientException {
        if (response.statusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new BadRequestException("The request is unacceptable - missing or misconfigured parameter");
        }

        if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new BadAPIKeyException("The API Key was either missing or incorrect");
        }

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new NewsFeedClientException("Unexpected response code from news feed server");
        }
    }

    private void assertNonNegativeOrZero(int param, String paramName) {
        if (param <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive");
        }
    }

    static
    {
        availableCountries = Map.ofEntries(
                Map.entry("The United Arab Emirates", "ae"),
                Map.entry("Argentina", "ar"),
                Map.entry("Austria", "at"),
                Map.entry("Australia", "au"),
                Map.entry("Belgium", "be"),
                Map.entry("Bulgaria", "bg"),
                Map.entry("Brazil", "br"),
                Map.entry("Canada", "ca"),
                Map.entry("Switzerland", "ch"),
                Map.entry("China", "cn"),
                Map.entry("Colombia", "co"),
                Map.entry("Cuba", "cu"),
                Map.entry("The Czech Republic", "cz"),
                Map.entry("Germany", "de"),
                Map.entry("Egypt", "eg"),
                Map.entry("France", "fr"),
                Map.entry("The United Kingdom", "gb"),
                Map.entry("Greece", "gr"),
                Map.entry("Hong Kong", "hk"),
                Map.entry("Indonesia", "id"),
                Map.entry("Ireland", "ie"),
                Map.entry("Israel", "il"),
                Map.entry("India", "in"),
                Map.entry("Italy", "it"),
                Map.entry("Japan", "jp"),
                Map.entry("South Korea", "kr"),
                Map.entry("Lithuania", "lt"),
                Map.entry("Latvia", "lv"),
                Map.entry("Morocco", "ma"),
                Map.entry("Mexico", "mx"),
                Map.entry("Malaysia", "my"),
                Map.entry("Nigeria", "ng"),
                Map.entry("The Netherlands", "nl"),
                Map.entry("Norway", "no"),
                Map.entry("New Zealand", "nz"),
                Map.entry("The Philippines", "ph"),
                Map.entry("Poland", "pl"),
                Map.entry("Portugal", "pt"),
                Map.entry("Romania", "ro"),
                Map.entry("Serbia", "rs"),
                Map.entry("Russia", "ru"),
                Map.entry("Saudi Arabia", "sa"),
                Map.entry("Sweden", "se"),
                Map.entry("Singapore", "sg"),
                Map.entry("Slovenia", "si"),
                Map.entry("Slovakia", "sk"),
                Map.entry("Thailand", "th"),
                Map.entry("Turkey", "tr"),
                Map.entry("Taiwan", "tw"),
                Map.entry("Ukraine", "ua"),
                Map.entry("The United States", "us"),
                Map.entry("Venezuela", "ve"),
                Map.entry("South Africa", "za")
        );

        availableCategories = Set.of(
                "business",
                "entertainment",
                "general",
                "health",
                "science",
                "sports",
                "technology"
        );
    }
}
