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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 12, 2013
 */
public class StartRestServiceCLI extends AbstractCommandLineInterface {

	private static final Logger logger = LoggerFactory
			.getLogger(StartRestServiceCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ StartRestServiceCLI.class;
	private static String[] params = new String[] {};

	public StartRestServiceCLI(String[] args) {
		super(args, params, USAGE);

	}

	public static void main(String[] args) {

		StartRestServiceCLI cli = new StartRestServiceCLI(args);
		int port = Integer.parseInt(System.getProperty("rest.port", "8888"));
		Server server = new Server(port);
		// Set some timeout options to make debugging easier.

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");

		server.setHandler(bb);

		try {
			System.out
					.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			while (System.in.available() == 0) {
				Thread.sleep(5000);
			}
			server.stop();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}

	}

}
