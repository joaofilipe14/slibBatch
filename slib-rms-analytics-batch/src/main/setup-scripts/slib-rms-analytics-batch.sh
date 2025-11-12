#!/bin/bash

HOME_DIR="/home/data/slib-rms-analytics-batch"
#export SPRING_CONFIG_LOCATION="$HOME_DIR"/conf/
exec "$JAVA_HOME"/bin/java -jar "$HOME_DIR"/lib/*.jar
