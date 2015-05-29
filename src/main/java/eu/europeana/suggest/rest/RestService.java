/**
 *  Copyright 2013 Diego Ceccarelli
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
package eu.europeana.suggest.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eu.europeana.suggest.QueryCompletionClient;
import eu.europeana.suggest.Response;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @author Luca Incrocci <incrocci@cli.di.unipi.it>
 * 
 *         Created on Feb 2, 2013
 */

@Path("/")
public class RestService {

	private static final Logger logger = LoggerFactory
			.getLogger(RestService.class);

	private static Gson gson = new Gson();

	@Context
	HttpServletRequest request;
	private final QueryCompletionClient qcc = new QueryCompletionClient();

	@GET
	@Path("suggest.json")
	@Produces({ MediaType.APPLICATION_JSON })
	public String query(@QueryParam("query") String query,
			@QueryParam("rows") @DefaultValue("10") int n,
			@QueryParam("language") @DefaultValue("en") String language)
			throws SolrServerException {
		Response s = qcc.getCompletions(query, n, language);

		return gson.toJson(s);
	}

}
