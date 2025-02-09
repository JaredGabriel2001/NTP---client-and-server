//mvn package

//executar server COM hmac: mvn exec:java -Dexec.args="server hmac"
//executar client COM hmac: mvn exec:java -Dexec.args="client hmac 127.0.0.1"

//executar server SEM hmac: mvn exec:java -Dexec.args="server plain"
//executar client SEM hmac: mvn exec:java -Dexec.args="client plain 127.0.0.1"

//executar client para servidor oficial: mvn exec:java -Dexec.args="client official a.ntp.br"

package org.application.main;

import org.application.client.NtpClient;
import org.application.server.NtpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        // Validação mínima: deve haver pelo menos 2 argumentos
        if (args == null || args.length < 2) {
            System.err.println("Usage: java Main <client|server> <hmac|plain|official> [serverAddress]");
            System.exit(1);
        }

        // Remove espaços em branco e converte para minúsculas para facilitar a comparação
        String role = args[0].trim().toLowerCase();
        String mode = args[1].trim().toLowerCase();

        // Validação do papel (role)
        if (!role.equals("client") && !role.equals("server")) {
            System.err.println("Invalid role: '" + args[0] + "'. Use 'client' or 'server'.");
            System.exit(1);
        }

        // Validação do modo
        if (!mode.equals("hmac") && !mode.equals("plain") && !mode.equals("official")) {
            System.err.println("Invalid mode: '" + args[1] + "'. Use 'hmac', 'plain', or 'official'.");
            System.exit(1);
        }

        boolean useHmac = false;
        int port = 8123; // porta padrão para o modo customizado (hmac ou plain)

        if (mode.equals("hmac")) {
            useHmac = true;
            port = 8123;
        } else if (mode.equals("plain")) {
            useHmac = false;
            port = 8123;
        } else if (mode.equals("official")) {
            useHmac = false;
            port = 123; // porta oficial NTP
        }

        // Se o usuário escolheu o papel de servidor
        if (role.equals("server")) {
            if (mode.equals("official")) {
                System.err.println("Server mode 'official' is not supported. Use 'hmac' or 'plain' for server.");
                System.exit(1);
            }
            System.out.println("Iniciando servidor no modo " + mode + " na porta " + port);
            new NtpServer(useHmac, port).start();
        }
        // Se o usuário escolheu o papel de cliente
        else if (role.equals("client")) {
            // Para o cliente, o terceiro argumento (serverAddress) é obrigatório
            if (args.length < 3 || args[2].trim().isEmpty()) {
                if (mode.equals("official")) {
                    System.err.println("Usage for official mode: java Main client official <serverAddress>");
                } else {
                    System.err.println("Usage: java Main client <hmac|plain> <serverAddress>");
                }
                System.exit(1);
            }
            String serverAddress = args[2].trim();
            System.out.println("Iniciando cliente no modo " + mode + " para se conectar a " + serverAddress + ":" + port);
            new NtpClient(useHmac, port).requestTime(serverAddress);
        }
    }
}



