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
import org.kivadoc.dom.Header;

public final class HeaderParser implements ComponentParser {
	private static final Pattern HEADERLINE = Pattern.compile("^[-=]{3,}$");
	private static final Pattern HEADERTEXT = Pattern.compile("^[^ \t=-].*$");
	
	@Override
	public boolean match(ParserState s) {
		return !s.inParagraph() && s.getNextLine() != null && HEADERLINE.matcher(s.getNextLine()).matches() && HEADERTEXT.matcher(s.getLine()).matches();
	}

	@Override
	public void parse(ParserState s) {
		s.closeParagraph();
		final String levels = "=-";
		int level = levels.indexOf(s.getNextLine().charAt(0)) + 1;
		Header header = new Header(level, s.parseParagraph(s.getLineNumber(), s.getLine()));
		header.setLineNumber(s.getLineNumber());
		s.addStyledBlock(header);
		s.next();
	}
}