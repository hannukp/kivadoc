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


public final class P extends BlockLevel implements HasContent<InlineLevel> {
	private final Elements<InlineLevel> content;
	
	public P(Elements<InlineLevel> content) {
		this.content = content;
	}
	
	public static P of(InlineLevel ... content) {
		return new P(new Elements<InlineLevel>(content));
	}
	
	@Override
	public Elements<InlineLevel> getContent() {
		return content;
	}
}
