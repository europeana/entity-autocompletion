#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=1

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` solr-documents.json"
  exit $E_BADARGS
fi

#echo "convert mongo-db object to solr objects (in $2)"
#./scripts/dump-surface-forms.py $1 $2

#echo "deleting the previous index"
#echo curl $SERVER_URI"/update?stream.body=<delete><query>*:*</query></delete>"

echo "index $1"
echo curl $SERVER_URI"/update -H Content-type:text/json --data-binary @$1"

echo "commit"
echo curl $SERVER_URI"/update?stream.body=<commit/>"

echo "done"
