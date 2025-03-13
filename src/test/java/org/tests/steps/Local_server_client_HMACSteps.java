package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;
import org.application.server.NtpServer;
import org.application.client.NtpClient;

public class Local_server_client_HMACSteps {
    private NtpServer server;
    private NtpClient client;
    private boolean clientSuccess;
    private String errorMessage;

    @Given("the local server using HMAC is running correctly")
    public void startServer() throws InterruptedException {
        server = new NtpServer(true, 123);
        new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2000); // Aguarda 2 segundos para o servidor iniciar
    }

    @When("the local client using HMAC runs correctly")
    public void runClient() {
        client = new NtpClient(true, 123);
        try {
            client.requestTime("127.0.0.1");
            clientSuccess = true;
        } catch (Exception e) {
            clientSuccess = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("the return must be correct with HMAC")
    public void verifySuccess() {
        assertTrue(clientSuccess, "Cliente falhou: " + errorMessage);
    }

    @When("the local client using HMAC does not run correctly")
    public void runInvalidClient() {
        client = new NtpClient(true, 124); // Porta inválida
        try {
            client.requestTime("127.0.0.1");
            clientSuccess = true;
        } catch (Exception e) {
            clientSuccess = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("an error message should be displayed with HMAC")
    public void verifyError() {
        assertFalse(clientSuccess, "Cliente deveria ter falhado!");
        assertNotNull(errorMessage, "Nenhuma mensagem de erro foi gerada.");
        System.out.println("Erro esperado: " + errorMessage);
    }
}