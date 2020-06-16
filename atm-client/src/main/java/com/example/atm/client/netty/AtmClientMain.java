package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class AtmClientMain {

    static final String ID = String.format("%07d", ProcessHandle.current().pid());
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

    public static void main(String[] args) throws Exception {
        AtmClient client = new AtmClient(ID, HOST, PORT);
        try {
            client.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                AtmMessage msg = new AtmMessage();
                msg.setId(ID);
                msg.setBody(line);

                client.write(msg);

                if ("bye".equals(line.toLowerCase())) {
                    client.close();
                    break;
                }
            }
        } finally {
            client.shutdown();
        }
    }

}
