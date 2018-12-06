#!/usr/bin/env bash
docker stop pravega
docker rm pravega
docker run --name pravega -e HOST_IP=192.168.1.126 -p 9090:9090 -p 12345:12345 pravega/pravega:0.4.0-7a9bdb4-SNAPSHOT standalone
