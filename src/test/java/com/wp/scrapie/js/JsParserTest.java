package com.wp.scrapie.js;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class JsParserTest {

	@Test
	public void testGetMethods() throws Exception {
		List<JsSignature> jsMethods = JsParser
				.getMethods("/**\n"
						+ " * <code>\n"
						+ " * Print a string at info level.\n"
						+ " * </code>\n"
						+ " * \n"
						+ " * @param pMessage\n"
						+ " *            The message to print out.\n"
						+ " * @internal\n"
						+ " */\n"
						+ "function print(pMessage) {\n"
						+ "	emitter.print(pMessage);\n"
						+ "}\n"
						+ "\n"
						+ "/**\n"
						+ " * <code>\n"
						+ " * Print the currently contexts root element and all children at info level.\n"
						+ " * </code>\n" + " */\n"
						+ "function printDocument() {\n"
						+ "	emitter.printDocument();\n" + "}\n");
		assertEquals(2, jsMethods.size());
		JsSignature print = jsMethods.get(0);
		assertEquals("print", print.getName());
		assertEquals("pMessage", print.getParams().get(0));
		assertEquals("Print a string at info level.", print.getComments());
		JsAnnotation param = print.getAnnotations().get(0);
		JsAnnotation internal = print.getAnnotations().get(1);
		assertEquals("internal", internal.getType());
			assertEquals("param", param.getType());
		assertEquals("pMessage", param.getName());
		assertEquals("The message to print out.", param.getComments());
	}

	@Test
	public void testGetSubMethods() throws Exception {
		String fullFile = "/**\n"
				+ " * <code>\n"
				+ " * Load a file at iterate through each line.\n"
				+ " * Each line is assumed to be a URL.  The file is loaded relative to the JavaScript file that \n"
				+ " * is calling the file iterator.\n"
				+ " * \n"
				+ " *     new FileIterator(\"idList.txt\").forEach(function(pContext){\n"
				+ " *         pContext.emit(\"title\", pContext.getJqText(\"title\"));\n"
				+ " *         pContext.flush();\n"
				+ " *     });\n"
				+ " *     \n"
				+ " * </code>\n"
				+ " * \n"
				+ " * @param pFilePath\n"
				+ " *            the file to be loaded in. The file path is relative to the calling\n"
				+ " *            JavaScript file.\n"
				+ " */\n"
				+ "var FileIterator = function(pFilePath) {\n"
				+ "\n"
				+ "	this.lineIterator = emitter.getLineIterator(pFilePath);\n"
				+ "\n"
				+ "	/**\n"
				+ "	 * <code>\n"
				+ "	 * Iterate though each line in the file, load the URL and pass that in to\n"
				+ "	 * the pDealWith function as a context object.\n"
				+ "	 * </code>\n"
				+ "	 * \n"
				+ "	 * @param pDealWith\n"
				+ "	 *            the method call to be called back after a document is loaded.\n"
				+ "	 */\n"
				+ "	this.forEach = function(pDealWith) {\n"
				+ "		while (this.lineIterator.hasNext() && emitter.keepGoing()) {\n"
				+ "			var url = this.lineIterator.nextLine();\n"
				+ "			emitter.load(url);\n"
				+ "			pDealWith(new EmitterWrapper(emitter));\n" + "		}\n"
				+ "		this.lineIterator.close();\n" + "	}\n" + "}";
		List<JsSignature> jsMethods = JsParser.getMethods(fullFile);
		List<JsSignature> jsConstructors = JsParser.getSignatures(fullFile);

		assertEquals(1, jsConstructors.size());
		assertEquals(1, jsMethods.size());
		JsSignature foreach = jsMethods.get(0);
		assertEquals("forEach", foreach.getName());
		assertEquals("pDealWith", foreach.getParams().get(0));
		assertEquals(
				"Iterate though each line in the file, load the URL and pass that in to\n" + 
				"the pDealWith function as a context object.",
				foreach.getComments());
		JsAnnotation dealWith = foreach.getAnnotations().get(0);
		assertEquals("param", dealWith.getType());
		assertEquals("pDealWith", dealWith.getName());
		assertEquals(
				"the method call to be called back after a document is loaded.",
				dealWith.getComments());

	}

	@Test
	public void testGetSignatures() throws Exception {
		List<JsSignature> signatures = JsParser
				.getSignatures("/**\n"
						+ " * <code>\n"
						+ " * Load a file at iterate through each line.\n"
						+ " * Each line is assumed to be a URL.  The file is loaded relative to the JavaScript file that \n"
						+ " * is calling the file iterator.\n"
						+ " * \n"
						+ " *     new FileIterator(\"idList.txt\").forEach(function(pContext){\n"
						+ " *         pContext.emit(\"title\", pContext.getJqText(\"title\"));\n"
						+ " *         pContext.flush();\n"
						+ " *     });\n"
						+ " *     \n"
						+ " * </code>\n"
						+ " * \n"
						+ " * @param pFilePath\n"
						+ " *            the file to be loaded in. The file path is relative to the calling\n"
						+ " *            JavaScript file.\n" + " * @internal\n"
						+ " */\n" + "var FileIterator = function(pFilePath) {");
		assertEquals(1, signatures.size());
		JsSignature sig = signatures.get(0);
		assertEquals("FileIterator", sig.getName());
		assertEquals(1, sig.getParams().size());
		assertEquals("pFilePath", sig.getParams().get(0));
		assertEquals(
				"Load a file at iterate through each line.\n"
						+ "Each line is assumed to be a URL.  The file is loaded relative to the JavaScript file that \n"
						+ "is calling the file iterator.\n"
						+ "\n"
						+ "    new FileIterator(\"idList.txt\").forEach(function(pContext){\n"
						+ "        pContext.emit(\"title\", pContext.getJqText(\"title\"));\n"
						+ "        pContext.flush();\n" + "    });",
				sig.getComments());
		assertEquals(2, sig.getAnnotations().size());
		JsAnnotation param = sig.getAnnotations().get(0);
		JsAnnotation internal = sig.getAnnotations().get(1);
		assertEquals("pFilePath", param.getName());
		assertEquals("param", param.getType());
		assertEquals(
				"the file to be loaded in. The file path is relative to the calling JavaScript file.",
				param.getComments());
		assertEquals("internal", internal.getType());
		assertEquals(null, internal.getComments());
		assertEquals(null, internal.getName());
	}

	@Test
	public void testBadCommentBlock() throws Exception {
		List<JsSignature> signatures = JsParser
				.getSignatures("/**\n"
						+ " * <code>\n"
						+ " * Load a file at iterate through each line.\n"
						+ " * Each line is assumed to be a URL.  The file is loaded relative to the JavaScript file that \n"
						+ " * is calling the file iterator.\n"
						+ " * \n"
						+ " *     new FileIterator(\"idList.txt\").forEach(function(pContext){\n"
						+ " *         pContext.emit(\"title\", pContext.getJqText(\"title\"));\n"
						+ " *         pContext.flush();\n"
						+ " *     });\n"
						+ " *     \n"
						+ " * </code>\n"
						+ " * \n"
						+ " * @param pFilePath\n"
						+ " *            the file to be loaded in. The file path is relative to the calling\n"
						+ " *            JavaScript file.\n" + " * @internal\n"
						+ " \n" + "var FileIterator = function(pFilePath) {");
		assertEquals(1, signatures.size());
		assertEquals(null, signatures.get(0).getComments());

	}
}
