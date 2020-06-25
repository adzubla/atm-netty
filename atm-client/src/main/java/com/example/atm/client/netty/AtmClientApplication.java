package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootApplication
public class AtmClientApplication implements ApplicationRunner {

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(AtmClientApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> nonOptionArgs = args.getNonOptionArgs();

        long id = nonOptionArgs.size() <= 0 ? ProcessHandle.current().pid() : Long.parseLong(nonOptionArgs.get(0));

        execute(id);
    }

    public void execute(long id) throws Exception {
        String atmId = String.format("%07d", id);

        AtmClient client = new AtmClient(host, port);
        try {
            client.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    continue;
                }
                if ("bye".equals(line.toLowerCase())) {
                    break;
                }

                AtmMessage msg = new AtmMessage(atmId, line);
                client.write(msg);
            }
        } finally {
            client.close();
            System.err.println("Closed");
        }
    }

}
