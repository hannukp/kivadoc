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


import java.util.ArrayList;
import java.util.Iterator;

public final class Elements<T extends Element> implements Iterable<T> {
	private final ArrayList<T> elements = new ArrayList<T>();
	
	public Elements(T...elements) {
		for (T e : elements) {
			add(e);
		}
	}
	
	public void add(T e) {
		elements.add(e);
	}
	
	public void addAll(Elements<T> es) {
		elements.addAll(es.elements);
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}
	
	public ArrayList<T> getElements() {
		return elements;
	}
}
