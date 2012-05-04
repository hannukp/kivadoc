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

import java.util.TreeMap;

import org.junit.Test;
import org.kivadoc.FullDocumentParser;
import org.kivadoc.KivaDocument;
import org.kivadoc.KivaError;
import org.kivadoc.KivaErrorType;
import org.kivadoc.emitters.DocHtmlEmitter;
import org.kivadoc.emitters.HtmlBuilder;

public class FullDocumentParserTest {
	@Test
	public void testMinimal() {
		assertDocument("", "{}", "", "");
		assertDocument("helo", "{}", "", "helo");
		assertDocument("helo", "{}", "<p>world </p>", "helo\nworld");
		assertDocument("helo", "{}", "<p>world </p>", "helo\rworld");
		assertDocument("helo", "{}", "<p>world </p>", "helo\r\nworld");
		assertDocument("helo", "{foo=bar}", "", "helo\n=\rfoo:bar");
		assertDocument("helo", "{foo=bar:baw}", "", "helo\n=\rfoo:bar:baw");
		assertDocument("helo", "{a=, b=c, foo=bar:baw}", "", "helo\n=\rfoo:bar:baw\r\na:\nb:c");
		assertDocument("helo", "{a=, b=c, foo=bar:baw}", "<p>body </p><p>para2 </p>", "helo\n=\rfoo:bar:baw\r\na:\nb:c\n\r\nbody\n\npara2");
	}
	
	@Test
	public void testErrors() {
		//"foo" and " x:3" are invalid headers here:
		assertDocument(
				"helo", "{y=4}", "<p>text </p>",
				new KivaError[] {new KivaError(3, new KivaErrorType.InvalidHeader()), new KivaError(4, new KivaErrorType.InvalidHeader())},
				"helo\n=\nfoo\n x:3\ny:4\n\ntext");
	}
	
	@Test
	public void testTitle() {
		FullDocumentParser parser = new FullDocumentParser();
		assertEquals("", parser.parseTitle(""));
		assertEquals("f", parser.parseTitle("f"));
		assertEquals("f", parser.parseTitle("f\r"));
		assertEquals("f", parser.parseTitle("f\rx"));
	}
	
	public void assertDocument(String expectedTitle, String expectedConfig, String expectedBody, String docText) {
		assertDocument(expectedTitle, expectedConfig, expectedBody, new KivaError[0], docText);
	}

	public void assertDocument(String expectedTitle, String expectedConfig, String expectedBody, KivaError[] errors, String docText) {
		FullDocumentParser parser = new FullDocumentParser();
		KivaDocument doc = parser.parse(docText);
		assertEquals(errors.length, doc.getErrors().size());
		for (int i = 0; i < errors.length; i++) {
			assertEquals(errors[i], doc.getErrors().get(i));
		}
		assertEquals(expectedTitle, doc.getTitle());
		assertEquals(expectedConfig, new TreeMap<String, String>(doc.getConfiguration()).toString());
		assertEquals(expectedBody, new DocHtmlEmitter().toHtmlString(new HtmlBuilder().compacted(), doc.getBody()));
	}
}
