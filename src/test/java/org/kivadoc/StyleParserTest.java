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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kivadoc.StyleParser;

public class StyleParserTest {
	@Test
	public void testBasicStyle() {
		assertStyle("", "");
		assertStyle("color:red; ", "color:red");
		assertStyle("color:red; ", "color:red;");
		assertStyle("color:red; ", ";color:red;;");
		assertStyle("color:rgb(1,2,3); ", "COLOR:RGB(1,2,3)");
		assertStyle(
				"background-color:#faa; border:1.3em solid black; width:5%; ",
				"  width  :  5%  ; backgrounD-color:#faa;border:1.3em  Solid \t\r\n Black;  ");
	}
	
	@Test
	public void testInvalidCharacters() {
		assertStyle("a:3; ", "äa:3_");
	}

	private void assertStyle(String expected, String original) {
		assertEquals("Style[" + expected + "]", new StyleParser().parse(original).toString());
	}
}
