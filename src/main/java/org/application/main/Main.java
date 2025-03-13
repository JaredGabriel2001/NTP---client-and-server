// Para compilar e empacotar o projeto:
// mvn package

// OBS: para executar o servidor local na porta 123, é necessário executar o servidor com o
// prefixo "sudo", pois é uma porta que necessita de privilégios para ser acessada.

// Executar com Maven:
// - Servidor COM HMAC: mvn exec:java -Dexec.args="server hmac"
// - Cliente COM HMAC: mvn exec:java -Dexec.args="client hmac 127.0.0.1"
// - Servidor SEM HMAC: mvn exec:java -Dexec.args="server plain"
// - Cliente SEM HMAC: mvn exec:java -Dexec.args="client plain 127.0.0.1"
// - Cliente para servidor oficial: mvn exec:java -Dexec.args="client official a.ntp.br"

// Executar sem Maven (após compilar e gerar o .jar):
// - Servidor COM HMAC: sudo java -jar target/ntp-app.jar server hmac
// - Cliente COM HMAC: java -jar target/ntp-app.jar client hmac 127.0.0.1
// - Servidor SEM HMAC: sudo java -jar target/ntp-app.jar server plain
// - Cliente SEM HMAC: java -jar target/ntp-app.jar client plain 127.0.0.1
// - Cliente para servidor oficial: java -jar target/ntp-app.jar client official a.ntp.br

package org.application.main;

import org.application.client.NtpClient;
import org.application.server.NtpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        // Validação mínima dos argumentos
        if (args == null || args.length < 2) {
            System.err.println("Usage: java Main <client|server> <hmac|plain|official> [serverAddress]");
            System.exit(1);
        }

        String role = args[0].trim().toLowerCase();
        String mode = args[1].trim().toLowerCase();

        if (!role.equals("client") && !role.equals("server")) {
            System.err.println("Invalid role: '" + args[0] + "'. Use 'client' or 'server'.");
            System.exit(1);
        }

        if (!mode.equals("hmac") && !mode.equals("plain") && !mode.equals("official")) {
            System.err.println("Invalid mode: '" + args[1] + "'. Use 'hmac', 'plain', or 'official'.");
            System.exit(1);
        }

        // Alterado: porta padrão para local passa a ser 123
        int port = 123;
        boolean useHmac = mode.equals("hmac");

        if (mode.equals("official")) {
            useHmac = false;
            port = 123; // Porta oficial NTP permanece 123
        }

        if (role.equals("server")) {
            if (mode.equals("official")) {
                System.err.println("Server mode 'official' is not supported. Use 'hmac' or 'plain' for server.");
                System.exit(1);
            }
            System.out.println("Starting NTP server in " + mode + " mode on port " + port);
            new NtpServer(useHmac, port).start();
        } else if (role.equals("client")) {
            if (args.length < 3 || args[2].trim().isEmpty()) {
                if (mode.equals("official")) {
                    System.err.println("Usage for official mode: java Main client official <serverAddress>");
                } else {
                    System.err.println("Usage: java Main client <hmac|plain> <serverAddress>");
                }
                System.exit(1);
            }
            String serverAddress = args[2].trim();
            System.out.println("Starting NTP client in " + mode + " mode connecting to " + serverAddress + ":" + port);
            new NtpClient(useHmac, port).requestTime(serverAddress);
        }
    }
}





