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
import java.util.Map;

import org.kivadoc.dom.Element;
import org.kivadoc.dom.ElementVisitor;
import org.kivadoc.dom.TableCell;
import org.kivadoc.dom.TableRow;

public class GlossaryExtractor {
	public static Map<String, String> extract(Element e) {
		Visitor v = new Visitor();
		v.visit(e);
		return v.glossary;
	}
	
	private static class Visitor extends ElementVisitor {
		private Map<String, String> glossary = new HashMap<String, String>();
		private int state = 0;
		private String key;
		
		@Override
		protected void tableRow(TableRow e) {
			state = 0;
			super.tableRow(e);
		}
		
		@Override
		protected void tableCell(TableCell e) {
			state++;
			if (state == 1) {
				key = TextExtractor.extract(e);
			} else if (state == 2) {
				glossary.put(key, TextExtractor.extract(e));
			}
			super.tableCell(e);
		}
	}
}
