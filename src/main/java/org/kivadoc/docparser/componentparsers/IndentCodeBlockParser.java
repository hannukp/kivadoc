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
package org.kivadoc.docparser.componentparsers;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kivadoc.docparser.ParserState;
import org.kivadoc.dom.Code;

public final class IndentCodeBlockParser implements ComponentParser {
	private static final Pattern INDENT_CODE = Pattern.compile("^[ \t]([ \t]*[^ \t].*)$");
	private static final Pattern INDENT_CODE_RELAXED = Pattern.compile("^[ \t](.*)$|^$");
	
	@Override
	public boolean match(ParserState s) {
		return !s.inParagraph() && INDENT_CODE.matcher(s.getLine()).matches();
	}

	@Override
	public void parse(ParserState s) {
		s.closeParagraph();
		Code indentCodeBlock = new Code();
		s.addStyledBlock(indentCodeBlock);
		
		s.prev();
		while (s.next()) {
			// Inside indent-code keep adding code until a line has invalid format.
			// INDENT_CODE_RELAXED allows for empty lines inside code block.
			Matcher codeMatcher = INDENT_CODE_RELAXED.matcher(s.getLine());
			if (codeMatcher.matches()) {
				String codeText = codeMatcher.group(1);
				indentCodeBlock.addLine(codeText == null ? "" : codeText);
			} else {
				break;
			}
		}
		s.prev();
	}
}