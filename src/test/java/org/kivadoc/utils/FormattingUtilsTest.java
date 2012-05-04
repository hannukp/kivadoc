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
package org.kivadoc.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kivadoc.utils.FormattingUtils;

public class FormattingUtilsTest {
	
	@Test
	public void testTrimLines() {
		assertEquals("[foo, bar, baz]", Arrays.asList(FormattingUtils.trimLines("foo,bar,baz".split(","))).toString());
		assertEquals("[bar, baz]", Arrays.asList(FormattingUtils.trimLines(",bar,baz".split(","))).toString());
		assertEquals("[bar]", Arrays.asList(FormattingUtils.trimLines(",bar,".split(","))).toString());
		assertEquals("[]", Arrays.asList(FormattingUtils.trimLines(",,".split(","))).toString());
		assertEquals("[]", Arrays.asList(FormattingUtils.trimLines("".split(","))).toString());
	}
	
	
	@Test
	public void testTrimRight() {
		assertEquals(" foo", FormattingUtils.trimRight(" foo "));
		assertEquals("", FormattingUtils.trimRight("  "));
		assertEquals("", FormattingUtils.trimRight(""));
	}

	@Test
	public void testTrimIndentation() {
		assertEquals("foo,  bar,baz", trimIndentationResult(" foo,   bar, baz"));
		assertEquals("foo,  bar,baz", trimIndentationResult(" foo,   bar,baz"));
		assertEquals("foo,bar,baz", trimIndentationResult("   foo, bar,  baz"));
		assertEquals("", trimIndentationResult(""));
	}
	
	public String trimIndentationResult(String input) {
		String[] split = input.split(",");
		FormattingUtils.trimIndentation(split);
		return StringUtils.join(split, ",");
	}
}
