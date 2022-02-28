package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.dto.ApiResponse;
import bg.sofia.uni.fmi.mjt.news.dto.Article;
import bg.sofia.uni.fmi.mjt.news.dto.Source;
import bg.sofia.uni.fmi.mjt.news.exceptions.BadAPIKeyException;
import bg.sofia.uni.fmi.mjt.news.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsFeedClientException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeedClientTest {

    private static ApiResponse firstApiResponse;
    private static ApiResponse secondApiResponseOneArticle;
    private static ApiResponse secondApiResponseTwoArticles;
    private static Article tennisArticle;
    private static Article covidArticle;
    private static Article politicsArticle;
    private static Article entertainmentArticle;
    private static String firstApiResponseJson;
    private static String secondApiResponseOneArticleJson;
    private static String secondApiResponseTwoArticlesJson;

    @Mock
    private HttpClient newsFeedHttpClientMock;

    @Mock
    private HttpResponse<String> httpNewsFeedResponseMock;

    private NewsFeedClient client;

    @BeforeAll
    static void setUpClass() {
        tennisArticle = new Article(new Source("p_varna", "p_varna"), "Peter Kolev",
                "Grigor Dimitrov won", "Grigor Dimitrov defeated Jiri Lehecka in the first round of AO.",
                "https://tennis.bg/article/...", "https://tennis.bg/uploaded/posts/90ec5f0f0b9c56ea61e8a5b7eb81be58.jpg",
                "17-01-2022", "Grigor Dimitrov won in four sets - 6:4 4:6 6:3 7:5. His next opponent will be...");

        covidArticle = new Article(new Source("p_varna", "p_varna"), "Peter Kolev",
                "10000 new cases", "There are 10000 new cases in Bulgaria",
                "https://btv.com", "https://...", "20-01-2022",
                "New record in Bulgaria - 10000 new cases");

        politicsArticle = new Article(new Source("p_varna", "p_varna"), "Peter Kolev",
                "Kiril Petkov...", "Nov premier", "...", "...", "01-01-2022",
                "Kiril Petjov e noviat premier na Bulgaria");

        entertainmentArticle = new Article(new Source("p_varna", "p_varna"), "Peter Kolev",
                "Survivor is back!", "Survivor will be back during February", "...", "...",
                "01-02-2022", "The best reality show Survivor will be back!!!");

        firstApiResponse = new ApiResponse("ok", 3, new Article[] {tennisArticle, politicsArticle});
        secondApiResponseOneArticle = new ApiResponse("ok", 3, new Article[] {covidArticle});
        secondApiResponseTwoArticles = new ApiResponse("ok", 3, new Article[] {covidArticle, entertainmentArticle});
        firstApiResponseJson = new Gson().toJson(firstApiResponse);
        secondApiResponseOneArticleJson = new Gson().toJson(secondApiResponseOneArticle);
        secondApiResponseTwoArticlesJson = new Gson().toJson(secondApiResponseTwoArticles);
    }

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        /*when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpNewsFeedResponseMock);*/

        this.client = new NewsFeedClient(newsFeedHttpClientMock);
    }

    @Test
    void testGetArticlesWithNullKeywords() {
        String assertMessage = "There should me at least one keyword";
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(null, null, null),
                assertMessage);
    }

    @Test
    void testGetArticlesWithEmptyArrayForKeywords() {
        String assertMessage = "There should me at least one keyword";
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(new String[0], null, null),
                assertMessage);
    }

    @Test
    void testGetArticlesWithCategoryThatIsNotSupported() {
        String assertMessage = "There is a well-defined list of categories that are supported";
        String[] keywords = {"test"};
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(keywords, "test", null),
                assertMessage);
    }

    @Test
    void testGetArticlesWithCountryThatIsNotSupported() {
        String assertMessage = "There is a well-defined list of countries that are supported";
        String[] keywords = {"test"};
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(keywords, null, "test"),
                assertMessage);
    }

    @Test
    void testGetArticlesWithNegativeNumberOfArticlesPerPage() {
        String assertMessage = "The number of articles per page should be positive";
        String[] keywords = {"test"};
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(keywords, null, null,
                        -1, 10), assertMessage);
    }

    @Test
    void testGetArticlesWithZeroArticlesPerPage() {
        String assertMessage = "The number of articles per page should be positive";
        String[] keywords = {"test"};
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(keywords, null, null,
                0, 10), assertMessage);
    }

    @Test
    void testGetArticlesWithNegativeMaxArticles() {
        String assertMessage = "The number of max articles should be positive";
        String[] keywords = {"test"};
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(keywords, null, null,
                10, -1), assertMessage);
    }

    @Test
    void testGetArticlesWithZeroMaxArticles() {
        String assertMessage = "The number of max articles should be positive";
        String[] keywords = {"test"};
        assertThrows(IllegalArgumentException.class, () -> this.client.getArticles(keywords, null, null,
                10, 0), assertMessage);
    }

    @Test
    void testGetArticlesWithReturnedCode400() throws IOException, InterruptedException {
        String assertMessage = "An exception should be thrown if the status code is 400";
        String[] keywords = {"test"};

        when(httpNewsFeedResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpNewsFeedResponseMock);

        assertThrows(BadRequestException.class, () -> this.client.getArticles(keywords, null, null),
                assertMessage);
    }

    @Test
    void testGetArticlesWithReturnedCode401() throws IOException, InterruptedException {
        String assertMessage = "An exception should be thrown if the status code is 401";
        String[] keywords = {"test"};

        when(httpNewsFeedResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpNewsFeedResponseMock);

        assertThrows(BadAPIKeyException.class, () -> this.client.getArticles(keywords, null, null),
                assertMessage);
    }

    @Test
    void testGetArticlesWithReturnedCode500() throws IOException, InterruptedException {
        String assertMessage = "An exception should be thrown if the status code is different from 200, 400 or 401";
        String[] keywords = {"test"};

        when(httpNewsFeedResponseMock.statusCode()).thenReturn(500);
        when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpNewsFeedResponseMock);

        assertThrows(NewsFeedClientException.class, () -> this.client.getArticles(keywords, null, null),
                assertMessage);
    }

    @Test
    void testGetArticlesWithSuccessfulResponseWithTwoArticlesOnOnePage() throws IOException, InterruptedException, NewsFeedClientException {
        String[] keywords = {"test"};

        when(httpNewsFeedResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpNewsFeedResponseMock);

        when(httpNewsFeedResponseMock.body()).thenReturn(firstApiResponseJson);

        List<Article> actual = client.getArticles(keywords, "sports", "Bulgaria", 2, 2);

        assertEquals(2, actual.size());
        assertEquals(tennisArticle, actual.get(0));
        assertEquals(politicsArticle, actual.get(1));
    }

    @Test
    void testGetArticlesWithSuccessfulResponseWithThreeArticlesOnTwoPages() throws IOException, InterruptedException, NewsFeedClientException {
        String[] keywords = {"test"};

        when(httpNewsFeedResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpNewsFeedResponseMock);

        when(httpNewsFeedResponseMock.body()).thenReturn(firstApiResponseJson)
                                             .thenReturn(secondApiResponseOneArticleJson);

        List<Article> actual = client.getArticles(keywords, "sports", "Bulgaria", 2, 3);

        assertEquals(3, actual.size());
        assertEquals(tennisArticle, actual.get(0));
        assertEquals(politicsArticle, actual.get(1));
        assertEquals(covidArticle, actual.get(2));
    }

    @Test
    void testGetArticlesWithSuccessfulResponseWithFourArticlesOnTwoPagesWithMaxThreeArticles() throws IOException, InterruptedException, NewsFeedClientException {
        String[] keywords = {"test"};

        when(httpNewsFeedResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpNewsFeedResponseMock);

        when(httpNewsFeedResponseMock.body()).thenReturn(firstApiResponseJson)
                                             .thenReturn(secondApiResponseTwoArticlesJson);

        List<Article> actual = client.getArticles(keywords, "sports", "Bulgaria", 2, 3);

        assertEquals(3, actual.size());
        assertEquals(tennisArticle, actual.get(0));
        assertEquals(politicsArticle, actual.get(1));
        assertEquals(covidArticle, actual.get(2));
    }
}