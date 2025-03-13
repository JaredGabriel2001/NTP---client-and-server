Feature: Local server and client

  Scenario: Success when running local server and client
    Given the local server is running correctly
    When the local client runs correctly
    Then the return must be correct

  Scenario: Failed to run local server and client
    Given the local server is running correctly
    When the local client does not run correctly
    Then an error message should be displayed