package com.wp.scrapie;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jaxen.JaxenException;
import org.jaxen.jsoup.JsoupXPath;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Js {

	private static final Logger LOG = Logger.getLogger(Js.class);

	private static final String DEFAULT_RECORD_HOLDER = "DEFAULT_RECORD_HOLDER";

	private Element document;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Js parent = null;

	private Writer writer;

	private String workingId;

	Map<String, Map<String, Set<String>>> records = new HashMap<>();

	private static Map<String, String> cookies = null;

	public Js() {
	}

	public void runFile(String pSrc, Writer pWriter)
			throws FileNotFoundException, IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Reading file: " + pSrc);
		}
		String js = IOUtils.toString(new FileInputStream(pSrc));
		run(js, pWriter);
	}

	public List<Js> breakIntoSections(String pQuery) {
		Elements elements = document.select(pQuery);
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<Js>();
		}

		List<Js> sections = new ArrayList<Js>();

		for (Element element : elements) {
			Js js = new Js();
			js.setWriter(writer);
			js.setDocument(element);
			js.setParent(this);
			sections.add(js);
		}
		return sections;
	}

	public List<Js> processUrlsJq(String pQuery) throws IOException {
		Elements elements = document.select(pQuery);

		if (elements == null || elements.isEmpty()) {
			return new ArrayList<Js>();
		}

		List<Js> pages = new ArrayList<Js>();

		for (Element element : elements) {
			if (element.hasAttr("href")) {
				String url = element.attr("href");
				Js current = this;
				if (!url.startsWith("http")) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Does not start with HTTP looking in parents for base url");
					}
					while (current.getParent() != null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Going one level up");
						}
						current = current.getParent();
					}

					String baseUri = ((Document) current.getDocument())
							.baseUri();
					url = baseUri.substring(0, baseUri.indexOf("/", 10)) + url;
				}
				Js js = new Js();
				js.setWriter(writer);
				js.setDocument(loadUrl(url));
				js.setParent(this);
				pages.add(js);
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Couldn't find href in selected tag: " + element);
				}
			}
		}
		return pages;
	}

	public void run(String pJs, Writer pWriter) throws FileNotFoundException,
			IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Running Context");
		}

		writer = pWriter;
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		Object wrappedOut = Context.javaToJS(this, scope);
		ScriptableObject.putProperty(scope, "emitter", wrappedOut);
		cx.evaluateReader(scope, new FileReader("src/main/js/UrlIterator.js"),
				"urlIterator", 1, null);
		cx.evaluateString(scope, pJs, "config", 1, null);
	}

	public void emit(String pKey, String... pValue) {
		List<String> listValues = new ArrayList<String>();
		if (pValue == null) {
			return;
		}
		for (String value : pValue) {
			listValues.add(value);
		}
		emit(pKey, listValues);
	}

	public void emit(String pKey, List<String> pValue) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("emitting: " + pKey + ": " + pValue);
		}
		if (!records.containsKey(DEFAULT_RECORD_HOLDER)) {
			records.put(DEFAULT_RECORD_HOLDER,
					new TreeMap<String, Set<String>>());
		}
		Map<String, Set<String>> map = records.get(DEFAULT_RECORD_HOLDER);
		addKeyValue(map, pKey, pValue);
	}

	private void addKeyValue(Map<String, Set<String>> map, String pKey,
			List<String> pValue) {
		if (!map.containsKey(pKey)) {
			Set<String> listItems = new TreeSet<String>();
			map.put(pKey, listItems);
		}
		map.get(pKey).addAll(pValue);
		map.get(pKey).remove("");
		if (map.get(pKey).isEmpty()) {
			map.remove(pKey);
		}
	}

	public void emitForWorkingId(String pKey, String pValue) {
		List<String> listValues = new ArrayList<String>();
		listValues.add(pValue);
		emitForWorkingId(pKey, listValues);
	}

	public void emitForWorkingId(String pKey, List<String> pValue) {
		Js current = this;
		while (current != null) {
			if (current.getWorkingId() != null) {
				break;
			}
			current = current.getParent();
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("emitting: " + pKey + ": " + pValue);
		}
		String workingId = current.getWorkingId();
		if (workingId == null) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Could not emit for working ID as no working ID found in any parent context");
			}
			return;
		}

		Map<String, Map<String, Set<String>>> myRecords = current.getRecords();
		if (!myRecords.containsKey(workingId)) {
			myRecords.put(workingId, new TreeMap<String, Set<String>>());
		}
		Map<String, Set<String>> map = myRecords.get(workingId);
		addKeyValue(map, pKey, pValue);
	}

	public void flush() {
		Set<Entry<String, Map<String, Set<String>>>> entrySet = records
				.entrySet();
		for (Entry<String, Map<String, Set<String>>> entry : entrySet) {
			try {
				writer.write(MAPPER.writeValueAsString(entry.getValue()));
				writer.write("\n");
				entry.getValue().clear();
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		records.clear();
	}

	public String html() {
		return document.toString();
	}

	public Elements getJq(String pQuery) {
		return document.select(pQuery);
	}

	public List<Node> getXPath(String pQuery) throws JaxenException {
		return document.select(new JsoupXPath(pQuery));
	}

	public List<String> getJqText(String pQuery) {
		Elements elements = document.select(pQuery);
		if (elements == null || elements.size() == 0) {
			return new ArrayList<String>();
		}
		List<String> texts = new ArrayList<>();
		for (Element element : elements) {
			texts.add(element.text());
		}
		return texts;
	}

	public List<String> getXPathText(String pQuery) throws JaxenException {
		List<Node> elements = document.select(new JsoupXPath(pQuery));
		if (elements == null || elements.size() == 0) {
			return new ArrayList<String>();
		}
		List<String> texts = new ArrayList<>();
		for (Node element : elements) {
			texts.add(element.toString());
		}
		return texts;
	}

	public void loadDom(String pUrl) throws IOException {
		document = loadUrl(pUrl);
	}

	public void login(String pUrl, String... pKeyValues) throws IOException {
		Connection.Response res = Jsoup.connect(pUrl).data(pKeyValues)
				.method(Method.POST).execute();
		cookies = res.cookies();
	}

	private Document loadUrl(String pUrl) throws IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Loading Url: " + pUrl);
		}
		Connection connection = Jsoup.connect(pUrl);
		connection.timeout(10000);
		if (cookies != null) {
			for (Map.Entry<String, String> cookie : cookies.entrySet()) {
				connection.cookie(cookie.getKey(), cookie.getValue());
			}
		}
		return connection.get();
	}

	public String evaluateJq(String pJq) throws ScriptException {
		return "";
	}

	public void setDocument(Element doc) {
		this.document = doc;
	}

	public Element getDocument() {
		return document;
	}

	public void setParent(Js parent) {
		this.parent = parent;
	}

	public Js getParent() {
		return parent;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public Writer getWriter() {
		return writer;
	}

	public String getWorkingId() {
		return workingId;
	}

	public void setWorkingId(String workingId) {
		this.workingId = workingId;
	}

	public Map<String, Map<String, Set<String>>> getRecords() {
		return records;
	}

	public void print(String pString) {
		if (LOG.isInfoEnabled()) {
			LOG.info(pString);
		}
	}

}
