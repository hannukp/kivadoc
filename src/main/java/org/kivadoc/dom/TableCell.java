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

public final class TableCell extends BlockLevel implements HasContent<BlockLevel> {
	private final Elements<BlockLevel> content;
	private final boolean header;
	private int colSpan = 1;
	private int rowSpan = 1;

	public TableCell(boolean header, Elements<BlockLevel> content) {
		this.content = content;
		this.header = header;
	}
	
	public int getColSpan() {
		return colSpan;
	}
	
	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}
	
	public int getRowSpan() {
		return rowSpan;
	}
	
	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	@Override
	public Elements<BlockLevel> getContent() {
		return content;
	}
	
	public boolean isHeader() {
		return header;
	}
}
