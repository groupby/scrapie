package com.wp.scrapie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import org.apache.commons.io.LineIterator;
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
 * All the JavaScript methods listed here are generated from a Java class so the
 * docs are a little bit Java-y.  
 * 
 * Sometimes the methods are overloaded so you can call a JavaScript method with a 
 * single value or an array and the JavaScript interpreter will select the correct 
 * Java method at runtime.
 * 
 * 
 * </code>
 * 
 * @author will
 * 
 */
public class Emitter implements EmitterWrapper {

	private static final Logger LOG = Logger.getLogger(Emitter.class);

	private static final String DEFAULT_RECORD_HOLDER = "DEFAULT_RECORD_HOLDER";

	private Element document;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Emitter parent = null;

	private static Writer writer;

	private String workingId;

	private static Set<String> excludeValues = new HashSet<>();

	private static Map<String, Map<String, Set<String>>> records = new HashMap<>();

	private static Map<String, String> cookies = new HashMap<String, String>();

	private Map<String, String> postData = new HashMap<>();

	private String url = null;

	private static File sourceDirectory;

	private static String sourceFileName;
	private static int recordCount = 0;
	private static int record = 0;

	/**
	 * @internal <code>
	 * Default initializer.
	 * </code>
	 */
	public Emitter() {
	}

	/**
	 * <code>
	 * Are we in record mode.  If record is greater than 0, we are and all processing will
	 * stop when we have flushed that number of records.
	 * </code>
	 * 
	 * @return The current value of record.
	 */
	public static int getRecord() {
		return record;
	}

	/**
	 * <code>
	 * Set the number of records to emit. This also tells the URL loader to
	 * write each request to disk and cache them so when you run this scraper again,
	 * it will hit disk and not the internet.  Good when you need to quickly iterate
	 * through trials of your CSS/XPath/RegEx selectors.
	 * </code>
	 * 
	 * @param pRecord
	 *            The number of records to emit before stopping.
	 */
	public static void setRecord(int pRecord) {
		record = pRecord;
	}

	/**
	 * @internal
	 */
	@DontGenerate
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
		sourceDirectory = new File(sourceFile.getAbsoluteFile().getParent());
		sourceFileName = sourceFile.getName();
		run(js, pWriter);
	}

	/**
	 * 
	 * <code>
	 * Breaks a DOM up by a specific CSS selector and creates a new context object
	 * for each section.  This new context object is passed to the callback function.
	 * 
	 * ```JavaScript
	 * var sections = pContext.breakIntoSections(".item", function(pContext){
	 *     // You'll probably want to do set a working ID so that all the emits are grouped togethr.
	 *     pContext.setWorkingId(pContext.getJqText(".idHolder"));
	 *     // do some scraping
	 *     pContext.emitForWorkingId("title", pContext.getJqText(".title"));
	 *     pContext.flush();
	 * });     
	 * ```
	 * </code>
	 * 
	 * @param pQuery
	 *            The CSS query to find the sections to break up into.
	 * @param pDealWith
	 *            The callback function used to deal with each section.
	 */
	@DontGenerate
	public void breakIntoSections(String pQuery, Function pDealWith) {
		// placeholder for docs
	}

	/**
	 * 
	 * @internal
	 * @param pQuery
	 * @return
	 */
	@DontGenerate
	public List<Emitter> breakIntoSections(String pQuery) {
		Elements elements = document.select(pQuery);
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<Emitter>();
		}

		List<Emitter> sections = new ArrayList<Emitter>();

		for (Element element : elements) {
			Emitter emitter = new Emitter();
			emitter.setDocument(element);
			emitter.setParent(this);
			sections.add(emitter);
		}
		return sections;
	}

	/**
	 * 
	 * <code>
	 * 
	 * Method finds all the anchor tags that match the query.  For each
	 * anchor tag the URL is loaded and a new Emitter object is created 
	 * and passed back into into the dealWith function.
	 * 
	 * ```JavaScript
	 * pContext.processUrlsJq("a", function(pContext){
	 *     // do some scraping
	 * });
	 * ```
	 * </code>
	 * 
	 * @param pQuery
	 *            The CSS query to find the anchor tags to generate the next
	 *            level of detail.
	 * @param pDealWith
	 *            The callback function used to deal with each new page.
	 */
	@DontGenerate
	public void processUrlsJq(String pQuery, Function pDealWith) {
		// placeholder for docs
	}

	/**
	 * @internal
	 */
	@DontGenerate
	public List<Emitter> processUrlsJq(String pQuery) throws IOException {
		Elements elements = document.select(pQuery);

		if (elements == null || elements.isEmpty()) {
			return new ArrayList<Emitter>();
		}

		List<Emitter> pages = new ArrayList<Emitter>();

		for (Element element : elements) {
			if (element.hasAttr("href")) {
				String url = element.attr("href");
				Emitter current = this;
				if (!url.startsWith("http")) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Does not start with HTTP looking in parents for base url");
					}
					while (current.getParent() != null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Going one level up");
						}
						current = (Emitter) current.getParent();
					}

					String baseUri = ((Document) current.getDocument())
							.baseUri();
					url = baseUri.substring(0, baseUri.indexOf("/", 10)) + url;
				}
				Emitter emitter = new Emitter();
				emitter.load(url);
				emitter.setParent(this);
				pages.add(emitter);
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Couldn't find href in selected tag: " + element);
				}
			}
		}
		return pages;
	}

	/**
	 * @internal
	 * @param pJs
	 * @param pWriter
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
		cx.evaluateReader(scope, new InputStreamReader(this.getClass()
				.getClassLoader().getResourceAsStream("FileIterator.js")),
				"FileIterator.js", 1, null);
		cx.evaluateReader(scope, new InputStreamReader(this.getClass()
				.getClassLoader().getResourceAsStream("GlobalFunctions.js")),
				"GlobalFunctions.js", 1, null);
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
		File jsSource = new File("src/main/java/com/wp/scrapie/Emitter.java");
		if (jsSource.exists()) {
			source = builder.addSource(jsSource);
		} else {
			InputStreamReader inputStreamReader = new InputStreamReader(this
					.getClass().getClassLoader()
					.getResourceAsStream("Emitter.java"));
			source = builder.addSource(inputStreamReader);
			inputStreamReader.close();
		}
		JavaClass js = source.getClassByName("Emitter");
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

	/**
	 * <code>
	 * Search up the parent chain for the first URL that this block is contained by.
	 * </code>
	 * 
	 * @return The URL that the current block is contained by.
	 */
	public String getUrl() {
		if (url == null) {
			return parent.getUrl();
		} else {
			return url;
		}
	}

	/**
	 * <code>
	 * Set the URL for the current emitter.  Generally set when a document is loaded.
	 * </code>
	 * 
	 * @param pUrl
	 *            The URL to set.
	 */
	public void setUrl(String pUrl) {
		url = pUrl;
	}

	/**
	 * <code>
	 * Emit's a key value pair into a map.  By default this goes into a global map
	 * that is flushed to disk when `flush()` is called.
	 * </code>
	 * 
	 * @param pKey
	 *            The name of the property to emit
	 * @param pValue
	 *            The value of the property.
	 */
	public void emit(String pKey, Object... pValue) {
		List<Object> listValues = new ArrayList<Object>();
		if (pValue == null) {
			return;
		}
		for (Object value : pValue) {
			listValues.add(value);
		}
		emit(pKey, listValues);
	}

	/**
	 * @param pKey
	 *            The name of the property to emit
	 * @param pValue
	 *            The value of the property.
	 */
	public void emit(String pKey, List<Object> pValue) {
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
			List<Object> pValue) {
		if (!map.containsKey(pKey)) {
			Set<String> listItems = new TreeSet<String>();
			map.put(pKey, listItems);
		}
		for (Object value : pValue) {
			String unescapedValue = StringEscapeUtils
					.unescapeHtml(value.toString()).replaceAll(" ", " ").trim();
			if (!excludeValues.contains(unescapedValue)) {
				map.get(pKey).add(unescapedValue);
			}
		}
		map.get(pKey).remove("");
		if (map.get(pKey).isEmpty()) {
			map.remove(pKey);
		}
	}

	/**
	 * <code>
	 * Similar to emit, however this method emits into a map with a specific name.
	 * Then name is set by calling `setWorkingId(String id)`.  This is held globally and 
	 * statically and thus any subsequent call to emitForWorkingId will always now store 
	 * key value pairs in that map.  The map is written to disk with a call to 
	 * `flush()`  
	 * 
	 * </code>
	 * 
	 * @param pKey
	 *            The name of the property to emit
	 * @param pValue
	 *            The value of the property.
	 */
	public void emitForWorkingId(String pKey, Object... pValue) {
		List<Object> listValues = new ArrayList<Object>();
		for (Object value : pValue) {
			listValues.add(value);
		}
		emitForWorkingId(pKey, listValues);
	}

	/**
	 * <code>
	 * By default this will return the entire matched string.
	 * If you specify a group in the regular expression, by using
	 * quotes, then the first group will be returned instead.
	 * </code>
	 * 
	 * @param pPattern
	 *            The Regex Pattern to match.
	 * @return
	 */
	public List<String> getReText(String pPattern) {
		List<String> results = new ArrayList<>();
		Pattern p = Pattern.compile(pPattern);
		Matcher matcher = p.matcher(document.toString());
		while (matcher.find()) {
			String group0 = matcher.group();
			if (matcher.groupCount() > 1) {
				group0 = matcher.group(1);
			}
			if (StringUtils.isNotBlank(group0)) {
				results.add(group0);
			}
		}
		return results;
	}

	/**
	 * <code>
	 * Same as `emitForWorkingId()` but takes a list of values 
	 * </code>
	 */
	@DontGenerate
	public void emitForWorkingId(String pKey, List<Object> pValue) {
		Emitter current = this;
		while (current != null) {
			if (current.getWorkingId() != null) {
				break;
			}
			current = (Emitter) current.getParent();
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

	/**
	 * <code>
	 * Write the current set of mpas to disk.  You can safely call this when nothing
	 * has yet been emitted.  It is wise to call this as frequently as makes sense 
	 * as these maps are held in memory and you will run out of memory if you're crawling
	 * a large site.  
	 * </code>
	 */
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
					recordCount++;
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
	}

	/**
	 * 
	 * @return A string representation of this contexts html
	 */
	public String getHtml() {
		return document.toString();
	}

	/**
	 * <code>
	 * Return an Elements object of all the matching elements against the provided 
	 * query.  
	 * 
	 * Elements has a bunch of helper methods that return just the first element's string.
	 * 
	 * </code>
	 * 
	 * @param pQuery
	 *            The CSS selector.
	 * @return a list of elements that match this CSS path.
	 */
	public Elements getJq(String pQuery) {
		return document.select(pQuery);
	}

	/**
	 * <code>
	 * Rather than returning the text associated with a desired element or elements
	 * return a specific attribute from each element as a list.
	 * </code>
	 * 
	 * @param pQuery
	 *            The CSS selector to the elements desired.
	 * @param pAttr
	 *            The attribute to retrieve from each element.
	 * @return A list of strings of the elements that had this attribute and the
	 *         attribute value.
	 */
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

	/**
	 * 
	 * <code>
	 * Returns a list of nodes, rather than an Elements section as XPath can return
	 * the text nodes as well as elements.
	 * </code>
	 * 
	 * @param pQuery
	 *            The XPath query.
	 * @return A list of nodes that match this XPath expression.
	 */
	public List<Node> getXPath(String pQuery) throws JaxenException {
		return document.select(new JsoupXPath(pQuery));
	}

	/**
	 * <code>
	 * Rather than get the list of elements, return a list of the 
	 * text under each element.
	 * </code>
	 * 
	 * @param pQuery
	 *            The CSS path to select the desired elements.
	 * @return A list of strings of the text of the matched elements
	 */
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

	/**
	 * @internal
	 * @param pFilePath
	 * @return
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	@DontGenerate
	public LineIterator getLineIterator(String pFilePath)
			throws IllegalArgumentException, FileNotFoundException {
		String path = sourceDirectory.getPath() + "/" + pFilePath;
		if (LOG.isInfoEnabled()) {
			LOG.info("Reading file: " + path);
		}
		return new LineIterator(new FileReader(path));
	}

	/**
	 * <code>
	 * Return a list of strings that match the XPath query.
	 * `toString()` is called on each of the matching nodes.
	 * </code>
	 * 
	 * @param pQuery
	 *            The XPath expression to evaluate.
	 * @return A list of strings that represent these nodes.
	 */
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

	/**
	 * <code>
	 * Rather than returning the text associated with a desired element or elements
	 * return a specific attribute from each element as a list.
	 * </code>
	 * 
	 * @param pQuery
	 *            The XPath query to the elements desired.
	 * @param pAttr
	 *            The attribute to retrieve from each element.
	 * @return A list of strings of the elements that had this attribute and the
	 *         attribute value.
	 * @throws JaxenException
	 */
	public List<String> getXPathAttr(String pQuery, String pAttr)
			throws JaxenException {
		List<Node> elements = document.select(new JsoupXPath(pQuery));
		if (elements == null || elements.size() == 0) {
			return new ArrayList<String>();
		}
		List<String> results = new ArrayList<>();
		for (Node element : elements) {
			if (element.hasAttr(pAttr)) {
				results.add(element.attr(pAttr));
			}
		}
		return results;
	}

	/**
	 * <code>
	 * Load a url and store it in the document variable.
	 * Accessible through `getDocument()`.
	 * Also see the get* methods that pull data from this document.
	 * </code>
	 * 
	 * @param pUrl
	 *            The URL to retrieve.
	 */
	public void load(String pUrl) throws IOException {
		document = loadUrl(pUrl);
		url = pUrl;
	}

	/**
	 * <code>
	 * Login to a website. Find the login URL from the action element of the form that 
	 * is used on the login page of that website. 
	 * Typically it's best to use the Chrome nework inspector to copy all of the form values
	 * that are submitted.  This is especially important when you're talking to a .NET application
	 * as they have crazy viewstate parameters that need to be sent. 
	 * </code>
	 * 
	 * @param pUrl
	 *            The URL to submit the login to.
	 * @param pKeyValues
	 *            A splat of key value pairs that will be submitted as part of
	 *            the login
	 */
	public void login(String pUrl, String... pKeyValues) throws IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("----------------------");
			LOG.trace("Emitter.login() started");
		}
		loadOrCache(pUrl, Method.POST, pKeyValues);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Emitter.login() complete");
			LOG.trace("----------------------");
		}
	}

	private Document loadUrl(String pUrl) throws IOException {
		Method method = Method.GET;
		if (LOG.isTraceEnabled()) {
			LOG.trace("----------------------");
			LOG.trace("Emitter.loadUrl() started");
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("Loading Url: " + pUrl);
		}
		Document newDocument = loadOrCache(pUrl, method);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Emitter.loadUrl() complete");
			LOG.trace("----------------------");
		}
		return newDocument;
	}

	private Document loadOrCache(String pUrl, Method method,
			String... pKeyValues) throws IOException {
		Document newDocument = null;
		String cacheDir = createCacheDir();
		File hashFile = new File(cacheDir
				+ DigestUtils.md5Hex(extractSaliantParts(pUrl))
				+ describe(pUrl) + ".html");
		if (isRecord() && hashFile.exists()) {
			ensureCacheDirExists(cacheDir);
			newDocument = Jsoup.parse(hashFile, "UTF8");
			newDocument.setBaseUri(pUrl);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loaded from cache");
			}
		} else {
			ensureCacheDirExists(cacheDir);
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

	private void ensureCacheDirExists(String cacheDir) {
		File tmp = new File(cacheDir);
		if (LOG.isDebugEnabled()) {
			LOG.debug("ensuring dir exists: " + tmp.getAbsolutePath());
		}
		tmp.mkdirs();
	}

	private String extractSaliantParts(String pUrl) {
		int endOfDomain = pUrl.indexOf("/", 9);
		if (endOfDomain == -1) {
			return "";
		}
		return pUrl.substring(endOfDomain);
	}

	private String createCacheDir() {
		return sourceDirectory + "/" + sourceFileName + "_data/";
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
		// connection.ignoreHttpErrors(true);
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
			Set<Entry<String, String>> entrySet = cookies.entrySet();
			for (Entry<String, String> entry : entrySet) {
				connection.cookie(entry.getKey(), entry.getValue());

			}
		}
		return connection;
	}

	/**
	 * <code>
	 * Returns the, always instantiated, map of cookies.
	 * Useful if you need to override a specific cookie value at some point in the crawl.
	 * </code>
	 * 
	 * @return The current cookie map.
	 */
	public static Map<String, String> getCookies() {
		return cookies;
	}

	/**
	 * @internal
	 * @param pCookies
	 */
	@DontGenerate
	public static void setCookies(Map<String, String> pCookies) {
		cookies = pCookies;
	}

	/**
	 * <code>
	 * Post data that should be set on a request.  
	 * </code>
	 * 
	 * @return An, always instatiated, map of key value pairs.
	 */
	public Map<String, String> getPostData() {
		return postData;
	}

	/**
	 * @internal
	 */
	@DontGenerate
	public void setPostData(Map<String, String> pPostData) {
		postData = pPostData;
	}

	/**
	 * @internal
	 * @param pRecords
	 */
	@DontGenerate
	public void setRecords(Map<String, Map<String, Set<String>>> pRecords) {
		records = pRecords;
	}

	/**
	 * @internal
	 * @param doc
	 */
	@DontGenerate
	public void setDocument(Element doc) {
		this.document = doc;
	}

	/**
	 * <code>
	 * Returns the document that was loaded by the `loadUrl()` call.
	 * This will also represent the document 
	 * </code>
	 * 
	 * @return The current document in this context.
	 */
	public Element getDocument() {
		return document;
	}

	/**
	 * @internal
	 * @param parent
	 */
	@DontGenerate
	public void setParent(Emitter parent) {
		this.parent = parent;
	}

	/**
	 * <code>
	 * Get the parent context of this context. Useful if you need access to the parent page,
	 * or the previous url document.
	 * </code>
	 * 
	 * @return The parent context. It's safe to call all the JavaScript methods.
	 */
	@DontGenerate
	public EmitterWrapper getParent() {
		return parent;
	}

	/**
	 * @internal
	 * @param pWriter
	 */
	@DontGenerate
	public static void setWriter(Writer pWriter) {
		writer = pWriter;
	}

	/**
	 * 
	 * @internal
	 * @return
	 */
	@DontGenerate
	public static Writer getWriter() {
		return writer;
	}

	/**
	 * <code>
	 * The name of the map that all calls to `emitForWorkingId()` will be inserted into
	 * </code>
	 * 
	 * @return Get the working ID.
	 */
	public String getWorkingId() {
		return workingId;
	}

	/**
	 * <code>
	 * Once the working id is set, all calls to `emitForWorkingId()` will
	 * be put into a map with this ID as a key.
	 * </code>
	 * 
	 * @param workingId
	 *            The context working id.
	 * 
	 */
	public void setWorkingId(String workingId) {
		this.workingId = workingId;
	}

	/**
	 * @internal
	 * @return
	 */
	public Map<String, Map<String, Set<String>>> getRecords() {
		return records;
	}

	/**
	 * <code>
	 * Helper method to print a string to standard out.  Prints at info level.
	 * </code>
	 * 
	 * @param pMessage
	 *            The string to print
	 */
	public void print(String pMessage) {
		if (LOG.isInfoEnabled()) {
			LOG.info(pMessage);
		}
	}

	/**
	 * <code>
	 * Helper method to print the entire loaded document to standard out. 
	 * Prints at info level.
	 * </code>
	 */
	public void printDocument() {
		if (LOG.isInfoEnabled()) {
			LOG.info(document.toString());
		}
	}

	/**
	 * <code>
	 * Global setting to tell the emitter that if it finds this 
	 * value associated with any key, to drop that key value pair.
	 * </code>
	 * 
	 * @param pValue
	 *            The value that should never be emitted.
	 */
	@DontGenerate
	public static void addExcludeValue(String pValue) {
		excludeValues.add(pValue);
	}

	/**
	 * @internal
	 * @param pRecordCount
	 */
	@DontGenerate
	public static void setRecordCount(int pRecordCount) {
		recordCount = pRecordCount;
	}

	/**
	 * @internal
	 * @return
	 */
	@DontGenerate
	public static boolean keepGoing() {
		return record == 0 || (record > 0 && recordCount < record);
	}

	/**
	 * @internal
	 * @return
	 */
	@DontGenerate
	public static boolean isRecord() {
		return record > 0;
	}

	/**
	 * <code>
	 * Get the name of the JS scraper file currently being processed.
	 * <code>
	 * 
	 * @return The filename
	 * 
	 */
	@DontGenerate
	public static String getSourceFileName() {
		return sourceFileName;
	}

	/**
	 * @internal
	 * @param pSourceFileName
	 */
	@DontGenerate
	public static void setSourceFileName(String pSourceFileName) {
		sourceFileName = pSourceFileName;
	}

	/**
	 * @internal
	 * @param pSourceDirectory
	 */
	@DontGenerate
	public static void setSourceDirectory(File pSourceDirectory) {
		sourceDirectory = pSourceDirectory;
	}

}
