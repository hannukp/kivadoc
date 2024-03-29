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


public final class UL extends BlockLevel implements HasContent<LI> {
	private final Elements<LI> content;
	
	public UL(Elements<LI> content) {
		this.content = content;
	}
	
	public UL(LI ... content) {
		this(new Elements<LI>(content));
	}

	@Override
	public Elements<LI> getContent() {
		return content;
	}
}
