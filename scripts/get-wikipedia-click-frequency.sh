#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=2

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` wikipedia_2015_01_clickstream.tsv.gz wikipedia-frequencies.tsv"
  exit $E_BADARGS
fi

#echo "convert mongo-db object to solr objects (in $2)"
#./scripts/dump-surface-forms.py $1 $2

#echo "deleting the previous index"
#echo curl $SERVER_URI"/update?stream.body=<delete><query>*:*</query></delete>"

echo "get frequencies (requires datamash and the wikipedia clickstream, in http://figshare.com/articles/Wikipedia_Clickstream/1305770"
zcat $1 | grep -v "other-empty" | awk -F'	' '{print $3"\t"$5}'    | datamash -g 2 sum 1 > $2
