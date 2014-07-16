Js
=====


Main interface class between the java world and the javascript world




###Constructors
- [Js()](#2316330)

###Methods
- [addExcludeValue(string pValue)](#-1766630263)
- [breakIntoSections(string pQuery)](#1072741149)
- [emit(string pKey, List pValue)](#-1189644189)
- [emit(string pKey, string pValue)](#1297550710)
- [emitForWorkingId(string pKey, List pValue)](#-1751602266)
- [emitForWorkingId(string pKey, string pValue)](#-1873249287)
- [emitForWorkingId(string pKey, string pValue)](#-1873249287)
- [flush()](#-760377595)
- [getCookies()](#216551706)
- [getDocument()](#-1112689838)
- [getHtml()](#488960226)
- [getJq(string pQuery)](#-1360056019)
- [getJqAttr(string pQuery, string pAttr)](#-412419114)
- [getJqText(string pQuery)](#-190723392)
- [getParent()](#-1041905503)
- [getPostData()](#874444513)
- [getReText(string pPattern)](#-428785716)
- [getRecord()](#1278664328)
- [getRecords()](#983960589)
- [getWorkingId()](#-511304457)
- [getWriter()](#231446058)
- [getXPath(string pQuery)](#-1771439069)
- [getXPathText(string pQuery)](#-1154340170)
- [isRecord()](#-1592506276)
- [keepGoing()](#-44872394)
- [load(string pUrl)](#246213981)
- [login(string pUrl, string pKeyValues)](#-987013076)
- [print(string pString)](#-1937161636)
- [printDocument()](#-994781719)
- [processUrlsJq(string pQuery)](#-1456389584)
- [run(string pJs, Writer pWriter)](#1043501912)
- [runFile(string pSrc, Writer pWriter)](#-160001091)
- [setCookies(Map pCookies)](#1384324107)
- [setDocument(Element doc)](#175956064)
- [setFlushCount(int pFlushCount)](#1418642554)
- [setParent(Js parent)](#1391625220)
- [setPostData(Map pPostData)](#-1860901489)
- [setRecord(int pRecord)](#-1092772818)
- [setRecords(Map pRecords)](#1456381227)
- [setWorkingId(string workingId)](#-708634322)
- [setWriter(Writer pWriter)](#1581417664)


<a name="2316330">Js</a>()
-----


<a style="font-size:16px;" name="-1766630263">addExcludeValue</a><span style="font-size:16px;">(string pValue)</span>
------


<span style="font-size:12px;color:#AAAAAA">List&lt;com.wp.scrapie.Js&gt;</span> <a style="font-size:16px;" name="1072741149">breakIntoSections</a><span style="font-size:16px;">(string pQuery)</span>
------


<a style="font-size:16px;" name="-1189644189">emit</a><span style="font-size:16px;">(string pKey, List pValue)</span>
------


<a style="font-size:16px;" name="1297550710">emit</a><span style="font-size:16px;">(string pKey, string pValue)</span>
------


<a style="font-size:16px;" name="-1751602266">emitForWorkingId</a><span style="font-size:16px;">(string pKey, List pValue)</span>
------


<a style="font-size:16px;" name="-1873249287">emitForWorkingId</a><span style="font-size:16px;">(string pKey, string pValue)</span>
------


<a style="font-size:16px;" name="-1873249287">emitForWorkingId</a><span style="font-size:16px;">(string pKey, string pValue)</span>
------


<a style="font-size:16px;" name="-760377595">flush</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">Map&lt;string,string&gt;</span> <a style="font-size:16px;" name="216551706">getCookies</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">org.jsoup.nodes.Element</span> <a style="font-size:16px;" name="-1112689838">getDocument</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">string</span> <a style="font-size:16px;" name="488960226">getHtml</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">org.jsoup.select.Elements</span> <a style="font-size:16px;" name="-1360056019">getJq</a><span style="font-size:16px;">(string pQuery)</span>
------


<span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="-412419114">getJqAttr</a><span style="font-size:16px;">(string pQuery, string pAttr)</span>
------


<span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="-190723392">getJqText</a><span style="font-size:16px;">(string pQuery)</span>
------


<a style="font-size:16px;" name="-1041905503">getParent</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">Map&lt;string,string&gt;</span> <a style="font-size:16px;" name="874444513">getPostData</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="-428785716">getReText</a><span style="font-size:16px;">(string pPattern)</span>
------


<span style="font-size:12px;color:#AAAAAA">int</span> <a style="font-size:16px;" name="1278664328">getRecord</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">Map&lt;string,Map&lt;string,Set&lt;string&gt;&gt;&gt;</span> <a style="font-size:16px;" name="983960589">getRecords</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">string</span> <a style="font-size:16px;" name="-511304457">getWorkingId</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">java.io.Writer</span> <a style="font-size:16px;" name="231446058">getWriter</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">List&lt;org.jsoup.nodes.Node&gt;</span> <a style="font-size:16px;" name="-1771439069">getXPath</a><span style="font-size:16px;">(string pQuery)</span>
------


<span style="font-size:12px;color:#AAAAAA">List&lt;string&gt;</span> <a style="font-size:16px;" name="-1154340170">getXPathText</a><span style="font-size:16px;">(string pQuery)</span>
------


<span style="font-size:12px;color:#AAAAAA">boolean</span> <a style="font-size:16px;" name="-1592506276">isRecord</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">boolean</span> <a style="font-size:16px;" name="-44872394">keepGoing</a><span style="font-size:16px;">()</span>
------


<a style="font-size:16px;" name="246213981">load</a><span style="font-size:16px;">(string pUrl)</span>
------


<a style="font-size:16px;" name="-987013076">login</a><span style="font-size:16px;">(string pUrl, string pKeyValues)</span>
------


<a style="font-size:16px;" name="-1937161636">print</a><span style="font-size:16px;">(string pString)</span>
------


<a style="font-size:16px;" name="-994781719">printDocument</a><span style="font-size:16px;">()</span>
------


<span style="font-size:12px;color:#AAAAAA">List&lt;com.wp.scrapie.Js&gt;</span> <a style="font-size:16px;" name="-1456389584">processUrlsJq</a><span style="font-size:16px;">(string pQuery)</span>
------


<a style="font-size:16px;" name="1043501912">run</a><span style="font-size:16px;">(string pJs, Writer pWriter)</span>
------


<a style="font-size:16px;" name="-160001091">runFile</a><span style="font-size:16px;">(string pSrc, Writer pWriter)</span>
------


<a style="font-size:16px;" name="1384324107">setCookies</a><span style="font-size:16px;">(Map pCookies)</span>
------


<a style="font-size:16px;" name="175956064">setDocument</a><span style="font-size:16px;">(Element doc)</span>
------


<a style="font-size:16px;" name="1418642554">setFlushCount</a><span style="font-size:16px;">(int pFlushCount)</span>
------


<a style="font-size:16px;" name="1391625220">setParent</a><span style="font-size:16px;">(Js parent)</span>
------


<a style="font-size:16px;" name="-1860901489">setPostData</a><span style="font-size:16px;">(Map pPostData)</span>
------


<a style="font-size:16px;" name="-1092772818">setRecord</a><span style="font-size:16px;">(int pRecord)</span>
------


<a style="font-size:16px;" name="1456381227">setRecords</a><span style="font-size:16px;">(Map pRecords)</span>
------


<a style="font-size:16px;" name="-708634322">setWorkingId</a><span style="font-size:16px;">(string workingId)</span>
------


<a style="font-size:16px;" name="1581417664">setWriter</a><span style="font-size:16px;">(Writer pWriter)</span>
------


