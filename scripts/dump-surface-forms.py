#!/usr/bin/env python

import sys
import json
import HTMLParser

mongo_db_dump = sys.argv[1]

output_file = sys.argv[2]
type_same_as = sys.argv[3]
type_name = sys.argv[4]

def get_title(uri):
    pos = uri.rfind("/")
    title = uri[pos+1:]
    title = title.replace("_"," ")
    h = HTMLParser.HTMLParser()
    title = h.unescape(title)
    return title

print "generate surface forms from ",mongo_db_dump
print "load same as from ",type_same_as

# freebase -> n_same_as
freebase_records = {} 
# dbpedia -> freebase
sameAs = {}

with open(type_same_as, 'rU') as s: 
    for line in s: 
        record = json.loads(line);
        if ('id' in record) :
            #print record['id']
            sameAs[record['id']]=''
        if ('id' in record and 'sameAs' in record):
            #print record['id'], "->",record['sameAs']
            sameAs[record['id']]=record['sameAs']
            freebase_records[record['sameAs']]=''
        if ('codeUri' in record):
            #print record['codeUri']
            sameAs[record['codeUri']]=''
        
print "load freebase"
# load freebase:
with open(mongo_db_dump, 'rU') as f:
    for line in f:
        record = json.loads(line);
        codeUri = record['codeUri']
        if 'freebase' in codeUri:
            if codeUri in freebase_records: 
                if 'representation' in record and 'owlSameAs' in record['representation']:
                    labels = len(record['representation']['owlSameAs'])
                    #print "match for ",codeUri, "-> ", labels
                    freebase_records[codeUri]=labels

print "write solr documents"
with open(output_file,'w') as o:
    o.write("[\n")
    start = True
    with open(mongo_db_dump, 'rU') as f:
      for line in f:
         if (not start): o.write(",\n")
         record = json.loads(line)
         document = {}
         uri = record['codeUri']
         # filter only dbpedia records
         if not 'dbpedia' in uri: continue
         # filter only the correct type entities (contained in the given sameAs)
         if not uri in sameAs: continue
         if 'en' not in record['representation']['prefLabel']:
             print "en not in description for ",uri,"skipping"
             continue
         mentions=record['representation']['prefLabel']['en']
         #prt= uri+"\t"+"\t".join(mentions)
         #print prt.encode('utf8', 'replace')
         document["uri"] = uri
         document["text"] = mentions
         document["type"]= type_name
         document["en_title"]=get_title(uri)
         document["same_as_n"]= 0
         if 'owlSameAs' in record['representation']:
             document["same_as_n"]=len(record['representation']['owlSameAs'])
         freebase_n = 0
         if uri in sameAs:
             freebase_uri = sameAs[uri]
             if freebase_uri in freebase_records:
                 freebase_n = freebase_records[freebase_uri]
         document['freebase_same_as']=freebase_n


         output = {}
         output['add'] = document
         o.write(json.dumps(document))
         start = False

    o.write("\n]")
         