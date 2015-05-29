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
package eu.europeana.suggest;

import java.util.Date;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 11, 2015
 */
public class LogRequest {
	public enum Type {
		QUERY, CLICK
	};

	private Type type;
	private Response response;
	private Suggestion selected;
	private long time;
	private Date timeStr;
	private String ip;
	private String userAgent;
	private Integer clickPos;

	public LogRequest() {

	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Suggestion getSelected() {
		return selected;
	}

	public void setSelected(Suggestion selected) {
		this.selected = selected;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public Date getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(Date timeStr) {
		this.timeStr = timeStr;
	}

	public void setPos(Integer pos) {
		this.clickPos = pos;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public int getClickPos() {
		return clickPos;
	}

	public void setClickPos(int pos) {
		this.clickPos = pos;
	}

}
