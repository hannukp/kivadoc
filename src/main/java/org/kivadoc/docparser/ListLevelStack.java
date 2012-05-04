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
package org.kivadoc.docparser;

import java.util.ArrayList;
import java.util.List;

public final class ListLevelStack {
	private final List<ListLevel> levels = new ArrayList<ListLevel>();
	
	public ListLevel top() {
		return levels.get(levels.size() - 1);
	}

	public void clear() {
		levels.clear();
	}
	
	public void pop() {
		levels.remove(levels.size() - 1);
	}

	public void add(ListLevel newLevel) {
		levels.add(newLevel);
	}

	public ListLevel find(ListLevel level) {
		int index = levels.indexOf(level);
		return index >= 0 ? levels.get(index) : null;
	}

	public boolean isEmpty() {
		return levels.isEmpty();
	}
}
