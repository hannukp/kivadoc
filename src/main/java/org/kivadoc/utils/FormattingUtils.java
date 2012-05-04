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
package org.kivadoc.utils;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

public abstract class FormattingUtils {

	public static String[] trimLines(String[] lines) {
		int start = 0;
		for (; start < lines.length; start++) {
			if (!lines[start].trim().isEmpty()) {
				break;
			}
		}
		int end = lines.length - 1;
		for (; end >= start; end--) {
			if (!lines[end].trim().isEmpty()) {
				break;
			}
		}
		return Arrays.copyOfRange(lines, start, end + 1);
	}
	
	public static void trimIndentation(String[] lines) {
		if (lines.length == 0) {
			return;
		}
		int indent = StringUtils.indexOfAnyBut(lines[0], " \t");
		if (indent == -1) {
			return;
		}
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			int e = 0;
			for (; e < indent && e < line.length(); e++) {
				char ch = line.charAt(e);
				if (ch != ' ' && ch != '\t') {
					break;
				}
			}
			lines[i] = line.substring(e, line.length());
		}
	}

	public static String trimRight(String str) {
		int i = str.length() - 1;
		for (; i >= 0; i--) {
			char ch = str.charAt(i);
			if (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n') {
				break;
			}
		}
		return str.substring(0, i + 1);
	}

}
