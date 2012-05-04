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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kivadoc.KivaError;
import org.kivadoc.utils.UtfUtils;

public class DocTestingUtils {

	public static String resourceToString(String resourceName) throws IOException {
		return UtfUtils.decode(IOUtils.toByteArray(DocTestingUtils.class.getResourceAsStream("/org/kivadoc/" + resourceName)));
	}

	public static String normalizeHtml(String html) {
		return html.trim().replace("\r", "").replaceAll(">\\s*<", "><").replace("<p> ", "<p>").replace(" </p>", "</p>").replace("\n</p>", "</p>").replace("<p>\n", "<p>");
	}

	public static void assertErrors(KivaError[] expected, List<KivaError> actual) {
		assertEquals(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual.get(i));
		}
	}

	public static void assertEqualsHtml(String comment, String expectedHtml, String actualHtml) {
		if (!normalizeHtml(expectedHtml).equals(normalizeHtml(actualHtml))) {
			System.err.println(comment);
			System.err.println("====================================");
			System.err.println(actualHtml);
			System.err.println("====================================");
			fail();
		}
	}

}
