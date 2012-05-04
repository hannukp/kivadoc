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

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.kivadoc.FullDocumentParser;
import org.kivadoc.GlossaryExtractor;
import org.kivadoc.KivaDocument;

public class GlossaryExtractorTest {
	@Test
	public void testExtractor() throws IOException {
		FullDocumentParser parser = new FullDocumentParser();
		KivaDocument doc = parser.parse(DocTestingUtils.resourceToString("test_glossary.txt"));
		Map<String, String> glossary = GlossaryExtractor.extract(doc.getBody());
		
		assertEquals(4, glossary.size());
		
		assertEquals("typical", glossary.get("foo"));
		assertEquals("atypical", glossary.get("bar"));
		assertEquals("followed by world", glossary.get("hello"));
		assertEquals("y", glossary.get("x"));
	}
}
