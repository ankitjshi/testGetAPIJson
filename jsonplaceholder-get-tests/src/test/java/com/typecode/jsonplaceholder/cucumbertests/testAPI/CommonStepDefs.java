package com.typecode.jsonplaceholder.cucumbertests.testAPI;

import com.typecode.jsonplaceholder.cucumbertests.components.Specification;
import com.typecode.jsonplaceholder.cucumbertests.components.RequestExecutorComponent;
import com.typecode.jsonplaceholder.cucumbertests.models.response.PostResponse;
import com.typecode.jsonplaceholder.cucumbertests.utils.ConfigProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Singleton;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.typecode.jsonplaceholder.cucumbertests.utils.Logger;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CommonStef Definition class of feature file, gives implementation to all the scenarios
 * Calls the RequestExecutor to make client API request
 * Validates all the response in the form of Asserts
 */

@Singleton
public class CommonStepDefs {
    /**
     * Initialise Loggers and all the important attributes used throughout the class
     * ConfigProvider helps to generate URL for the client call
     * object mapper helps to convert the response to relevant PostResponse Object
     */

    private final org.apache.logging.log4j.Logger log = Logger.log;
    private ObjectMapper objectMapper;
    public String environment;
    public ConfigProvider config;
    public Specification specification;
    private List<PostResponse> allPostsResponse;
    private String testResourceFolder;
    private Response response;
    private List<Pair<String, PostResponse>> futureResponse;

    /**
     * Before the Tests begins, Initial setups like object mapper initialization invoked
     */
    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Background: Initialises other important classes to be used throughout
     */
    @Given("Base environment setup for {string} is completed")
    public void base_environment_setup(String environment) {
        this.environment = environment;
        this.config = new ConfigProvider(environment);
        this.specification = new Specification();
        log.info("Base Environment is Ready to Proceed");
    }

    /**
     * Background: Initialises the directory to fetch expected responses
     */
    @And("test resource directory is {string}")
    public void testResourcePath(String resourcePath) {
        testResourceFolder = resourcePath;
        log.info("Resource Directory is assigned successfully");
    }

    /**
     * Generates URL to hit the type of get request service - 'posts'
     */
    @Given("the API is utilised to request the service function - {string}")
    public void set_base_url_service(String serviceName) {
        ConfigProvider.ENDPOINT_SERVICE = serviceName;
        log.info("URL generated is ready to request " + ConfigProvider.BASE_URL + "\\" + ConfigProvider.ENDPOINT_SERVICE);
    }

    /**
     * Test the client server connection and request hit
     * Hits the Get API and fetches response stores in - allPostsResponse
     */
    @When("I \\(user) send request to get all the posts from the API")
    public void verifyGetPostsByUserId() {
        try {
            response = RequestExecutorComponent.getAllPosts();
            allPostsResponse = Arrays.asList(response.getBody().as(PostResponse[].class));
            log.info("Response is obtained successfully by requesting the resource");
        } catch (Exception e) {
            log.error("There is error in accessing the response");
            e.printStackTrace();
        }
    }

    /**
     * Tests the response code
     * Validates the actual response code with the expected response code
     * parsed from feature file
     */
    @Then("I \\(user) receive successful response with response code {int}")
    public void validate_response_code_get_posts(int expectedResponseCode) {
        try {
            Assert.assertEquals("Error in validating response code", expectedResponseCode, response.statusCode());
            log.info("Response code validated successfully as response code: " + expectedResponseCode);
        } catch (AssertionError e) {
            log.error("Expected:" + expectedResponseCode + "Actual:" + response.statusCode());
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Tests the size of the posts
     * Validates that all the posts is equivalent to desired size
     */
    @And("I \\(user) can view {int} posts as the output of the system")
    public void user_access_to_posts(int expectedSize) {
        try {
            Assert.assertEquals("Error in validating posts size", expectedSize, allPostsResponse.size());
            log.info("Correct number of PostResponse (" + expectedSize + ")A ccessed successfully");
        } catch (AssertionError e) {
            log.error("Expected:" + expectedSize + "Actual:" + allPostsResponse.size());
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Tests the fields of the posts as per the expected posts
     */
    @And("I \\(user) can view the fields {int} {int} {string} {string} as the output of the system")
    public void validate_all_fields_using_one_reference(int userID, int id, String title, String body) {
        PostResponse specificPost;
        try {
            specificPost = allPostsResponse.stream().filter(p -> p.getId() == id).collect(Collectors.toList()).get(0);
            Assert.assertEquals("Error in validating userID", userID, specificPost.getUserId());
            Assert.assertEquals("Error in validating id", id, specificPost.getId());
            Assert.assertEquals("Error in validating title", title, specificPost.getTitle());
            Assert.assertEquals("Error in validating body", body, specificPost.getBody());
            log.info("Correct number of Post with id (" + id + ") validated successfully");
        } catch (AssertionError e) {
            log.error("Validation failed !!");
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Tests all the n posts recieved and fields with the sample post file
     */
    @And("I \\(user) can see accurate information for all the post as file {string}")
    public void validate_all_the_posts(String filename) {
        try {
            InputStream resourceAsStream = CommonStepDefs.class.getClassLoader()
                    .getResourceAsStream(testResourceFolder + filename);

            PostResponse[] expectedPostList = objectMapper.readValue(resourceAsStream, PostResponse[].class);
            Comparator<PostResponse> postComparator = Comparator.comparing(PostResponse::getId);

            List<PostResponse> expectedPosts = new ArrayList<>(Arrays.asList(expectedPostList));
            expectedPosts.sort(postComparator);
            List<PostResponse> actualPosts = new ArrayList<>(allPostsResponse);
            actualPosts.sort(postComparator);

            Assert.assertEquals("Size Mismatch for the expected vs actual",
                    expectedPosts.size(), actualPosts.size());


            for (int i = 0; i < expectedPosts.size(); i++) {
                Assert.assertTrue("Mismatch in post for id:" + expectedPosts.get(i).getId(), expectedPosts.get(i).equals(actualPosts.get(i)));
            }
            log.info("All the post accessed are as per the expected ");
        } catch (AssertionError | IOException e) {
            log.error("Failure in Expected vs Actual");
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Tests the execution time of the request made
     */
    @And("Request execution time should be less than {int} milliseconds")
    public void requestExecutionTimeShouldBeLessThanMilliseconds(int threshold) {
        try {
            Assert.assertTrue("Actual time take is longer than threshold of 1000ms",
                    RequestExecutorComponent.executionTime <= threshold);
            log.info("All the posts extracted in shorter time frame," + RequestExecutorComponent.executionTime);
        } catch (AssertionError e) {
            log.error("Request took longer than expected, expected:" + threshold + "actual:" + RequestExecutorComponent.executionTime);
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * User hits GetAPI for single Post with specific ID
     */
    @When("I \\(user) wants to see single post with id {int}")
    public void user_request_specific_id_post(int id) {
        try {
            response = RequestExecutorComponent.getSpecificPostsID(id);
            log.info("Specific post extracted by the user");
        } catch (AssertionError e) {
            log.error("Problem in requesting Post ID" + id);
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Tests the GetAPI has all the non-null and non-empty fields
     */
    @And("I \\(user) can see all the expected information displayed with the request")
    public void validate_all_fields_post() {
        try {
            PostResponse specificId = response.getBody().as(PostResponse.class);
            validateAllPostsFields(specificId);
            log.info("Specific post validated for all the fields extracted by the user");
        } catch (AssertionError e) {
            log.error("Problem in validating the fields of Post");
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Tests all the n posts per userID and its fields with the sample post file
     */
    @And("I \\(user) can see accurate information for the id post as file {string}")
    public void validate_post_with_specific_id(String filename) {
        try {
            PostResponse actualPost = response.getBody().as(PostResponse.class);

            InputStream resourceAsStream = CommonStepDefs.class.getClassLoader()
                    .getResourceAsStream(testResourceFolder + filename);

            PostResponse expectedPost = objectMapper.readValue(resourceAsStream, PostResponse.class);

            Assert.assertTrue("Mismatch in the id;s",
                    expectedPost.equals(actualPost));

            log.info("The post with specific ID accessed is as expected ");
        } catch (AssertionError e) {
            log.error("Failure in Expected vs Actual");
            e.printStackTrace();
            Assert.fail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * User hits GetAPI for multiple Post per user ID
     */
    @When("I \\(user) wants to see single post with userid {int}")
    public void user_request_specific_userid_post(int userID) {
        try {
            response = RequestExecutorComponent.getSpecificPostsUserID(userID);
            allPostsResponse = Arrays.asList(response.getBody().as(PostResponse[].class));
            log.info("Response is obtained successfully by requesting the resource");
        } catch (Exception e) {
            log.error("There is error in accessing the response");
            e.printStackTrace();
        }

    }

    /**
     * Randomly generates endpoints and hits the getAPI async way
     * tests its parallel execution and valid response across each calll
     */
    @When("Fetch {int} random ids for the posts access and send request Async parallely")
    public void fetch_random_endpoints_send_async_requests(int randomPosts) {
        try {
            List<String> endpoints = createRandomPostEndpoints(randomPosts);
            futureResponse = RequestExecutorComponent.getPostParallel(endpoints);
            log.info("Random Endpoints Fetched and Async Requests completed");
        } catch (Exception e) {
            log.error("There is error in requesting async requests");
            e.printStackTrace();
        }
    }

    /**
     * Randomly generates endpoints and hits the getAPI async way
     * tests its parallel execution and valid response across each calll
     */
    @Then("Validate the  response codes and response ID's corresponding to respective post")
    public void validate_parallel_requests() {
        try {
            futureResponse.forEach(pair -> validateResponse(pair.getKey(), pair.getValue()));
            log.info("Validation of Future Responses Completed Successfully");
        } catch (AssertionError | Exception e) {
            log.error("There is error in validation the future requests");
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * tests response code for the parallel request
     * tests ID requested has the same response ID
     */
    public void validateResponse(String endpoint, PostResponse response) {
        String responseCode = endpoint.split(":status")[1];
        String id = endpoint.split(":status")[0].replace(ConfigProvider.BASE_URL + ConfigProvider.ENDPOINT_SERVICE + "/", "");
        Assert.assertEquals(Integer.parseInt(responseCode), 200);
        Assert.assertEquals(response.getId(), Integer.parseInt(id));
    }

    /**
     * Randomly generates number from 1 to 100 and
     * generates URL to hit asynchronously
     */
    private List<String> createRandomPostEndpoints(int randomPosts) {
        Random random = new Random();
        List<String> endpoints = new ArrayList<>();
        for (int i = 0; i < randomPosts; i++) {
            int postId = random.nextInt(100) + 1;
            String endpoint = ConfigProvider.BASE_URL + ConfigProvider.ENDPOINT_SERVICE + "/" + postId;
            endpoints.add(endpoint);
        }
        return endpoints;
    }

    /**
     * Added functionality to hit GetAPI based on customised URL passed
     */
    @When("I \\(user) send request to get the posts with the URL {string}")
    public void user_send_request_url(String url) {
        try {
            response = RequestExecutorComponent.getPostWithURL(url);
            log.info("Specific request sent to the url: " + url);
        } catch (Exception e) {
            log.error("Problem in requesting url: " + url);
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Test Negative scenarios for empty responses for failed or invalid requests
     * It also tests successful requests with empty responses
     */
    @And("I \\(user) can view empty response {string}")
    public void user_recieves_empty_response(String expectedResponse) {
        try {
            Assert.assertEquals("Response is not empty", response.body().print(), expectedResponse);
            log.info("Empty Response is obtained successfully by requesting incorrect request");
        } catch (AssertionError e) {
            log.error("There is error in accessing the response");
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Test non-null and non-empty for all the posts
     */
    @And("I \\(user) can view all the not null or not empty fields")
    public void validate_not_null_not_empty() {
        try {
            allPostsResponse = Arrays.asList(response.getBody().as(PostResponse[].class));
            allPostsResponse.forEach(this::validateAllPostsFields);
            log.info("Specific post validated for all the fields extracted by the user");
        } catch (AssertionError e) {
            log.error("Problem in validating the fields of Post");
            e.printStackTrace();
            Assert.fail();
        }
    }


    private void validateAllPostsFields(@NotNull PostResponse post) {
        validateNotNull(post);
        validateNonEmpty(post);
    }

    private void validateNotNull(@NotNull PostResponse post) {
        Assert.assertNotNull(post.getTitle());
        Assert.assertNotNull(post.getBody());
    }

    private void validateNonEmpty(@NotNull PostResponse post) {
        Assert.assertTrue("Invalid UserID is found", post.getUserId() > 0);
        Assert.assertTrue("Invalid ID is found", post.getId() > 0);
        Assert.assertTrue("Title is found blank or empty", post.getTitle().length() != 0);
        Assert.assertTrue("Body is found blank or empty", post.getBody().length() != 0);
    }
}
