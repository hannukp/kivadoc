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

import org.kivadoc.dom.BlockLevel;
import org.kivadoc.dom.Body;
import org.kivadoc.dom.Element;
import org.kivadoc.dom.ElementVisitor;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.Header;
import org.kivadoc.dom.InlineLevel;
import org.kivadoc.dom.LI;
import org.kivadoc.dom.P;
import org.kivadoc.dom.Text;
import org.kivadoc.dom.TextRef;
import org.kivadoc.dom.UL;

public final class TocHtmlEmitter extends ElementVisitor {
	private boolean showNumbers = false;
	private boolean createReferences = true;
	private int maxLevel = 2;
	
	private final HeaderLevels headerLevels = new HeaderLevels();
	private Elements<InlineLevel> headerTarget = null;
	private final List<UL> listLevels = new ArrayList<UL>();
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	public boolean isShowNumbers() {
		return showNumbers;
	}
	
	public void setShowNumbers(boolean showNumbers) {
		this.showNumbers = showNumbers;
	}
	
	public boolean isCreateReferences() {
		return createReferences;
	}
	
	public void setCreateReferences(boolean createReferences) {
		this.createReferences = createReferences;
	}
	
	@Override
	protected void header(Header e) {
		if (headerTarget != null) {
			return;
		}
		int level = headerLevels.getLevel(e);
		if (level > maxLevel) {
			return;
		}
		
		headerLevels.updateLevels(level);
		
		while (listLevels.size() <= level) {
			UL newList = new UL(new Elements<LI>());
			Elements<LI> content = listLevels.get(listLevels.size() - 1).getContent();
			ArrayList<LI> elements = content.getElements();
			if (!elements.isEmpty()) {
				elements.get(elements.size() - 1).getContent().add(newList);
			} else {
				// Skip headers that suddenly jump too deep. (e.g. straight from "1." to "1.1.1")
				return;
			}
			listLevels.add(newList);
		}
		while (listLevels.size() > level + 1) {
			listLevels.remove(listLevels.size() - 1);
		}
		
		//add a new LI element to the correct level
		headerTarget = new Elements<InlineLevel>();
		Elements<InlineLevel> pTarget = headerTarget;
		if (createReferences) {
			TextRef ref = new TextRef(headerTarget);
			ref.setTarget("#" + headerLevels.getHeaderName());
			pTarget = new Elements<InlineLevel>(ref);
		}
		Elements<LI> levelList = listLevels.get(level).getContent();
		Elements<BlockLevel> blockTarget = new Elements<BlockLevel>(new P(pTarget));
		levelList.add(new LI(blockTarget));
		
		if (showNumbers) {
			headerTarget.add(Text.of(headerLevels.getHeaderNumber() + " "));
		}
		
		visitElements(e.getContent());
		headerTarget = null;
	}
	
	@Override
	protected void text(Text e) {
		if (headerTarget != null) {
			headerTarget.add(e);
		}
	}

	public String toHtmlString(Element e) {
		listLevels.add(new UL());
		visit(e);
		return new DocHtmlEmitter().toHtmlString(new Body(new Elements<BlockLevel>(listLevels.get(0))));
	}
}
