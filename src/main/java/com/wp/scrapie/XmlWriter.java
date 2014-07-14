package com.wp.scrapie;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class XmlWriter extends Writer {

	private static final Logger LOG = Logger.getLogger(XmlWriter.class);
	private Writer out = null;

	public XmlWriter(Writer pOut) throws IOException {
		out = pOut;
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<rss>\n"
				+ "<channel>\n");
	}

	@Override
	public void write(String pStr) throws IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(pStr);
		}
		if (pStr == null || pStr.trim().length() == 0) {
			return;
		}
		JSONObject o = new JSONObject(pStr);
		String[] names = JSONObject.getNames(o);
		if (names != null) {
			for (String name : names) {
				if (!name.matches("[a-zA-Z]+[a-zA-Z0-9]*")) {
					throw new RuntimeException("Name badly formed for xml "
							+ name);
				}
			}
		}
		String xml = org.json.XML.toString(o);
		out.write("    <item>\n        " + xml.replaceAll("><", ">\n        <")
				+ "\n    </item>\n");
	}

	@Override
	public void close() throws IOException {
		out.write("</channel>\n" + "</rss>");
		out.flush();
		out.close();

	}

	@Override
	public void write(char[] pCbuf, int pOff, int pLen) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() throws IOException {
		out.flush();

	}

}
