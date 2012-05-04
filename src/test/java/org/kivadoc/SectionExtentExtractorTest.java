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

import org.junit.Test;
import org.kivadoc.FullDocumentParser;
import org.kivadoc.SectionExtentExtractor;
import org.kivadoc.KivaDocument;
import org.kivadoc.SectionExtentExtractor.SectionExtents;

public class SectionExtentExtractorTest {
	@Test
	public void testExtract() throws IOException {
		FullDocumentParser parser = new FullDocumentParser();
		KivaDocument doc = parser.parse(DocTestingUtils.resourceToString("test_section_extents.txt"));

		assertExtent(5, null, SectionExtentExtractor.extract(doc.getBody(), 0));
		assertExtent(8, 14, SectionExtentExtractor.extract(doc.getBody(), 1));
		assertExtent(11, 14, SectionExtentExtractor.extract(doc.getBody(), 2));
		assertExtent(14, null, SectionExtentExtractor.extract(doc.getBody(), 3));
		assertExtent(17, null, SectionExtentExtractor.extract(doc.getBody(), 4));
		assertExtent(null, null, SectionExtentExtractor.extract(doc.getBody(), 5));
	}
	
	private void assertExtent(Integer expectedStart, Integer expectedEnd, SectionExtents extent) {
		assertEquals(expectedStart, extent.sectionStartLine);
		assertEquals(expectedEnd, extent.sectionEndLine);
	}
}
