#  Tests all the Positive UseCases with valid requests
#  The API should send correct responses with expected response codes
@Regression @Performance
Feature: Automation Regression for JsonPlaceholder Get Posts API - NonFunctional Performance

#  Background to setup the environment variables and URIs
  Background:
    Given Base environment setup for 'staging' is completed
    And test resource directory is 'data/requests/get/'

#  Tests all the requests have threshold execution time - Tests Latency of the system
  @Smoke @Sanity
  Scenario: Validate that a user can have access to all the posts in shorter time span
    Given the API is utilised to request the service function - 'posts'
    When I (user) send request to get all the posts from the API
    Then I (user) receive successful response with response code 200
    And I (user) can view 100 posts as the output of the system
    And Request execution time should be less than 2500 milliseconds

#  Tests the varying increasing load of request when executed in parallel
#  It does not impact latency and gives correct responses - Test Performance
  Scenario Outline: Validate random posts access via multiple parallel hits
    Given the API is utilised to request the service function - 'posts'
    When Fetch <randomIds> random ids for the posts access and send request Async parallely
    Then Validate the  response codes and response ID's corresponding to respective post
    And Request execution time should be less than <executionTime> milliseconds

    Examples:
      | randomIds  | executionTime     |
      | 10         | 2500              |
      | 30         | 3500              |
      | 50         | 4500              |
      | 90         | 5500              |