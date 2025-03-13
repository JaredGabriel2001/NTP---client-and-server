package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.application.client.NtpClient;

import static org.junit.jupiter.api.Assertions.*;

public class Official_serverSteps {
    private boolean serverConfigured;
    private Exception clientException;

    @Given("the official server is configured correctly")
    public void the_official_server_is_configured_correctly() {
        // Simulação de configuração bem-sucedida do servidor oficial
        serverConfigured = true;
    }

    @When("the local client \\(port {int}) runs correctly")
    public void the_local_client_port_runs_correctly(Integer port) {
        try {
            assertTrue(serverConfigured, "O servidor oficial não está configurado corretamente!");
            NtpClient client = new NtpClient(false, port);
            client.requestTime("a.ntp.br"); // Testando com um servidor NTP oficial
            clientException = null;
        } catch (Exception e) {
            clientException = e;
        }
    }
    @Then("the return must be correct with Official")
    public void the_return_must_be_correct_with_official() {
        assertNull(clientException, "Ocorreu um erro inesperado ao consultar o servidor NTP oficial!");
        System.out.println("A resposta do servidor NTP oficial foi recebida corretamente.");
    }


    @When("the local client \\(port {int}) does not run correctly")
    public void the_local_client_port_does_not_run_correctly(Integer port) {
        try {
            assertTrue(serverConfigured, "O servidor oficial não está configurado corretamente!");
            NtpClient client = new NtpClient(false, port);
            client.requestTime("invalid.ntp.server"); // Simulando falha com um servidor inválido
            clientException = null;
        } catch (Exception e) {
            clientException = e;
        }
    }

    @Then("an error message should be displayed with Official")
    public void an_error_message_should_be_displayed() {
        assertNotNull(clientException, "O erro esperado não ocorreu!");
        System.out.println("Erro esperado: " + clientException.getMessage());
    }
}
