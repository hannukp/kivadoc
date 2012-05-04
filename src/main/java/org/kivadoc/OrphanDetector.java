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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.kivadoc.dom.Body;
import org.kivadoc.dom.ElementVisitor;
import org.kivadoc.dom.TextA;
import org.kivadoc.dom.TextImage;
import org.kivadoc.dom.TextRef;
import org.kivadoc.emitters.DocHtmlEmitter;

/**
 * Detects orphaned documents and resources in a document repository.
 * <p>
 * Usage:
 * <pre>
 * LinkGraph lg = new LinkGraph();
 * lg.addDocument("/foo.txt", fooBody);
 * lg.addDocument("/home.txt", homeBody);
 * lg.addFile("/yes.jpg");
 * Orphans orphans = OrphanDetector.findOrphans(lg, "/home");
 * //orphans.getDocuments();
 * //orphans.getFiles();
 * </pre>
 * You may not want to be notified of every orphan there is. For that
 * you may use the OrphanDetector.filterOutOrphans function, to filter
 * out orphans that match a certain pattern.
 */
public class OrphanDetector {
	public static class LinkGraph {
		private Map<String, List<String>> documentReferences = new HashMap<String, List<String>>();
		private Map<String, List<String>> fileReferences = new HashMap<String, List<String>>();
		private List<String> documents = new ArrayList<String>();
		private List<String> files = new ArrayList<String>();
		
		public void addDocument(String documentUri, Body body) {
			documents.add(documentUri);
			
			RefVisitor refVisitor = new RefVisitor(documentUri);
			refVisitor.visit(body);
			documentReferences.put(documentUri, refVisitor.docTargets);
			fileReferences.put(documentUri, refVisitor.fileTargets);
		}
		
		public void addFile(String fileUri) {
			files.add(fileUri);
		}
		
		@Override
		public String toString() {
			return debugString();
		}
		
		public String debugString() {
			StringBuilder sb = new StringBuilder();
			for (String d : documents) {
				sb.append(d + "\n");
				sb.append(" ==> docs: " + documentReferences.get(d) + "\n");
				sb.append(" ==> files: " + fileReferences.get(d) + "\n");
			}
			sb.append("\n");
			for (String f : files) {
				sb.append("file: " + f + "\n");
			}
			return sb.toString();
		}
	}
	
	public static class Orphans implements Serializable {
		private static final long serialVersionUID = 1L;
		private List<String> documents = new ArrayList<String>();
		private List<String> files = new ArrayList<String>();
		
		public List<String> getDocuments() {
			return documents;
		}
		public List<String> getFiles() {
			return files;
		}
	}
	
	/**
	 * Find orphans in the given LinkGraph by recursively scanning from
	 * rootDocument to all documents and files that can be reached.
	 */
	public static Orphans findOrphans(LinkGraph linkGraph, String rootDocument) {
		Set<String> visitedDocs = new HashSet<String>();
		Set<String> visitedFiles = new HashSet<String>();
		findOrphansRecur(visitedDocs, visitedFiles, linkGraph, rootDocument);
		
		Orphans orphans = new Orphans();
		for (String f : linkGraph.documents) {
			if (!visitedDocs.contains(f)) {
				orphans.documents.add(f);
			}
		}
		for (String f : linkGraph.files) {
			if (!visitedFiles.contains(f)) {
				orphans.files.add(f);
			}
		}
		return orphans;
	}
	
	/**
	 * Filter out orphans using the given pattern.
	 */
	public static Orphans filterOutOrphans(Orphans orphans, Pattern ignoreOrphans) {
		Orphans newOrphans = new Orphans();
		filterOrphans(orphans.documents, newOrphans.documents, ignoreOrphans);
		filterOrphans(orphans.files, newOrphans.files, ignoreOrphans);
		return newOrphans;
	}

	private static void filterOrphans(List<String> names, List<String> newNames, Pattern ignoreOrphans) {
		for (String name : names) {
			if (!ignoreOrphans.matcher(name).find()) {
				newNames.add(name);
			}
		}
	}

	private static void findOrphansRecur(Set<String> visitedDocs, Set<String> visitedFiles, LinkGraph linkGraph, String uri) {
		visitedDocs.add(uri);
		List<String> linkedDocuments = linkGraph.documentReferences.get(uri);
		if (linkedDocuments != null) {
			for (String doc : linkedDocuments) {
				if (!visitedDocs.contains(doc)) {
					findOrphansRecur(visitedDocs, visitedFiles, linkGraph, doc);
				}
			}
		}
		
		List<String> linkedFiles = linkGraph.fileReferences.get(uri);
		if (linkedFiles != null) {
			visitedFiles.addAll(linkedFiles);
		}
	}
	
	
	private static class RefVisitor extends ElementVisitor {
		private final List<String> docTargets = new ArrayList<String>();
		private final List<String> fileTargets = new ArrayList<String>();
		private final String documentDirUri;
		
		public RefVisitor(String documentUri) {
			this.documentDirUri = Uri.dirName(documentUri);
		}
		
		@Override
		protected void textRef(TextRef e) {
			try {
				docTargets.add(Uri.abs(Uri.join(documentDirUri, e.getTargetUri())));
			} catch (IllegalArgumentException ex) {
				// just ignore invalid references
			}
		}
		
		@Override
		protected void textA(TextA e) {
			addFileTarget(e.getHref().trim());
		}
		
		@Override
		protected void textImage(TextImage e) {
			addFileTarget(e.getSrc().trim());
		}

		private void addFileTarget(String realUrl) {
			String uri = DocHtmlEmitter.getLocalResourceUri(documentDirUri, realUrl);
			if (uri != null) {
				fileTargets.add(uri);
			}
		}
	}
}
