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
import java.util.ArrayList;
import java.util.List;

import org.kivadoc.ParagraphParser;
import org.kivadoc.StyleParser;
import org.kivadoc.KivaError;
import org.kivadoc.KivaErrorType;
import org.kivadoc.dom.BlockLevel;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.InlineLevel;
import org.kivadoc.dom.P;
import org.kivadoc.dom.Style;

public final class ParserState {
	private String[] lines = null;
	private int lineIndex = -1;
	
	private final List<KivaError> errors = new ArrayList<KivaError>();
	private PrintStream out = null;
	
	private Style currentStyle = null;
	
	private Elements<BlockLevel> bodyTarget = null;
	private Elements<BlockLevel> blockTarget = null;
	private Elements<InlineLevel> inlineTarget = null;
	private int inlineTargetStartLineNumber = 0;
	private StringBuilder inlineBuffer = null;
	
	private final ListLevelStack listLevels = new ListLevelStack();
	private int startLineNumber = 1;
	
	public void reset(String[] lines) {
		this.lines = lines;
		this.bodyTarget = new Elements<BlockLevel>();
		this.blockTarget = bodyTarget;
		this.lineIndex = -1;
	}
	
	public List<KivaError> getErrors() {
		return errors;
	}
	
	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}
	
	/**
	 * Body target is the top-level target of this parsing.
	 */
	public Elements<BlockLevel> getBodyTarget() {
		return bodyTarget;
	}

	public void setDebugOut(PrintStream out) {
		this.out = out;
	}
	
	/////////////////////////////////////////////////////////
	// The following methods are for use by ComponentParsers
	
	public void logLine(String text) {
		if (out != null) {
			out.println("Process: " + text.trim());
		}
	}
	
	public void logParse(String text) {
		if (out != null) {
			out.println(" ==> " + text.trim());
		}
	}
	
	public void prev() {
		lineIndex--;
	}
	
	public boolean next() {
		lineIndex++;
		if (lineIndex < lines.length) {
			logLine(lines[lineIndex]);
		}
		return lineIndex < lines.length;
	}
	
	public void beginParagraph() {
		closeParagraph();
		inlineBuffer = new StringBuilder();
		inlineTarget = new Elements<InlineLevel>();
		inlineTargetStartLineNumber = getLineNumber();
		addStyledBlock(new P(inlineTarget));
	}

	public void writeToParagraph(String text) {
		if (!inParagraph()) {
			beginParagraph();
		}
		inlineBuffer.append(text + "\n");
	}
	
	public void closeParagraph() {
		if (inlineBuffer != null) {
			inlineTarget.addAll(parseParagraph(inlineTargetStartLineNumber, inlineBuffer.toString()));
		}
		inlineBuffer = null;
		inlineTarget = null;
		inlineTargetStartLineNumber = 0;
	}
	
	public void resetList() {
		this.blockTarget = this.bodyTarget;
		this.listLevels.clear();
	}
	
	public void setBlockTarget(Elements<BlockLevel> blockTarget) {
		this.blockTarget = blockTarget;
	}
	
	public boolean inParagraph() {
		return inlineTarget != null;
	}
	
	public Elements<InlineLevel> parseParagraph(int lineNumber, String text) {
		ParagraphParser parser = new ParagraphParser();
		parser.setStartLineNumber(lineNumber);
		Elements<InlineLevel> result = parser.parse(text);
		errors.addAll(parser.getErrors());
		return result;
	}

	public char getCh() {
		String line = getLine();
		return line.isEmpty() ? '\0' : line.charAt(0);
	}
	
	public String getNextLine() {
		return lineIndex < lines.length - 1 ? lines[lineIndex + 1] : null;
	}

	public String getLine() {
		return lines[lineIndex];
	}
	
	public Style parseStyle(String text) {
		return new StyleParser().parse(text);
	}
	
	public Elements<BlockLevel> parseNested(int startLine, String text) {
		DocumentParser parser = new DocumentParser();
		parser.setStartLineNumber(startLine);
		Elements<BlockLevel> result = parser.parse(text);
		errors.addAll(parser.getErrors());
		return result;
	}
	
	public void addStyledBlock(BlockLevel element) {
		if (currentStyle != null) {
			element.setStyle(currentStyle);
			currentStyle = null;
		}
		blockTarget.add(element);
	}
	
	public void setCurrentStyle(Style currentStyle) {
		this.currentStyle = currentStyle;
	}

	public void addError(KivaErrorType type) {
		addError(getLineNumber(), type);
	}
	
	public void addError(int lineNumber, KivaErrorType type) {
		errors.add(new KivaError(lineNumber, type));
	}
	
	public int getLineNumber() {
		return startLineNumber + lineIndex;
	}

	public ListLevelStack getListLevels() {
		return listLevels;
	}
}
