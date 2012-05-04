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
import org.kivadoc.dom.Text;

public final class TextExtractor {
	public static String extract(Element e) {
		Visitor v = new Visitor();
		v.visit(e);
		return v.getText();
	}
	
	private static class Visitor extends ElementVisitor {
		private StringBuilder sb = new StringBuilder();
		
		@Override
		protected void text(Text e) {
			sb.append(e.getText());
			sb.append(' ');
		}

		public String getText() {
			return sb.toString().replaceAll("\\s+", " ").trim();
		}
	}
}
