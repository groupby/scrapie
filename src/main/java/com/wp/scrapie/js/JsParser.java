package com.wp.scrapie.js;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class JsParser {
	private final static Pattern SIGNATURE_PATTERN = Pattern
			.compile("var\\s+([A-Z][a-zA-Z]+)\\s*=\\s*function\\(([a-zA-Z ,]*)\\)");
	private final static Pattern METHOD_PATTERN = Pattern
			.compile("function\\s+([a-zA-Z0-9]+)\\(([a-zA-Z ,]*)\\)");
	private final static Pattern SUB_METHOD_PATTERN = Pattern
			.compile("this.([a-zA-Z0-9]+)\\s+=\\s+function\\(([a-zA-Z ,]*)\\)");
	
	public static List<JsSignature> getSignatures(String pFileContents) {
		return getSignatures(pFileContents, SIGNATURE_PATTERN);
	}

	private static List<JsSignature> getSignatures(String pFileContents,
			Pattern signaturePattern) {
		List<JsSignature> jsSignatures = new ArrayList<>();
		Matcher matcher = signaturePattern.matcher(pFileContents);
		while (matcher.find()) {
			JsSignature sig = new JsSignature();
			sig.setName(matcher.group(1));
			jsSignatures.add(sig);
			if (matcher.groupCount() == 2) {
				String paramString = matcher.group(2);
				String[] params = paramString.split(",");
				for (String param : params) {
					if (StringUtils.isNotBlank(param)) {
						sig.getParams().add(param.trim());
					}
				}
			}
			String wholeMatch = matcher.group(0);
			int index = pFileContents.indexOf(wholeMatch);
			for (int i = index; i > 0; i--) {
				String ch = pFileContents.substring(i - 2, i);
				if (ch.equals("*/")) {
					String commentString = processComment(pFileContents, i, sig);
					if (StringUtils.isNotBlank(commentString)) {
						sig.setComments(commentString);
					}
				} else if (Character.isWhitespace(ch.charAt(1))) {
					continue;
				} else {
					break;
				}
			}
		}
		return jsSignatures;
	}

	private static String processComment(String pFileContents, int pI,
			JsSignature pSig) {
		for (int i = pI; i > 0; i -= 1) {
			String ch = pFileContents.substring(i - 3, i);
			if (ch.equals("/**")) {
				return processComment(pFileContents, i, pI, pSig);
			}
		}
		throw new IllegalStateException("Couldn't find closing comment block");
	}

	private static String processComment(String pFileContents, int pI, int pI2,
			JsSignature pSig) {
		String comment = pFileContents.substring(pI, pI2);
		StringBuilder cleanedComment = new StringBuilder();
		String[] codeBlocks = comment.split("</*code>");
		if (codeBlocks.length != 3) {
			return "";
		}
		String[] lines = codeBlocks[1].split("\n");
		for (String line : lines) {
			if (StringUtils.isNotBlank(line)) {
				String[] ba = line.split("\\s*\\* ");
				if (ba.length == 2) {
					cleanedComment.append(ba[1]);
				}
				cleanedComment.append("\n");
			}
		}
		lines = codeBlocks[2].split("\n");

		JsAnnotation ann = null;
		for (int i = 0; i < lines.length - 1; i++) {
			String line = lines[i];
			if (line.contains("@")) {
				addAnnotation(pSig, ann);
				ann = new JsAnnotation();
				int at = line.indexOf("@");
				String propLine = line.substring(at + 1);
				String[] typeNameComment = propLine.split("\\s+", 3);
				if (typeNameComment.length == 0) {
					ann = null;
					continue;
				}
				if (typeNameComment.length > 0) {
					ann.setType(typeNameComment[0]);
				}
				if (typeNameComment.length > 1) {
					ann.setName(typeNameComment[1]);
				}
				if (typeNameComment.length > 2
						&& StringUtils.isNotBlank(typeNameComment[2])) {
					ann.setComments(typeNameComment[2].trim());
				}
			} else if (ann != null) {
				ann.setComments((ann.getComments() == null ? "" : ann
						.getComments().trim() + " ")
						+ line.split("\\s*\\*")[1].trim());
			}
		}
		addAnnotation(pSig, ann);
		return cleanedComment.toString().trim();
	}

	private static void addAnnotation(JsSignature pSig, JsAnnotation ann) {
		if (ann != null) {
			pSig.getAnnotations().add(ann);
		}
	}

	public static List<JsSignature> getMethods(String pFileContents) {
		List<JsSignature> methods = getSignatures(pFileContents, METHOD_PATTERN);
		methods.addAll(getSignatures(pFileContents, SUB_METHOD_PATTERN));
		return methods;
	}
}
