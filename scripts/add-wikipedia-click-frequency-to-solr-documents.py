#!/usr/bin/env python

import sys
import json
import gzip
import urllib

wikipedia_freq = sys.argv[1]


wfreq = {}

with gzip.open(wikipedia_freq, 'r') as w:
#with open(wikipedia_freq, 'rU') as w: 
    i = 0
    for line in w: 
	#print line
	#print "|".join(line.split("\t"))
        (wiki,freq) = line.decode('utf-8').split("\t") #decoded!
        if "Kierkegaard" in wiki:
            print repr(wiki)
            print type(wiki)
        wfreq[wiki]=int(freq)
        if (i % 100000 == 0): print "load ",i, " freqs";
        i+=1


#with open(sys.argv[2], 'rU') as i:
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
                # wikipedia title uri format
                en_title = urllib.unquote_plus(str(en_title)).decode('utf8')
                #en_title = urllib.unquote_plus(en_title)
                en_title = en_title.replace(' ','_')
                if "Kierkegaard" in en_title:
                    print type(en_title)
                    print repr(en_title), repr(wfreq[en_title])
                if (en_title in wfreq):
                    obj['wikipedia_clicks']=wfreq[en_title]
                else: 
                    #print "cannot find freq for ", en_title.encode('utf8')
                    obj['wikipedia_clicks']=-1
                o.write(json.dumps(obj))
            o.write('\n]')
    
         
         
