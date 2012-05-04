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
package org.kivadoc.emitters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.kivadoc.DocTestingUtils;
import org.kivadoc.FullDocumentParser;
import org.kivadoc.KivaDocument;
import org.kivadoc.emitters.FullHtmlEmitter;

public class FullHtmlEmitterTest {
	@Test
	public void testToExtraStyleHtmlString() {
		FullHtmlEmitter emitter = new FullHtmlEmitter();
		KivaDocument doc = new KivaDocument();
		doc.putConfiguration("style", "");
		assertTrue(emitter.toExtraStyleHtmlString(doc).isEmpty());
		
		// invalid path
		doc.putConfiguration("style", "/../foo.css");
		assertTrue(emitter.toExtraStyleHtmlString(doc).isEmpty());
		
		doc.putConfiguration("style", "mystyle.css, /otherstyle.css");
		assertEquals("<link rel=\"stylesheet\" href=\"mystyle.css\" type=\"text/css\" /><link rel=\"stylesheet\" href=\"otherstyle.css\" type=\"text/css\" />", emitter.toExtraStyleHtmlString(doc));
	}
	
	@Test
	public void testToHtmlString() throws IOException {
		String docText = DocTestingUtils.resourceToString("test_fulldoc.txt");
		KivaDocument doc = new FullDocumentParser().parse(docText);
		
		FullHtmlEmitter emitter = new FullHtmlEmitter();
		String actualHtml = emitter.toHtmlString(doc);
		String expectedHtml =  DocTestingUtils.resourceToString("test_fulldoc.html");
		DocTestingUtils.assertEqualsHtml("fulldoc", expectedHtml, actualHtml);
	}
}
