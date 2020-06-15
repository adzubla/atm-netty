/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.example.atm.netty.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class ChatClientMain {

    static final String ID = String.format("%06d", ProcessHandle.current().pid());
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient(ID, HOST, PORT);
        try {
            client.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                client.write(line);

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
