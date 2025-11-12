#!/bin/sh

HOME_DIR=/home/data/app
cd "$HOME_DIR"
export SPRING_CONFIG_LOCATION="$HOME_DIR"/conf/
. $HOME_DIR/conf/.docker-jasypt.conf
exec "$JAVA_HOME"/bin/java -Dlogging.config="$HOME_DIR"/conf/logback-spring.xml -jar "$HOME_DIR"/lib/*.jar
