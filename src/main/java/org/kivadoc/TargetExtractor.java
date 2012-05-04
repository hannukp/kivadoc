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


import java.util.HashSet;
import java.util.Set;

import org.kivadoc.dom.Element;
import org.kivadoc.dom.ElementVisitor;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.Header;
import org.kivadoc.dom.InlineLevel;
import org.kivadoc.dom.P;
import org.kivadoc.dom.TextTarget;
import org.kivadoc.emitters.HeaderLevels;

public class TargetExtractor {
	public static Set<String> extract(Element e) {
		Visitor v = new Visitor();
		v.visit(e);
		return v.targets;
	}
	
	private static class Visitor extends ElementVisitor {
		private final Set<String> targets = new HashSet<String>();
		protected HeaderLevels headerLevels = new HeaderLevels();
		
		@Override
		protected void textTarget(TextTarget e) {
			targets.add(e.getName());
		}

		@Override
		protected void header(Header e) {
			Elements<InlineLevel> c = new Elements<InlineLevel>();
			headerLevels.updateLevels(headerLevels.getLevel(e));
			c.add(new TextTarget(headerLevels.getHeaderName()));
			String headerText = TextExtractor.extract(new P(e.getContent()));
			c.add(new TextTarget(headerText));
			c.addAll(e.getContent());
			visitElements(c);
		}
	}
}
