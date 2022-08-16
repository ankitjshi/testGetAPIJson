#  Tests all the Negative UseCases with invalid requests
#  The API should gracefully be handled without errors
@Regression @Negative
Feature: Automation Regression for JsonPlaceholder Get Posts API - Negative Functional UseCases

#  Background to setup the environment variables and URIs
  Background:
    Given Base environment setup for 'staging' is completed
    And test resource directory is 'data/requests/get/'

#  Tests for invalid requests - user id, post id and service 'posts'
  Scenario Outline: Validate that a user accessing incorrect information is handled <Scenario>
    When I (user) send request to get the posts with the URL <Url>
    Then I (user) receive successful response with response code <expectedResponseCode>
    And I (user) can view empty response <expectedResponse>

    Examples:
      | Scenario              | Url                                                       | expectedResponseCode     | expectedResponse |
      | 'Invalid service 1'   | "https://jsonplaceholder.typicode.com/posts1"             | 404              | '{}'       |
      | 'Invalid service 2'   | "https://jsonplaceholder.typicode.com//posts"             | 404              | '{}'       |
      | 'invalid post id'     | "https://jsonplaceholder.typicode.com/posts/1000"         | 404              | '{}'       |
      | 'invalid user id 1'   | "https://jsonplaceholder.typicode.com/posts?userId="      | 200              | '[]'       |
      | 'invalid user id 2'   | "https://jsonplaceholder.typicode.com/posts?userId=2000"  | 200              | '[]'       |