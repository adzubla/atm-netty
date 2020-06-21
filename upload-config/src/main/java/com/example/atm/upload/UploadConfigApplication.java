package com.example.atm.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

@SpringBootApplication
@EnableJms
public class UploadConfigApplication implements CommandLineRunner {

    @Autowired
    TopicPublisher topic;

    public static void main(String[] args) {
        SpringApplication.run(UploadConfigApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length <= 0) {
            System.err.println("Erro");
        } else {
            String text = readFile(args[args.length - 1]);
            topic.send(text);
        }
    }

    private String readFile(String name) throws IOException {
        File file = new File(name);
        LineNumberReader in = new LineNumberReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = in.readLine()) != null) {
            builder.append(line).append('\n');
        }

        System.out.println(in.getLineNumber() + " lines");

        return builder.toString();
    }

}
