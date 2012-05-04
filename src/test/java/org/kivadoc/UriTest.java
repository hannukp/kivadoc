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
import static org.kivadoc.Uri.abs;
import static org.kivadoc.Uri.baseName;
import static org.kivadoc.Uri.dirName;
import static org.kivadoc.Uri.dirOf;
import static org.kivadoc.Uri.join;
import static org.kivadoc.Uri.join2;
import static org.kivadoc.Uri.norm;
import static org.kivadoc.Uri.rel;
import static org.kivadoc.Uri.splitExt;
import static org.kivadoc.Uri.title;

import org.junit.Test;

public class UriTest {

	@Test
	public void testUriNorm() {
		try {
			norm("foo//bar");
			fail();
		} catch (IllegalArgumentException e) {
		}
		assertEquals("../baz", norm("../bam/../baz"));
		assertEquals("../baz", norm("bam/./bar/.././../../baz"));
		assertEquals("/../mama", norm("/.././mama"));
		assertEquals("foo", norm("./foo"));
		assertEquals("/", norm("/"));
		assertEquals("/", norm("/."));
		assertEquals("", norm("."));
		assertEquals("", norm("./"));
		assertEquals("foo/", norm("foo/."));
		assertEquals("foo/", norm("foo/bar/.."));
	}

	@Test
	public void testUriAbs() {
		assertEquals("/foo/baz", abs("/foo/bar/../baz"));
		assertEquals("/baz", abs("/foo/bar/../.././baz"));
		try {
			abs("/foo/bar/../../../baz");
			fail();
		} catch (IllegalArgumentException e) {
		}
		assertEquals("/mam", abs("/./mam"));
		assertEquals("/bar", abs("/foo/../bar"));
		assertEquals("/", abs("/foo/.."));
		assertEquals("/", abs("/."));
	}

	@Test
	public void testUriJoin2() {
		assertEquals("baz/nan", join2("bam/../baz", "nan"));
		assertEquals("../bam/baz/nan", join2("../bam/baz", "nan"));
		assertEquals("../bam/nan", join2("../bam/baz", "../nan"));
		assertEquals("../nan", join2("../bam/baz", "../../nan"));
		assertEquals("../../nan", join2("../bam/baz", "../../../nan"));
		assertEquals("bar", join2("", "bar"));
	}

	@Test
	public void testUriJoin() {
		assertEquals("foo/bar/m/baz", join("foo", "bar/m/", "baz"));
		assertEquals("/foo/bar/m/baz/", join("/foo", "bar/m/", "baz/"));
		assertEquals("/bar/m/baz", join("foo", "/bar/m/", "baz"));
		assertEquals("bar", join("foo", "..", "bar"));
		assertEquals("/foo/bob.xml", join("/foo", "bob.xml"));
		assertEquals("/foo/bob.xml", join("/foo/", "bob.xml"));
		assertEquals("bob.xml", join("", "bob.xml"));
		assertEquals("/bob.xml", join("/", "bob.xml"));
		assertEquals("/bam.html", join(dirName("/foo/bar.html"), "../bam.html"));
		assertEquals("/foo/bam.html", join(dirName("/foo/bar.html"), "bam.html"));
	}

	@Test
	public void testUriRel() {
		assertEquals("bar", rel("foo/bar", "foo"));
		assertEquals("bar/bam", rel("/foo/bar/bam", "/foo"));
		assertEquals("../bar", rel("foo/../bar", "foo"));
		assertEquals("bar", rel("/foo/../bar", "/"));
		assertEquals("../foo/bar.txt", rel("/foo/bar.txt", "/bam/"));
	}

	@Test
	public void testUriSplitExt() {
		assertEqualsArray(new String[] { "foo", ".ext" }, splitExt("foo.ext"));
		assertEqualsArray(new String[] { "foo", "" }, splitExt("foo"));
		assertEqualsArray(new String[] { "foo.bar/nan", "" }, splitExt("foo.bar/nan"));
		assertEqualsArray(new String[] { "foo.bar/nan", ".txt" }, splitExt("foo.bar/nan.txt"));
	}

	private void assertEqualsArray(String[] expected, String[] actual) {
		assertEquals(2, expected.length);
		assertEquals(2, actual.length);
		assertEquals(expected[0], actual[0]);
		assertEquals(expected[1], actual[1]);
	}

	@Test
	public void testUriBaseName() {
		assertEquals("foo.ext", baseName("foo.ext"));
		assertEquals("nan.txt", baseName("foo.bar/nan.txt"));
		assertEquals("nan.txt", baseName("foo.bar/zz/nan.txt"));
		assertEquals("nan", baseName("foo.bar/nan/"));
	}

	@Test
	public void testUriTitle() {
		assertEquals("foo", title("foo.ext"));
		assertEquals("foo", title("foo"));
		assertEquals("nan", title("foo.bar/nan"));
		assertEquals("nan", title("foo.bar/nan.txt"));

	}

	@Test
	public void testUriDirName() {
		assertEquals("", dirName("foo.ext"));
		assertEquals("foo.bar/", dirName("foo.bar/nan.txt"));
		assertEquals("/", dirName("/foo.txt"));
		assertEquals("/foo/bar/", dirName("/foo/bar/baz.txt"));
		// different between dirName and dirOf:
		assertEquals("/foo/bar/", dirName("/foo/bar/"));
	}

	@Test
	public void testUriDirOf() {
		assertEquals("", dirOf("foo.ext"));
		assertEquals("foo.bar/", dirOf("foo.bar/nan.txt"));
		assertEquals("/", dirOf("/foo.txt"));
		assertEquals("/", dirOf("/foo/"));
		assertEquals("/foo/", dirOf("/foo/bar/"));
	}
}
