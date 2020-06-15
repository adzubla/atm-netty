#!/usr/bin/env bash

cd atm-server || exit 1

mvn exec:java -Dexec.mainClass=com.example.atm.netty.server.AtmServerMain
