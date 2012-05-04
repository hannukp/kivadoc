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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kivadoc.DocTestingUtils;
import org.kivadoc.KivaError;
import org.kivadoc.KivaErrorType;
import org.kivadoc.docparser.DocumentParser;
import org.kivadoc.dom.Body;
import org.kivadoc.emitters.DocHtmlEmitter;

public class DocumentParserTest {
	@Test
	public void testSyntax() throws IOException {
		assertParse("syntax");
	}
	
	@Test
	public void testCode() throws IOException {
		assertParse("test_code");
	}
	
	@Test
	public void testComments() throws IOException {
		assertParse("test_comments");
	}
	
	@Test
	public void testHeaders() throws IOException {
		assertParse("test_headers");
	}
	
	@Test
	public void testLists() throws IOException {
		assertParse("test_lists");
	}
	
	@Test
	public void testLists2() throws IOException {
		assertParse("test_lists2");
	}
	
	@Test
	public void testParagraphs() throws IOException {
		assertParse("test_paragraphs");
	}
	
	@Test
	public void testStyleBlocks() throws IOException {
		assertParse("test_styleblocks");
	}
	
	@Test
	public void testStyling() throws IOException {
		assertParse("test_styling");
	}
	
	@Test
	public void testTable() throws IOException {
		assertParse("test_table");
	}
	
	@Test
	public void testErrors() throws IOException {
		assertParse("test_errors",
				new KivaError(4, new KivaErrorType.TableFirstCellHasData("stuff\n")),
				new KivaError(9, new KivaErrorType.TableHasInvalidSpan()),
				new KivaError(11, new KivaErrorType.TableHasInvalidSpan()),
				new KivaError(14, new KivaErrorType.TableIsUnderfull(1)),
				new KivaError(17, new KivaErrorType.TableIsEmpty()),
				new KivaError(21, new KivaErrorType.TableHasInvalidSpan()),
				new KivaError(22, new KivaErrorType.ParagraphTargetNotClosed("STRONG")));
	}
	
	@Test
	public void testErrors2() throws IOException {
		assertParse("test_errors2",
				new KivaError(1, new KivaErrorType.ParagraphTargetNotClosed("CODE")),
				new KivaError(3, new KivaErrorType.ParagraphTargetNotClosed("STRONG")),
				new KivaError(5, new KivaErrorType.ParagraphTargetNotClosed("EM")),
				new KivaError(7, new KivaErrorType.ParagraphTargetNotClosed("CODE")),
				new KivaError(9, new KivaErrorType.ParagraphTargetNotClosed("STRONG")),
				new KivaError(10, new KivaErrorType.ParagraphTargetNotClosed("EM")),
				new KivaError(12, new KivaErrorType.InvalidTarget("../error7")),
				new KivaError(14, new KivaErrorType.InvalidTarget("error8")));
	}
	
	private void assertParse(String resourceName, KivaError ... errors) throws IOException {
		String docText = DocTestingUtils.resourceToString(resourceName + ".txt");
		String expectedHtml = DocTestingUtils.resourceToString(resourceName + ".html");
		DocumentParser parser = new DocumentParser();
		DocHtmlEmitter emitter = new DocHtmlEmitter();
		String actualHtml = emitter.toHtmlString(new Body(parser.parse(docText)));
		DocTestingUtils.assertEqualsHtml(resourceName, expectedHtml, actualHtml);
		
		List<KivaError> allErrors = new ArrayList<KivaError>();
		allErrors.addAll(parser.getErrors());
		allErrors.addAll(emitter.getErrors());
		
		DocTestingUtils.assertErrors(errors, allErrors);
	}
}
