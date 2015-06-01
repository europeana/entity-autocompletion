#!/usr/bin/env python
import gzip
import sys
import json
import HTMLParser
import urllib

solr_file = sys.argv[1]
wiki_data = sys.argv[2]
output_file = sys.argv[3]

def get_title(uri):
    pos = uri.rfind("/")
    title = uri[pos+1:]
    title = title.replace("_"," ")
    h = HTMLParser.HTMLParser()
    title = h.unescape(title)
    return title


def title(lang,data):
    if 'sitelinks' in data:
        l = data['sitelinks']
        w = lang+"wiki"
        if w in l:
            return l[w]['title']
    
    if 'labels' in data:
        l = data['labels']
        if lang in l:
            return l[lang]['value']
   
    return ""    
        
def dump_lang(lang, data):
    labels = set()
    if 'aliases' in data:
        aliases = data['aliases']

        if lang in aliases:
            for item in aliases[lang]:
                labels.add(item['value'])
    if 'labels' in data:
        l = data['labels']

        if lang in l:
            labels.add(l[lang]['value'])  
    if 'sitelinks' in data:
        l = data['sitelinks']
        lang = lang+"wiki"
        if lang in l:
            labels.add(l[lang]['title'])                  
        
    return labels
    
        

print "languages surface forms from ",wiki_data

solr = {}

languages = ["it", "fr", "de"]
print "loading solr"
#with open(solr_file,'rU') as i:
with open(solr_file,'r') as i:
    for line in i:
	if line not in ['\n', '\r\n', ',\n']:
            line = line.rstrip(',\n')
            try:
                data = json.loads(line)
		transform = urllib.unquote_plus(str(data['en_title'])).decode('utf8')
		#data['en_title']=transform
                solr[transform]=data
		if "Kierkegaard" in data['en_title']:
			print repr(transform)
			print repr(data)
            except ValueError as inst:
                print inst
            	#continue
print "done"
print "Number of loaded agents", len(solr)
#print solr[urllib.unquote_plus("S%C3%B8ren Kierkegaard")]

print "output the enriched solr documents"
with open(output_file,'w') as o:
    o.write("[\n")
    start = True
    with gzip.open(wiki_data, 'rb') as f:
        for line in f:
            try:
                
                line = line[:-2]
                if not line: continue 
                data = json.loads(line)
               
                if "sitelinks" in data:
                    if "enwiki" in data['sitelinks']:
                        #print data['sitelinks']['enwiki']['title']
                        enwiki = data['sitelinks']['enwiki']['title']
			if "Kierkegaard" in enwiki:
				print repr(enwiki), repr(solr[enwiki]) #quetso e strano, mica carica un json
				print solr[enwiki]
			
                        if enwiki in solr:
                            #print enwiki
                            e = solr[enwiki]	
                            
                            for lang in languages: 
                                e[lang+"_labels"] =  list(dump_lang(lang,data))
                                e[lang+"_title"] =  title(lang,data)
                                #print lang,"["+title(lang,data).encode("UTF-8")+"]",",".join(dump_lang(lang,data)).encode("UTF-8")
                            o.write(json.dumps(e))
                            o.write(",\n")
            except Exception as inst:
                print inst
                #print "error", inst
                #print "line",line
                continue
         
    o.write("\n]")
         
