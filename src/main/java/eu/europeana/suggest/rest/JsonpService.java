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

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.suggest.LogRequest;
import eu.europeana.suggest.QueryCompletionClient;
import eu.europeana.suggest.Response;
import eu.europeana.suggest.Suggestion;
import eu.europeana.util.RestRequestLogger;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
@Api(value = "jsonp", description = "Entity autocompletion API")
@Path("jsonp")
public class JsonpService {

	private static RestRequestLogger restLog = RestRequestLogger.getInstance();

	private static final Logger logger = LoggerFactory
			.getLogger(JsonpService.class);

	private static Gson gson = new Gson();

	@Context
	HttpServletRequest request;
	private final QueryCompletionClient qcc = new QueryCompletionClient();

	@GET
	@Path("/suggest.json")
	@ApiOperation(value = "Returns completions for a given query", response = Response.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String query(@Context HttpServletRequest req,
			@Context HttpHeaders headers,
			@QueryParam("callback") @DefaultValue("") String callback,
			@QueryParam("query") String query,
			@QueryParam("rows") @DefaultValue("10") int n,
			@QueryParam("language") @DefaultValue("en") String language)
			throws SolrServerException {
		System.out.println("q " + query);

		Response s = qcc.getCompletions(query, n, language);

		LogRequest lr = new LogRequest();
		lr.setType(LogRequest.Type.QUERY);
		lr.setResponse(s);
		lr.setUserAgent(headers.getRequestHeader("user-agent").get(0));
		lr.setIp(req.getRemoteHost());
		lr.setTime(System.currentTimeMillis());
		lr.setTimeStr(new Date(System.currentTimeMillis()));
		try {
			restLog.log(lr);
		} catch (IOException e) {
			logger.error("logging record \n{}", gson.toJson(lr));
			e.printStackTrace();
		}
		// if the callback is empty it returns a normal json object
		if (callback.isEmpty())
			return gson.toJson(s);
		// otherwise it returns a jsonp callback
		return callback + "(" + gson.toJson(s) + ");";
	}

	// semantic query
	@GET
	@Path("sq.json")
	@Produces({ MediaType.APPLICATION_JSON })
	public String query(@Context HttpServletRequest req,
			@Context HttpHeaders headers,
			@QueryParam("callback") @DefaultValue("") String callback,
			@QueryParam("query") String query,
			@QueryParam("language") @DefaultValue("en") String language,
			@QueryParam("clicked-uri") String uri) throws SolrServerException {
		Response s = qcc.getCompletions(query, 10, language, true);
		LogRequest lr = new LogRequest();
		lr.setType(LogRequest.Type.CLICK);
		lr.setResponse(s);
		lr.setUserAgent(headers.getRequestHeader("user-agent").get(0));
		lr.setIp(req.getRemoteHost());
		lr.setPos(-1);
		lr.setTime(System.currentTimeMillis());
		lr.setTimeStr(new Date(System.currentTimeMillis()));
		int pos = 0;
		for (Suggestion ss : s.getSuggestions()) {
			if (ss.getUri().equals(uri)) {
				lr.setPos(pos);
				lr.setSelected(ss);
				break;
			}

			pos++;
		}
		try {
			restLog.log(lr);
		} catch (IOException e) {
			logger.error("logging record \n{}", gson.toJson(lr));
			e.printStackTrace();
		}

		if (callback.isEmpty())
			return gson.toJson(lr);
		return callback + "(" + gson.toJson(lr) + ");";
	}
}
