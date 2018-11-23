#!/usr/bin/env bash

cd `dirname $0`
echo 'stopping kafka-demo'
sh kafka-demo-stop.sh

echo 'starting kafka-demo'
sh kafka-demo.sh
