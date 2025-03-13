package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import org.application.client.NtpClient;

public class Local_server_client_HMACSteps {
    private NtpClient client;
    private boolean clientSuccess;
    private String errorMessage;

    @Given("the local server using HMAC is running correctly")
    public void the_local_server_using_hmac_is_running_correctly() {
        // O servidor já foi iniciado pelo hook, então não precisamos fazer nada aqui
    }

    @When("the local client using HMAC runs correctly")
    public void the_local_client_using_hmac_runs_correctly() {
        // Inicia o cliente com HMAC e tenta se conectar ao servidor local
        client = new NtpClient(true, 123); // Porta 123 com HMAC
        try {
            client.requestTime("127.0.0.1"); // Conecta ao servidor local
            clientSuccess = true; // Indica que o cliente rodou corretamente
        } catch (Exception e) {
            clientSuccess = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("the return must be correct with HMAC")
    public void the_return_must_be_correct_with_HMAC() {
        assertTrue(clientSuccess, "O cliente não rodou corretamente.");
        System.out.println("Cliente rodou corretamente e recebeu uma resposta válida.");
    }

    @When("the local client using HMAC does not run correctly")
    public void the_local_client_using_hmac_does_not_run_correctly() {
        // Simula um erro no cliente (por exemplo, servidor não disponível ou HMAC inválido)
        client = new NtpClient(true, 124); // Porta 124 (não usada pelo servidor)
        try {
            client.requestTime("127.0.0.1"); // Tenta se conectar à porta errada
            clientSuccess = true; // Se não lançar exceção, o cliente rodou corretamente
        } catch (Exception e) {
            clientSuccess = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("an error message should be displayed with HMAC")
    public void an_error_message_should_be_displayed_with_HMAC() {
        // Verifica se uma mensagem de erro foi exibida
        assertFalse(clientSuccess, "O cliente rodou corretamente, mas era esperado um erro.");
        assertNotNull(errorMessage, "Nenhuma mensagem de erro foi exibida.");
        System.out.println("Mensagem de erro exibida: " + errorMessage);
    }
}