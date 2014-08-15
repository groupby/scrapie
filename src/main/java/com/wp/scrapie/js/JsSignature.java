package com.wp.scrapie.js;

import java.util.ArrayList;
import java.util.List;

public class JsSignature {

	private String comments = null;
	private String name = null;;
	private List<String> params = new ArrayList<>();
	private List<JsAnnotation> annotations = new ArrayList<>();
	
	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> pParams) {
		params = pParams;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<JsAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<JsAnnotation> annotations) {
		this.annotations = annotations;
	}


}
