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
import org.kivadoc.ParagraphParser;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.InlineLevel;
import org.kivadoc.dom.P;
import org.kivadoc.emitters.DocHtmlEmitter;

public class ParagraphParserTest {
	@Test
	public void testLinks() {
		assertParse("<a href=\"ftp://url\">LINK</a>", "ftp://url[LINK]");
		assertParse("<a href=\"http://foo\">http://foo</a>", "http://foo");
		assertParse("<a href=\"http://foo\">http://foo</a>", "http://foo[]");
		assertParse("<a href=\"http://foo\">http://foo</a>.", "http://foo.");
		assertParse("<a href=\"http://foo\">http://foo</a>.", "http://foo[].");
		assertParse("<a href=\"http://foo.\">http://foo.</a>", "http://foo.[]");
		assertParse("<a href=\"http://foo\">http://foo</a>?", "http://foo?");
		assertParse("<a href=\"http://foo\">http://foo</a>!", "http://foo!");
		assertParse("<a href=\"http://foo\">http://foo</a>,", "http://foo,");
		assertParse("<a href=\"http://foo\">http://foo</a>\u201C", "http://foo\"");
		assertParse("Link: <a href=\"http://foo/x(elokuva)\">http://foo/x(elokuva)</a>.", "Link: http://foo/x(elokuva).");
		assertParse("<a href=\"foo.txt\">foo.txt</a>", "link:foo.txt");
		assertParse("<a href=\"foo.txt\">FOO</a>", "link:foo.txt[FOO]");
		assertParse("<em><a href=\"foo.txt\">FOO</a></em>", "//link:foo.txt[FOO]//");
	}
	
	@Test
	public void testEmphasis() {
		assertParse("a<strong>foo</strong>b", "a**foo**b");
		assertParse("a<strong> foo </strong>b", "a** foo **b");
		assertParse("a <strong> foo </strong> b", "a ** foo ** b");
		assertParse("a<em>foo</em>b", "a//foo//b");
		assertParse("a<em><strong>foo</strong></em>b", "a//**foo**//b");
		// Invalid stylings disappear silently
		assertParse("a<em><strong>foo</strong></em>b", "a//**fo//o**//b");
		assertParse("Got <strong>milk</strong>?", "Got **milk**?");
	}
	
	@Test
	public void testCode() {
		// styles and symbols don't work inside a code block
		assertParse("a<code>//**foo**//(c) =&gt; mama</code>b", "a++//**foo**//(c) => mama++b");
		
		// ..but styles can be applied from the outside
		assertParse("a<em><code>foo</code></em>b", "a//++foo++//b");
	}
	
	@Test
	public void testImage() {
		assertParse("<img src=\"foo.png\" alt=\"foo.png\" />", "image:foo.png");
		assertParse("<img src=\"foo.png\" alt=\"FOO\" />", "image:foo.png[FOO]");
		assertParse("<img src=\"foo.png\" alt=\"foo.png\" />.", "image:foo.png[].");
		
		assertParse("<strong><a href=\"http://url\">nono</a></strong>", "**http://url[n**on**o]**");
		assertParse("<a href=\"http://url\"><img src=\"foo.png\" alt=\"FOO\" /> n<strong>on</strong>o</a>", "http://url[image:foo.png[FOO] n**on**o]");
	}
	
	@Test
	public void testEncoding() {
		assertParse("h&lt;ello <strong>wo&gt;rld</strong>", "h<ello **wo>rld**");
		assertParse("<img src=\"foo&amp;.png\" alt=\"&quot;\" />", "image:foo&.png[\"]");
	}
	
	@Test
	public void testBR() {
		assertParse("a  ", "a ~ ");
		assertParse("a <br />", "a ~");
		assertParse("a <br />b", "a ~\nb");
	}
	
	@Test
	public void testSpan() {
		assertParse("a<span style=\"color:red;\">hello world </span>b", "a(% color:red %)[hello world ]b");
		assertParse("a<span class=\"foo bar\" style=\"color:red;\">hello world </span>b", "a(% style:foo bar; color:red %)[hello world ]b");
	}
	
	@Test
	public void testSymbols() {
		assertParse("a\u27f7b\u27fac", "a<-->b<==>c");
		assertParse("a\u2194b\u21D4c", "a<->b<=>c");
		assertParse("a\u2192b\u2190c\u21D2d\u21D0e", "a->b<-c=>d<=e");
		assertParse("a\u27f6b\u27f5c\u27f9d\u27f8e", "a-->b<--c==>d<==e");
		assertParse("a\u00BBb\u00ABc", "a>>b<<c");
		assertParse("a\u2013b\u2014c", "a--b---c");
		assertParse("\u00A9\u00A9\u00AE\u00AE\u2122\u2122", "(c)(C)(r)(R)(tm)(TM)");
		assertParse("hello\u2026", "hello...");
		
		assertParse("“quote” “quo<code>&quot;te2</code>”", "\"quote\" \"quo++\"te2++\"");
		assertParse("&quot;", "~\"");
		assertParse("a“<strong>b”</strong>", "a\"**b\"**");
	}
	
	@Test
	public void testEscaping() {
		assertParse("-&gt;", "~->");
		assertParse("**foo**", "~**foo~**");
		assertParse("**foo**", "*~*foo*~*");
		assertParse("**foo**", "~*~*~f~o~o~*~*");
		assertParse("http://foo.bar", "~http:~//foo.bar");
		assertParse("http://foo.bar", "http~:~//foo.bar");
		assertParse("<a href=\"http://foo.bar\">a]b</a>", "http://foo.bar[a~]b]");
		assertParse("<img src=\"foo.png\" alt=\"a]b\" />", "image:foo.png[a~]b]");
		assertParse("~", "~~");
	}
	
	@Test
	public void testReferences() {
		assertParse("goto <a href=\"#target\"> » target</a> <a name=\"target\"></a>x", "goto {#target} ##target##x");
		assertParse("<a href=\"x.html\">x</a>", "{x}");
		assertParse("<a href=\"x.html#other\">x » other</a>", "{x#other}");
		assertParse("<a href=\"x.html\">H<strong>EL</strong>O</a>", "{x}[H**EL**O]");
		assertParse("<a href=\"~x.html\">H]ELO</a>]", "{~x}[H~]ELO]]");
		assertParse("<a href=\"foo/x.html\">x</a>", "{foo/x}");
		assertParse("<a href=\"foo/x.html\">x</a>", "{/foo/x}");
		assertParse("<span class=\"invalidlink\">../foo/x</span>", "{../foo/x}");
	}
	
	@Test
	public void testSimpleError() {
		ParagraphParser pp = new ParagraphParser();
		assertParse("a<strong>b</strong>", pp.parse("a**b"));
		assertEquals(1, pp.getErrors().size());
		assertEquals("KivaError[lineNumber=0, type=ParagraphTargetNotClosed[STRONG]]", pp.getErrors().get(0).toString());
	}
	
	@Test
	public void testNestedError() {
		ParagraphParser pp = new ParagraphParser();
		assertParse("a<strong><code>b</code></strong>", pp.parse("a**++b"));
		assertEquals(2, pp.getErrors().size());
		assertEquals("KivaError[lineNumber=0, type=ParagraphTargetNotClosed[CODE]]", pp.getErrors().get(0).toString());
		assertEquals("KivaError[lineNumber=0, type=ParagraphTargetNotClosed[STRONG]]", pp.getErrors().get(1).toString());
	}
	
	@Test
	public void testNestedError2() {
		ParagraphParser pp = new ParagraphParser();
		assertParse("a<strong><em>b</em></strong>", pp.parse("a**//b"));
		assertEquals(2, pp.getErrors().size());
		assertEquals("KivaError[lineNumber=0, type=ParagraphTargetNotClosed[EM]]", pp.getErrors().get(0).toString());
		assertEquals("KivaError[lineNumber=0, type=ParagraphTargetNotClosed[STRONG]]", pp.getErrors().get(1).toString());
	}

	private static void assertParse(String expected, String input) {
		ParagraphParser pp = new ParagraphParser();
		pp.setEncodeQuotations(true);
		assertParse(expected, pp.parse(input));
		assertEquals(0, pp.getErrors().size());
	}

	private static void assertParse(String expected, Elements<InlineLevel> parsed) {
		String html = new DocHtmlEmitter().toHtmlString(new P(parsed));
		String expectedHtml = "<p>" + expected + "</p>\n";
		if (!expectedHtml.equals(html)) {
			System.err.println(html.replace("\"", "\\\""));
		}
		assertEquals(expectedHtml, html);
	}
}
