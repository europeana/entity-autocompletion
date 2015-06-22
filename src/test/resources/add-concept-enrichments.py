#!/usr/bin/env python

import sys
import json
import urllib


wikipedia_freq = sys.argv[1]


wfreq = {}

def stripUrl(url):
    entity = url.split("/")[-1]
    entity = entity.replace("_"," ")
    return entity


with open(wikipedia_freq, 'r') as w: 
    i = 0
    for line in w: 
        (wiki,freq) = line.decode('utf8').split(";")
	wiki = stripUrl(wiki)
        wfreq[wiki]=int(freq)
        if (i % 100000 == 0): print "load ",i, " freqs";
        i+=1

print "Finished"

with open(sys.argv[2], 'rU') as i:
    with open(sys.argv[3], 'w') as o:
            start=True
            o.write('[\n')
            for line in i:
                
                try: 
                    obj = json.loads(line[:-2])
                except:
                    print "error line: ",line
                    continue
                if not start: o.write(',\n')
                start = False
                en_title = obj['en_title']
                #en_title = urllib.unquote_plus(str(en_title)).decode('utf8')
                # wikipedia title uri format
                if (en_title in wfreq):
                    obj['enrichment']=wfreq[en_title]
                elif not "enrichment" in obj:   
                    #print "cannot find freq for ",en_title
                    obj['enrichment']=1
		    # this is 1 because the boost is a product and we have no enrichments for concepts
                o.write(json.dumps(obj))
		o.flush()
            o.write('\n]')
    
         
         
