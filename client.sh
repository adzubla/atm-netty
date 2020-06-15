#!/usr/bin/env bash

cd atm-client || exit 1

mvn exec:java -Dexec.mainClass=com.example.atm.netty.client.ChatClientMain
