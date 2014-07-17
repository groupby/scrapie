![Scrapie](../src/main/images/sheepVerySmall.png) Js
=====


Main interface class between the Java world and the JavaScript world




###Constructors
- [Js()](#2316330)

###Methods
- [addExcludeValue(String pValue)](#1526343849)
- [breakIntoSections(String pQuery)](#70747965)
- [emit(String pKey, List pValue)](#856545347)
- [emit(String pKey, String pValue)](#-411319946)
- [emitForWorkingId(String pKey, List pValue)](#294587270)
- [emitForWorkingId(String pKey, String pValue)](#712847353)
- [emitForWorkingId(String pKey, String pValue)](#712847353)
- [flush()](#-760377595)
- [getCookies()](#216551706)
- [getDocument()](#-1112689838)
- [getHtml()](#488960226)
- [getJq(String pQuery)](#1932918093)
- [getJqAttr(String pQuery, String pAttr)](#-51822634)
- [getJqText(String pQuery)](#-1192716576)
- [getParent()](#-1041905503)
- [getPostData()](#874444513)
- [getReText(String pPattern)](#-1271561236)
- [getRecord()](#1278664328)
- [getRecords()](#983960589)
- [getSourceFileName()](#-205233319)
- [getWorkingId()](#-511304457)
- [getWriter()](#231446058)
- [getXPath(String pQuery)](#1521535043)
- [getXPathText(String pQuery)](#2138633942)
- [isRecord()](#-1592506276)
- [keepGoing()](#-44872394)
- [load(String pUrl)](#410534269)
- [login(String pUrl, String pKeyValues)](#-1781132244)
- [print(String pString)](#1360788028)
- [printDocument()](#-994781719)
- [processUrlsJq(String pQuery)](#1836584528)
- [run(String pJs, Writer pWriter)](#336624440)
- [runFile(String pSrc, Writer pWriter)](#-598366243)
- [setCookies(Map pCookies)](#1384324107)
- [setDocument(Element doc)](#175956064)
- [setParent(Js parent)](#1391625220)
- [setPostData(Map pPostData)](#-1860901489)
- [setRecord(int pRecord)](#-1092772818)
- [setRecordCount(int pRecordCount)](#-623969762)
- [setRecords(Map pRecords)](#1456381227)
- [setSourceFileName(String pSourceFileName)](#817008066)
- [setWorkingId(String workingId)](#-1064871666)
- [setWriter(Writer pWriter)](#1581417664)


<a name="2316330">Js</a>()
-----


#### <a style="font-size:16px;" name="1526343849">addExcludeValue</a><span style="font-size:16px;">(String pValue)</span>

#### <span style="font-size:12px;color:#AAAAAA">List&lt;com.wp.scrapie.Js&gt;</span> <a style="font-size:16px;" name="70747965">breakIntoSections</a><span style="font-size:16px;">(String pQuery)</span>

#### <a style="font-size:16px;" name="856545347">emit</a><span style="font-size:16px;">(String pKey, List pValue)</span>

#### <a style="font-size:16px;" name="-411319946">emit</a><span style="font-size:16px;">(String pKey, String pValue)</span>

#### <a style="font-size:16px;" name="294587270">emitForWorkingId</a><span style="font-size:16px;">(String pKey, List pValue)</span>

#### <a style="font-size:16px;" name="712847353">emitForWorkingId</a><span style="font-size:16px;">(String pKey, String pValue)</span>

#### <a style="font-size:16px;" name="712847353">emitForWorkingId</a><span style="font-size:16px;">(String pKey, String pValue)</span>

#### <a style="font-size:16px;" name="-760377595">flush</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">Map&lt;string,string&gt;</span> <a style="font-size:16px;" name="216551706">getCookies</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">org.jsoup.nodes.Element</span> <a style="font-size:16px;" name="-1112689838">getDocument</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">string</span> <a style="font-size:16px;" name="488960226">getHtml</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">org.jsoup.select.Elements</span> <a style="font-size:16px;" name="1932918093">getJq</a><span style="font-size:16px;">(String pQuery)</span>

#### <span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="-51822634">getJqAttr</a><span style="font-size:16px;">(String pQuery, String pAttr)</span>

#### <span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="-1192716576">getJqText</a><span style="font-size:16px;">(String pQuery)</span>

#### <a style="font-size:16px;" name="-1041905503">getParent</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">Map&lt;string,string&gt;</span> <a style="font-size:16px;" name="874444513">getPostData</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="-1271561236">getReText</a><span style="font-size:16px;">(String pPattern)</span>

#### <span style="font-size:12px;color:#AAAAAA">int</span> <a style="font-size:16px;" name="1278664328">getRecord</a><span style="font-size:16px;">()</span>

Are we in record mode.  If record is greater than 0, we are and all processing will
stop when we have flushed that number of records.



#### <span style="font-size:12px;color:#AAAAAA">Map&lt;string,Map&lt;string,Set&lt;string&gt;&gt;&gt;</span> <a style="font-size:16px;" name="983960589">getRecords</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">string</span> <a style="font-size:16px;" name="-205233319">getSourceFileName</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">string</span> <a style="font-size:16px;" name="-511304457">getWorkingId</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">java.io.Writer</span> <a style="font-size:16px;" name="231446058">getWriter</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">List&lt;org.jsoup.nodes.Node&gt;</span> <a style="font-size:16px;" name="1521535043">getXPath</a><span style="font-size:16px;">(String pQuery)</span>

#### <span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="2138633942">getXPathText</a><span style="font-size:16px;">(String pQuery)</span>

#### <span style="font-size:12px;color:#AAAAAA">boolean</span> <a style="font-size:16px;" name="-1592506276">isRecord</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">boolean</span> <a style="font-size:16px;" name="-44872394">keepGoing</a><span style="font-size:16px;">()</span>

#### <a style="font-size:16px;" name="410534269">load</a><span style="font-size:16px;">(String pUrl)</span>

#### <a style="font-size:16px;" name="-1781132244">login</a><span style="font-size:16px;">(String pUrl, String pKeyValues)</span>

#### <a style="font-size:16px;" name="1360788028">print</a><span style="font-size:16px;">(String pString)</span>

#### <a style="font-size:16px;" name="-994781719">printDocument</a><span style="font-size:16px;">()</span>

#### <span style="font-size:12px;color:#AAAAAA">List&lt;com.wp.scrapie.Js&gt;</span> <a style="font-size:16px;" name="1836584528">processUrlsJq</a><span style="font-size:16px;">(String pQuery)</span>

#### <a style="font-size:16px;" name="336624440">run</a><span style="font-size:16px;">(String pJs, Writer pWriter)</span>

#### <a style="font-size:16px;" name="-598366243">runFile</a><span style="font-size:16px;">(String pSrc, Writer pWriter)</span>

Write a file



#### <a style="font-size:16px;" name="1384324107">setCookies</a><span style="font-size:16px;">(Map pCookies)</span>

#### <a style="font-size:16px;" name="175956064">setDocument</a><span style="font-size:16px;">(Element doc)</span>

#### <a style="font-size:16px;" name="1391625220">setParent</a><span style="font-size:16px;">(Js parent)</span>

#### <a style="font-size:16px;" name="-1860901489">setPostData</a><span style="font-size:16px;">(Map pPostData)</span>

#### <a style="font-size:16px;" name="-1092772818">setRecord</a><span style="font-size:16px;">(int pRecord)</span>
- <b>pRecord</b>: 

Set the number of records to emit. This also tells the URL loader to
write each request to disk.



#### <a style="font-size:16px;" name="-623969762">setRecordCount</a><span style="font-size:16px;">(int pRecordCount)</span>

#### <a style="font-size:16px;" name="1456381227">setRecords</a><span style="font-size:16px;">(Map pRecords)</span>

#### <a style="font-size:16px;" name="817008066">setSourceFileName</a><span style="font-size:16px;">(String pSourceFileName)</span>

#### <a style="font-size:16px;" name="-1064871666">setWorkingId</a><span style="font-size:16px;">(String workingId)</span>

#### <a style="font-size:16px;" name="1581417664">setWriter</a><span style="font-size:16px;">(Writer pWriter)</span>

