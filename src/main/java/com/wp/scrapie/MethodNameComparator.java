package com.wp.scrapie;

import java.util.Comparator;

import com.thoughtworks.qdox.model.JavaMethod;
/**
 * @internal
 * @author will
 *
 */
final class MethodNameComparator implements Comparator<JavaMethod> {
	@Override
	public int compare(JavaMethod pO1, JavaMethod pO2) {
		int nameCompare = pO1.getName().compareTo(pO2.getName());
		if (nameCompare == 0) {
			return pO1.getParameters().toString()
					.compareTo(pO2.getParameters().toString());
		}
		return nameCompare;
	}
}