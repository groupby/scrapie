![Scrapie](src/main/images/sheepVerySmall.png) Scrapie
======= 
A Web Scraper.  
_Not the fatal, degenerative disease that affects the nervous systems of sheep and goats._

[ ![Codeship Status for groupby/scrapie](https://codeship.io/projects/1df14350-ef55-0131-b5ae-023491d184db/status)](https://codeship.io/projects/27011) [![Stories in Ready](https://badge.waffle.io/groupby/scrapie.png?label=ready&title=Ready)](https://waffle.io/groupby/scrapie)
Quickstart
------

1. Make sure Java 1.7 is on your computer and the java command is on your path.
1. Download the [scrapie-latest.zip](scrapie-latest.zip?raw=true) 
1. Unpack it `scrapie-x.x.x` and go into the directory `cd scrapie-x.x.x`
1. run the test 
 - on *nix `./scrapie -f google.js -o google.xml`   
 - on windows `scrapie.bat -f google.js -o google.xml` 

Docs
----

Read the [API.md](API.md) and the [Emitter.md](Emitter.md) to learn about scrapie.

Usage
-----

Requires that Java 1.7 is installed and on your path.

```
usage: scrapie
 -f,--file <arg>        The JavaScript file to use
 -o,--output <arg>      The file to output to
 -r,--record <arg>      Record this run and stop after N records have been emitted
 -c,--cachelessLogin	If in record mode, don't go to the cache for logins.
 -t,--type <arg>        The record type, json or xml (default)
 -v,--verbosity <arg>   Log Level, trace, debug, info (default)
```

    ./scrapie -f myScraper.js -o records.xml

Examples Scraper Files
------


###Low Complexity

Where each URL contains one record.

```JavaScript
var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://www.example.com/index.html?id=" + pIndex;
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

Where each URL is a list page with ten items and each item should be emitted as a separate record.

```JavaScript
var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://www.example.com/list?page=" + pIndex;
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext) {
    pContext.breakIntoSections(".item", function(pContext){
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

```JavaScript
var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://www.example.com/list?page=" + pIndex;
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext) {
	print(pContext.getHtml());
    pContext.breakIntoSections(".item", function(pContext){
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

Goals
-----

A scraper that will generate URLs to crawl and convert them into objects we want to keep.

- must not use XML configuration as using XML to parse HTML is an escaping nightmare.
- must understand the concept of multiple of the same objects being created from one big page.
- must be able to log in to password protected sites.
- must be able to understand the concept of a listing page that goes to a detail page to generate the object or objects.
- must be able to resuse global items across pages.  Maybe back multiple pages.
- the syntax must be as small as possible.
- be threaded (which it isn't yet)

Choices
------
Under the hood the JavaScript scraper files connect to a Java object that uses Jsoup.  
Jsoup was extended to include XPath support.  
A regular expression matcher is also available.
