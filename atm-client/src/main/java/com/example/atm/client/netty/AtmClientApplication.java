package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootApplication
public class AtmClientApplication implements ApplicationRunner, ExitCodeGenerator {

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(AtmClientApplication.class, args)));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> nonOptionArgs = args.getNonOptionArgs();

        long id = nonOptionArgs.size() <= 0 ? ProcessHandle.current().pid() : Long.parseLong(nonOptionArgs.get(0));
        System.err.printf("\nClient id: %d%n", id);

        if (nonOptionArgs.size() > 1) {
            String msgFile = nonOptionArgs.get(1);
            processFile(id, msgFile);
        } else {
            processConsole(id);
        }
    }

    @Override
    public int getExitCode() {
        return 0;
    }

    public void processConsole(long id) throws Exception {
        String atmId = String.format("%07d", id);

        try (AtmClient client = new AtmClient(host, port);
             InputStreamReader streamReader = new InputStreamReader(System.in)) {

            client.connect();

            BufferedReader in = new BufferedReader(streamReader);
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    continue;
                }
                if ("bye".equals(line.toLowerCase())) {
                    break;
                }
                if (line.length() < 20) {
                    line = line.concat("                    ").substring(0, 20);
                }

                AtmMessage msg = new AtmMessage(atmId, line);
                client.write(msg);
            }
        }
    }

    public void processFile(long id, String fileName) throws Exception {
        String atmId = String.format("%07d", id);

        try (AtmClient client = new AtmClient(host, port);
             FileReader fileReader = new FileReader(fileName)) {

            client.connect();

            BufferedReader in = new BufferedReader(fileReader);
            String msg = in.readLine();
            StringBuilder m = new StringBuilder();
            for (int i = 0; i < msg.length(); i += 2) {
                String s = msg.substring(i, i + 2);
                m.append((char) Integer.parseInt(s, 16));
            }
            System.err.printf("%nMsgAtm: %s%n", m);

            AtmMessage msgAtm = new AtmMessage(atmId, m.toString());
            client.write(msgAtm);

            // Espera pela resposta
            Thread.sleep(1000);
        }
    }

}
