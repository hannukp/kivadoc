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
package org.kivadoc.emitters;


import java.util.ArrayList;
import java.util.List;

import org.kivadoc.dom.Header;

public final class HeaderLevels {
	private int maxLevel = -1;
	private final int[] levels = new int[] { 0, 0, 0, 0, 0, 0, 0 };
	
	public void updateLevels(int level) {
		maxLevel = level;
		levels[level]++;
		for (int i = level + 1; i < levels.length; i++) {
			levels[i] = 0;
		}
	}
	
	public int getLevel(Header e) {
		return Math.max(0, e.getLevel() - 2);
	}
	
	public List<Integer> getCurrentLevels() {
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < levels.length && i <= maxLevel; i++) {
			res.add(levels[i]);
		}
		return res;
	}
	
	public String getHeaderNumber() {
		StringBuilder b = new StringBuilder();
		for (int i : getCurrentLevels()) {
			b.append(i);
			b.append('.');
		}
		return b.toString();
	}
	
	public String getHeaderName() {
		StringBuilder b = new StringBuilder();
		b.append("KDOC_HEADER");
		for (int i : getCurrentLevels()) {
			b.append("_" + i);
		}
		return b.toString();
	}
}
