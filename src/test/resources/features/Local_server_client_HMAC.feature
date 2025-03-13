Feature: Local client and server HMAC

  Scenario: Success when running local client and server with HMAC
    Given the local server using HMAC is running correctly
    When the local client using HMAC runs correctly
    Then the return must be correct with HMAC

  Scenario: Failed to run local client and server with HMAC
    Given the local server using HMAC is running correctly
    When the local client using HMAC does not run correctly
    Then an error message should be displayed with HMAC