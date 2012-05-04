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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.InlineLevel;
import org.kivadoc.dom.P;
import org.kivadoc.dom.Text;
import org.kivadoc.dom.TextA;
import org.kivadoc.dom.TextBR;
import org.kivadoc.dom.TextImage;
import org.kivadoc.dom.TextRef;
import org.kivadoc.dom.TextSpan;
import org.kivadoc.dom.TextStyled;
import org.kivadoc.dom.TextTarget;
import org.kivadoc.emitters.DocHtmlEmitter;


public final class ParagraphParser {

	public static void main(String[] args) {
		ParagraphParser parser = new ParagraphParser();
		//Elements<InlineLevel> parse = parser.parse("hello **++wo**&rld++** m<an");
		//Elements<InlineLevel> parse = parser.parse("hello http://url[] _and_ more");
		//Elements<InlineLevel> parse = parser.parse("hello http://url[n**on**o] _and_ more");
		//Elements<InlineLevel> parse = parser.parse("a(% color:red %)[hello world ]b");
		//Elements<InlineLevel> parse = parser.parse("goto {#target}  ##target## ");
		//Elements<InlineLevel> parse = parser.parse("goto {target}[H**EL**O]  ##target## ");
		//Elements<InlineLevel> parse = parser.parse("goto {#target}  ++target++f");
		
		Elements<InlineLevel> parse = parser.parse("hello \"world\" image:foo.png[] a~\nhello");
		//Elements<InlineLevel> parse = parser.parse("a +");
		System.out.println(new DocHtmlEmitter().toHtmlString(new P(parse)));
	}
	
	private static enum TargetName {
		STRONG, EM, CODE, MACRO
	}
	
	private static class TargetLevel {
		TargetName name;
		Elements<InlineLevel> target;
	}
	
	private boolean encodeQuotations = false;
	private boolean openQuotation = false;
	private final List<KivaError> errors = new ArrayList<KivaError>();
	private final List<TargetLevel> targetStack = new ArrayList<TargetLevel>();
	private final Set<TargetName> targetNames = new HashSet<TargetName>();
	private StringBuilder textTarget = null;
	private Elements<InlineLevel> paraTarget;
	private Elements<InlineLevel> inlineTarget;
	private String input;
	private int index;
	private int startLineNumber = 0;
	
	private static final String[] SYMBOLS = new String[] {
		"<==>", "\u27fa",
		"<-->", "\u27f7",
		"<==", "\u27f8",
		"==>", "\u27f9",
		"<--", "\u27f5",
		"-->", "\u27f6",
		"<->", "\u2194",
		"<=>", "\u21d4",
		"->", "\u2192",
		"<-", "\u2190",
		"=>", "\u21d2",
		"<=", "\u21d0",
		">>", "\u00bb",
		"<<", "\u00ab",
		"---", "\u2014",
		"--", "\u2013",
		"(c)", "\u00a9",
		"(C)", "\u00a9",
		"(r)", "\u00ae",
		"(R)", "\u00ae",
		"(tm)", "\u2122",
		"(TM)", "\u2122",
		"...", "\u2026",
	};
	
	/**
	 * Macro format = {macro_name}:{data}[{options}]
	 */
	private static final Pattern MACRO = Pattern.compile("^([a-z]+:([^ \t][^\\[ \t]*))(\\[)?");
	
	public Elements<InlineLevel> parse(String string) {
		//System.out.println("PARAGRAPH:" + StringEscapeUtils.escapeJava(string));
		paraTarget = inlineTarget = new Elements<InlineLevel>();
		input = string;
		index = -1;
		while (next()) {
			char ch = getCh();
			if (isMacro("http") || isMacro("https") || isMacro("ftp") || isMacro("file")) {
				parseLink(true);
				continue;
			}
			if (isMacro("link")) {
				parseLink(false);
				continue;
			}
			if (isMacro("image")) {
				parseImage();
				continue;
			}
			if (ch < 'A') {
				char nextCh = getNextCh();
				if (ch == '*' && nextCh == '*') {
					applyStyling(TargetName.STRONG);
					continue;
				}
				if (ch == '#' && nextCh == '#') {
					parseTarget();
					continue;
				}
				if (ch == '+' && nextCh == '+') {
					parseCode();
					continue;
				}
				if (ch == '/' && nextCh == '/') {
					applyStyling(TargetName.EM);
					continue;
				}
				if ((ch == '<' || ch == '>' || ch == '-' || ch == '=' || ch == '(' || ch == '.') && matchSymbol()) {
					continue;
				}
				if (encodeQuotations && ch == '"') {
					startText();
					textTarget.append(openQuotation ? '\u201D' : '\u201C');
					openQuotation = !openQuotation;
					continue;
				}
				if (ch == '(' && nextCh == '%') {
					parseSpan();
					continue;
				}
			}
			if (ch == '{') {
				parseRef();
				continue;
			}
			if (ch == '~' && (getNextCh() == '\r' || getNextCh() == '\n' || getNextCh() == '\0')) {
				index++;
				textTarget = null;
				inlineTarget.add(new TextBR());
				continue;
			}
			if (ch == ']' && closeTarget(TargetName.MACRO)) {
				continue;
			}
			if (ch == '~') {
				if (!next()) {
					continue;
				}
				ch = getCh();
			}
			
			startText();
			textTarget.append(ch);
		}
		if (!targetStack.isEmpty()) {
			List<TargetLevel> stack = new ArrayList<TargetLevel>(targetStack);
			Collections.reverse(stack);
			for (TargetLevel tl : stack) {
				errors.add(new KivaError(getLineNumber(), new KivaErrorType.ParagraphTargetNotClosed(tl.name.toString())));
			}
		}
		
		return paraTarget;
	}
	
	public int getLineNumber() {
		// TODO: adjust line number as the parsing progresses
		return startLineNumber;
	}
	
	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}
	
	public void setEncodeQuotations(boolean encodeQuotations) {
		this.encodeQuotations = encodeQuotations;
	}
	
	public List<KivaError> getErrors() {
		return errors;
	}

	private void startText() {
		if (textTarget == null) {
			textTarget = new StringBuilder();
			inlineTarget.add(new Text(textTarget));
		}
	}

	private boolean matchSymbol() {
		char ch = getCh();
		for (int i = 0; i < SYMBOLS.length; i += 2) {
			if (ch == SYMBOLS[i].charAt(0) && isPrefix(SYMBOLS[i])) {
				index += SYMBOLS[i].length() - 1;
				startText();
				textTarget.append(SYMBOLS[i + 1]);
				return true;
			}
		}
		return false;
	}
	
	private TargetLevel newTargetLevel(TargetName targetName) {
		TargetLevel s = new TargetLevel();
		s.target = inlineTarget;
		s.name = targetName;
		return s;
	}
	
	private void applyStyling(TargetName style) {
		if (!closeTarget(style) && !targetNames.contains(style)) {
			Elements<InlineLevel> newTarget = new Elements<InlineLevel>();
			Elements<InlineLevel> oldTarget = inlineTarget;
			
			textTarget = null;
			targetStack.add(newTargetLevel(style));
			targetNames.add(style);
			inlineTarget = newTarget;
			
			String htmlStyle;
			switch (style) {
			case EM:
				htmlStyle = "em";
				break;
			case STRONG:
				htmlStyle = "strong";
				break;
			case CODE:
				htmlStyle = "code";
				break;
			default:
				throw new IllegalStateException("Unknown style " + style);
			}
			
			oldTarget.add(new TextStyled(htmlStyle, inlineTarget));
		}
		index++;
	}
	
	private boolean closeTarget(TargetName targetName) {
		if (!targetStack.isEmpty() && targetStack.get(targetStack.size() - 1).name.equals(targetName)) {
			TargetLevel s = targetStack.get(targetStack.size() - 1);
			targetStack.remove(targetStack.size() - 1);
			targetNames.remove(targetName);
			textTarget = null;
			inlineTarget = s.target;
			return true;
		}
		return false;
	}

	private char getCh() {
		return input.charAt(index);
	}

	private char getNextCh() {
		return index + 1 < input.length() ? input.charAt(index + 1) : '\0';
	}
	
	private char getNextCh(int i) {
		return index + i < input.length() ? input.charAt(index + i) : '\0';
	}

	private void parseImage() {
		Matcher linkMatcher = getMacroMatch();
		String href = linkMatcher.group(2);
		TextImage img = new TextImage();
		img.setLineNumber(getLineNumber());
		img.setSrc(href);
		index = linkMatcher.end() - 1;
		if (linkMatcher.group(3) != null) {
			StringBuilder alt = new StringBuilder();
			while (next()) {
				char ch = getCh();
				if (ch == ']') {
					break;
				}
				if (ch == '~') {
					if (!next()) {
						continue;
					}
					ch = getCh();
				}
				alt.append(ch);
			}
			String altString = alt.toString();
			if (!StringUtils.isBlank(altString)) {
				img.setAlt(altString);
			}
		}
		textTarget = null;
		inlineTarget.add(img);
	}

	private void parseLink(boolean absolute) {
		Matcher linkMatcher = getMacroMatch();
		String href = linkMatcher.group(absolute ? 1 : 2);
		int indexMinus = 1;
		if (linkMatcher.group(3) != null) {
			if (getNextCh(linkMatcher.end() - index) == ']') {
				//link of style "link:foo.png[]" is handled as a special case
				TextA textA = TextA.of(href);
				textA.setLineNumber(getLineNumber());
				inlineTarget.add(textA);
				indexMinus = 0;
			} else {
				Elements<InlineLevel> newTarget = new Elements<InlineLevel>();
				TextA textA = new TextA(newTarget);
				textA.setLineNumber(getLineNumber());
				textA.setHref(href);
				inlineTarget.add(textA);
				targetStack.add(newTargetLevel(TargetName.MACRO));
				inlineTarget = newTarget;
			}
		} else {
			if (linkMatcher.group(3) == null && absolute) {
				char lastChar = href.charAt(href.length() - 1);
				if (",.?!:;\"'".indexOf(lastChar) != -1) {
					href = href.substring(0, href.length() - 1);
					indexMinus = 2;
				}
			}
			
			TextA textA = TextA.of(href);
			textA.setLineNumber(getLineNumber());
			inlineTarget.add(textA);
		}
		textTarget = null;
		index = linkMatcher.end() - indexMinus;
	}
	
	private void parseRef() {
		StringBuilder tb = new StringBuilder();
		while (next() && getCh() != '}') {
			tb.append(getCh());
		}
		String target = tb.toString();
		if (getNextCh() == '[') {
			next();
			Elements<InlineLevel> newTarget = new Elements<InlineLevel>();
			TextRef textRef = new TextRef(newTarget);
			textRef.setLineNumber(getLineNumber());
			textRef.setTarget(target);
			inlineTarget.add(textRef);
			targetStack.add(newTargetLevel(TargetName.MACRO));
			inlineTarget = newTarget;
		} else {
			TextRef textRef = TextRef.of(target);
			textRef.setLineNumber(getLineNumber());
			inlineTarget.add(textRef);
		}
		textTarget = null;
	}
	
	private void parseTarget() {
		index++;
		StringBuilder name = new StringBuilder();
		while (next() && !(getCh() == '#' && getNextCh() == '#')) {
			name.append(getCh());
		}
		index++;
		textTarget = null;
		inlineTarget.add(new TextTarget(name.toString()));
	}


	private void parseCode() {
		applyStyling(TargetName.CODE);
		startText();
		while (next() && !(getCh() == '+' && getNextCh() == '+')) {
			textTarget.append(getCh());
		}
		if (isAtEnd()) {
			errors.add(new KivaError(getLineNumber(), new KivaErrorType.ParagraphTargetNotClosed("CODE")));
		}
		applyStyling(TargetName.CODE);
	}

	private void parseSpan() {
		index++;
		StringBuilder spanStyle = new StringBuilder();
		while (next() && !(getCh() == '%' && getNextCh() == ')')) {
			spanStyle.append(getCh());
		}
		if (getNextCh(2) != '[') {
			return;
		}
		index += 2;
		
		Elements<InlineLevel> newTarget = new Elements<InlineLevel>();
		TextSpan span = new TextSpan(newTarget);
		span.setStyle(new StyleParser().parse(spanStyle.toString()));
		inlineTarget.add(span);
		targetStack.add(newTargetLevel(TargetName.MACRO));
		inlineTarget = newTarget;
		textTarget = null;
	}


	private Matcher getMacroMatch() {
		Matcher m = MACRO.matcher(input).region(index, input.length());
		return m.find() ? m : null;
	}
	
	private boolean isMacro(String prefix) {
		if (!isPrefix(prefix)) {
			return false;
		}
		int i = index + prefix.length();
		if (i >= input.length() || input.charAt(i) != ':') {
			return false;
		}
		i++;
		if (i >= input.length()) {
			return false;
		}
		char ch = input.charAt(i);
		if (isWhite(ch)) {
			return false;
		}
		return true;
	}

	private boolean isPrefix(String prefix) {
		if (getCh() != prefix.charAt(0)) {
			return false;
		}
		for (int i = 1; i < prefix.length(); i++) {
			if (index + i >= input.length()) {
				return false;
			}
			if (input.charAt(index + i) != prefix.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private boolean isWhite(char ch) {
		return ch == ' ' || ch == '\t';
	}

	private boolean next() {
		index++;
		return index < input.length();
	}
	
	private boolean isAtEnd() {
		return index >= input.length();
	}
}
