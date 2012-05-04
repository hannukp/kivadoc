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
import org.kivadoc.TextExtractor;
import org.kivadoc.dom.P;
import org.kivadoc.dom.Text;

public class TextExtractorTest {
	@Test
	public void testTextExtractor() {
		assertEquals("hello x y", TextExtractor.extract(P.of(Text.of("  hello \n"), Text.of("x"), Text.of("y"))));
	}
}
