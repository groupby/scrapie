package com.wp.scrapie;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.junit.Test;

public class XmlWriterTest {

	@Test
	public void testJsonToXml() throws Exception {
		StringWriter sWriter = new StringWriter();
		XmlWriter writer = new XmlWriter(sWriter);
		writer.write("{\"k\":[\"l\", \"m\"]}");
		writer.close();
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
				+ "<rss>\n" + "<channel>\n" + "    <item>\n"
				+ "        <k>l</k>\n" + "        <k>m</k>\n" + "    </item>\n"
				+ "</channel>\n" + "</rss>", sWriter.getBuffer().toString());

	}

	@Test
	public void testNoSpaceInName() throws Exception {
		try {
			StringWriter sWriter = new StringWriter();
			XmlWriter writer = new XmlWriter(sWriter);
			writer.write("{\"k k\":[\"l\", \"m\"]}");
			writer.close();
			fail("should error out");
		} catch (Exception e) {
			// expected
		}

	}

}
