/**
 * Copyright 2012 Hannu Kankaanpää
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * @author Hannu Kankaanpää <hannu.kp@gmail.com>
 */
package org.kivadoc;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kivadoc.dom.Body;

public final class KivaDocument {
	private final Map<String, String> configuration = new HashMap<String, String>();
	private String title;
	private Body body;
	private List<KivaError> errors;
	
	public Map<String, String> getConfiguration() {
		return this.configuration;
	}
	public String getConfiguration(String key) {
		return configuration.get(key);
	}
	public void putConfiguration(String key, String value) {
		configuration.put(key, value);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Body getBody() {
		return body;
	}
	public void setBody(Body body) {
		this.body = body;
	}
	public List<KivaError> getErrors() {
		return errors;
	}
	public void setErrors(List<KivaError> errors) {
		this.errors = errors;
	}
	
	@Override
	public String toString() {
		return "KivaDocument[title=" + title + ", configuration=" + configuration + ", body=" + body + "]";
	}
}
