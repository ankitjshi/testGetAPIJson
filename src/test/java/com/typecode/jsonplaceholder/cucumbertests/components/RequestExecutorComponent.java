package com.typecode.jsonplaceholder.cucumbertests.components;

import com.typecode.jsonplaceholder.cucumbertests.models.response.PostResponse;
import com.typecode.jsonplaceholder.cucumbertests.utils.Logger;
import com.typecode.jsonplaceholder.cucumbertests.utils.ConfigProvider;
import io.restassured.response.Response;

import java.util.function.Supplier;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;


/**
 * This Class generates client with help of RestAssured
 * RestAssured used specifications to hit the URL
 * The response fetch is then validated
 * There are various ways to hit the service noted in separate methods.
 */
public class RequestExecutorComponent { // each method in a component would return a REST-assured Response object
    /**
     * Initialize Loggers - Commonly used to log information
     * variable execution time - to measure actual time take by request
     */
    public static org.apache.logging.log4j.Logger log = Logger.log;
    public static long executionTime;

    /**
     * Requests service using URL passed and returns response
     */
    public static @NotNull Response getPostWithURL(String url) {
        Response response = given()
                .spec(Specification.requestSpecification)
                .get(url);

        log.info("PostResponse are fetched successfully for url:" + url);
        return response;
    }

    /**
     * Requests service 'posts' and returns response for all the posts
     */
    public static @NotNull Response getAllPosts() {
        long startExecutionTime = System.currentTimeMillis();
        Response response = given()
                .spec(Specification.requestSpecification)
                .get(ConfigProvider.ENDPOINT_SERVICE);
        response
                .then()
                .assertThat()
                .spec(Specification.responseSpecification);
        long endExecutionTime = System.currentTimeMillis();
        executionTime = endExecutionTime - startExecutionTime;

        log.info("All the PostResponse are fetched successfully with time:" + executionTime + "ms");
        return response;
    }

    /**
     * Requests using parameter UserID to fetch all the posts with specific userID
     */
    public static @NotNull Response getSpecificPostsUserID(int userId) {
        Response response = given()
                .spec(Specification.requestSpecification)
                .param("userId", userId)
                .get(ConfigProvider.ENDPOINT_SERVICE);
        response
                .then()
                .assertThat()
                .spec(Specification.responseSpecification);

        log.info("PostResponse are fetched successfully for userID:" + userId + "" + "ms");
        return response;
    }

    /**
     * Requests specific post with ID and returns response
     */
    public static @NotNull Response getSpecificPostsID(int id) {
        Response response = given()
                .spec(Specification.requestSpecification)
                .get(ConfigProvider.ENDPOINT_SERVICE + "/" + id);
        response
                .then()
                .assertThat()
                .spec(Specification.responseSpecification);

        log.info("PostResponse are fetched successfully for ID:" + id + "" + "ms");
        return response;
    }

    /**
     * This method calls the service 'posts' using async parallel threads with random endpoints
     */
    public static List<Pair<String, PostResponse>> getPostParallel(@NotNull List<String> endpoints) {
        long startExecutionTime = System.currentTimeMillis();

        List<CompletableFuture<Pair<String, PostResponse>>> futures =
                endpoints.stream()
                        .map(endpoint -> restAPICallAsync(endpoint))
                        .collect(Collectors.toList());

        List<Pair<String, PostResponse>> result =
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());

        long endExecutionTime = System.currentTimeMillis();

        executionTime = endExecutionTime - startExecutionTime;

        log.info("Async Calls requests are completed successfully with time:" + executionTime + "ms");

        return result;
    }

    /**
     * This method calls is called by the previous method to return the futures
     * ( AsyncResponses List)
     */
    static @NotNull CompletableFuture<Pair<String, PostResponse>> restAPICallAsync(String endpoint) {
        CompletableFuture<Pair<String, PostResponse>> futureResponse = CompletableFuture.supplyAsync(
                new Supplier<Pair<String, PostResponse>>() {

                    public Pair<String, PostResponse> get() {
                        Response response = given()
                                .relaxedHTTPSValidation()
                                .get(endpoint)
                                .then()
                                .extract().response();
                        log.info("Endpoint : " + endpoint + " Status : " + response.getStatusCode());
                        return new Pair<String, PostResponse>(endpoint + ":status" + response.getStatusCode() + "", response.body().as(PostResponse.class));
                    }
                });
        return futureResponse;
    }
}