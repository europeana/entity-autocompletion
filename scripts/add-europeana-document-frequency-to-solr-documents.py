#!/usr/bin/env python

import sys
import json
import urllib


wikipedia_freq = sys.argv[1]


wfreq = {}

with open(wikipedia_freq, 'r') as w: 
    i = 0
    for line in w: 
        (wiki,freq) = line.decode('utf8').split("\t")
        if "Kierkegaard" in wiki:
            print repr(wiki)
            print type(wiki)
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
                if "Kierkegaard" in en_title:
                    print type(en_title)
                    print repr(en_title), repr(wfreq[en_title])
                if (en_title in wfreq):
                    obj['europeana_df']=wfreq[en_title]
                elif not "europeana_df" in obj:   
                    #print "cannot find freq for ",en_title
                    obj['europeana_df']=-1
                o.write(json.dumps(obj))
		o.flush()
            o.write('\n]')
    
         
         
