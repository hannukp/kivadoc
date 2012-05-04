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
import org.kivadoc.dom.StyledBlock;

public final class StyleBlockParser implements ComponentParser {
	private static final Pattern BLOCK_BEGIN = Pattern.compile("^\\[{3}(.*)$");
	private static final Pattern BLOCK_END = Pattern.compile("\\]{3}$");
	
	private Matcher blockMatcher;

	@Override
	public boolean match(ParserState s) {
		return !s.inParagraph() && s.getCh() == '[' && (blockMatcher = BLOCK_BEGIN.matcher(s.getLine())).matches();
	}

	@Override
	public void parse(ParserState s) {
		StringBuilder contents = new StringBuilder();
		String line = blockMatcher.group(1);
		int startLine = s.getLineNumber();
		while (true) {
			Matcher endMatcher = BLOCK_END.matcher(line);
			if (endMatcher.find()) {
				contents.append(line.substring(0, endMatcher.start(0)));
				break;
			} else {
				contents.append(line);
				contents.append('\n');
			}
			if (!s.next()) {
				break;
			}
			line = s.getLine();
		}
		
		s.addStyledBlock(new StyledBlock(s.parseNested(startLine, contents.toString())));
	}
}