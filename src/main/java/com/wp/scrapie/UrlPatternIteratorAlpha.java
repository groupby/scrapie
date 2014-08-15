package com.wp.scrapie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @internal
 * @author will.warren@groupbyinc.com
 * 
 */
public class UrlPatternIteratorAlpha implements Iterator<String>,
		UrlPatternIteratorGeneric {
	private String patternStart = null;
	private String patternEnd = null;
	private List<Character> indexStart = new ArrayList<>();
	private String indexEnd = null;
	private boolean more = true;

	private final static Pattern ALPHA_PATTERN = Pattern
			.compile("(\\[([a-z]+),([a-z]+)\\])");

	public static boolean is(String pPattern) {
		return ALPHA_PATTERN.matcher(pPattern).find();
	}

	public UrlPatternIteratorAlpha(String pPattern) {
		Matcher numericMatcher = ALPHA_PATTERN.matcher(pPattern);
		if (numericMatcher.find()) {
			int patternStartIndex = numericMatcher.start(1);
			int patternEndIndex = numericMatcher.end(1);
			indexStart = chop(numericMatcher.group(2));
			indexEnd = numericMatcher.group(3);
			patternStart = pPattern.substring(0, patternStartIndex);
			patternEnd = pPattern.substring(patternEndIndex);
		} else {
			throw new IllegalStateException(
					"Tried to make a alpha pattern generator from this pattern: "
							+ pPattern);
		}
	}

	private List<Character> chop(String pGroup) {
		List<Character> result = new ArrayList<>();
		char[] chars = pGroup.toCharArray();
		for (char c : chars) {
			result.add(c);
		}
		return result;
	}

	@Override
	public boolean hasNext() {
		return more;
	}

	private String unchop() {
		StringBuilder result = new StringBuilder();
		for (Character character : indexStart) {
			result.append(character.toString());
		}
		return result.toString();
	}

	@Override
	public String next() {
		StringBuilder result = new StringBuilder(patternStart).append(unchop())
				.append(patternEnd);
		if (unchop().equals(indexEnd)) {
			more = false;
		}
		incrementIndex(indexStart.size());
		return result.toString();
	}

	private void incrementIndex(int pIndex) {
		if (pIndex == 0) {
			return;
		}
		char nextCharacter = nextCharacter(pIndex);
		indexStart.set(pIndex - 1, nextCharacter);
		if (nextCharacter == 'a') {
			incrementIndex(pIndex - 1);
		}
	}

	private char nextCharacter(int pIndex) {
		char possibleNew = (char) (indexStart.get(pIndex - 1).charValue() + 1);
		if (possibleNew == '{') {
			return 'a';
		}
		return possibleNew;
	}

	@Override
	public void remove() {
		throw new IllegalStateException("not implemented");
	}

}
