#!/usr/bin/env bash

cd atm-client-cli || exit 1

if [ "$1" == "-k" ]; then
  OPTS="--iso-server-host=$(minikube ip) --iso-server-port=30992"
  shift
fi

mvn spring-boot:run -Dspring-boot.run.arguments="$OPTS $*"
