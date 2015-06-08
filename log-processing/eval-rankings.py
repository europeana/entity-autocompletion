__author__ = 'cris'

"""
we evaluate different rankings by slightly tweaking the ranking functions

"""
import sys
import json
import requests
import xml.etree.ElementTree as ET

log_file = sys.argv[1]


def stripUrl(url):
    entity = url.split("/")[-1]
    return entity

'''
This function returns the URL for querying the API with a ranking function that has the following parameters:
- implicit: number of returned results (rows=20) and eDisMax Query Parser (https://wiki.apache.org/solr/ExtendedDisMax)
- ranking function: these params remain more ore less the same in all URLs and they are explained in the link above
- boost (additive): bf boost is added to the score of the ranking function obtained above and is made of the product
    between Europeana document frequency  and Wikipedia clicks
- explain (optional): shows the actual scores obtained to each results returned by the API

'''
def queryParamsAditiveBoost():
    implicit = "&rows=20&defType=edismax"
    ranking_func= "&qf=text_suggest^30+text_suggest_ngram^50.0&pf=text_suggest_edge^50.0&fq=europeana_df:[1%20TO%20*]"
    additive_boost = "&bf=product(europeana_df,wikipedia_clicks)"
    explain = "&fl=*,[explain%20style=nl]"
    return implicit+ranking_func+additive_boost


'''
Replace "bf" with "boost".
 Boost - multiplies the boost into the score, whereas bf adds the boosting to the score.
 Here we bring all weights to a positive values, as, when missing, wikipedia clicks and enrichment have as value "-1"
'''
def queryParamsProductBoost():
    implicit = "&rows=20&defType=edismax"
    ranking_func= "&qf=text_suggest^90+text_suggest_ngram^50.0&pf=text_suggest_edge^50.0&fq=europeana_df:[1%20TO%20*]"
    additive_boost = "&boost=product(europeana_df,sum(wikipedia_clicks,2), sum(enrichment,2))"
    return implicit+ranking_func+additive_boost

'''
We parametrize the function in order vary the weight for certain fields.
We use the additive boost and sum the product between each field and its weight
'''
# ts - text_suggest; tsng - text_suggest_ngrams ; tse - text_suggest_edge
def queryParamsAdditiveBoostWeighedSum(europeana_weight, wiki_weight, enrichment_weight, ts_weight, tsng_weight, tse_weight):
    implicit = "&rows=20&defType=edismax"
    ranking_func = "&qf=text_suggest^30+text_suggest_ngram^50.0&pf=text_suggest_edge^50.0&fq=europeana_df:[1%20TO%20*]"
    additive_boost = "&bf=sum(product(" + str(europeana_weight) + ",europeana_df),product(" + str(
        wiki_weight) + ",sum(wikipedia_clicks,1)),product(" + str(enrichment_weight) + ",sum(enrichment,1)),product(" + str(ts_weight) + ",text_suggest),product(" + str(tsng_weight) + ",text_suggest_ngram),product(" + str(tse_weight) + ",text_suggest_edge))"
    explain = "&fl=*,[explain%20style=nl]"
    return implicit + ranking_func + additive_boost + explain

'''
With "termfreq" look for the occurrence of query in the text_suggest_ngram field
'''
def queryParamsAdditiveBoostWeighedTermFreq():
    implicit = "&rows=20&defType=edismax"
    ranking_func= "&qf=text_suggest^30+text_suggest_ngram^50.0&pf=text_suggest_edge^50.0&fq=europeana_df:[1%20TO%20*]"
    additive_boost = "&bf=sum(product(0.8,europeana_df),product(0.6,termfreq(text_suggest_ngram,$q)))"
    return implicit+ranking_func+additive_boost

'''
This function returns the position of the correct "entity" in the list of results obtained from the API
'''
def getPositionFromSuggestions(query, entity, europeana_weight, wiki_weight, enrichment_weight, ts_weight, tsng_weight, tse_weight):
    suggestions_demo_url = "http://node5.novello.isti.cnr.it:8080/select?q="
    #query_params = queryParamsAditiveBoost()
    #query_params = queryParamsProductBoost()
    query_params = queryParamsAdditiveBoostWeighedSum(europeana_weight, wiki_weight, enrichment_weight, ts_weight, tsng_weight, tse_weight)
    #query_params = queryParamsAdditiveBoostWeighedTermFreq()

    query_url = suggestions_demo_url + query + query_params
    print query_url
    response = requests.get(query_url, stream=True)
    tree = ET.parse(response.raw)
    root = tree.getroot()

    countPos = 0
    position = 0

    result = root[1]
    for doc in result:
        for attribute in doc.findall("str"):
            if attribute.get('name') == 'uri':
                candidateEntity = stripUrl(attribute.text)
            if attribute.get('name') == 'en_title':
                enTitle = attribute.text
                if len(enTitle) > 5:
                    countPos += 1
                if candidateEntity == entity:
                    position = countPos
                    #print "Real E: ", entity, " Candidate E: ", candidateEntity, "in position: ", position

    return position

'''
This function return the average position of the correct entity for all the dataset, given one set of parameters
'''
def run_one_time_all_set():
    sum = 0.0
    items = 0
    with open(log_file, 'r') as f:
        for line in f:
            data = json.loads(line)
            if data["clickPos"] >= 0:  # this means that a result has be selected, otherwise -1
                query = data["response"]["query"]
                entity = stripUrl(data["selected"]["uri"])
                positionSolr = getPositionFromSuggestions(query, entity, 1000, 1000, 100)
                if positionSolr > 0:
                    items = items + 1
                    sum = sum + positionSolr

    print sum
    print items

    print "Average position of clicks", sum / items


'''
This function returns the average position of the correct entity for all the dataset, for multiple combinations of
weights for europeana_df and wikipedia_clicks
'''
def run_with_params_EDF_WKC():
    sum = 0.0
    items = 0
    output = open("results-BF-tune-wiki-europeana.tsv", "w")

    for i in xrange(0, 1050, 50):
        for j in xrange(0, 1050, 50):
            with open(log_file, 'r') as f:
                for line in f:
                    data = json.loads(line)
                    if data["clickPos"] >= 0:  # this means that a result has be selected, otherwise -1
                        query = data["response"]["query"]
                        entity = stripUrl(data["selected"]["uri"])
                        if i == 0:
                            europeana_weight = 1
                        else:
                            europeana_weight = i
                        if j == 0:
                            wiki_weight = 1
                        else:
                            wiki_weight = j
                        positionSolr = getPositionFromSuggestions(query, entity, europeana_weight, wiki_weight)

                        if positionSolr > 0:
                            items = items + 1
                            sum = sum + positionSolr

            print sum / items
            output.write("{0}\t{1}\t{2}\n".format(str(europeana_weight), str(wiki_weight), str(sum / items)))
    output.close()

'''
This function returns the average position of the correct entity for all the dataset, for a single combination of
weights for europeana_df and wikipedia_clicks and multiple weights for enrichment
'''
def run_with_params_ENRICH():
    sum = 0.0
    items = 0
    output = open("results-BF-wiki-europ-enrichments.tsv", "w")

    for j in xrange(0, 2050, 50):
        with open(log_file, 'r') as f:
            for line in f:
                data = json.loads(line)
                if data["clickPos"] >= 0:  # this means that a result has be selected, otherwise -1
                    query = data["response"]["query"]
                    entity = stripUrl(data["selected"]["uri"])

                    if j == 0:
                        enrichment_weight = 1
                    else:
                        enrichment_weight = j
                    positionSolr = getPositionFromSuggestions(query, entity, 1000, 1000, enrichment_weight)

                    if positionSolr > 0:
                        items = items + 1
                        sum = sum + positionSolr

        print sum / items
        output.write("{0}\t{1}\t{2}\t{3}\n".format(str(1000), str(1000), str(enrichment_weight),str(sum / items)))

    output.close()


'''
This function returns the average position of the correct entity for all the dataset, for a single combination of
weights for europeana_df, wikipedia_clicks and enrichment and changing weight for text_suggest, text_suggest_ngram
and text_suggest_edge in the boosting parameter.
'''
def run_with_params_TS_TSNG_TSE():

    sum = 0.0
    items = 0
    output = open("results-BF-tune-wiki-europeana.tsv", "w")

    for i in xrange(100, 5000, 100):
        for j in xrange(100, 5000, 100):
            for k in xrange(100, 5000, 100):
                with open(log_file, 'r') as f:
                    for line in f:
                        data = json.loads(line)
                        if data["clickPos"] >= 0:  # this means that a result has be selected, otherwise -1
                            query = data["response"]["query"]
                            entity = stripUrl(data["selected"]["uri"])
                            positionSolr = getPositionFromSuggestions(query, entity, 1000, 1000, 1300, i, j, k)
                            if positionSolr > 0:
                                items = items + 1
                                sum = sum + positionSolr

                print sum / items
                output.write("{0}\t{1}\t{2}\t{3}\n".format(str(i), str(j), str(k), str(sum / items)))
    output.close()


#run_one_time_all_set()
#run_with_params_EDF_WKC()
#run_with_params_ENRICH()
run_with_params_TS_TSNG_TSE()
