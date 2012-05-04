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

import org.apache.commons.lang.StringUtils;
import org.kivadoc.docparser.ParserState;
import org.kivadoc.dom.Header;

public final class SimpleHeaderParser implements ComponentParser {
	private static final Pattern SIMPLEHEADER = Pattern.compile("^([=]{1,6})[ \t]+([^ \t=].*)$");
	
	private Matcher headerMatcher;

	@Override
	public boolean match(ParserState s) {
		return s.getCh() == '=' && (headerMatcher = SIMPLEHEADER.matcher(s.getLine())).matches();
	}

	@Override
	public void parse(ParserState s) {
		s.closeParagraph();
		int level = headerMatcher.end(1);
		String headerText = headerMatcher.group(2);
		headerText = StringUtils.stripEnd(headerText, "=");
		Header header = new Header(level, s.parseParagraph(s.getLineNumber(), headerText));
		header.setLineNumber(s.getLineNumber());
		s.addStyledBlock(header);
	}
}