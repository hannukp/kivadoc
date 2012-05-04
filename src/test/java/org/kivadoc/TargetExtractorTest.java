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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kivadoc.TargetExtractor;
import org.kivadoc.docparser.DocumentParser;
import org.kivadoc.dom.Body;

public class TargetExtractorTest {
	@Test
	public void testExtractEmpty() {
		assertEquals("[]", extractFrom("hello world").toString());
	}
	
	@Test
	public void testExtractSomeTargets() {
		assertEquals("[another, helo]", extractFrom("hello world ##helo##\n\n##another## target").toString());
	}
	
	@Test
	public void testExtractSomeHeaders() {
		assertEquals("[KDOC_HEADER_1, KDOC_HEADER_1_1, KDOC_HEADER_2, lev1, lev2, lev3]", extractFrom("hello \n== lev1\n\n=== lev2\n\n== lev3").toString());
	}

	private List<String> extractFrom(String doc) {
		Set<String> r = TargetExtractor.extract(new Body(new DocumentParser().parse(doc)));
		List<String> list = new ArrayList<String>(r);
		Collections.sort(list);
		return list;
	}
}
