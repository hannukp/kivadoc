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

import java.io.Serializable;


public abstract class KivaErrorType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Table's first cell had some content on it. That cell is
	 * ignored.
	 */
	public static class TableFirstCellHasData extends KivaErrorType {
		private static final long serialVersionUID = 1L;
		private String cellText;

		public TableFirstCellHasData(String cellText) {
			this.cellText = cellText;
		}

		@Override
		public String toString() {
			return "TableFirstCellHasData[cellText=" + cellText + "]";
		}
	}
	
	/**
	 * Empty tables are meaningless.
	 */
	public static class TableIsEmpty extends KivaErrorType {
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return "TableIsEmpty";
		}
	}
	
	/**
	 * rowspan/colspan is invalid.
	 */
	public static class TableHasInvalidSpan extends KivaErrorType {
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return "TableHasInvalidSpan";
		}
	}
	
	/**
	 * Table did not have enough columns to fill the last row.
	 */
	public static class TableIsUnderfull extends KivaErrorType {
		private static final long serialVersionUID = 1L;
		
		private int missingColumns;

		public TableIsUnderfull(int missingColumns) {
			this.missingColumns = missingColumns;
		}

		@Override
		public String toString() {
			return "TableIsUnderfull[missingColumns=" + missingColumns + "]";
		}
	}
	
	/**
	 * Paragraph target (such as "bold", "emphasis", "style") was not closed properly.
	 */
	public static class ParagraphTargetNotClosed extends KivaErrorType {
		private static final long serialVersionUID = 1L;
		private String targetName;

		public ParagraphTargetNotClosed(String targetName) {
			this.targetName = targetName;
		}

		@Override
		public String toString() {
			return "ParagraphTargetNotClosed[" + targetName + "]";
		}
	}
	
	/**
	 * Cross-reference target is invalid.
	 */
	public static class InvalidTarget extends KivaErrorType {
		private static final long serialVersionUID = 1L;
		private String targetName;
		
		public InvalidTarget(String targetName) {
			this.targetName = targetName;
		}

		@Override
		public String toString() {
			return "InvalidTarget[" + targetName + "]";
		}
	}
	
	/**
	 * Target is referring to a missing document.
	 */
	public static class MissingTarget extends KivaErrorType {
		private static final long serialVersionUID = 1L;
		private String targetName;
		
		public MissingTarget(String targetName) {
			this.targetName = targetName;
		}

		@Override
		public String toString() {
			return "MissingTarget[" + targetName + "]";
		}
	}
	
	public static class InvalidHeader extends KivaErrorType {
		private static final long serialVersionUID = 1L;

		public InvalidHeader() {
		}

		@Override
		public String toString() {
			return "InvalidHeader[]";
		}
	}
}
