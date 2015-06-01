#!/usr/bin/env python

import sys
import json
import HTMLParser
import urllib2, urllib
import re
import threading
from unidecode import unidecode

mongo_db_dump = sys.argv[1]
output_file = sys.argv[2]
lock = threading.Lock()
i = 0;
start = False

class EThread (threading.Thread):
    
    def get_query(self, obj):
        labels = set()
        for key in obj.keys():
            if "labels" in key or "text" in key:
                for v in obj[key]:
                    v = unidecode(v)
                    v = v.replace("("," ")
                    v = v.replace(")"," ")
                    v = v.replace("["," ")
                    v = v.replace("]"," ")                    
                    v = v.replace("\"","")
                    v = v.replace(','," ")
                    ## normalize multiple spaces
                    v = ' '.join(v.split())
                    v = v.strip()
                    
                    
                    if (len(v) <= 1): continue
                    if (len(v.split()) <= 1): continue
                    
                    v = "who:\""+v+"\""
                    
                    labels.add(v)
        if (len(labels) == 0): return ""            
        v = " OR ".join(list(labels))
        #print v
        #v = v.replace('"'," ")
        return v;
        
    
    
    def __init__(self, i, f,o):
        print "start thread ",i
        threading.Thread.__init__(self)
        self.f = f
        self.o = o
        
    def run(self):
        global i, start
        while True:
            lock.acquire()
            line = self.f.readline()
            if (line == ''): 
                lock.release()
                return
            lock.release()
            
            i+=1
            line = line.strip("[, ]")
            line = line.strip()
            if (len(line) <= 0): continue; 
            if (line[-1] == ','): line = line[:-1]        
            #if (not start): o.write(",\n")
            record = json.loads(line)
            en_title = record["en_title"]
            title = urllib.unquote(en_title)
            title = title.encode('latin-1')
            #title = re.sub(r'\([^)]*\)', '', title)
            title = title.strip()
            title = title.replace(" ","*")
            freq = 0
          
                
            #print "query:",q
            q = self.get_query(record)
            
                
            europeana_url = 'http://test.portal2.eanadev.org/api/v2/search.json'
            data = urllib.urlencode({"wskey":"api2demo", "query":q, "start":"1","rows":"0","profile":"standard"})
            if (q == ""):
                freq = -1
            else:
                try:
                    request = urllib2.Request(europeana_url, data) # Manual encoding required
                    handler = urllib2.urlopen(request)
                except:
                    print "ERROR", data
                    continue
                res = json.loads( handler.read())
                freq = res['totalResults']
            lock.acquire()
            self.o.write(q+"\n")
            
            self.o.write(en_title+"\t"+str(freq)+"\n")
            lock.release()
            if (i % 10 is 0): print "processed ", i , "entities"
            start = False
  
        
        

print "add document frequencies to ",mongo_db_dump, "in ",output_file
with open(output_file,'w') as o:
    start = True
    i = 0;
    with open(mongo_db_dump, 'rU') as f:
        threads = [] 
        for i in range(40):
            print "run ",i
            t = EThread(i,f,o)
            t.start()
            threads.append(t)
        for t in threads:   
            t.join() 
    

    
         