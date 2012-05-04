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


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kivadoc.TextExtractor;
import org.kivadoc.KivaError;
import org.kivadoc.KivaErrorType;
import org.kivadoc.Uri;
import org.kivadoc.KivaErrorType.MissingTarget;
import org.kivadoc.dom.BlockLevel;
import org.kivadoc.dom.Code;
import org.kivadoc.dom.Element;
import org.kivadoc.dom.ElementVisitor;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.Header;
import org.kivadoc.dom.InlineLevel;
import org.kivadoc.dom.LI;
import org.kivadoc.dom.OL;
import org.kivadoc.dom.P;
import org.kivadoc.dom.Style;
import org.kivadoc.dom.StyledBlock;
import org.kivadoc.dom.Table;
import org.kivadoc.dom.TableCell;
import org.kivadoc.dom.TableRow;
import org.kivadoc.dom.Text;
import org.kivadoc.dom.TextA;
import org.kivadoc.dom.TextImage;
import org.kivadoc.dom.TextRef;
import org.kivadoc.dom.TextSpan;
import org.kivadoc.dom.TextStyled;
import org.kivadoc.dom.TextTarget;
import org.kivadoc.dom.UL;
import org.kivadoc.emitters.HtmlBuilder.Attr;
import org.kivadoc.emitters.HtmlBuilder.Attribute;
import org.kivadoc.utils.FormattingUtils;

public /* NonFinal */ class DocHtmlEmitter extends ElementVisitor {
	protected HeaderLevels headerLevels;
	protected HtmlBuilder b;
	protected String documentDirUri = "/";
	protected List<KivaError> errors = new ArrayList<KivaError>();
	
	protected Set<String> allTargets = new HashSet<String>();
	protected List<TextRef> verifyRefs = new ArrayList<TextRef>();
	protected boolean documentFragment = false;
	
	/**
	 * Where is this document located in document hierarchy?
	 * <p>
	 * For example document "/foo/bar.txt" is located in "/foo/".
	 * <p>
	 * This information is used to convert absolute paths such
	 * as {/xyz} into relative paths.
	 */
	public void setDocumentDirUri(String documentDirUri) {
		this.documentDirUri = documentDirUri;
	}
	
	public void setDocumentFragment(boolean documentFragment) {
		this.documentFragment = documentFragment;
	}
	
	/**
	 * Return emitter errors.
	 */
	public List<KivaError> getErrors() {
		return errors;
	}
	
	public String toHtmlString(Element e) {
		return toHtmlString(new HtmlBuilder(), e);
	}
	
	public String toHtmlString(HtmlBuilder b, Element e) {
		this.headerLevels = new HeaderLevels();
		this.b = b;
		visit(e);
		for (TextRef ref : verifyRefs) {
			if (!allTargets.contains(ref.getTargetFrag())) {
				errors.add(new KivaError(ref.getLineNumber(), new KivaErrorType.InvalidTarget(ref.getTargetFrag())));
			}
		}
		return this.b.toString();
	}
	
	protected void addError(int lineNumber, KivaErrorType type) {
		errors.add(new KivaError(lineNumber, type));
	}
	
	protected void addError(KivaErrorType type) {
		errors.add(new KivaError(currentLineNumber, type));
	}
	
	@Override
	protected void code(Code e) {
		String[] lines = e.getCode().split("\n");
		lines = FormattingUtils.trimLines(lines);
		FormattingUtils.trimIndentation(lines);
		
		HtmlBuilder bc = b.compacted();
		bc.tag("pre",
				styleToAttr(e.getStyle()));
		bc.write(b.textToHtmlPre(StringUtils.join(lines, '\n')));
		bc.ctag("pre");
	}

	@Override
	protected void textTarget(TextTarget e) {
		allTargets.add(e.getName());
		b.tag("a",
				attr("name", e.getName()),
				styleToAttr(e.getStyle()));
		b.ctag("a");
	}

	@Override
	protected void textStyled(TextStyled e) {
		inlineToHtml(e.getKind(), e.getStyle(), e.getContent());
	}

	@Override
	protected void textSpan(TextSpan e) {
		inlineToHtml("span", e.getStyle(), e.getContent());
	}

	@Override
	protected void textRef(TextRef e) {
		DocTarget target = parseDocumentTarget(e.getTargetUri(), e.getTargetFrag());
		String url = null;
		if (target != null) {
			try {
				url = getUrl(target);
			} catch (Exception ex) {
			}
		}

		if (url == null) {
			addError(new KivaErrorType.InvalidTarget(e.getTargetUri()));
			b.write("<span class=\"invalidlink\">" + HtmlBuilder.textToHtml(e.getTargetUri()) + "</span>");
			return;
		}

		// verify internal links internally (collect them to verifyRefs)
		// and verify external references using doesTargetExist
		if (target.isInternal() && !documentFragment) {
			verifyRefs.add(e);
		} else if (!target.isInternal() && !doesTargetExist(target)) {
			addError(new KivaErrorType.MissingTarget(makeTitleWithFragment(target.uri, target.frag)));
		}

		b.tag("a",
				attr("href", url),
				styleToAttr(e.getStyle()));
		Elements<InlineLevel> content = e.getContent();
		if (content == null) {
			content = new Elements<InlineLevel>(Text.of(getTitle(target)));
		}
		inlineToHtml(null, null, content);
		b.ctag("a");
	}
	
	public static class DocTarget {
		/**
		 * Absolute uri of the target document. E.g. "/home".
		 */
		public String uri = "";
		public String frag = null;
		
		public boolean isInternal() {
			return uri.isEmpty();
		}
	}
	
	public DocTarget parseDocumentTarget(String targetUri, String targetFrag) {
		DocTarget result = new DocTarget();
		if (!targetUri.isEmpty()) {
			result.uri = getAbsoluteUri(targetUri);
			if (result.uri == null) {
				return null;
			}
		}
		result.frag = targetFrag;
		return result;
	}
	
	public String getAbsoluteUri(String uri) {
		try {
			return Uri.abs(Uri.join(documentDirUri, uri));
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	protected String makeTitleWithFragment(String title, String frag) {
		if (frag == null) {
			return title;
		}
		return title + " » " + frag;
	}
	
	public String getTitle(DocTarget target) {
		return makeTitleWithFragment(Uri.baseName(target.uri), target.frag);
	}
	
	public String getFileUrl(String uri) {
		try {
			return new URI(null, null, getUrlPath(uri), null, null).toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getUrl(DocTarget target) {
		try {
			return new URI(null, null, target.isInternal() ? null : getUrlPath(target.uri + ".html"), null, target.frag).toString();
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String getUrlPath(String uri) {
		return Uri.rel(uri, documentDirUri);
	}

	@Override
	protected void textImage(TextImage e) {
		String realUrl = e.getSrc().trim();
		b.emptyTag("img",
				attr("src", realUrl),
				attr("alt", (e.getAlt() == null ? realUrl : e.getAlt())),
				styleToAttr(e.getStyle()));
		checkIfLocalResourceExists(realUrl);
	}

	@Override
	protected void textBr() {
		b.emptyTag("br");
	}

	@Override
	protected void textA(TextA e) {
		String realUrl = e.getHref().trim();
		b.tag("a",
				attr("href", realUrl),
				styleToAttr(e.getStyle()));
		Elements<InlineLevel> content = e.getContent();
		if (content == null) {
			content = new Elements<InlineLevel>(Text.of(realUrl));
		}
		checkIfLocalResourceExists(realUrl);
		inlineToHtml(null, null, content);
		b.ctag("a");
	}

	private void checkIfLocalResourceExists(String realUrl) {
		if (isLocalResource(realUrl)) {
			String absUri = getLocalResourceUri(documentDirUri, realUrl);
			if (absUri == null || !doesResourceExist(absUri)) {
				addError(new MissingTarget(realUrl));
			}
		}
	}

	protected boolean doesResourceExist(String uri) {
		return true;
	}

	protected boolean doesTargetExist(DocTarget target) {
		return true;
	}
	
	/**
	 * Given a real URL, return a repository-local URI for it if possible; Otherwise returns null.
	 * @param documentDirUri URI for this document's directory.
	 * @param realUrl The real URL we are analyzing here.
	 * @return
	 */
	public static String getLocalResourceUri(String documentDirUri, String realUrl) {
		if (!isLocalResource(realUrl)) {
			return null;
		}
		try {
			return Uri.abs(documentDirUri + URLDecoder.decode(realUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		} catch (IllegalArgumentException e) {
		}
		return null;
	}
	
	private static final Pattern ABSOLUTE_URL = Pattern.compile("^[a-z]+:");
	
	private static boolean isLocalResource(String href) {
		if (ABSOLUTE_URL.matcher(href).find()) {
			return false;
		}
		if (href.startsWith("//")) {
			return false;
		}
		return true;
	}

	@Override
	protected void text(Text e) {
		b.write(HtmlBuilder.textToHtml(e.getText()));
	}

	@Override
	protected void tableRow(TableRow e) {
		blockToHtml("tr", e.getStyle(), e.getContent());
	}
	
	@Override
	protected void styledBlock(StyledBlock e) {
		blockToHtml("div", e.getStyle(), e.getContent());
	}

	@Override
	protected void tableCell(TableCell e) {
		String tag = e.isHeader() ? "th" : "td";
		b.tag(tag,
				styleToAttr(e.getStyle()),
				attr("rowspan", e.getRowSpan() > 1 ? "" + e.getRowSpan() : null),
				attr("colspan", e.getColSpan() > 1 ? "" + e.getColSpan() : null));
		b.indent();
		visitElements(e.getContent());
		b.dedent();
		b.ctag(tag);
	}

	@Override
	protected void table(Table e) {
		blockToHtml("table", e.getStyle(), e.getContent());
	}

	@Override
	protected void p(P e) {
		inlineToHtml("p", e.getStyle(), e.getContent());
	}

	@Override
	protected void ul(UL e) {
		blockToHtml("ul", e.getStyle(), e.getContent());
	}

	@Override
	protected void ol(OL e) {
		blockToHtml("ol", e.getStyle(), e.getContent());
	}

	@Override
	protected void li(LI e) {
		blockToHtml("li", e.getStyle(), e.getContent());
	}

	@Override
	protected void header(Header e) {
		Elements<InlineLevel> c = new Elements<InlineLevel>();
		headerLevels.updateLevels(headerLevels.getLevel(e));
		c.add(new TextTarget(headerLevels.getHeaderName()));
		String headerText = TextExtractor.extract(new P(e.getContent()));
		c.add(new TextTarget(headerText));
		c.addAll(e.getContent());
		inlineToHtml("h" + e.getLevel(), e.getStyle(), c);
	}
	
	protected <T extends InlineLevel>
	void inlineToHtml(String tag, Style style, Elements<T> content) {
		HtmlBuilder oldB = b;
		b = new HtmlBuilder().compacted();
		if (tag != null) {
			b.tag(tag, styleToAttr(style));
		}
		visitElements(content);
		if (tag != null) {
			b.ctag(tag);
		}
		oldB.write(b.toString());
		b = oldB;
	}

	protected <T extends BlockLevel>
	void blockToHtml(String tag, Style style, Elements<T> content) {
		b.tag(tag, styleToAttr(style));
		b.indent();
		visitElements(content);
		b.dedent();
		b.ctag(tag);
	}
	
	protected Attr styleToAttr(Style style) {
		if (style == null) {
			return null;
		}
		StringBuilder b = new StringBuilder();
		for (Map.Entry<String, String> kv : style.entrySet()) {
			if (kv.getKey().equals("style")) {
				continue;
			}
			b.append(kv.getKey());
			b.append(':');
			b.append(kv.getValue());
			b.append("; ");
		}
		return new HtmlBuilder.Attribute2(
				attr("class", style.get("style")),
				attr("style", b.toString()));
	}
	
	protected final Attribute attr(String key, String value) {
		return new Attribute(key, value);
	}
}
