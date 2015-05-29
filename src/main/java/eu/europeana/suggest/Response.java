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

import java.util.ArrayList;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Dec 6, 2014
 */
@ApiModel(value = "Response", description = "suggestions for a given query")
public class Response {
	@ApiModelProperty(value = "The query", required = true)
	private String query;
	@ApiModelProperty(value = "Language", required = true)
	private String language = "en";
	@ApiModelProperty(value = "Rows value", required = true)
	private long totalResults;
	@ApiModelProperty(value = "Total results for the query", required = true)
	private int itemCount;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@ApiModelProperty(value = "Possible suggestions for the query", required = true)
	List<Suggestion> suggestions = new ArrayList<Suggestion>();

	public Response(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<Suggestion> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<Suggestion> annotations) {
		this.suggestions = annotations;
	}

	/**
	 * @return the totalResults
	 */
	public long getTotalResults() {
		return totalResults;
	}

	/**
	 * @param totalResults
	 *            the totalResults to set
	 */
	public void setTotalResults(long totalResults) {
		this.totalResults = totalResults;
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount() {
		return itemCount;
	}

	/**
	 * @param itemCount
	 *            the itemCount to set
	 */
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

}
