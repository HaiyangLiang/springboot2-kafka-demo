#!/usr/bin/env bash

cd `dirname $0`
#export JAVA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=8610"

nohup java $JAVA_OPTS -jar ./kafka-demo.jar --spring.config.location=../config/application.yml >nohup.txt 2>&1 &
