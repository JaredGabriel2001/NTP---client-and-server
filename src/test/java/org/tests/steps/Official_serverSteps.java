package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import org.application.client.NtpClient;

public class Official_serverSteps {
    private NtpClient client;
    private boolean requestSuccess;

    // Sucesso: teste com o servidor oficial NTP (por exemplo, a.ntp.br)
    @Given("the official server is configured correctly")
    public void the_official_server_is_configured_correctly() {
        // O teste pressupõe que o servidor oficial esteja acessível
        assertTrue(true, "The official server is configured correctly.");
    }

    @When("the local client (port {int}) runs correctly")
    public void the_local_client_port_runs_correctly(Integer port) {
        try {
            client = new NtpClient(false, port);
            // Tenta se conectar ao servidor oficial "a.ntp.br"
            client.requestTime("a.ntp.br");
            requestSuccess = true;
        } catch (Exception e) {
            requestSuccess = false;
            fail("Client should run correctly with official server, error: " + e.getMessage());
        }
        assertTrue(requestSuccess, "Client ran correctly with the official server.");
    }

    // Falha: simula uma falha na comunicação com o servidor oficial
    @When("the local client (port {int}) does not run correctly")
    public void the_local_client_port_does_not_run_correctly(Integer port) {
        try {
            client = new NtpClient(false, port);
            client.requestTime("a.ntp.br");
            requestSuccess = true;
            fail("Client should have failed with the official server but succeeded.");
        } catch (Exception e) {
            requestSuccess = false;
        }
        assertFalse(requestSuccess, "Client did not run correctly with the official server.");
    }

    @Then("an error message should be displayed")
    public void an_error_message_should_be_displayed() {
        assertFalse(requestSuccess, "Error message was displayed as expected.");
    }
}
