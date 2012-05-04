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

import org.kivadoc.dom.Element;
import org.kivadoc.dom.ElementVisitor;
import org.kivadoc.dom.Header;

public final class SectionExtentExtractor {
	private final int sectionNumber;
	
	private SectionExtentExtractor(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	
	public static class SectionExtents {
		public Integer sectionStartLine = null;
		public Integer sectionEndLine = null;
	}
	
	public SectionExtents extract(Element e) {
		Visitor visitor = new Visitor();
		visitor.visit(e);
		return visitor.extents;
	}
	
	public static SectionExtents extract(Element e, int sectionNumber) {
		return new SectionExtentExtractor(sectionNumber).extract(e);
	}
	
	private class Visitor extends ElementVisitor {
		private int currentSection = 0;
		private int sectionLevel = -1;
		private SectionExtents extents = new SectionExtents();
	
		@Override
		protected void header(Header e) {
			if (currentSection == sectionNumber && extents.sectionStartLine == null) {
				sectionLevel = e.getLevel();
				extents.sectionStartLine = e.getLineNumber();
			} else {
				if (e.getLevel() <= sectionLevel && extents.sectionEndLine == null) {
					extents.sectionEndLine = e.getLineNumber();
				}
			}
			
			currentSection++;
		}
	}
}
