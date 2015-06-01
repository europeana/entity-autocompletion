#!/usr/bin/env python 

import sys

with open(sys.argv[1]) as i:
    with open(sys.argv[2],"w") as o:
        for line in i:
            (name,freq) = line.split("\t")
            wikiname = name.replace(" ","_")
            dbpedia = "http://dbpedia.org/resource/"+wikiname
            o.write(dbpedia+"\t"+name+"\t"+freq)
            