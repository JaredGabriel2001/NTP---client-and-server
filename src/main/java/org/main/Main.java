//mvn package

//executar server COM hmac: mvn exec:java -Dexec.args="server hmac"
//executar server SEM hmac: mvn exec:java -Dexec.args="server plain"

//executar client COM hmac: mvn exec:java -Dexec.args="client hmac 127.0.0.1"
//executar client SEM hmac: mvn exec:java -Dexec.args="client plain 127.0.0.1"

//executar client para servidor oficial: mvn exec:java -Dexec.args="client official a.ntp.br"

package org.main;

import org.client.NtpClient;
import org.server.NtpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java Main <client|server> <hmac|plain|official> [serverAddress]");
            return;
        }

        String role = args[0].toLowerCase();
        String mode = args[1].toLowerCase();

        boolean useHmac = false;
        int port = 8123; // porta padrão customizada

        if (mode.equals("hmac")) {
            useHmac = true;
            port = 8123;
        } else if (mode.equals("plain")) {
            useHmac = false;
            port = 8123;
        } else if (mode.equals("official")) {
            useHmac = false;
            port = 123; // porta oficial NTP
        } else {
            System.err.println("Invalid mode. Use 'hmac', 'plain', or 'official'.");
            return;
        }

        if (role.equals("server")) {
            if (mode.equals("official")) {
                System.err.println("Server mode 'official' is not supported. Use 'hmac' or 'plain' for server.");
                return;
            }
            new NtpServer(useHmac, port).start();
        } else if (role.equals("client")) {
            if (args.length < 3) {
                System.err.println("Usage: java Main client <hmac|plain|official> <serverAddress>");
                return;
            }
            String serverAddress = args[2];
            new NtpClient(useHmac, port).requestTime(serverAddress);
        } else {
            System.err.println("Invalid role. Use 'client' or 'server'.");
        }
    }
}

