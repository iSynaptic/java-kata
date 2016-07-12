#!/bin/sh

set -e

./build.sh

echo ""
echo "=========RUNNING WITH EXAMPLE INPUT========="
echo ""

build/javakata.run testfiles/example-org-file testfiles/example-user-file

echo ""