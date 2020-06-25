#!/usr/bin/env bash

mvn clean install

cd dummy-responder || exit 1
mvn spring-boot:build-image

cd ..

cd atm-server || exit 1
mvn spring-boot:build-image
