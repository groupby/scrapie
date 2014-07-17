package com.wp.scrapie;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

public class JsTest extends JsLoader {

	@Rule
	public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties(
			"workingDir");

	@Test
	public void testSimple() throws Exception {
		server.setResource("/index.html?id=0", createWebPage("0", ""));
		server.setResource("/index.html?id=1", createWebPage("1", ""));
		StringWriter writer = run(new Js(), "src/test/js/iterateLow.js");
		assertEquals("{\"title\":[\"title 0\"]}\n"
				+ "{\"title\":[\"title 1\"]}\n", writer.getBuffer().toString());
	}

	@Test
	public void testRecord() throws Exception {
		System.setProperty("workingDir", "src/test/java/");
		server = null;
		Js.setRecordCount(0);
		Js.setRecord(1);
		Js.setSourceFileName("iterateLow.js");
		Js js = new Js();
		StringWriter writer = new StringWriter();
		js.run(IOUtils.toString(
				new FileInputStream("src/test/js/iterateLow.js")).replaceAll(
				"####", "80"), writer);
		assertEquals("{\"title\":[\"title 0\"]}\n", writer.getBuffer()
				.toString());
		Js.setRecord(2);
		Js.setRecordCount(0);
		js = new Js();
		writer = new StringWriter();
		js.run(IOUtils.toString(
				new FileInputStream("src/test/js/iterateLow.js")).replaceAll(
				"####", "80"), writer);
		assertEquals("{\"title\":[\"title 0\"]}\n"
				+ "{\"title\":[\"title 1\"]}\n", writer.getBuffer().toString());
	}

	@Test
	public void testEmit() throws Exception {
		Js test = new Js();
		Js.setWriter(new StringWriter());
		test.emit("key1", "value1");
		Js.flush();
		assertEquals("{\"key1\":[\"value1\"]}\n", Js.getWriter().toString());
	}

	@Test
	public void testEmitForWorkingId() throws Exception {
		Js test = new Js();
		test.setWorkingId("someId");
		Js.setWriter(new StringWriter());
		test.emitForWorkingId("key1", "value1");
		Js.flush();
		assertEquals("{\"key1\":[\"value1\"]}\n", Js.getWriter().toString());
	}

	@Test
	public void testEmitForWorkingIdParentContext() throws Exception {
		Js test = new Js();
		Js parent = new Js();
		test.setParent(parent);
		parent.setWorkingId("bob");
		StringWriter writer = new StringWriter();
		Js.setWriter(writer);
		test.emitForWorkingId("key1", "value1");
		parent.emitForWorkingId("key2", "value2");
		Js.flush();
		assertEquals("{\"key1\":[\"value1\"],\"key2\":[\"value2\"]}\n", Js
				.getWriter().toString());
	}

	@Test
	public void testHigh() throws Exception {
		createListPages();
		server.setResource("/detail.jsp?id=abc",
				createWebPage("0", "<div id='price'>$29.99</div>"));
		server.setResource("/detail.jsp?id=bcd",
				createWebPage("0", "<div id='price'>$39.99</div>"));
		server.setResource("/detail.jsp?id=cde",
				createWebPage("0", "<div id='price'>$49.99</div>"));
		server.setResource("/detail.jsp?id=def",
				createWebPage("0", "<div id='price'>$59.99</div>"));
		StringWriter writer = run(new Js(), "src/test/js/iterateHigh.js");
		assertEquals(
				"{\"id\":[\"abc\"],\"price\":[\"$29.99\"],\"title\":[\"link 1\"]}\n"
						+ "{\"id\":[\"bcd\"],\"price\":[\"$39.99\"],\"title\":[\"link 2\"]}\n"
						+ "{\"id\":[\"cde\"],\"price\":[\"$49.99\"],\"title\":[\"link 3\"]}\n"
						+ "{\"id\":[\"def\"],\"price\":[\"$59.99\"],\"title\":[\"link 4\"]}\n",
				writer.getBuffer().toString());

	}

	@Test
	public void testMedium() throws Exception {
		createListPages();
		StringWriter writer = run(new Js(), "src/test/js/iterateMedium.js");
		assertEquals("{\"id\":[\"abc\"],\"title\":[\"link 1\"]}\n"
				+ "{\"id\":[\"bcd\"],\"title\":[\"link 2\"]}\n"
				+ "{\"id\":[\"cde\"],\"title\":[\"link 3\"]}\n"
				+ "{\"id\":[\"def\"],\"title\":[\"link 4\"]}\n", writer
				.getBuffer().toString());

	}

	private void createListPages() {
		server.setResource(
				"/list?page=0",
				createWebPage(
						"0",
						"<div class='item'><a href='/detail.jsp?id=abc'>link 1</a></div>"
								+ "<div class='item'><a href='/detail.jsp?id=bcd'>link 2</a></div>"));
		server.setResource(
				"/list?page=1",
				createWebPage(
						"1",
						"<div class='item'><a href='/detail.jsp?id=cde'>link 3</a></div>"
								+ "<div class='item'><a href='/detail.jsp?id=def'>link 4</a></div>"));
	}

	private String createWebPage(final String pTitle, String pBody) {
		return "<html><head>" + "<title>title " + pTitle + "</title>"
				+ "</head><body>" + pBody + "</body></html>";
	}
}
