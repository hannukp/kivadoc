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

import org.kivadoc.docparser.ListLevel;
import org.kivadoc.docparser.ParserState;
import org.kivadoc.dom.BlockLevel;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.LI;
import org.kivadoc.dom.OL;
import org.kivadoc.dom.UL;

public final class ListParser implements ComponentParser {
	private static final Pattern LIST = Pattern.compile("^([ \t]*)([-#*]+)");
	
	// the level that we matched on currently parsed line
	private ListLevel theLevel;

	@Override
	public boolean match(ParserState s) {
		Matcher listMatcher = LIST.matcher(s.getLine());
		// it's a list if it looks like a list AND...
		if (!listMatcher.find()) {
			return false;
		}
		
		theLevel = new ListLevel(listMatcher.group(1).length(), trimSymbols(listMatcher.group(2)));
		
		// (valid new list must be a single symbol)
		boolean isValidNewList = (theLevel.symbols.length() == 1);
		
		// if there's no existing list, it should be a new list
		if (s.getListLevels().isEmpty()) {
			return isValidNewList;
		}
		
		// OR it can be one of the previous levels
		ListLevel find = s.getListLevels().find(theLevel);
		if (find != null) {
			theLevel = find;
			return true;
		}
		
		// OR it is one step deeper than the previous level
		ListLevel prevLevel = s.getListLevels().top();
		if (prevLevel.depth < theLevel.depth && theLevel.symbols.length() == 1) {
			return true;
		}
		
		// OR it has the same depth but one more symbol
		if (prevLevel.depth == theLevel.depth
				&& theLevel.symbols.substring(0, theLevel.symbols.length() - 1).equals(prevLevel.symbols)) {
			return true;
		}
		
		// OR it can still be the start of a new list, because
		// it's incompatible with the current list
		if (isValidNewList) {
			s.resetList();
			return true;
		}
		
		return false;
	}

	private static String trimSymbols(String allSymbols) {
		char bullet = ' ';
		int i = 0;
		for (; i < allSymbols.length(); i++) {
			char ch = allSymbols.charAt(i);
			if (ch != '#') {
				if (bullet != ' ' && ch != bullet) {
					break;
				}
				bullet = ch;
			}
		}
		return allSymbols.substring(0, i);
	}
	
	@Override
	public void parse(ParserState s) {
		s.closeParagraph();
		
		if (theLevel.listTarget == null) {
			// new list or new level on an existing list
			s.getListLevels().add(theLevel);
			theLevel.listTarget = new Elements<LI>();
			boolean ordered = theLevel.symbols.charAt(theLevel.symbols.length() - 1) == '#';
			s.addStyledBlock(ordered ? new OL(theLevel.listTarget) : new UL(theLevel.listTarget));
		} else {
			// roll up the listLevel-stack to the level we're at
			while (!s.getListLevels().top().equals(theLevel)) {
				s.getListLevels().pop();
			}
		}
		
		// add a new LI-element to the active list, and make it the blockTarget
		Elements<BlockLevel> listBlock = new Elements<BlockLevel>();
		theLevel.listTarget.add(new LI(listBlock));
		s.setBlockTarget(listBlock);
		
		String lineData = s.getLine().substring(theLevel.depth + theLevel.symbols.length());
		s.writeToParagraph(lineData);
	}
}