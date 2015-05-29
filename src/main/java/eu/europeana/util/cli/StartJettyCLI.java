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
package eu.europeana.util.cli;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europeana.util.JettyServer;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 12, 2013
 */
public class StartJettyCLI extends AbstractCommandLineInterface {

	private static final Logger logger = LoggerFactory
			.getLogger(StartJettyCLI.class);

	private static String[] params = new String[] {};

	public StartJettyCLI(String[] args) {
		super(args, params, "");
	}

	public static void main(String[] args) {
		StartJettyCLI cli = new StartJettyCLI(args);
		System.setProperty("solr.data.dir", "src/test/resources/solr");
		JettyServer server = new JettyServer();
		server.start();

	}

}
