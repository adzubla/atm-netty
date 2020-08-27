package com.example.atm.client.cli;

import com.example.atm.client.netty.AtmClient;
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
import java.math.BigInteger;
import java.util.List;

@SpringBootApplication
public class AtmClientApplication implements ApplicationRunner, ExitCodeGenerator {

    @Value("${atm-server.host}")
    private String host;

    @Value("${atm-server.port}")
    private int port;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(AtmClientApplication.class, args)));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> nonOptionArgs = args.getNonOptionArgs();

        long id = nonOptionArgs.size() <= 0 ? ProcessHandle.current().pid() : Long.parseLong(nonOptionArgs.get(0));
        if (id > 9999999L) {
            id = BigInteger.valueOf(id).mod(BigInteger.valueOf(10000000L)).longValue();
        }
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

    public void processConsole(Long atmId) throws Exception {

        try (AtmClient client = new AtmClient(host, port, new CliAtmClientHandler());
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
                if ("ping".equals(line.toLowerCase())) {
                    client.ping(atmId);
                    continue;
                }

                boolean useMac = true;
                String mti = "0100";
                StringBuilder bitmapBin = new StringBuilder("0010001000010000000000000001000100000010110000000100100000000101");
                if (line.startsWith("x")) {
                    // sem o MAC
                    bitmapBin.setCharAt(63, '0');
                    useMac = false;
                }
                String bitmapHex = (new BigInteger(bitmapBin.toString(), 2)).toString(16).toUpperCase();
                assert bitmapHex.length() == 16;

                AtmMessage msg = new AtmMessage(atmId, mti + bitmapHex + line);
                client.write(msg, useMac);
            }
        }
    }

    public void processFile(Long atmId, String fileName) throws Exception {

        try (AtmClient client = new AtmClient(host, port, new CliAtmClientHandler());
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
            client.write(msgAtm, false);

            // Espera pela resposta
            Thread.sleep(1000);
        }
    }

}
