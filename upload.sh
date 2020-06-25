#!/usr/bin/env bash

cd upload-config || exit 1

FILE=$(pwd)/config.txt

# --ibm.mq.connName="localhost(1414)"

mvn spring-boot:run -Dspring-boot.run.arguments="$FILE $*"
