package com.wp.scrapie;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @internal
 * @author user
 * 
 */
public class UrlPatternIteratorNumeric implements Iterator<String>,
		UrlPatternIteratorGeneric {
	private String patternStart = null;
	private String patternEnd = null;
	private long indexStart = 0;
	private long indexEnd = 0;
	private long index = 0;
	private final static Pattern NUMERIC_PATTERN = Pattern
			.compile("(\\[([0-9]+),([0-9]+)\\])");

	public static boolean is(String pPattern) {
		return NUMERIC_PATTERN.matcher(pPattern).find();
	}

	public UrlPatternIteratorNumeric(String pPattern) {
		Matcher numericMatcher = NUMERIC_PATTERN.matcher(pPattern);
		if (numericMatcher.find()) {
			int patternStartIndex = numericMatcher.start(1);
			int patternEndIndex = numericMatcher.end(1);
			indexStart = new Long(numericMatcher.group(2));
			index = indexStart;
			indexEnd = new Long(numericMatcher.group(3));
			patternStart = pPattern.substring(0, patternStartIndex);
			patternEnd = pPattern.substring(patternEndIndex);
		} else {
			throw new IllegalStateException(
					"Tried to make a numeric pattern generator from this pattern: "
							+ pPattern);
		}
	}

	@Override
	public boolean hasNext() {
		return index <= indexEnd;
	}

	@Override
	public String next() {
		StringBuilder result = new StringBuilder(patternStart);
		result.append(index);
		result.append(patternEnd);
		index++;
		return result.toString();
	}

	@Override
	public void remove() {
		throw new IllegalStateException("not implemented");

	}

}
