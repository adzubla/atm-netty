#!/usr/bin/env bash

cd atm-server || exit 1

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dio.netty.leakDetectionLevel=PARANOID -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"
