package com.wp.scrapie;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DriverTest {

	@Test
	public void test() throws IOException {
		Driver.main(new String[] { "-f", "src/test/js/justExit.js", "-o",
				"target/test.xml" });
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
				+ "<rss>\n" + "<channel>\n" + "    <item>\n"
				+ "        <key2>value2</key2>\n"
				+ "        <key2>value3</key2>\n" + "    </item>\n"
				+ "    <item>\n" + "        <key1>value1</key1>\n"
				+ "    </item>\n" + "</channel>\n" + "</rss>",
				FileUtils.readFileToString(new File("target/test.xml")));
	}

}
