#!/usr/bin/env bash

curl -X POST http://localhost:8081/actuator/shutdown
echo
curl -X POST http://localhost:8082/actuator/shutdown
