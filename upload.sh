#!/usr/bin/env bash

cd upload-registry || exit 1

FILE=$(pwd)/registry.txt

if [ "$1" == "-k" ]; then
  OPTS="--ibm.mq.connName='$(minikube ip)(32014)'"
  shift
fi

mvn spring-boot:run -Dspring-boot.run.arguments="$OPTS $FILE $*"
