#!/usr/bin/env bash

cd atm-client || exit 1

# --server.host=172.17.0.3 --server.port=30992

mvn spring-boot:run -Dspring-boot.run.arguments="$*"
