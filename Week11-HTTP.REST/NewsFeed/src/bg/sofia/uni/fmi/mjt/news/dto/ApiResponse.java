package bg.sofia.uni.fmi.mjt.news.dto;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Objects;

public class ApiResponse {

    private final String status;
    private final int totalResults;
    private final Article[] articles;

    public ApiResponse(String status, int totalResults, Article[] articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public Article[] getArticles() {
        return articles;
    }

    public static ApiResponse of(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, ApiResponse.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse apiResponse = (ApiResponse) o;
        return totalResults == apiResponse.totalResults && status.equals(apiResponse.status) && Arrays.equals(articles, apiResponse.articles);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(status, totalResults);
        result = 31 * result + Arrays.hashCode(articles);
        return result;
    }
}
