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


import java.io.PrintStream;
import java.util.List;

import org.kivadoc.KivaError;
import org.kivadoc.docparser.componentparsers.CodeBlockParser;
import org.kivadoc.docparser.componentparsers.CommentBlockParser;
import org.kivadoc.docparser.componentparsers.CommentLineParser;
import org.kivadoc.docparser.componentparsers.ComponentParser;
import org.kivadoc.docparser.componentparsers.ContinuationParser;
import org.kivadoc.docparser.componentparsers.EmptyParser;
import org.kivadoc.docparser.componentparsers.HeaderParser;
import org.kivadoc.docparser.componentparsers.IndentCodeBlockParser;
import org.kivadoc.docparser.componentparsers.ListParser;
import org.kivadoc.docparser.componentparsers.PlainParser;
import org.kivadoc.docparser.componentparsers.SimpleHeaderParser;
import org.kivadoc.docparser.componentparsers.StyleBlockParser;
import org.kivadoc.docparser.componentparsers.StyleChangeParser;
import org.kivadoc.docparser.componentparsers.TableParser;
import org.kivadoc.dom.BlockLevel;
import org.kivadoc.dom.Elements;


public final class DocumentParser {
	private ParserState state = new ParserState();
	
	public Elements<BlockLevel> parse(String string) {
		return parse(string.split("\r\n|\r|\n"));
	}
	
	public Elements<BlockLevel> parse(String[] lines) {
		state.reset(lines);
		
		ComponentParser[] parsers = new ComponentParser[] {
				new CommentBlockParser(),
				new CommentLineParser(),
				new IndentCodeBlockParser(),
				new EmptyParser(),
				new CodeBlockParser(),
				new TableParser(),
				new StyleChangeParser(),
				new SimpleHeaderParser(),
				new ListParser(),
				new ContinuationParser(),
				new StyleBlockParser(),
				new HeaderParser(),
				new PlainParser()
		};
		
		while (state.next()) {
			for (ComponentParser parser : parsers) {
				if (parser.match(state)) {
					state.logParse(parser.getClass().getName());
					parser.parse(state);
					break;
				}
			}
		}
		state.closeParagraph();
		return state.getBodyTarget();
	}
	
	public void setStartLineNumber(int startLine) {
		state.setStartLineNumber(startLine);
	}
	
	public void setDebugOut(PrintStream out) {
		state.setDebugOut(out);
	}

	public List<KivaError> getErrors() {
		return state.getErrors();
	}
}
