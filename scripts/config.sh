#!/usr/bin/env bash

VERSION="0.0.1-SNAPSHOT"
XMX="-Xmx2000m"
LOG=INFO
##LOG=DEBUG
LOGAT=1000
E_BADARGS=65
SERVER_URI="http://localhost:8080/europeana"

JAVA="java $XMX -Dlogat=$LOGAT -Dlog=$LOG -cp .:./target/entity-suggestions-4.3.1.jar"
CLI=eu.europeana.util.cli

export LC_ALL=C
