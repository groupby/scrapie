![Scrapie](src/main/images/sheepVerySmall.png) Scrapie
======= 
A Web Scraper.  
_Not the fatal, degenerative disease that affects the nervous systems of sheep and goats._

[ ![Codeship Status for groupby/scrapie](https://codeship.io/projects/1df14350-ef55-0131-b5ae-023491d184db/status)](https://codeship.io/projects/27011)

Goals
-----

A scraper that will generate URLs to crawl and convert them into objects we want to keep.

- must not use XML configuration as using XML to parse HTML is an escaping nightmare.
- must understand the concept of multiple of the same objects being created from one big page.
- must be able to log in to password protected sites.
- must be able to understand the concpet of a listing page that goes to a detail page to generate the object or objects.
- must be able to resuse global items across pages.  Maybe back multiple pages.
- the syntax must be as small as possible.

Choices
------
Under the hood the JavaScript scraper files connect to a Java object that uses Jsoup.  
Jsoup was extended to include XPath support.  
A regular expression matcher is also available.
See the [API Docs](api.md) for a list of all the features. 

Usage
-----

Requires that Java 1.7 is installed and on your path.

```
usage: scrapie
 -f,--file <arg>        The JavaScript file to use
 -o,--output <arg>      The file to output to
 -r,--record <arg>      Record this run and stop after N flushes
 -t,--type <arg>        The record type, json or xml (default)
 -v,--verbosity <arg>   Log Level, trace, debug, info (default)
```

    ./dist/scrapie -f myScraper.js -o records.xml

Examples Scraper Files
------


###Low Complexity

Where ach URL contains one record.

```Java
var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://localhost:####/index.html?id=" + pIndex;
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext){
   pContext.emit("title", pContext.getJqText("title"));
   pContext.flush();
});
```

###Medium Complexity

Where ach URL is a list page with ten items and each item should be emitted as a separate record.

```Java
var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://localhost:####/list?page=" + pIndex;
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext) {
    var sections = pContext.breakIntoSections(".item", function(pContext){
        process(pContext);
        pContext.flush();
    });
});
function process(pContext){
 	var id = pContext.getJq("a").attr("href").split("=")[1];
 	pContext.emit("id", id);
    pContext.emit("title", pContext.getJqText("a"));
}

```

###High Complexity

Where each URL is a list page, each list page has 10 items and each item has a detail page URL with additional info.

```Java
var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://localhost:####/list?page=" + pIndex;
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext) {
	print(pContext.getHtml());
    var sections = pContext.breakIntoSections(".item", function(pContext){
     	var workingId = pContext.getJq("a").attr("href").split("=")[1];
    	pContext.setWorkingId(workingId);
        processListItem(pContext);
     	pContext.emitForWorkingId("id", workingId);
        pContext.processUrlsJq("a", function(pContext){
            processDetailPage(pContext);
            pContext.flush();
        });
    });    
});
function processListItem(pContext){
    pContext.emitForWorkingId("title", pContext.getJqText("a"));
}
function processDetailPage(pContext){
    pContext.emitForWorkingId("price", pContext.getJqText("#price"));
}
```
