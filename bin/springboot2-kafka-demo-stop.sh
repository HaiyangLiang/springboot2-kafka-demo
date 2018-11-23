#!/usr/bin/env bash

cd `dirname $0`
#lsof -i:8611 | grep java | awk '{print $2}' | xargs kill -9
ps -ef | grep kafka-demo | grep java | awk '{print $2}' | xargs kill -9

