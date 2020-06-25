#!/usr/bin/env bash

cd upload-config || exit 1

FILE=$(pwd)/config.txt

# --ibm.mq.connName="172.17.0.3(32014)"

mvn spring-boot:run -Dspring-boot.run.arguments="$FILE $*"
