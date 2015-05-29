/**
 *  Copyright 2014 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.europeana.suggest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Dec 6, 2014
 */
public class QueryCompletionClient {

	public String serverUrl = "http://localhost:8080";
	private final String[] languages = new String[] { "it", "de", "fr" };

	private static final Logger logger = LoggerFactory
			.getLogger(QueryCompletionClient.class);

	private final HttpSolrServer server;

	public QueryCompletionClient() {
		String uri = System.getenv("server");
		if (uri != null) {
			serverUrl = uri;
		}
		serverUrl += "/europeana";
		server = new HttpSolrServer(serverUrl);

	}

	public Response getCompletions(String query, int n, String language)
			throws SolrServerException {
		return getCompletions(query, n, language, false);
	}

	public Response getCompletions(String query, int n, String language,
			boolean addExplain) throws SolrServerException {

		if (language == null || language.isEmpty()) {
			logger.info("no language for query {}, using default English. ",
					language, query);
			return getCompletions(query, n);
		}

		language = language.toLowerCase();

		if (!language.contains(language)) {
			logger.info(
					"unknown language {} for query {}, using default English. ",
					language, query);
			language = "en";
		}

		SolrQuery sq = new SolrQuery(query);
		if (language.equals("en")) {
			sq.setRequestHandler("/suggest");
		} else {
			sq.setRequestHandler("/" + language + "_suggest");
		}
		// set the results number
		sq.setRows(n);
		if (addExplain) {
			// if requested add the explain (how the score is computed) to the
			// results, used in the logging
			sq.setFields("*", "[explain]");
		}
		// send the query to the server, each document returned is an entity
		QueryResponse qr = server.query(sq);
		SolrDocumentList sdl = qr.getResults();

		Response result = new Response(query);

		result.setItemCount(n);
		result.setTotalResults(sdl.getNumFound());
		result.setLanguage(language);

		List<Suggestion> annotations = new ArrayList<Suggestion>();
		for (SolrDocument sd : sdl) {
			Suggestion annotation = new Suggestion();
			String enTitle = (decode((String) sd.get("en_title")));
			// short titles usually are not really relevant and have high
			// ranking since they are ambigous.. remove them :)
			if (enTitle.length() <= 5)
				continue;
			int wikipediaClicks = ((Integer) sd.get("wikipedia_clicks"));
			int europeanaDf = ((Integer) sd.get("europeana_df"));
			int enrichment = ((Integer) sd.get("enrichment"));
			String type = (decode((String) sd.get("type"))).toLowerCase();
			annotation.setType(type);
			annotation.addPrefLabel("en", enTitle);
			annotation.setEuropeana_df(europeanaDf);
			annotation.setWikipedia_clicks(wikipediaClicks);
			annotation.setEnrichment(enrichment);
			if (addExplain) {
				annotation.setExplain((String) sd.get("[explain]"));
			}

			for (String lang : languages) {
				String label = decode((String) sd.get(lang + "_title"));

				if (label == null || label.isEmpty())
					continue;
				// if (label == null || label.isEmpty()) {
				// label = enTitle;
				//
				// }
				annotation.addPrefLabel(lang, label);
			}
			annotation.setUri((String) sd.get("uri"));
			annotation.setImage(((String) sd.get("uri")).replace(
					"http://dbpedia.org/resource/",
					"http://wikiname2image.herokuapp.com/"));
			annotation.setUri(annotation.getUri().replace(
					"http://dbpedia.org/resource/",
					"http://data.europeana.eu/" + type + "/"));
			annotation.setSearch("entity:" + annotation.getUri());
			annotations.add(annotation);
		}
		result.setSuggestions(annotations);

		return result;

	}

	public Response getCompletions(String query, int n)
			throws SolrServerException {

		return getCompletions(query, n, "en");

	}

	private String decode(String string) {
		try {
			return java.net.URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}

	public static void main(String[] args) throws SolrServerException {
		QueryCompletionClient qcc = new QueryCompletionClient();
		Response sugg = qcc.getCompletions("leonard da vinci", 10, "fr");
		for (Suggestion ann : sugg.getSuggestions()) {
			System.out.println(ann.getPrefLabel());
		}
	}
}
