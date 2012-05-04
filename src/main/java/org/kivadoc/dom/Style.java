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
package org.kivadoc.dom;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public final class Style {
	private final Map<String, String> map = new TreeMap<String, String>();
	
	public void put(String key, String value) {
		this.map.put(key, value);
	}
	
	public Set<Entry<String, String>> entrySet() {
		return map.entrySet();
	}
	
	public String get(String key) {
		return map.get(key);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Style[");
		for (Map.Entry<String, String> kv : map.entrySet()) {
			b.append(kv.getKey());
			b.append(':');
			b.append(kv.getValue());
			b.append("; ");
		}
		b.append("]");
		return b.toString();
	}
}
