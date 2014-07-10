package com.wp.scrapie;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Test;

import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;

public class GroceryGatewaySmokeTest extends JsLoader {

	@Test
	public void testGroceryGateway() throws Exception {
		setupLogin();
		setupTestFiles();
		StringWriter writer = run("src/test/js/groceryGateway.js");
		StringWriter newWriter = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		String[] rows = writer.getBuffer().toString().split("\n");
		for (String row : rows) {
			ObjectWriter pretty = mapper.writer().withDefaultPrettyPrinter();
			pretty.writeValue(newWriter,
					mapper.readValue(row.trim(), Map.class));
			newWriter.append("\n");
		}

		System.out.println(newWriter.toString());
	}

	private void setupTestFiles() throws IOException {
		File[] listFiles = new File("src/test/groceryGatewayFiles/")
				.listFiles();
		for (File file : listFiles) {
			server.setResource("Product/?n=4294961273&p=" + file.getName(),
					FileUtils.readFileToString(file));
		}
	}

	private void setupLogin() {
		server.setResource("/Login/Login.aspx", new PseudoServlet() {
			@Override
			public WebResource getPostResponse() throws IOException {
				return new WebResource("");
			}
		});
	}

}
