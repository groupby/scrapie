![Emitter](src/main/images/sheepVeryVerySmall.png) Emitter
=====


All the JavaScript methods listed here are generated from a Java class so the
docs are a little bit Java-y.  

Sometimes the methods are overloaded so you can call a JavaScript method with a 
single value or an array and the JavaScript interpreter will select the correct 
Java method at runtime.






###Methods
- [addExcludeValue(String pValue)](#1526343849) 
- [breakIntoSections(String pQuery, Function pDealWith)](#907510991)
- [emit(String pKey, List pValue)](#856545347) 
- [emit(String pKey, Object... pValue)](#-418495782) 
- [emitForWorkingId(String pKey, List pValue)](#294587270)
- [emitForWorkingId(String pKey, Object... pValue)](#1789501815) 
- [flush()](#-760377595) 
- [getCookies()](#216551706)  returns Map
- [getDocument()](#-1112689838)  returns org.jsoup.nodes.Element
- [getHtml()](#488960226)  returns String
- [getJq(String pQuery)](#1932918093)  returns org.jsoup.select.Elements
- [getJqAttr(String pQuery, String pAttr)](#-51822634)  returns List
- [getJqText(String pQuery)](#-1192716576)  returns List
- [getParent()](#-1041905503)  returns EmitterWrapper
- [getPostData()](#874444513)  returns Map
- [getReText(String pPattern)](#-1271561236)  returns List
- [getRecord()](#1278664328)  returns int
- [getSourceFileName()](#-205233319)  returns String
- [getUrl()](#1967378682)  returns String
- [getWorkingId()](#-511304457)  returns String
- [getXPath(String pQuery)](#1521535043)  returns List
- [getXPathAttr(String pQuery, String pAttr)](#-859866804)  returns List
- [getXPathText(String pQuery)](#2138633942)  returns List
- [load(String pUrl)](#410534269) 
- [login(String pUrl, String... pKeyValues)](#-1609977486) 
- [print(String pMessage)](#1272621438) 
- [printDocument()](#-994781719) 
- [processUrlsJq(String pQuery, Function pDealWith)](#405324898) 
- [setRecord(int pRecord)](#-1092772818) 
- [setUrl(String pUrl)](#-1399905258) 
- [setWorkingId(String workingId)](#-1064871666) 


#### <a style="font-size:16px;" name="1526343849">addExcludeValue</a><span style="font-size:16px;">(String pValue)</span>
- <b>pValue</b>: 
           The value that should never be emitted.

Global setting to tell the emitter that if it finds this 
value associated with any key, to drop that key value pair.



#### <a style="font-size:16px;" name="907510991">breakIntoSections</a><span style="font-size:16px;">(String pQuery, Function pDealWith)</span>
- <b>pQuery</b>: 
           The CSS query to find the sections to break up into.
- <b>pDealWith</b>: 
           The callback function used to deal with each section.

Breaks a DOM up by a specific CSS selector and creates a new context object
for each section.  This new context object is passed to the callback function.

```JavaScript
var sections = pContext.breakIntoSections(".item", function(pContext){
    // You'll probably want to do set a working ID so that all the emits are grouped togethr.
    pContext.setWorkingId(pContext.getJqText(".idHolder"));
    // do some scraping
    pContext.emitForWorkingId("title", pContext.getJqText(".title"));
    pContext.flush();
});     
```



#### <a style="font-size:16px;" name="856545347">emit</a><span style="font-size:16px;">(String pKey, List pValue)</span>
- <b>pKey</b>: 
           The name of the property to emit
- <b>pValue</b>: 
           The value of the property.

#### <a style="font-size:16px;" name="-418495782">emit</a><span style="font-size:16px;">(String pKey, Object... pValue)</span>
- <b>pKey</b>: 
           The name of the property to emit
- <b>pValue</b>: 
           The value of the property.

Emit's a key value pair into a map.  By default this goes into a global map
that is flushed to disk when `flush()` is called.



#### <a style="font-size:16px;" name="294587270">emitForWorkingId</a><span style="font-size:16px;">(String pKey, List pValue)</span>

Same as `emitForWorkingId()` but takes a list of values 



#### <a style="font-size:16px;" name="1789501815">emitForWorkingId</a><span style="font-size:16px;">(String pKey, Object... pValue)</span>
- <b>pKey</b>: 
           The name of the property to emit
- <b>pValue</b>: 
           The value of the property.

Similar to emit, however this method emits into a map with a specific name.
Then name is set by calling `setWorkingId(String id)`.  This is held globally and 
statically and thus any subsequent call to emitForWorkingId will always now store 
key value pairs in that map.  The map is written to disk with a call to 
`flush()`  




#### <a style="font-size:16px;" name="-760377595">flush</a><span style="font-size:16px;">()</span>

Write the current set of mpas to disk.  You can safely call this when nothing
has yet been emitted.  It is wise to call this as frequently as makes sense 
as these maps are held in memory and you will run out of memory if you're crawling
a large site.  



#### <span style="font-size:12px;color:#AAAAAA">Map&lt;String,String&gt;</span> <a style="font-size:16px;" name="216551706">getCookies</a><span style="font-size:16px;">()</span>
- <b>returns</b>: The current cookie map.

Returns the, always instantiated, map of cookies.
Useful if you need to override a specific cookie value at some point in the crawl.



#### <span style="font-size:12px;color:#AAAAAA">org.jsoup.nodes.Element</span> <a style="font-size:16px;" name="-1112689838">getDocument</a><span style="font-size:16px;">()</span>
- <b>returns</b>: The current document in this context.

Returns the document that was loaded by the `loadUrl()` call.
This will also represent the document 



#### <span style="font-size:12px;color:#AAAAAA">String</span> <a style="font-size:16px;" name="488960226">getHtml</a><span style="font-size:16px;">()</span>
- <b>returns</b>: A string representation of this contexts html

#### <span style="font-size:12px;color:#AAAAAA">org.jsoup.select.Elements</span> <a style="font-size:16px;" name="1932918093">getJq</a><span style="font-size:16px;">(String pQuery)</span>
- <b>pQuery</b>: 
           The CSS selector.
- <b>returns</b>: a list of elements that match this CSS path.

Return an Elements object of all the matching elements against the provided 
query.  

Elements has a bunch of helper methods that return just the first element's string.




#### <span style="font-size:12px;color:#AAAAAA">List&lt;String&gt;</span> <a style="font-size:16px;" name="-51822634">getJqAttr</a><span style="font-size:16px;">(String pQuery, String pAttr)</span>
- <b>pQuery</b>: 
           The CSS selector to the elements desired.
- <b>pAttr</b>: 
           The attribute to retrieve from each element.
- <b>returns</b>: A list of strings of the elements that had this attribute and the
        attribute value.

Rather than returning the text associated with a desired element or elements
return a specific attribute from each element as a list.



#### <span style="font-size:12px;color:#AAAAAA">List&lt;String&gt;</span> <a style="font-size:16px;" name="-1192716576">getJqText</a><span style="font-size:16px;">(String pQuery)</span>
- <b>pQuery</b>: 
           The CSS path to select the desired elements.
- <b>returns</b>: A list of strings of the text of the matched elements

Rather than get the list of elements, return a list of the 
text under each element.



#### <span style="font-size:12px;color:#AAAAAA">EmitterWrapper</span> <a style="font-size:16px;" name="-1041905503">getParent</a><span style="font-size:16px;">()</span>
- <b>returns</b>: The parent context. It's safe to call all the JavaScript methods.

Get the parent context of this context. Useful if you need access to the parent page,
or the previous url document.



#### <span style="font-size:12px;color:#AAAAAA">Map&lt;String,String&gt;</span> <a style="font-size:16px;" name="874444513">getPostData</a><span style="font-size:16px;">()</span>
- <b>returns</b>: An, always instatiated, map of key value pairs.

Post data that should be set on a request.  



#### <span style="font-size:12px;color:#AAAAAA">List&lt;String&gt;</span> <a style="font-size:16px;" name="-1271561236">getReText</a><span style="font-size:16px;">(String pPattern)</span>
- <b>pPattern</b>: 
           The Regex Pattern to match.

By default this will return the entire matched string.
If you specify a group in the regular expression, by using
quotes, then the first group will be returned instead.



#### <span style="font-size:12px;color:#AAAAAA">int</span> <a style="font-size:16px;" name="1278664328">getRecord</a><span style="font-size:16px;">()</span>
- <b>returns</b>: The current value of record.

Are we in record mode.  If record is greater than 0, we are and all processing will
stop when we have flushed that number of records.



#### <span style="font-size:12px;color:#AAAAAA">String</span> <a style="font-size:16px;" name="-205233319">getSourceFileName</a><span style="font-size:16px;">()</span>
- <b>returns</b>: The filename

#### <span style="font-size:12px;color:#AAAAAA">String</span> <a style="font-size:16px;" name="1967378682">getUrl</a><span style="font-size:16px;">()</span>
- <b>returns</b>: The URL that the current block is contained by.

Search up the parent chain for the first URL that this block is contained by.



#### <span style="font-size:12px;color:#AAAAAA">String</span> <a style="font-size:16px;" name="-511304457">getWorkingId</a><span style="font-size:16px;">()</span>
- <b>returns</b>: Get the working ID.

The name of the map that all calls to `emitForWorkingId()` will be inserted into



#### <span style="font-size:12px;color:#AAAAAA">List&lt;org.jsoup.nodes.Node&gt;</span> <a style="font-size:16px;" name="1521535043">getXPath</a><span style="font-size:16px;">(String pQuery)</span>
- <b>pQuery</b>: 
           The XPath query.
- <b>returns</b>: A list of nodes that match this XPath expression.

Returns a list of nodes, rather than an Elements section as XPath can return
the text nodes as well as elements.



#### <span style="font-size:12px;color:#AAAAAA">List&lt;String&gt;</span> <a style="font-size:16px;" name="-859866804">getXPathAttr</a><span style="font-size:16px;">(String pQuery, String pAttr)</span>
- <b>pQuery</b>: 
           The XPath query to the elements desired.
- <b>pAttr</b>: 
           The attribute to retrieve from each element.
- <b>returns</b>: A list of strings of the elements that had this attribute and the
        attribute value.

Rather than returning the text associated with a desired element or elements
return a specific attribute from each element as a list.



#### <span style="font-size:12px;color:#AAAAAA">List&lt;String&gt;</span> <a style="font-size:16px;" name="2138633942">getXPathText</a><span style="font-size:16px;">(String pQuery)</span>
- <b>pQuery</b>: 
           The XPath expression to evaluate.
- <b>returns</b>: A list of strings that represent these nodes.

Return a list of strings that match the XPath query.
`toString()` is called on each of the matching nodes.



#### <a style="font-size:16px;" name="410534269">load</a><span style="font-size:16px;">(String pUrl)</span>
- <b>pUrl</b>: 
           The URL to retrieve.

Load a url and store it in the document variable.
Accessible through `getDocument()`.
Also see the get* methods that pull data from this document.



#### <a style="font-size:16px;" name="-1609977486">login</a><span style="font-size:16px;">(String pUrl, String... pKeyValues)</span>
- <b>pUrl</b>: 
           The URL to submit the login to.
- <b>pKeyValues</b>: 
           A splat of key value pairs that will be submitted as part of
           the login

Login to a website. Find the login URL from the action element of the form that 
is used on the login page of that website. 
Typically it's best to use the Chrome nework inspector to copy all of the form values
that are submitted.  This is especially important when you're talking to a .NET application
as they have crazy viewstate parameters that need to be sent. 



#### <a style="font-size:16px;" name="1272621438">print</a><span style="font-size:16px;">(String pMessage)</span>
- <b>pMessage</b>: 
           The string to print

Helper method to print a string to standard out.  Prints at info level.



#### <a style="font-size:16px;" name="-994781719">printDocument</a><span style="font-size:16px;">()</span>

Helper method to print the entire loaded document to standard out. 
Prints at info level.



#### <a style="font-size:16px;" name="405324898">processUrlsJq</a><span style="font-size:16px;">(String pQuery, Function pDealWith)</span>
- <b>pQuery</b>: 
           The CSS query to find the anchor tags to generate the next
           level of detail.
- <b>pDealWith</b>: 
           The callback function used to deal with each new page.


Method finds all the anchor tags that match the query.  For each
anchor tag the URL is loaded and a new Emitter object is created 
and passed back into into the dealWith function.

```JavaScript
pContext.processUrlsJq("a", function(pContext){
    // do some scraping
});
```



#### <a style="font-size:16px;" name="-1092772818">setRecord</a><span style="font-size:16px;">(int pRecord)</span>
- <b>pRecord</b>: 
           The number of records to emit before stopping.

Set the number of records to emit. This also tells the URL loader to
write each request to disk and cache them so when you run this scraper again,
it will hit disk and not the internet.  Good when you need to quickly iterate
through trials of your CSS/XPath/RegEx selectors.



#### <a style="font-size:16px;" name="-1399905258">setUrl</a><span style="font-size:16px;">(String pUrl)</span>
- <b>pUrl</b>: 
           The URL to set.

Set the URL for the current emitter.  Generally set when a document is loaded.



#### <a style="font-size:16px;" name="-1064871666">setWorkingId</a><span style="font-size:16px;">(String workingId)</span>
- <b>workingId</b>: 
           The context working id.

Once the working id is set, all calls to `emitForWorkingId()` will
be put into a map with this ID as a key.



