package com.wp.scrapie;

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
		server = new PseudoServer();
	}

	protected StringWriter run(String pTestScript)
			throws FileNotFoundException, IOException {
		Js js = new Js();
		StringWriter writer = new StringWriter();
		js.run(IOUtils.toString(new FileInputStream(pTestScript)).replaceAll(
				"####", String.valueOf(server.getConnectedPort())), writer);
		return writer;
	}

	
}
