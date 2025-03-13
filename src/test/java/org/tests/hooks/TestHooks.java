package org.tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.application.server.NtpServer;

public class TestHooks {
    private static NtpServer server;

    @Before("@LocalServer")
    public void startServer() {
        // Inicia o servidor local em modo HMAC
        server = new NtpServer(true, 123); // Porta 123 com HMAC
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

    @After("@LocalServer")
    public void stopServer() {
        // Para o servidor após o cenário
        if (server != null) {
            server.stop(); // Adicione um método `stop` na classe NtpServer para encerrar o servidor
        }
    }
}