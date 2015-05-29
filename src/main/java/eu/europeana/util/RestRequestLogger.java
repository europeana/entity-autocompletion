/**
 *  Copyright 2015 Diego Ceccarelli
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

import it.cnr.isti.hpc.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 11, 2015
 */
public class RestRequestLogger {

	private static final Logger log = LoggerFactory
			.getLogger(RestRequestLogger.class);
	private static RestRequestLogger instance = new RestRequestLogger();

	private final File dir;
	private static Date currentdaytime = null;
	private static BufferedWriter bw;
	private static Gson gson = new Gson();

	private RestRequestLogger() {
		String name = "rest-logs";
		if (System.getProperty("rest.log.dir") != null) {
			name = System.getProperty("rest.log.dir");
		}
		dir = new File(name);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	private static Date getCurrentDayTime() {
		long time = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private static String getCurrentDayString() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");// dd/MM/yyyy
		String strDate = sdfDate.format(getCurrentDayTime());
		return strDate;
	}

	private synchronized void updateFile() {

		Date cd = getCurrentDayTime();
		if (currentdaytime == null || !currentdaytime.equals(cd)) {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.info("error closing file");
					e.printStackTrace();
				}
			}
			String filename = getCurrentDayString() + "-logs.json";
			File f = new File(dir, filename);
			int n = 1;
			while (f.exists()) {
				filename = getCurrentDayString() + "-" + n + "-logs.json";
				f = new File(dir, filename);
				n++;
			}
			bw = IOUtils.getPlainOrCompressedUTF8Writer(f.getAbsolutePath());
			currentdaytime = cd;
		}

	}

	public static RestRequestLogger getInstance() {
		return instance;
	}

	public synchronized void log(Object o) throws IOException {
		updateFile();
		bw.write(gson.toJson(o));
		bw.newLine();
		bw.flush();

	}
}
