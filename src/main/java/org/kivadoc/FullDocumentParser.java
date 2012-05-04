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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.kivadoc.docparser.DocumentParser;
import org.kivadoc.dom.Body;

public final class FullDocumentParser {
	private static final int MAX_TITLE_LENGTH = 100;
	private static final Pattern VALID_HEADER_KEY = Pattern.compile("[a-zA-Z0-9_]+");

	public KivaDocument parse(String input) {
		KivaDocument doc = new KivaDocument();
		List<KivaError> errors = new ArrayList<KivaError>();
		doc.setTitle(parseTitle(input));
		String[] lines = input.split("\r\n|\r|\n");
		int i = 1;
		if (lines.length > 2 && lines[1].matches("=+")) {
			for (i = 2; i < lines.length && !lines[i].trim().isEmpty(); i++) {
				String[] header = lines[i].split(":", 2);
				if (header.length == 2 && VALID_HEADER_KEY.matcher(header[0]).matches()) {
					doc.putConfiguration(header[0], header[1]);
				} else {
					errors.add(new KivaError(i + 1, new KivaErrorType.InvalidHeader()));
				}
			}
		}
		DocumentParser parser = new DocumentParser();
		parser.setStartLineNumber(i + 1);
		doc.setBody(new Body(parser.parse(Arrays.copyOfRange(lines, i, lines.length))));
		errors.addAll(parser.getErrors());
		doc.setErrors(errors);
		return doc;
	}
	
	public String parseTitle(String text) {
		int top = Math.min(MAX_TITLE_LENGTH, text.length());
		StringBuilder b = new StringBuilder();
		for (int ix = 0; ix < top; ix++) {
			char ch = text.charAt(ix);
			if (ch == '\r' || ch == '\n') {
				break;
			}
			b.append(ch);
		}
		return b.toString().trim();
	}
}
