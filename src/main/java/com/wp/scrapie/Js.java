package com.wp.scrapie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * <code>
 * Main interface class between the Java world and the JavaScript world
 * </code>
 * 
 * @author will
 *
 */
public class Js {

	private static final Logger LOG = Logger.getLogger(Js.class);

	private static final String DEFAULT_RECORD_HOLDER = "DEFAULT_RECORD_HOLDER";

	private Element document;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Js parent = null;

	private static Writer writer;

	private String workingId;

	private static Set<String> excludeValues = new HashSet<>();

	private static Map<String, Map<String, Set<String>>> records = new HashMap<>();

	private static Map<String, String> cookies = new HashMap<String, String>();

	private Map<String, String> postData = new HashMap<>();

	private static String sourceFileName;
	private static int flushCount = 0;
	private static int record = 0;

	public Js() {
	}

	/**
	 * <code>
	 * Are we in record mode.  If record is greater than 0, we are and all processing will
	 * stop when we have flushed that number of records.
	 * </code>
	 * 
	 * @return
	 */
	public static int getRecord() {
		return record;
	}

	/**
	 * <code>
	 * Set the number of records to emit. This also tells the URL loader to
	 * write each request to disk.
	 * </code>
	 * 
	 * @param pRecord
	 */
	public static void setRecord(int pRecord) {
		record = pRecord;
	}

	/**
	 * <code>
	 * Write a file
	 * </code>
	 */
	public void runFile(String pSrc, Writer pWriter)
			throws FileNotFoundException, IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Reading file: " + pSrc);
		}
		File sourceFile = new File(pSrc);
		if (!sourceFile.exists()) {
			sourceFile = new File(System.getProperty("workingDir", ""));
		}
		String js = IOUtils.toString(new FileInputStream(sourceFile));
		sourceFileName = sourceFile.getName();
		run(js, pWriter);
	}

	@DontGenerate
	public List<Js> breakIntoSections(String pQuery) {
		Elements elements = document.select(pQuery);
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<Js>();
		}

		List<Js> sections = new ArrayList<Js>();

		for (Element element : elements) {
			Js js = new Js();
			js.setDocument(element);
			js.setParent(this);
			sections.add(js);
		}
		return sections;
	}

	@DontGenerate
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
		cx.evaluateReader(scope, new InputStreamReader(this.getClass()
				.getClassLoader().getResourceAsStream("UrlIterator.js")),
				"UrlIterator.js", 1, null);
		cx.evaluateReader(scope, generateEmitterWrapperCode(),
				"EmitterWrapper.js", 1, null);
		cx.evaluateString(scope, pJs, "config", 1, null);
	}

	private StringReader generateEmitterWrapperCode() throws IOException {
		String rawEmitter = IOUtils.toString(new InputStreamReader(this
				.getClass().getClassLoader()
				.getResourceAsStream("EmitterWrapper.js"), "UTF8"));
		int start = rawEmitter.indexOf("//GENERATED") + "//GENERATED".length();
		int end = rawEmitter.indexOf("//GENERATED", start);
		String startCode = rawEmitter.substring(0, start);
		String endCode = rawEmitter.substring(end);
		return new StringReader(startCode + getInjectedCode() + endCode);
	}

	private String getInjectedCode() throws IOException {
		StringBuilder sb = new StringBuilder();
		JavaProjectBuilder builder = new JavaProjectBuilder();

		JavaSource source = null;
		File jsSource = new File("src/main/java/com/wp/scrapie/Js.java");
		if (jsSource.exists()) {
			source = builder.addSource(jsSource);
		} else {
			InputStreamReader inputStreamReader = new InputStreamReader(this
					.getClass().getClassLoader().getResourceAsStream("Js.java"));
			source = builder.addSource(inputStreamReader);
			inputStreamReader.close();
		}
		JavaClass js = source.getClassByName("Js");
		List<JavaMethod> methods = js.getMethods();
		Collections.sort(methods, new Comparator<JavaMethod>() {

			@Override
			public int compare(JavaMethod pO1, JavaMethod pO2) {
				return pO1.getName().compareTo(pO2.getName());
			}
		});
		for (JavaMethod method : methods) {
			List<JavaAnnotation> annotations = method.getAnnotations();

			if (shouldGenerate(method, annotations)) {
				StringBuilder p = getParameterList(method);
				sb.append("\n\tthis.").append(method.getName())
						.append(" = function(");
				sb.append(p);
				sb.append("){ ");
				if (!method.getReturns().isVoid()) {
					sb.append("return ");
				}
				sb.append("this.emitter.").append(method.getName()).append("(");
				sb.append(p);
				sb.append("); }");
			}
		}
		return sb.toString();
	}

	private StringBuilder getParameterList(JavaMethod method) {
		StringBuilder p = new StringBuilder();
		List<JavaParameter> parameters = method.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			JavaParameter param = parameters.get(i);
			String name = param.getName();
			p.append(name);
			if (i < parameters.size() - 1) {
				p.append(", ");
			}
		}
		return p;
	}

	private boolean shouldGenerate(JavaMethod pMethod,
			List<JavaAnnotation> pAnnotations) {
		if (!pMethod.isPublic()) {
			return false;
		}
		for (JavaAnnotation javaAnnotation : pAnnotations) {
			if (javaAnnotation.getType().getCanonicalName()
					.equals(DontGenerate.class.getName())) {
				return false;
			}
		}
		return true;
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

	@DontGenerate
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
		for (String value : pValue) {
			String unescapedValue = StringEscapeUtils.unescapeHtml(value)
					.replaceAll(" ", " ").trim();
			if (!excludeValues.contains(unescapedValue)) {
				map.get(pKey).add(unescapedValue);
			}
		}
		map.get(pKey).remove("");
		if (map.get(pKey).isEmpty()) {
			map.remove(pKey);
		}
	}

	public void emitForWorkingId(String pKey, String pValue) {
		emitForWorkingId(pKey, java.util.Arrays.asList(pValue));
	}

	@DontGenerate
	public void emitForWorkingId(String pKey, String[] pValue) {
		List<String> listValues = new ArrayList<String>();
		for (String value : listValues) {
			listValues.add(value);
		}
		emitForWorkingId(pKey, listValues);
	}

	public List<String> getReText(String pPattern) {
		List<String> results = new ArrayList<>();
		Pattern p = Pattern.compile(pPattern);
		Matcher matcher = p.matcher(document.toString());
		while (matcher.find()) {
			String group0 = matcher.group();
			System.out.println(group0);
			System.out.println("email: " + pPattern + " "
					+ matcher.groupCount());
			if (matcher.groupCount() > 1) {
				group0 = matcher.group(1);
				System.out.println(group0);
			}
			if (StringUtils.isNotBlank(group0)) {
				results.add(group0);
			}
		}
		return results;
	}

	@DontGenerate
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

	public static void flush() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("flush()");
		}
		Set<Entry<String, Map<String, Set<String>>>> entrySet = records
				.entrySet();
		for (Entry<String, Map<String, Set<String>>> entry : entrySet) {
			try {
				if (!entry.getValue().isEmpty()) {
					writer.write(MAPPER.writeValueAsString(entry.getValue()));
					writer.write("\n");
					entry.getValue().clear();
				}
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		records.clear();
		flushCount++;
	}

	public String getHtml() {
		return document.toString();
	}

	public Elements getJq(String pQuery) {
		return document.select(pQuery);
	}

	public List<String> getJqAttr(String pQuery, String pAttr) {
		Elements selects = document.select(pQuery);
		List<String> results = new ArrayList<>();
		for (Element select : selects) {
			if (select.hasAttr(pAttr)) {
				results.add(select.attr(pAttr));
			}
		}
		return results;
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

	public void load(String pUrl) throws IOException {
		document = loadUrl(pUrl);
	}

	public void login(String pUrl, String... pKeyValues) throws IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("----------------------");
			LOG.trace("Js.login() started");
		}
		loadOrCache(pUrl, Method.POST, pKeyValues);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Js.login() complete");
			LOG.trace("----------------------");
		}
	}

	private Document loadUrl(String pUrl) throws IOException {
		Method method = Method.GET;
		if (LOG.isTraceEnabled()) {
			LOG.trace("----------------------");
			LOG.trace("nJs.loadUrl() started");
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("Loading Url: " + pUrl);
		}
		Document newDocument = loadOrCache(pUrl, method);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Js.loadUrl() complete");
			LOG.trace("----------------------");
		}
		return newDocument;
	}

	private Document loadOrCache(String pUrl, Method method,
			String... pKeyValues) throws IOException {
		Document newDocument = null;
		String cacheDir = createCacheDir();
		File hashFile = new File(cacheDir + DigestUtils.md5Hex(pUrl)
				+ describe(pUrl) + ".txt");
		if (isRecord() && hashFile.exists()) {
			File tmp = new File(cacheDir);
			if (LOG.isDebugEnabled()) {
				LOG.debug("ensuring dir exists: " + tmp.getAbsolutePath());
			}
			tmp.mkdirs();

			newDocument = Jsoup.parse(hashFile, "UTF8");
			newDocument.setBaseUri(pUrl);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loaded from cache");
			}
		} else {
			Connection connection = createConnection(pUrl);
			connection.data(pKeyValues);
			Connection.Response res = connection.method(method).execute();
			cookies.putAll(res.cookies());
			printCookies("Receiving", res.cookies());
			newDocument = res.parse();
			if (isRecord()) {
				FileUtils.write(hashFile, res.body(), "UTF8");
			}
		}
		return newDocument;
	}

	private String createCacheDir() {
		return System.getProperty("workingDir", "") + "." + sourceFileName
				+ "_data/";
	}

	private String describe(String pUrl) {
		URI uri = URI.create(pUrl);
		return uri.getPath().replaceAll("/", "AOEUSLASHAOEU")
				.replaceAll("[^a-z^A-Z^0-9^-]", "")
				.replaceAll("AOEUSLASHAOEU", "-");
	}

	private void printCookies(String pPrefix, Map<String, String> pCookies) {
		if (LOG.isTraceEnabled()) {
			Set<Entry<String, String>> entrySet = pCookies.entrySet();
			LOG.trace("Cookies (" + pPrefix + "): ");
			for (Entry<String, String> entry : entrySet) {
				LOG.trace("\t" + entry);
			}
		}
	}

	private Connection createConnection(String pUrl) {
		Connection connection = Jsoup.connect(pUrl);
		connection.timeout(40000);
		connection.referrer("");
		connection.followRedirects(true);
		connection.ignoreHttpErrors(true);
		connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) "
				+ "AppleWebKit/537.36 (KHTML, like Gecko) "
				+ "Chrome/35.0.1916.153 Safari/537.36");
		if (postData != null && !postData.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Post Data: " + postData);
			}
			connection.data(postData);
		}
		if (cookies != null && !cookies.isEmpty()) {
			printCookies("Sending", cookies);
			connection.cookies(cookies);
		}
		return connection;
	}

	public static Map<String, String> getCookies() {
		return cookies;
	}

	public static void setCookies(Map<String, String> pCookies) {
		cookies = pCookies;
	}

	public Map<String, String> getPostData() {
		return postData;
	}

	public void setPostData(Map<String, String> pPostData) {
		postData = pPostData;
	}

	public void setRecords(Map<String, Map<String, Set<String>>> pRecords) {
		records = pRecords;
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

	@DontGenerate
	public static void setWriter(Writer pWriter) {
		writer = pWriter;
	}

	@DontGenerate
	public static Writer getWriter() {
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

	public void printDocument() {
		if (LOG.isInfoEnabled()) {
			LOG.info(document.toString());
		}
	}

	@DontGenerate
	public static void addExcludeValue(String pValue) {
		excludeValues.add(pValue);
	}

	@DontGenerate
	public static void setFlushCount(int pFlushCount) {
		flushCount = pFlushCount;
	}

	@DontGenerate
	public static boolean keepGoing() {
		return record == 0 || (record > 0 && flushCount < record);
	}

	@DontGenerate
	public static boolean isRecord() {
		return record > 0;
	}

	public static String getSourceFileName() {
		return sourceFileName;
	}

	public static void setSourceFileName(String pSourceFileName) {
		sourceFileName = pSourceFileName;
	}

}
