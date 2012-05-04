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
package org.kivadoc.docparser.componentparsers;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kivadoc.KivaErrorType;
import org.kivadoc.docparser.ParserState;
import org.kivadoc.dom.BlockLevel;
import org.kivadoc.dom.Elements;
import org.kivadoc.dom.P;
import org.kivadoc.dom.Style;
import org.kivadoc.dom.Table;
import org.kivadoc.dom.TableCell;
import org.kivadoc.dom.TableRow;
import org.kivadoc.dom.Text;

public final class TableParser implements ComponentParser {
	private static final Pattern CELLSTYLEDEF = Pattern.compile("^\\(%(.*)%\\)([^\r\n\\[]|$)");
	private static final Pattern TABLE = Pattern.compile("^\\|=+$");
	private static final Pattern EMPTY = Pattern.compile("^[ \t]*$");
	private static enum HeaderPhase { NO_HEADER_CREATED, HEADER_DONE, PARSING_REST }

	@Override
	public boolean match(ParserState s) {
		return s.getCh() == '|';
	}
	
	private List<TableCell> allCells;
	private StringBuilder cellTarget;
	private int cellStartLineNumber;
	private HeaderPhase headerPhase;
	private int numColumns;
	private Set<TablePos> reservedCells;
	
	@Override
	public void parse(ParserState s) {
		s.closeParagraph();
		boolean bigTable = TABLE.matcher(s.getLine()).matches();
		if (bigTable) {
			if (!s.next() || TABLE.matcher(s.getLine()).matches()) {
				// Empty table
				s.addError(new KivaErrorType.TableIsEmpty());
				return;
			}
		}
		
		allCells = new ArrayList<TableCell>();
		cellTarget = new StringBuilder();
		cellStartLineNumber = s.getLineNumber();
		headerPhase = HeaderPhase.NO_HEADER_CREATED;
		numColumns = -1;
		reservedCells = new HashSet<TablePos>();
		Pattern endPattern = bigTable ? TABLE : EMPTY;
		// Parse the cells of the table.
		// Count the cells on the first line to determine the number of columns.
		do {
			String line = s.getLine();
			if (endPattern.matcher(line).matches()) {
				break;
			}
			for (int i = 0; i < line.length(); i++) {
				char ch = line.charAt(i);
				if (ch == '~') {
					i++;
					if (i < line.length()) {
						cellTarget.append(line.charAt(i));
					}
					continue;
				}
				if (ch == '|') {
					completeCell(s);
					cellTarget = new StringBuilder();
					continue;
				}
				cellTarget.append(ch);
			}
			cellTarget.append('\n');
			if (headerPhase == HeaderPhase.NO_HEADER_CREATED && allCells.size() != 0) {
				headerPhase = HeaderPhase.HEADER_DONE;
				// The header ends here, at the end of the first non-empty line, but we have
				// to wait until completeCell is called the next time in order to count
				// how many columns there are.
			}
		} while (s.next());
		completeCell(s);
		
		if (numColumns <= 0 || headerPhase != HeaderPhase.PARSING_REST) {
			s.addError(new KivaErrorType.TableIsEmpty());
			return;
		}
		
		//TODO check for invalid spans such as:
		//-rowspan extends below last row
		//-rowspans fill an entire row so that row ends up containing nothing
		
		// Lay-out the parsed cells into a Table element
		int addedColumns = 0;
		Elements<TableRow> rows = new Elements<TableRow>();
		int index = 1;
		for (int row = 0; index < allCells.size(); row++) {
			Elements<TableCell> cells = new Elements<TableCell>();
			for (int col = 0; col < numColumns; col++) {
				TablePos pos = new TablePos(row, col);
				if (reservedCells.contains(pos)) {
					continue;
				}
				
				while (index >= allCells.size()) {
					addedColumns++;
					allCells.add(new TableCell(false, new Elements<BlockLevel>(P.of(Text.of("")))));
				}
				
				TableCell cell = allCells.get(index);
				if (!hasSpace(cell, row, col)) {
					s.addError(new KivaErrorType.TableHasInvalidSpan());
				}
				reserveSpace(cell, row, col);
				
				cells.add(cell);
				index++;
			}
			rows.add(new TableRow(cells));
		}
		if (addedColumns > 0) {
			s.addError(new KivaErrorType.TableIsUnderfull(addedColumns));
		}
		
		s.addStyledBlock(new Table(rows));
	}

	private void reserveSpace(TableCell cell, int row, int col) {
		for (int r = 0; r < cell.getRowSpan(); r++) {
			for (int c = 0; c < cell.getColSpan(); c++) {
				reservedCells.add(new TablePos(row + r, col + c));
			}
		}
	}

	private boolean hasSpace(TableCell cell, int row, int col) {
		for (int r = 0; r < cell.getRowSpan(); r++) {
			for (int c = 0; c < cell.getColSpan(); c++) {
				if (reservedCells.contains(new TablePos(row + r, col + c))) {
					return false;
				}
			}
		}
		if (col + cell.getColSpan() > numColumns) {
			return false;
		}
		return true;
	}

	private void completeCell(ParserState s) {
		String cellText = cellTarget.toString();
		if (allCells.isEmpty() && !StringUtils.isBlank(cellText)) {
			s.addError(cellStartLineNumber, new KivaErrorType.TableFirstCellHasData(cellText));
		}
		allCells.add(parseCell(s, cellText));
		if (headerPhase == HeaderPhase.HEADER_DONE) {
			headerPhase = HeaderPhase.PARSING_REST;
			numColumns = 0;
			for (int i = 1; i < allCells.size(); i++) {
				numColumns += allCells.get(i).getColSpan();
			}
		}
		cellStartLineNumber = s.getLineNumber();
	}

	private TableCell parseCell(ParserState s, String cellText) {
		boolean header = false;
		if (cellText.startsWith("=")) {
			cellText = cellText.substring(1);
			header = true;
		}
		cellText = cellText.trim();
		
		String cellStyle = null;
		Matcher colStyleMatcher = CELLSTYLEDEF.matcher(cellText);
		if (colStyleMatcher.find()) {
			cellStyle = colStyleMatcher.group(1);
			cellText = cellText.substring(colStyleMatcher.end(1) + 2).trim();
		}
		
		//System.out.println("CELL='" + StringEscapeUtils.escapeJava(cellText) + "'");
		TableCell cell = new TableCell(header, s.parseNested(cellStartLineNumber, cellText));
		if (cellStyle != null) {
			Style style = new Style();
			for (Entry<String, String> kv : s.parseStyle(cellStyle).entrySet()) {
				if (kv.getKey().equals("rowspan")) {
					cell.setRowSpan(parseSpan(s, kv.getValue()));
				} else if (kv.getKey().equals("colspan")) {
					cell.setColSpan(parseSpan(s, kv.getValue()));
				} else {
					style.put(kv.getKey(), kv.getValue());
				}
			}
			cell.setStyle(style);
		}
		return cell;
	}
	
	private int parseSpan(ParserState s, String spanValue) {
		try {
			int v = Integer.parseInt(spanValue);
			if (v >= 1 && v < 100 /* limit span to a reasonable number */) {
				return v;
			}
		} catch (NumberFormatException e) {
		}
		s.addError(new KivaErrorType.TableHasInvalidSpan());
		return 1;
	}
}