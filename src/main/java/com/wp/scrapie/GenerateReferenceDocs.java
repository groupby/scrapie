package com.wp.scrapie;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.wp.scrapie.js.JsAnnotation;
import com.wp.scrapie.js.JsParser;
import com.wp.scrapie.js.JsSignature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @internal
 * @author will.warren@groupbyinc.com
 * 
 */
public class GenerateReferenceDocs {

	private static final String DIST_DOCS = "./src/main/docs/";
	private static final Logger LOG = Logger
			.getLogger(GenerateReferenceDocs.class);

	public static void main(String[] args) throws IOException {
		generate();
	}

	private static List<JavaClass> getJavaSource(File sourceFile)
			throws IOException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		JavaSource source = builder.addSource(sourceFile);
		List<JavaClass> classes = source.getClasses();
		return classes;
	}

	public static void generate() throws IOException {
		generateJava();
		generateJavaScript();
	}

	public static void generateJavaScript() throws IOException {
		File sourceDir = new File("src/main/resources/");
		if (!sourceDir.exists()) {
			throw new IllegalStateException("Could not find: "
					+ sourceDir.getAbsolutePath());
		}
		new File(DIST_DOCS).mkdirs();
		File[] sourceFiles = getJsFiles(sourceDir);
		continueFile: for (int m = 0; m < sourceFiles.length; m++) {
			Writer out = null;
			try {
				String filename = createFilename(DIST_DOCS,
						sourceFiles[m].getName(), "");
				if (LOG.isInfoEnabled()) {
					LOG.info("Creating: " + filename);
				}
				String fileContents = FileUtils
						.readFileToString(sourceFiles[m]);

				List<JsSignature> methods = JsParser.getMethods(fileContents);
				List<JsSignature> constructors = JsParser
						.getSignatures(fileContents);
				if (!constructors.isEmpty()) {
					for (JsSignature con : constructors) {
						if (!con.getAnnotations().isEmpty()) {
							List<JsAnnotation> anns = con.getAnnotations();
							for (JsAnnotation ann : anns) {
								if (ann.getType().equals("internal")) {
									if (LOG.isDebugEnabled()) {
										LOG.debug("skipping");
									}
									continue continueFile;
								}
							}
						}
					}
				}
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(new File(filename)), "UTF-8"));
				out.write("![Emitter](src/main/images/sheepVeryVerySmall.png) ");
				out.write(sourceFiles[m].getName() + "\n");
				out.write("=====" + "\n\n");
				if (!methods.isEmpty()) {
					// Collections.sort(methods, new MethodNameComparator());
					writeJsToc(out, constructors, methods);
					writeJsConstructors(out, constructors);
					for (JsSignature javaMethod : methods) {
						if (isJsValid(javaMethod)) {
							writeJsMethodSignature(out, javaMethod);
							writeJsComment(out, javaMethod.getComments());
						}
					}
				}
			} finally {
				IOUtils.closeQuietly(out);
			}

		}
	}

	private static File[] getJsFiles(File pSourceDir) {
		return pSourceDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pPathname) {
				return pPathname.getName().endsWith(".js");
			}
		});
	}

	private static void generateJava() throws IOException,
			UnsupportedEncodingException, FileNotFoundException {
		File sourceDir = new File("src/main/java/com/wp/scrapie");
		if (!sourceDir.exists()) {
			throw new IllegalStateException("Could not find: "
					+ sourceDir.getAbsolutePath());
		}
		new File(DIST_DOCS).mkdirs();

		File[] sourceFiles = getJavaFiles(sourceDir);
		for (int m = 0; m < sourceFiles.length; m++) {
			List<JavaClass> classes = getJavaSource(sourceFiles[m]);
			for (int j = 0; j < classes.size(); j++) {
				JavaClass javaClass = classes.get(j);
				if (javaClass.getTagByName("internal") != null) {
					continue;
				}

				Writer out = null;
				try {
					String filename = createFilename(DIST_DOCS, javaClass, "");
					if (LOG.isInfoEnabled()) {
						LOG.info("Creating: " + filename);
					}
					out = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(new File(filename)), "UTF-8"));
					out.write("![Emitter](src/main/images/sheepVeryVerySmall.png) ");
					out.write(javaClass.getName() + "\n");
					out.write("=====" + "\n\n");
					if (javaClass.getComment() != null) {
						writeComment(out, javaClass.getComment());
					}

					List<JavaMethod> methods = javaClass.getMethods();
					List<JavaConstructor> constructors = javaClass
							.getConstructors();
					if (!methods.isEmpty()) {
						Collections.sort(methods, new MethodNameComparator());
						writeToc(out, constructors, methods);
						writeConstructors(out, constructors);
						for (JavaMethod javaMethod : methods) {
							if (isValid(javaMethod)) {
								writeMethodSignature(out, javaClass, javaMethod);
								writeComment(out, javaMethod.getComment());
							}
						}
					}
				} finally {
					IOUtils.closeQuietly(out);
				}
			}

		}
	}

	private static void writeComment(Writer out, String comment)
			throws IOException {
		if (comment != null) {
			int startCode = comment.indexOf("<code>");
			int endCode = comment.contains("JSON Reference") ? comment
					.indexOf("JSON Reference") : comment.indexOf("</code>");
			if (startCode != -1 && endCode != -1) {
				out.write(comment.substring(startCode + 6, endCode) + "\n\n");
			}

		}
		out.write("\n");
	}

	private static void writeJsComment(Writer out, String comment)
			throws IOException {
		out.write(comment + "\n\n");
	}

	private static final Pattern IN_PARENS = Pattern
			.compile("public\\s+([^\\(]+)\\(([^\\)]*)");

	private static void writeConstructors(Writer pOut,
			List<JavaConstructor> pConstructors) throws IOException {
		if (!hasConstructor(pConstructors)) {
			return;
		}

		for (JavaConstructor constructor : pConstructors) {
			if (isValid(constructor)) {
				StringBuilder constructorSig = createConstructorSignature(constructor);
				if (StringUtils.isNotBlank(constructorSig.toString())) {
					String[] nameAndParams = constructorSig.toString().split(
							"\\(", 2);
					if (nameAndParams.length != 2) {
						return;
					}
					pOut.write("<a name=\""
							+ constructorSig.toString().hashCode() + "\">");
					pOut.write(nameAndParams[0]);
					pOut.write("</a>(");
					pOut.write(nameAndParams[1]);
					pOut.write("\n-----\n\n");
					writeParameterComments(pOut,
							constructor.getTagsByName("param"));
					writeComment(pOut, constructor.getComment());
				}

			}
		}

	}

	private static void writeJsConstructors(Writer pOut,
			List<JsSignature> pConstructors) throws IOException {
		if (!hasConstructor(pConstructors)) {
			return;
		}

		for (JsSignature constructor : pConstructors) {
			if (isJsValid(constructor)) {
				StringBuilder constructorSig = createJsConstructorSignature(constructor);
				if (StringUtils.isNotBlank(constructorSig.toString())) {
					String[] nameAndParams = constructorSig.toString().split(
							"\\(", 2);
					if (nameAndParams.length != 2) {
						return;
					}
					pOut.write("<a name=\""
							+ constructorSig.toString().hashCode() + "\">");
					pOut.write(nameAndParams[0]);
					pOut.write("</a>(");
					pOut.write(nameAndParams[1]);
					pOut.write("\n-----\n\n");
					writeJsParameterComments(pOut, constructor.getAnnotations());
					writeJsComment(pOut, constructor.getComments());
				}

			}
		}

	}

	private static StringBuilder createConstructorSignature(
			JavaConstructor constructor) {
		StringBuilder constructorString = new StringBuilder();
		Pattern p = Pattern.compile("public\\s*" + constructor.getName()
				+ "[^\\)]*");

		String codeBlock = constructor.getCodeBlock();
		codeBlock = codeBlock.replaceAll("\n", "");
		Matcher matcher = p.matcher(codeBlock);
		if (matcher.find()) {
			String methodSignature = matcher.group(0);
			Matcher parenMatcher = IN_PARENS.matcher(methodSignature);
			if (parenMatcher.find()) {
				constructorString.append(parenMatcher.group(1) + "(");
				String[] properties = parenMatcher.group(2).split(",");
				for (int i = 0; i < properties.length; i++) {
					String prop = properties[i];
					// prop = prop.replaceAll("\\bString\\b", "string");
					constructorString.append(prop.substring(
							prop.lastIndexOf(".") + 1).trim()
							+ (i + 1 < properties.length ? ", " : ""));
				}
				constructorString.append(")");
			}
		}
		return constructorString;
	}

	private static StringBuilder createJsConstructorSignature(
			JsSignature constructor) {
		StringBuilder constructorString = new StringBuilder();

		constructorString.append(constructor.getName());
		constructorString.append("(");
		for (String param : constructor.getParams()) {
			constructorString.append(param).append(", ");
		}
		if (!constructor.getParams().isEmpty()) {
			constructorString.delete(constructorString.length() - 2,
					constructorString.length());
		}
		constructorString.append(")");
		return constructorString;
	}

	private static boolean isValid(JavaAnnotatedElement pElement) {
		if (isTagged(pElement, "deprecated") || isTagged(pElement, "internal")) {
			return false;
		}
		return ((JavaMember) pElement).isPublic();
	}

	private static boolean isJsValid(JsSignature pElement) {
		if (isJsTagged(pElement, "deprecated")
				|| isJsTagged(pElement, "internal")) {
			return false;
		}
		return true;
	}

	private static boolean isTagged(JavaAnnotatedElement pConstructor,
			String tagName) {
		DocletTag tagByName = pConstructor.getTagByName(tagName);
		if (tagByName != null) {
			return true;
		}
		return false;
	}

	private static boolean isJsTagged(JsSignature pConstructor, String tagName) {
		List<JsAnnotation> tags = pConstructor.getAnnotations();
		for (JsAnnotation jsAnnotation : tags) {
			if (jsAnnotation.getType().equals(tagName)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasConstructor(List<? extends Object> pConstructors) {
		return pConstructors != null && !pConstructors.isEmpty();
	}

	private static void writeToc(Writer pOut,
			List<JavaConstructor> pConstructors, List<JavaMethod> pMethods)
			throws IOException {
		if (hasValid(pConstructors)) {
			pOut.write("\n###Constructors\n");
		}

		for (JavaConstructor javaConstructor : pConstructors) {
			if (isValid(javaConstructor)) {
				String constructorSignature = createConstructorSignature(
						javaConstructor).toString();
				pOut.write("- [" + constructorSignature + "](#"
						+ constructorSignature.hashCode() + ")\n");
			}
		}

		if (hasValid(pMethods)) {
			pOut.write("\n###Methods\n");
		}
		for (JavaMethod javaMethod : pMethods) {
			if (isValid(javaMethod)) {
				pOut.write("- ["
						+ javaMethod.getName()
						+ parameterListToString(javaMethod)
						+ "](#"
						+ generateId(javaMethod)
						+ ") "
						+ (javaMethod.getReturnType().toString().equals("void") ? ""
								: " returns "
										+ formatReturn(javaMethod
												.getReturnType().toString()))
						+ "\n");
			}
		}
		pOut.write("\n\n");

	}

	private static void writeJsToc(Writer pOut,
			List<JsSignature> pConstructors, List<JsSignature> pMethods)
			throws IOException {
		if (hasJsValid(pConstructors)) {
			pOut.write("\n###Constructors\n");
		}

		for (JsSignature javaConstructor : pConstructors) {
			if (isJsValid(javaConstructor)) {
				String constructorSignature = createJsConstructorSignature(
						javaConstructor).toString();
				pOut.write("- [" + constructorSignature + "](#"
						+ constructorSignature.hashCode() + ")\n");
			}
		}

		if (hasJsValid(pMethods)) {
			pOut.write("\n###Methods\n");
		}
		for (JsSignature javaMethod : pMethods) {
			if (isJsValid(javaMethod)) {
				pOut.write("- [" + javaMethod.getName()
						+ jsParameterListToString(javaMethod.getParams())
						+ "](#" + generateJsId(javaMethod) + ") " + "\n");
			}
		}
		pOut.write("\n\n");

	}

	private static boolean hasValid(
			List<? extends JavaAnnotatedElement> pConstructors) {
		for (JavaAnnotatedElement javaConstructor : pConstructors) {
			if (isValid(javaConstructor)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasJsValid(List<JsSignature> pConstructors) {
		for (JsSignature jsConstructor : pConstructors) {
			if (isJsValid(jsConstructor)) {
				return true;
			}
		}
		return false;
	}

	private static int generateId(JavaMethod javaMethod) {
		return (javaMethod.getName() + parameterListToString(javaMethod))
				.hashCode();
	}

	private static int generateJsId(JsSignature javaMethod) {
		return (javaMethod.getName() + jsParameterListToString(javaMethod
				.getParams())).hashCode();
	}

	private static void writeMethodSignature(Writer pOut, JavaClass pJavaClass,
			JavaMethod pJavaMethod) throws IOException {
		pOut.write("#### ");
		if (pJavaMethod.getReturnType() != null
				&& !pJavaMethod.getReturnType().toString().equals("void")) {
			pOut.write("<span style=\"font-size:12px;color:#AAAAAA\">"
					+ formatReturn(pJavaMethod) + "</span> ");
		}
		pOut.write("<a style=\"font-size:16px;\" name=\""
				+ generateId(pJavaMethod) + "\">" + pJavaMethod.getName()
				+ "</a>");
		pOut.write("<span style=\"font-size:16px;\">"
				+ parameterListToString(pJavaMethod) + "</span>");
		pOut.write("\n");
		writeParameterComments(pOut, pJavaMethod.getTagsByName("param"));
		writeReturns(pOut, pJavaMethod);
	}

	private static void writeJsMethodSignature(Writer pOut,
			JsSignature pJavaMethod) throws IOException {
		pOut.write("#### ");

		pOut.write("<a style=\"font-size:16px;\" name=\""
				+ generateJsId(pJavaMethod) + "\">" + pJavaMethod.getName()
				+ "</a>");
		pOut.write("<span style=\"font-size:16px;\">"
				+ jsParameterListToString(pJavaMethod.getParams()) + "</span>");
		pOut.write("\n");
		writeJsParameterComments(pOut, pJavaMethod.getAnnotations());
	}

	private static String formatReturn(JavaMethod pJavaMethod) {
		String full = pJavaMethod.getReturnType().getGenericCanonicalName();
		return formatReturn(full);
	}

	private static String formatReturn(String pFull) {
		return pFull.replaceAll("java\\.lang\\.", "")
				.replaceAll("java\\.util\\.", "")
				.replaceAll("com\\.wp\\.scrapie\\.", "")
				.replaceAll("com\\.gbi\\.gsa\\.model\\.", "")
				.replaceAll("com\\.gbi\\.gsa\\.", "").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	private static void writeReturns(Writer pOut, JavaMethod pJavaMethod)
			throws IOException {
		DocletTag returns = pJavaMethod.getTagByName("return");
		if (returns != null && StringUtils.isNotBlank(returns.getValue())) {
			pOut.write("- <b>returns</b>: " + returns.getValue() + "\n");
		}
	}

	private static void writeParameterComments(Writer pOut,
			List<DocletTag> pParamTags) throws IOException {
		if (pParamTags == null || pParamTags.isEmpty()) {
			return;
		}

		for (DocletTag docletTag : pParamTags) {
			String[] value = docletTag.getValue().split("[\\s]", 2);

			pOut.write("- <b>" + value[0] + "</b>: "
					+ (value.length == 2 ? value[1] : "") + "\n");
		}
		pOut.write("\n");
	}

	private static void writeJsParameterComments(Writer pOut,
			List<JsAnnotation> pParamTags) throws IOException {
		if (pParamTags == null || pParamTags.isEmpty()) {
			return;
		}

		for (JsAnnotation docletTag : pParamTags) {
			if (docletTag.getType().equals("param")) {
				pOut.write("- <b>"
						+ docletTag.getName()
						+ "</b>: "
						+ (StringUtils.isNotBlank(docletTag.getComments()) ? docletTag
								.getComments() : "") + "\n");
			}
		}

		pOut.write("\n");
	}

	private static String parameterListToString(JavaMethod pJavaMethod) {
		return squareToCurly(pJavaMethod.getParameters());
	}

	private static String jsParameterListToString(List<String> pParams) {
		return "(" + pParams.toString().replaceAll("[\\[\\]]", "") + ")";
	}

	private static String squareToCurly(List<JavaParameter> pList) {
		StringBuilder result = new StringBuilder("(");
		for (int i = 0; i < pList.size(); i++) {
			JavaParameter param = pList.get(i);
			result.append(param.getType().toString()
					+ (param.isVarArgs() ? "... " : " "));
			result.append(param.getName());
			if (i < pList.size() - 1) {
				result.append(", ");
			}
		}
		result.append(")");
		return formatReturn(result.toString());
		// return pList.isEmpty() ? "()" : pList.toString().replaceAll("\\[",
		// "(")
		// .replaceAll("\\]", ")");
	}

	private static String createFilename(String pOutputDir,
			JavaClass javaClass, String prefix) {
		return pOutputDir + prefix + javaClass.getName() + ".md";
	}

	private static String createFilename(String pOutputDir, String pFilename,
			String prefix) {
		return pOutputDir + prefix + pFilename + ".md";
	}

	private static File[] getJavaFiles(File sourceDir) {
		File[] sourceFiles = sourceDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pArg0) {
				return pArg0.getName().endsWith(".java");
			}
		});
		return sourceFiles;
	}

}
