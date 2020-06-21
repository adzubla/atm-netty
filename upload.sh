#!/usr/bin/env bash

cd upload-config || exit 1

FILE=$(pwd)/config.txt

ls -l $FILE

mvn spring-boot:run -Dspring-boot.run.arguments=$FILE
