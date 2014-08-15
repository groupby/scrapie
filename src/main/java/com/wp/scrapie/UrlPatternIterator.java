package com.wp.scrapie;

import java.util.Iterator;

/**
 * @internal
 * @author user
 * 
 */
public class UrlPatternIterator implements Iterator<String> {

	private UrlPatternIteratorGeneric delegate = null;

	public UrlPatternIterator(String pPattern) {
		if (UrlPatternIteratorNumeric.is(pPattern)) {
			delegate = new UrlPatternIteratorNumeric(pPattern);
		} else if (UrlPatternIteratorAlpha.is(pPattern)) {
			delegate = new UrlPatternIteratorAlpha(pPattern);
		} else {
			throw new IllegalStateException(
					"Couldn't find a generator for this pattern: " + pPattern);
		}
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public String next() {
		return delegate.next();
	}

	@Override
	public void remove() {
		throw new IllegalStateException("not implemented");
	}

}
