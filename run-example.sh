#!/bin/sh

set -e

gradle build
java -jar build/libs/app.jar example-org-file example-user-file