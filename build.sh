#!/bin/sh

set -e

gradle build
cat stub.sh build/libs/app.jar > build/javakata.run
chmod +x build/javakata.run