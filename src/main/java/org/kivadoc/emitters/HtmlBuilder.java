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
package org.kivadoc.emitters;

public final class HtmlBuilder {
	private StringBuilder sb = new StringBuilder();
	private int indent = 0;
	private boolean compact = false;
	
	public HtmlBuilder() {
	}
	
	public HtmlBuilder(StringBuilder sb, int indent, boolean compact) {
		this.sb = sb;
		this.indent = indent;
		this.compact = compact;
	}

	public HtmlBuilder compacted() {
		return new HtmlBuilder(sb, indent, true);
	}
	
	public void write(String text) {
		if (compact) {
			sb.append(text);
		} else {
			writeIndent();
			sb.append(text.trim());
			sb.append('\n');
		}
	}

	private void writeIndent() {
		for (int i = 0; i < indent; i++) {
			sb.append(' ');
		}
	}
	
	public void indent() {
		indent++;
	}
	
	public void dedent() {
		indent--;
	}
	
	public static interface Attr {
		void write(StringBuilder s);
	}
	
	public static class Attribute implements Attr {
		public String key;
		public String value;
		public Attribute(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public void write(StringBuilder s) {
			if (value != null) {
				String v = value.trim();
				if (!v.isEmpty()) {
					s.append(' ');
					s.append(key);
					s.append("=\"");
					s.append(textToHtml(v));
					s.append('"');
				}
			}
		}
	}
	
	public static class Attribute2 implements Attr {
		public Attr a1;
		public Attr a2;
		
		public Attribute2(Attr a1, Attr a2) {
			this.a1 = a1;
			this.a2 = a2;
		}
		
		@Override
		public void write(StringBuilder s) {
			a1.write(s);
			a2.write(s);
		}
	}
	
	public void tag(String name, Attr ... args) {
		tagEx(">", name, args);
	}
	
	public void emptyTag(String name, Attr ... args) {
		tagEx(" />", name, args);
	}
	
	private void tagEx(String end, String name, Attr ... args) {
		if (!compact) {
			writeIndent();
		}
		sb.append('<');
		sb.append(name);
		for (Attr arg : args) {
			if (arg != null) {
				arg.write(sb);
			}
		}
		sb.append(end);
		if (!compact) {
			sb.append('\n');
		}
	}

	public void ctag(String name) {
		write("</" + name + ">");
	}
	
	public String textToHtmlPre(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char e = text.charAt(i);
			if (e == '<') {
				sb.append("&lt;");
			} else if (e == '>') {
				sb.append("&gt;");
			} else if (e == '&') {
				sb.append("&amp;");
			} else {
				sb.append(e);
			}
		}
		return sb.toString();
	}
	
	public static String textToHtml(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char e = text.charAt(i);
			if (e == '"') {
				sb.append("&quot;");
			} else if (e == '<') {
				sb.append("&lt;");
			} else if (e == '>') {
				sb.append("&gt;");
			} else if (e == '&') {
				sb.append("&amp;");
			} else if (e == '\n' || e == '\t' || e == '\r') {
				sb.append(" ");
			} else {
				sb.append(e);
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
