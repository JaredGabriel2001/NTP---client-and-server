//mvn package
//mvn exec:java -Dexec.args="server"
//mvn exec:java -Dexec.args="client 127.0.0.1"

package org.example.main;

import org.example.client.NtpClient;
import org.example.server.NtpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java Main <client|server> [serverAddress]");
            return;
        }

        if (args[0].equalsIgnoreCase("server")) {
            new NtpServer().start();
        } else if (args[0].equalsIgnoreCase("client")) {
            if (args.length < 2) {
                System.err.println("Usage: java Main client <serverAddress>");
                return;
            }
            new NtpClient().requestTime(args[1], 8123);
        } else {
            System.err.println("Invalid argument. Use 'client' or 'server'.");
        }
    }
}
