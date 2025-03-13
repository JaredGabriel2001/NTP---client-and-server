package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import org.application.client.NtpClient;
import org.application.server.NtpServer;

public class Local_server_client_HMACSteps {
    private NtpServer server;
    private NtpClient client;
    private boolean requestSuccess;

    // Cenário de sucesso: servidor e cliente com HMAC funcionando corretamente
    @Given("the local server using HMAC is running correctly")
    public void the_local_server_using_hmac_is_running_correctly() {
        try {
            // Constrói o servidor com HMAC habilitado na porta 8123
            server = new NtpServer(true, 8123);
            // Inicia o servidor em uma thread separada para não bloquear os testes
            new Thread(() -> {
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            // Aguarda um instante para garantir que o servidor iniciou
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("Failed to start local server with HMAC: " + e.getMessage());
        }
    }

    @When("the local client using HMAC runs correctly")
    public void the_local_client_using_hmac_runs_correctly() {
        try {
            // Cria o cliente com HMAC habilitado na mesma porta
            client = new NtpClient(true, 8123);
            // Tenta obter o tempo do servidor usando "localhost"
            client.requestTime("localhost");
            requestSuccess = true;
        } catch (Exception e) {
            requestSuccess = false;
            fail("Client with HMAC should run correctly, but error: " + e.getMessage());
        }
        assertTrue(requestSuccess, "Client with HMAC ran correctly");
    }

    // Cenário de falha: força uma falha (por exemplo, usando um hostname inválido)
    @When("the local client using HMAC does not run correctly")
    public void the_local_client_using_hmac_does_not_run_correctly() {
        try {
            client = new NtpClient(true, 8123);
            client.requestTime("invalid.host");
            requestSuccess = true;
            fail("Client with HMAC should have failed but succeeded.");
        } catch (Exception e) {
            requestSuccess = false;
        }
        assertFalse(requestSuccess, "Client with HMAC did not run correctly as expected");
    }
}
