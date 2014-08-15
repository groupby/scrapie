package com.wp.scrapie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Before;

import com.meterware.pseudoserver.PseudoServer;

public class JsLoader {
	protected PseudoServer server;

	@Before
	public void before() {
		Emitter.setRecord(0);
		server = new PseudoServer();
		Emitter.setSourceDirectory(new File("target"));
	}

	protected StringWriter run(Emitter pEmitter, String pTestScript)
			throws FileNotFoundException, IOException {
		StringWriter writer = new StringWriter();
		pEmitter.run(IOUtils.toString(new FileInputStream(pTestScript))
				.replaceAll("####", String.valueOf(server.getConnectedPort())),
				writer);
		return writer;
	}

	protected String createWebPage(final String pTitle, String pBody) {
		return "<html><head>" + "<title>title " + pTitle + "</title>"
				+ "</head><body>" + pBody + "</body></html>";
	}

	protected int getPort() throws IOException {
		return server.getConnectedPort();
	}

}
