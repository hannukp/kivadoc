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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.kivadoc.KivaDocument;
import org.kivadoc.KivaError;

public final class FullHtmlEmitter {
	private DocHtmlEmitter docHtmlEmitter = new DocHtmlEmitter();
	private List<KivaError> errors = new ArrayList<KivaError>();

	public void setDocHtmlEmitter(DocHtmlEmitter docHtmlEmitter) {
		this.docHtmlEmitter = docHtmlEmitter;
	}
	
	public String toHtmlString(KivaDocument doc) {
		StringBuilder html = new StringBuilder();
		html.append("<h1>" + StringEscapeUtils.escapeXml(doc.getTitle()) + "</h1>");
		if (doc.getConfiguration("toc") != null) {
			TocHtmlEmitter tocEmitter = new TocHtmlEmitter();
			//TODO configure tocEmitter
			//tocEmitter.setMaxLevel(2);
			//tocEmitter.setShowNumbers(true); //doc needs to show numbers too
			html.append("<div class=\"toc\">");
			html.append("<div class=\"toctitle\">Table of Contents</div>");
			html.append(tocEmitter.toHtmlString(doc.getBody()));
			html.append("</div>");
		}
		html.append(docHtmlEmitter.toHtmlString(doc.getBody()));
		errors.addAll(docHtmlEmitter.getErrors());
		return html.toString();
	}
	
	public String toExtraStyleHtmlString(KivaDocument doc) {
		StringBuilder extraStyles = new StringBuilder();
		String styleConfig = doc.getConfiguration("style");
		if (styleConfig != null) {
			for (String style : styleConfig.split(",")) {
				style = style.trim();
				if (!style.isEmpty()) {
					String uri = docHtmlEmitter.getAbsoluteUri(style);
					if (uri != null) {
						try {
							String styleUrl = docHtmlEmitter.getFileUrl(uri);
							extraStyles.append("<link rel=\"stylesheet\" href=\"" + StringEscapeUtils.escapeHtml(styleUrl) + "\" type=\"text/css\" />");
						} catch (Exception e) {
							// just ignore invalid style uris
						}
					}
				}
			}
		}
		return extraStyles.toString();
	}
	
	public List<KivaError> getErrors() {
		return errors;
	}
}
