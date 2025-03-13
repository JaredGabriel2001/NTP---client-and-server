Feature: Official server

  Scenario: Success when running official server
    Given the official server is configured correctly
    When the local client (port 123) runs correctly
    Then the return must be correct with Official

  Scenario: Failed to run official server
    Given the official server is configured correctly
    When the local client (port 123) does not run correctly
    Then an error message should be displayed with Official