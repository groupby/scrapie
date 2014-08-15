package com.wp.scrapie;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UrlPatternIteratorTest extends JsLoader {

	@Test
	public void test() throws FileNotFoundException, IOException {
		server.setResource("/index.html?id=0", createWebPage("0", ""));
		server.setResource("/index.html?id=1", createWebPage("1", ""));

		StringWriter writer = run(new Emitter(),
				"src/test/js/iterateGenerate.js");
		assertEquals("{\"url\":[\"http://localhost:" +
				getPort() +
				"/index.html?id=0\"]}\n" + 
				"{\"url\":[\"http://localhost:" +
				getPort() +
				"/index.html?id=1\"]}\n" + 
				"{\"url\":[\"http://localhost:" +
				getPort() +
				"/index.html?id=2\"]}\n",
				writer.getBuffer().toString());
	}

	@Test
	public void testNumeric() throws Exception {
		List<String> results = new ArrayList<>();
		UrlPatternIterator urlPatternIterator = new UrlPatternIterator(
				"http://example.com/index?p=[0,1]&t=x");
		while (urlPatternIterator.hasNext()) {
			results.add((String) urlPatternIterator.next());

		}
		assertEquals(
				"[http://example.com/index?p=0&t=x, http://example.com/index?p=1&t=x]",
				results.toString());
	}

	@Test
	public void testAlpha() throws Exception {
		List<String> results = new ArrayList<>();
		UrlPatternIterator urlPatternIterator = new UrlPatternIterator(
				"http://example.com/index?p=[az,bb]&t=x");
		while (urlPatternIterator.hasNext()) {
			results.add((String) urlPatternIterator.next());

		}
		assertEquals("[http://example.com/index?p=az&t=x, "
				+ "http://example.com/index?p=ba&t=x, "
				+ "http://example.com/index?p=bb&t=x]", results.toString());
	}

	@Test
	public void testAlpha2() throws Exception {
		UrlPatternIterator urlPatternIterator = new UrlPatternIterator(
				"http://example.com/index?p=[aaa,zzz]&t=x");
		int index = 0;
		while (urlPatternIterator.hasNext()) {
			String next = (String) urlPatternIterator.next();
			if (index == 0) {
				assertEquals("http://example.com/index?p=aaa&t=x", next);
			}
			if (index == 17576) {
				assertEquals("http://example.com/index?p=zzz&t=x", next);
			}
			index++;
		}
		assertEquals(17576, index);
	}

}
