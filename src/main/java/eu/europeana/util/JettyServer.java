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
package eu.europeana.util;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 14, 2013
 */
public class JettyServer {

	private final File solrdir;
	// private final File datadir;
	private final int port;

	private static final Logger logger = LoggerFactory
			.getLogger(JettyServer.class);

	public JettyServer() {

		// String data = System.getProperty("solr.data.dir");
		String solr = System.getProperty("solr.solr.dir", "./solr");

		port = Integer.parseInt(System.getProperty("solr.port", "8080"));
		solrdir = new File(solr);
		// datadir = new File(data);
	}

	public void start() {
		logger.info("starting solr server");
		String solrwar = System
				.getProperty("solr.war", "./solr/solr-4.3.1.war");
		System.setProperty("solr.home", solrdir.getPath());
		// System.setProperty("solr.data.dir", datadir.getPath());
		System.setProperty("jetty.home", "./solr");

		try {
			Server server = new Server(port);
			WebAppContext webapp = new WebAppContext();

			webapp.setContextPath("/");
			webapp.setWar(solrwar);
			server.setHandler(webapp);
			server.start();

		} catch (Exception e) {
			logger.error("starting jetty ({})", e.toString());
			System.exit(-1);
		}
	}

}
