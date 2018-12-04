
docker stop pravega
docker run --name pravega --rm -e HOST_IP=192.168.1.113 -p 9090:9090 -p 12345:12345 pravega/pravega:0.4.0-7a9bdb4-SNAPSHOT standalone
pause
