#!/bin/sh

set -e

./build.sh
build/javakata.run testfiles/example-org-file testfiles/example-user-file