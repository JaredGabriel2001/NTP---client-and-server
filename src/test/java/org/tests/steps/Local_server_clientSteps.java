package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import org.application.client.NtpClient;
import org.application.server.NtpServer;

public class Local_server_clientSteps {
    private NtpServer server;
    private NtpClient client;
    private boolean requestSuccess;

    // Cenário de sucesso: servidor e cliente sem HMAC funcionando corretamente
    @Given("the local server is running correctly")
    public void the_local_server_is_running_correctly() {
        try {
            // Constrói o servidor sem HMAC na porta 8123
            server = new NtpServer(false, 8123);
            new Thread(() -> {
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("Failed to start local server: " + e.getMessage());
        }
    }

    @When("the local client runs correctly")
    public void the_local_client_runs_correctly() {
        try {
            client = new NtpClient(false, 8123);
            client.requestTime("localhost");
            requestSuccess = true;
        } catch (Exception e) {
            requestSuccess = false;
            fail("Client should run correctly but error: " + e.getMessage());
        }
    }

    @Then("the return must be correct")
    public void the_return_must_be_correct() {
        assertTrue(requestSuccess, "Client ran correctly and returned correct results.");
    }

    // Cenário de falha: força uma falha usando um hostname inválido
    @When("the local client does not run correctly")
    public void the_local_client_does_not_run_correctly() {
        try {
            client = new NtpClient(false, 8123);
            client.requestTime("invalid.host");
            requestSuccess = true;
            fail("Client should have failed but succeeded.");
        } catch (Exception e) {
            requestSuccess = false;
        }
    }

    @Then("an error message should be displayed")
    public void an_error_message_should_be_displayed() {
        assertFalse(requestSuccess, "Client did not run correctly and error was reported.");
    }
}
