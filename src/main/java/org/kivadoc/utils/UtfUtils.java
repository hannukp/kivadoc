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

import java.nio.charset.Charset;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;

public class UtfUtils {
	private static final byte[] BOM = new byte[] {(byte)0xef, (byte)0xbb, (byte)0xbf};
	private static Charset UTF8 = Charset.forName(CharEncoding.UTF_8);
	private static Charset LATIN1 = Charset.forName(CharEncoding.ISO_8859_1);
	
	/**
	 * Decode from UTF-8, fallback to LATIN-1.
	 * Strip "byte order mark" and possibly decode as LATIN-1.
	 */
	public static String decode(byte[] bytes) {
		if (bytes.length >= 3 && bytes[0] == BOM[0] && bytes[1] == BOM[1] && bytes[2] == BOM[2]) {
			bytes = ArrayUtils.subarray(bytes, 3, bytes.length);
		}
		try {
			return new String(bytes, UTF8);
		} catch (Exception e) {
			return new String(bytes, LATIN1);
		}
	}
	
	/**
	 * Encode as UTF-8.
	 */
	public static byte[] encode(String text) {
		return text.getBytes(UTF8);
	}
}
