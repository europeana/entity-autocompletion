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

import java.util.HashMap;
import java.util.Map;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Dec 6, 2014
 */
@ApiModel(value = "Single Suggestion", description = "a single suggestion for a given query")
public class Suggestion {

	@ApiModelProperty(value = "The name of the entity in the required language", required = true)
	private Map<String, String> prefLabel;
	@ApiModelProperty(value = "Entity URI", required = true)
	private String uri;
	@ApiModelProperty(value = "Url to an image", required = false)
	private String image;
	@ApiModelProperty(value = "Type of the entity", required = false)
	private String type = "agent";
	@ApiModelProperty(value = "Formatted query for the search api", required = false)
	private String search;

	private String explain;

	@ApiModelProperty(value = "Matching documents in Europeana", required = false)
	private Integer europeana_df;

	@ApiModelProperty(value = "Clicks on Wikipedia", required = false)
	private Integer wikipedia_clicks;
	
	@ApiModelProperty(value = "Agent enrichment", required = false)
	private Integer enrichment;

	public Suggestion() {
		prefLabel = new HashMap<String, String>();
	}

	public Map<String, String> getPrefLabel() {
		return prefLabel;
	}

	public void setPrefLabel(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}

	public void addPrefLabel(String lang, String label) {
		prefLabel.put(lang, label);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Integer getEuropeana_df() {
		return europeana_df;
	}

	public void setEuropeana_df(Integer europeana_df) {
		this.europeana_df = europeana_df;
	}

	public Integer getWikipedia_clicks() {
		return wikipedia_clicks;
	}

	public void setWikipedia_clicks(Integer wikipedia_clicks) {
		this.wikipedia_clicks = wikipedia_clicks;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public Integer getEnrichment() {
		return enrichment;
	}

	public void setEnrichment(Integer enrichment) {
		this.enrichment = enrichment;
	}

	
}
