#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=0

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0`"
  exit $E_BADARGS
fi

echo "start rest service"

$JAVA $CLI.StartRestServiceCLI