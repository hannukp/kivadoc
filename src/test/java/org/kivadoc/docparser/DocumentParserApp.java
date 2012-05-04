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
package org.kivadoc.docparser;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.kivadoc.docparser.DocumentParser;
import org.kivadoc.dom.BlockLevel;
import org.kivadoc.dom.Body;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.Header;
import org.kivadoc.dom.InlineLevel;
import org.kivadoc.dom.LI;
import org.kivadoc.dom.P;
import org.kivadoc.dom.Text;
import org.kivadoc.dom.UL;
import org.kivadoc.emitters.DocHtmlEmitter;
import org.kivadoc.emitters.TocHtmlEmitter;

/**
 * For running manual tests.
 */
@Ignore
public class DocumentParserApp {
	public static void main(String[] args) throws Exception {
		//testDoc();
		//benchmark();
		
		//String resourceName = "test_table.txt";
		//String resourceName = "test_comments.txt";
		//String resourceName = "test_lists2.txt";
		//String resourceName = "test_code.txt";
		//String resourceName = "test_headers.txt";
		//String resourceName = "test_errors.txt";
		//String resourceName = "test_errors2.txt";
		//String resourceName = "test_paragraphs.txt";
		//String resourceName = "test_styleblocks.txt";
		//String resourceName = "doc.txt";
		//String resourceName = "testnew.txt";
		String resourceName = "syntax.txt";
		//String resourceName = "invalid_headers.txt";
		//String resourceName = "test2.txt";
		//String docText = IOUtils.toString(DocumentParser.class.getResourceAsStream("/org/kivadoc/" + resourceName));
		String docText = FileUtils.readFileToString(new File("README"), "UTF-8");
		DocumentParser d = new DocumentParser();
		d.setDebugOut(System.err);
		Body doc = new Body(d.parse(docText));
		System.out.println(doc);
		System.out.println(d.getErrors());
		
		TocHtmlEmitter toc = new TocHtmlEmitter();
		toc.setShowNumbers(true);
		//toc.setCreateReferences(true);
		//System.out.println(toc.toHtmlString(doc));
		
		//String template = FileUtils.readFileToString(new File("D:/download/template.html"));
		String template = IOUtils.toString(DocumentParser.class.getResourceAsStream("/org/kivadoc/template.html"));
		String result = template.replace("**BODY**", new DocHtmlEmitter().toHtmlString(doc));
		result = result.replace("**EXTRA_STYLES**", "");
		FileUtils.writeStringToFile(new File("C:/Users/Hannu/Downloads/test.html"), result);
	}

	static void benchmark() throws Exception {
		final String docText1 = IOUtils.toString(DocumentParser.class.getResourceAsStream("/org/kivadoc/syntax.txt"));
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < 1; i++) {
			b.append(docText1);
		}
		final String docText = b.toString();
		for (int e = 0; e < 200; e++) {
			DocumentParser d = new DocumentParser();
			new DocHtmlEmitter().toHtmlString(new Body(d.parse(docText)));
		}
		// 520 documents/second
		// => 400-445 documents/second (v 0.8.1)
		
		final int iterations = 500;
		long start = System.currentTimeMillis();
		for (int e = 0; e < iterations; e++) {
			DocumentParser d = new DocumentParser();
			new DocHtmlEmitter().toHtmlString(new Body(d.parse(docText)));
		}
		long elapsed = System.currentTimeMillis() - start;
		System.out.println("Doc size: " + docText.length());
		System.out.println("Docs/sec: " + (1.0 / (elapsed * 0.001 / iterations)));
		System.exit(0);
	}

	static void testDoc() {
		//Body body = new Body();
		Elements<BlockLevel> top = new Elements<BlockLevel>();
		top.add(new Header(1, new Elements<InlineLevel>(Text.of("header"))));
		top.add(P.of(Text.of("hello")));
		top.add(P.of(Text.of("world")));
		LI li1 = LI.of(P.of(Text.of("item1")));
		LI li2 = LI.of(P.of(Text.of("item2")), P.of(Text.of("continue item2")));
		top.add(new UL(li1, li2));
		System.out.println(new Body(top));
		System.exit(0);
	}
}
