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
package org.kivadoc.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kivadoc.utils.UtfUtils;

public class UtfUtilsTest {
	@Test
	public void testValidString() {
		String input = "hello world \u4522 \u457D";
		assertEquals(input, UtfUtils.decode(UtfUtils.encode(input)));
	}
	
	@Test
	public void testValidStringWithBOM() {
		byte[] string = new byte[] {(byte)0xef, (byte)0xbb, (byte)0xbf, (byte)'H', (byte)'I', (byte)'!'};
		assertEquals("HI!", UtfUtils.decode(string));
	}
}
