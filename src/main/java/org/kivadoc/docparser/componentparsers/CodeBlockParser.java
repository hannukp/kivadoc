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


import java.util.regex.Pattern;

import org.kivadoc.docparser.ParserState;
import org.kivadoc.dom.Code;

public final class CodeBlockParser implements ComponentParser {
	private static final Pattern CODEBLOCK_DELIM = Pattern.compile("^[+]{3,}$");
	
	@Override
	public boolean match(ParserState s) {
		return s.getCh() == '+' && CODEBLOCK_DELIM.matcher(s.getLine()).matches();
	}

	@Override
	public void parse(ParserState s) {
		s.closeParagraph();
		Code codeBlock = new Code();
		s.addStyledBlock(codeBlock);
		while (s.next() && !CODEBLOCK_DELIM.matcher(s.getLine()).matches()) {
			s.logParse("CodeBlock AddLine");
			codeBlock.addLine(s.getLine());
		}
	}
}