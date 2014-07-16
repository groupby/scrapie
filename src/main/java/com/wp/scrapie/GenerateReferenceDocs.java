package com.wp.scrapie;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaMember;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
/**
 * @internal
 * @author will
 *
 */
public class GenerateReferenceDocs {

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
		File sourceDir = new File("src/main/java/com/wp/scrapie");
		if (!sourceDir.exists()) {
			throw new IllegalStateException("Could not find: "
					+ sourceDir.getAbsolutePath());
		}
		new File("dist/docs").mkdirs();

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
					String filename = createFilename("dist/docs", javaClass, "");
					if (LOG.isInfoEnabled()) {
						LOG.info("Creating: " + filename);
					}
					out = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(new File(filename)), "UTF-8"));
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
					prop = prop.replaceAll("\\bString\\b", "string");
					constructorString.append(prop.substring(
							prop.lastIndexOf(".") + 1).trim()
							+ (i + 1 < properties.length ? ", " : ""));
				}
				constructorString.append(")");
			}
		}
		return constructorString;
	}

	private static boolean isValid(JavaAnnotatedElement pElement) {
		if (isTagged(pElement, "deprecated") || isTagged(pElement, "internal")) {
			return false;
		}
		return ((JavaMember) pElement).isPublic();
	}

	private static boolean isTagged(JavaAnnotatedElement pConstructor,
			String tagName) {
		DocletTag tagByName = pConstructor.getTagByName(tagName);
		if (tagByName != null) {
			return true;
		}
		return false;
	}

	private static boolean hasConstructor(List<JavaConstructor> pConstructors) {
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
				pOut.write("- [" + javaMethod.getName()
						+ parameterListToString(javaMethod) + "](#"
						+ generateId(javaMethod) + ")\n");
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

	private static int generateId(JavaMethod javaMethod) {
		return (javaMethod.getName() + parameterListToString(javaMethod))
				.hashCode();
	}

	private static void writeMethodSignature(Writer pOut, JavaClass pJavaClass,
			JavaMethod pJavaMethod) throws IOException {
		if (pJavaMethod.getReturnType() != null
				&& !pJavaMethod.getReturns().getName().toString()
						.equals(pJavaClass.getName())
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
		pOut.write("------\n\n");
		writeParameterComments(pOut, pJavaMethod.getTagsByName("param"));
		writeReturns(pOut, pJavaMethod);
	}

	private static String formatReturn(JavaMethod pJavaMethod) {
		String full = pJavaMethod.getReturnType().getGenericCanonicalName();
		return full.replaceAll("java\\.lang\\.String", "string")
				.replaceAll("java\\.lang\\.Object", "object")
				.replaceAll("java\\.lang\\.", "")
				.replaceAll("java\\.util\\.", "")
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

	}

	private static String parameterListToString(JavaMethod pJavaMethod) {
		return squareToCurly(pJavaMethod.getParameters());
	}

	private static String squareToCurly(List<? extends Object> pList) {
		return pList.isEmpty() ? "()" : pList.toString().replaceAll("\\[", "(")
				.replaceAll("\\]", ")").replaceAll("\\bString\\b", "string");
	}

	private static String createFilename(String pOutputDir,
			JavaClass javaClass, String prefix) {
		return pOutputDir + "/" + prefix + javaClass.getName() + ".md";
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
