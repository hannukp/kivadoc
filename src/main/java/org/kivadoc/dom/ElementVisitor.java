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
package org.kivadoc.dom;

public abstract class ElementVisitor {
	protected int currentLineNumber;
	
	public void visit(Element e) {
		currentLineNumber = e.getLineNumber();
		if (e instanceof Body) {
			body((Body) e);
		} else if (e instanceof Code) {
			code((Code) e);
		} else if (e instanceof Header) {
			header((Header) e);
		} else if (e instanceof LI) {
			li((LI) e);
		} else if (e instanceof OL) {
			ol((OL) e);
		} else if (e instanceof UL) {
			ul((UL) e);
		} else if (e instanceof P) {
			p((P) e);
		} else if (e instanceof Table) {
			table((Table) e);
		} else if (e instanceof TableCell) {
			tableCell((TableCell) e);
		} else if (e instanceof TableRow) {
			tableRow((TableRow) e);
		} else if (e instanceof StyledBlock) {
			styledBlock((StyledBlock) e);
		} else if (e instanceof Text) {
			text((Text) e);
		} else if (e instanceof TextA) {
			textA((TextA) e);
		} else if (e instanceof TextBR) {
			textBr();
		} else if (e instanceof TextImage) {
			textImage((TextImage) e);
		} else if (e instanceof TextRef) {
			textRef((TextRef) e);
		} else if (e instanceof TextSpan) {
			textSpan((TextSpan) e);
		} else if (e instanceof TextStyled) {
			textStyled((TextStyled) e);
		} else if (e instanceof TextTarget) {
			textTarget((TextTarget) e);
		} else {
			throw new IllegalStateException("Unknown element type: " + e.getClass());
		}
	}

	protected void body(Body e) {
		visitElements(e.getContent());
	}

	protected void code(Code e) {
	}

	protected void header(Header e) {
		visitElements(e.getContent());
	}

	protected void li(LI e) {
		visitElements(e.getContent());
	}

	protected void ol(OL e) {
		visitElements(e.getContent());
	}

	protected void ul(UL e) {
		visitElements(e.getContent());
	}

	protected void p(P e) {
		visitElements(e.getContent());
	}

	protected void table(Table e) {
		visitElements(e.getContent());
	}

	protected void tableCell(TableCell e) {
		visitElements(e.getContent());
	}

	protected void tableRow(TableRow e) {
		visitElements(e.getContent());
	}

	protected void styledBlock(StyledBlock e) {
		visitElements(e.getContent());
	}
	
	protected void text(Text e) {
	}

	protected void textA(TextA e) {
		visitElements(e.getContent());
	}

	protected void textBr() {
	}

	protected void textImage(TextImage e) {
	}

	protected void textRef(TextRef e) {
		visitElements(e.getContent());
	}

	protected void textSpan(TextSpan e) {
		visitElements(e.getContent());
	}

	protected void textStyled(TextStyled e) {
		visitElements(e.getContent());
	}

	protected void textTarget(TextTarget e) {
	}

	protected <T extends Element> void visitElements(Elements<T> elements) {
		if (elements != null) {
			for (T e : elements) {
				visit(e);
			}
		}
	}
}
