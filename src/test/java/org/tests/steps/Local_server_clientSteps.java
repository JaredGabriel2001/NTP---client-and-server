package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import org.application.server.NtpServer;
import org.application.client.NtpClient;

public class Local_server_clientSteps {
    private NtpServer server;
    private NtpClient client;
    private boolean clientSuccess;
    private String errorMessage;

    @Given("the local server is running correctly")
    public void the_local_server_is_running_correctly() {
        // Inicia o servidor local em modo "plain" (sem HMAC)
        server = new NtpServer(false, 123); // Porta 123
        try {
            new Thread(() -> {
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            Thread.sleep(1000); // Aguarda o servidor iniciar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("the local client runs correctly")
    public void the_local_client_runs_correctly() {
        // Inicia o cliente e tenta se conectar ao servidor local
        client = new NtpClient(false, 123); // Porta 123
        try {
            client.requestTime("127.0.0.1"); // Conecta ao servidor local
            clientSuccess = true;
        } catch (Exception e) {
            clientSuccess = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("the return must be correct")
    public void the_return_must_be_correct() {
        assertTrue(clientSuccess, "O cliente não rodou corretamente.");
    }

    @When("the local client does not run correctly")
    public void the_local_client_does_not_run_correctly() {
        // Altera a porta para uma inválida (ex: 124) para forçar erro
        client = new NtpClient(false, 124); // Porta 124 (não usada pelo servidor)
        try {
            client.requestTime("127.0.0.1"); // Tenta se conectar à porta errada
            clientSuccess = true;
        } catch (Exception e) {
            clientSuccess = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("an error message should be displayed")
    public void an_error_message_should_be_displayed() {
        assertFalse(clientSuccess, "O cliente rodou corretamente, mas era esperado um erro.");
        assertNotNull(errorMessage, "Nenhuma mensagem de erro foi exibida.");
        System.out.println("Mensagem de erro exibida: " + errorMessage);
    }
}