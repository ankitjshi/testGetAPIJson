#  Tests all the Positive UseCases with valid requests
#  The API should send correct responses with expected response codes
@Regression @Positive
Feature: Automation Regression for JsonPlaceholder Get Posts API - Positive Functional UseCases

#  Background to setup the environment variables and URIs
  Background:
    Given Base environment setup for 'staging' is completed
    And test resource directory is 'data/requests/get/'

#  Test all the 100 posts from the service - 'posts'
  @Smoke @Sanity
  Scenario: Validate that a user can have access to all the 100 posts when requested at once
    Given the API is utilised to request the service function - 'posts'
    When I (user) send request to get all the posts from the API
    Then I (user) receive successful response with response code 200
    And I (user) can view 100 posts as the output of the system

#  Test random posts from the list of all the posts with relevant ids and other attributes
  Scenario Outline: Validate that a user can see all the required fields in the posts with specific ids
    Given the API is utilised to request the service function - 'posts'
    When I (user) send request to get all the posts from the API
    And I (user) can view the fields <userID> <id> <title> <body> as the output of the system

    Examples:
      | userID  | id     | title                      | body                    |
      | 1       | 4      | "eum et est occaecati"     | "ullam et saepe reiciendis voluptatem adipisci\nsit amet autem assumenda provident rerum culpa\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\nquis sunt voluptatem rerum illo velit"     |
      | 1       | 7      | "magnam facilis autem"     | "dolore placeat quibusdam ea quo vitae\nmagni quis enim qui quis quo nemo aut saepe\nquidem repellat excepturi ut quia\nsunt ut sequi eos ea sed quas"     |
      | 5       | 45     | "ut numquam possimus omnis eius suscipit laudantium iure"     | "est natus reiciendis nihil possimus aut provident\nex et dolor\nrepellat pariatur est\nnobis rerum repellendus dolorem autem"     |
      # Failing Case just to test the flow
      #| 2       | 4      | "eum et est occaecati"     | "ullam et saepe reiciendis voluptatem adipisci\nsit amet autem assumenda provident rerum culpa\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\nquis sunt voluptatem rerum illo velit"     |

#  Test all the fields of all the posts with sample accurate response (ground truth)
  Scenario: Validate that a user gets accurate information for all posts
    Given the API is utilised to request the service function - 'posts'
    When I (user) send request to get all the posts from the API
    Then I (user) receive successful response with response code 200
    And I (user) can see accurate information for all the post as file 'sample_response_all'

#  Tests the https vs http hits and verifies all fields are non-null and non-empty
  Scenario: Validate that a user can have access to all the posts when requested without security certificate
    When I (user) send request to get the posts with the URL "http://jsonplaceholder.typicode.com/posts"
    Then I (user) receive successful response with response code 200
    And I (user) can view all the not null or not empty fields

#  Test all the fields of the post with ID 23 with sample accurate response (ground truth)
#  ID is randomly picked, can be changed, exploratory testing
  @Sanity
  Scenario: Validate that single user requests post with specific id
    Given the API is utilised to request the service function - 'posts'
    When I (user) wants to see single post with id 23
    Then I (user) receive successful response with response code 200
    And I (user) can see all the expected information displayed with the request
    And I (user) can see accurate information for the id post as file 'sample_response_id_23'

#  Test all the fields of the post with UserID 9 with sample accurate response (ground truth)
#  ID is randomly picked, can be changed, exploratory testing
  @Sanity
  Scenario: Validate that single user request post with specific userid
    Given the API is utilised to request the service function - 'posts'
    When I (user) wants to see single post with userid 9
    Then I (user) receive successful response with response code 200
    And I (user) can see accurate information for all the post as file 'sample_response_userid_9'