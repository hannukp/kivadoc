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

import org.kivadoc.dom.Style;

public final class StyleParser {
	public static void main(String[] args) {
		Style parse = new StyleParser().parse("width: 20px; height: 5%;");
		System.out.println(parse);
	}
	
	public Style parse(String input) {
		Style style = new Style();
		for (String kv : input.split(";")) {
			String[] split = kv.split(":", 2);
			if (split.length == 2) {
				style.put(simplify(split[0]), simplify(split[1]));
			}
		}
		return style;
	}

	private String simplify(String split) {
		return split.trim().toLowerCase().replaceAll("[^a-z0-9\\-'\"%,.() \t#]", "").replaceAll("\\s+", " ");
	}
}
