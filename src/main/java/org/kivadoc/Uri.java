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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Tools for manipulating KivaDoc Uris. The Uris are used
 * for site-specific links that may refer to other documents.
 */
public abstract class Uri {
	private static final Pattern SLASH = Pattern.compile("/");

	/**
	 * Normalize uri, i.e remove extraneous '.' and '..' path elements.
     * Throws IllegalArgumentException if the uri is malformed, e.g. contains '//' in it.
	 * @param uri
	 * @return
	 */
	public static String norm(String uri) {
		List<String> parts = splitUri(uri);
		int i = 0;
		while (i < parts.size()) {
			if (0 < i && i < parts.size() - 1 && parts.get(i).isEmpty()) {
				throw new IllegalArgumentException("Empty path element in uri: " + uri);
			}
			if (parts.get(i).equals(".")) {
				if (i == parts.size() - 1) {
					parts.set(i, "");
				} else {
					parts.remove(i);
				}
			} else if (i > 0 && parts.get(i).equals("..") && !parts.get(i - 1).isEmpty() && !parts.get(i - 1).equals("..")) {
				if (i == parts.size() - 1) {
					parts.remove(i - 1);
					parts.set(i - 1, "");
				} else {
					parts.remove(i - 1);
					parts.remove(i - 1);
				}
				i -= 1;
			} else {
				i += 1;
			}
		}
		return StringUtils.join(parts, '/');
	}

	private static ArrayList<String> splitUri(String uri) {
		return new ArrayList<String>(Arrays.asList(SLASH.split(uri, -1)));
	}
	
	/**
	 * Return a normalized absolute uri, or throw a IllegalArgumentException
     * if the uri is not a valid absolute uri.
	 * @param uri
	 * @throws IllegalArgumentException if the uri is not a valid absolute uri
	 * @return
	 */
	public static String abs(String uri) {
		if (!uri.startsWith("/")) {
			throw new IllegalArgumentException("Not an absolute uri: " + uri);
		}
		uri = norm(uri);
		if (uri.startsWith("/../")) {
			throw new IllegalArgumentException("Not an absolute uri: " + uri);
		}
		return uri;
	}
	
	public static String join2(String base, String rel) {
		if (rel.startsWith("/")) {
			return rel;
		}
		if (base.isEmpty()) {
			return norm(rel);
		}
		return norm(StringUtils.stripEnd(base, "/") + "/" + rel);
	}
	
	public static String joinList(List<String> uris) {
		String a = uris.get(0);
		for (int i = 1; i < uris.size(); i++) {
			a = join2(a, uris.get(i));
		}
		return a;
	}
	
	public static String join(String ... uris) {
		String a = uris[0];
		for (int i = 1; i < uris.length; i++) {
			a = join2(a, uris[i]);
		}
		return a;
	}
	
	public static String rel(String uri, String start) {
		List<String> pathList = splitUri(uri);
		List<String> startList = splitUri(start);
		int i = 0;
		while (i < pathList.size() && i < startList.size() && pathList.get(i).equals(startList.get(i))) {
			i++;
		}
		List<String> relList = new ArrayList<String>();
		for (int e = 0; e < startList.size() - i; e++) {
			if (!startList.get(e).equals(".") && !startList.get(e).isEmpty()) {
				relList.add("..");
			}
		}
		for (int e = i; e < pathList.size(); e++) {
			relList.add(pathList.get(e));
		}
		if (relList.isEmpty()) {
			return "";
		}
		return joinList(relList);
	}
	
	public static String[] splitExt(String uri) {
		String[] result = new String[2];
		int i = StringUtils.lastIndexOf(uri, '.');
		if (i <= StringUtils.lastIndexOf(uri, '/')) {
			result[0] = uri;
			result[1] = "";
		} else {
			result[0] = uri.substring(0, i);
			result[1] = uri.substring(i);
		}
		return result;
	}
	
	public static String ext(String uri) {
	    return splitExt(uri)[1];
	}
	
	/**
	 * Name of a file or a directory that the uri refers to.
	 * <pre>
	 * baseName("foo/bar") === "bar"
	 * baseName("foo/bar/") === "bar"
	 */
	public static String baseName(String uri) {
		String u = StringUtils.stripEnd(uri, "/");
	    int i = StringUtils.lastIndexOf(u, '/');
	    if (i >= 0) {
	        return u.substring(i + 1);
	    } else {
	        return u;
	    }
	}
	
	public static String title(String uri) {
	    return splitExt(baseName(uri))[0];
	}
	
	public static String dirName(String uri) {
	    int i = StringUtils.lastIndexOf(uri, '/');
	    if (i >= 0) {
	        return uri.substring(0, i + 1);
	    } else {
	        return "";
	    }
	}
	
	public static String dirOf(String uri) {
	    int i = StringUtils.lastIndexOf(StringUtils.stripEnd(uri, "/"), '/');
	    if (i >= 0) {
	        return uri.substring(0, i+1);
	    } else {
	        return "";
	    }
	}
}
