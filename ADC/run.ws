#!/bin/bash
echo Read an ADC, feed a WebSocket
#
CP=./classes
CP=$CP:/home/pi/pi4j/pi4j-distribution/target/distro-contents/lib/pi4j-core.jar
CP=$CP:./lib/orasocket-client-12.1.3.jar
#
sudo java -cp $CP -Dws.uri=ws://localhost:9876/ adc.sample.WebSocketFeeder $*