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

import java.util.regex.Pattern;

import org.junit.Test;
import org.kivadoc.OrphanDetector;
import org.kivadoc.OrphanDetector.LinkGraph;
import org.kivadoc.OrphanDetector.Orphans;
import org.kivadoc.docparser.DocumentParser;
import org.kivadoc.dom.Body;

public class OrphanDetectorTest {
	@Test
	public void testTrivialCase() {
		LinkGraph lg = new LinkGraph();
		lg.addDocument("/home", new Body(new DocumentParser().parse("doc 1")));
		
		Orphans orphans = OrphanDetector.findOrphans(lg, "/home");
		assertEquals("[]", orphans.getDocuments().toString());
		assertEquals("[]", orphans.getFiles().toString());
	}
	
	@Test
	public void testSimpleOrphansCase() {
		LinkGraph lg = new LinkGraph();
		lg.addDocument("/home", new Body(new DocumentParser().parse("doc 1")));
		lg.addDocument("/other", new Body(new DocumentParser().parse("doc 2")));
		lg.addFile("/foo/bar.jpg");
		lg.addFile("/yes.pdf");
		lg.addDocument("/foo/third", new Body(new DocumentParser().parse("doc 3")));
		
		Orphans orphans = OrphanDetector.findOrphans(lg, "/home");
		assertEquals("[/other, /foo/third]", orphans.getDocuments().toString());
		assertEquals("[/foo/bar.jpg, /yes.pdf]", orphans.getFiles().toString());
		
		Orphans result = OrphanDetector.filterOutOrphans(orphans, Pattern.compile("pdf$"));
		assertEquals("[/other, /foo/third]", result.getDocuments().toString());
		assertEquals("[/foo/bar.jpg]", result.getFiles().toString());
	}
	
	@Test
	public void testSimpleOrphansCaseSolved() {
		LinkGraph lg = new LinkGraph();
		lg.addDocument("/home", new Body(new DocumentParser().parse("doc 1 {/foo/third}")));
		lg.addDocument("/other", new Body(new DocumentParser().parse("doc 2 link:yes.pdf")));
		lg.addFile("/foo/bar.jpg");
		lg.addFile("/yes.pdf");
		lg.addDocument("/foo/third", new Body(new DocumentParser().parse("doc 3 {/other} link:bar.png link:../foo/bar.jpg")));
		
		assertEquals("/home\n" +
				" ==> docs: [/foo/third]\n" +
				" ==> files: []\n" +
				"/other\n" +
				" ==> docs: []\n" +
				" ==> files: [/yes.pdf]\n" +
				"/foo/third\n" +
				" ==> docs: [/other]\n" +
				" ==> files: [/foo/bar.png, /foo/bar.jpg]\n" +
				"\n" +
				"file: /foo/bar.jpg\n" +
				"file: /yes.pdf\n", lg.toString());
		
		Orphans orphans = OrphanDetector.findOrphans(lg, "/home");
		assertEquals("[]", orphans.getDocuments().toString());
		assertEquals("[]", orphans.getFiles().toString());
	}
}
