package com.example.atm.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@EnableJms
public class UploadConfigApplication implements ApplicationRunner {

    @Autowired
    TopicPublisher topic;

    public static void main(String[] args) {
        SpringApplication.run(UploadConfigApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> nonOptionArgs = args.getNonOptionArgs();

        if (nonOptionArgs.size() != 1) {
            System.err.println("Erro");
        } else {
            String fileName = nonOptionArgs.get(0);
            String text = readFile(fileName);
            topic.send(text);
            System.out.println("Done");
        }
    }

    private String readFile(String name) throws IOException {
        File file = new File(name);
        BufferedReader in = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();

        int c = 0;
        String line;
        while ((line = in.readLine()) != null) {
            if (!line.isBlank() && line.length() == 12) {
                builder.append(line).append('\n');
                c++;
            } else {
                System.out.printf("Ignoring '%s'%n", line);
            }
        }

        System.out.println(c + " items");

        return builder.toString();
    }

}
