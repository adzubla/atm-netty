#!/usr/bin/env bash

cd atm-client || exit 1

mvn exec:java -Dexec.mainClass=com.example.atm.client.netty.AtmClientCli -Dexec.args="$1"
