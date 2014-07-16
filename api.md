![Scrapie](src/main/images/sheepVerySmall.png) scrAPIe
======= 
A Web Scraper.  
_Not the fatal, degenerative disease that affects the nervous systems of sheep and goats._

JavaScript
----

We decided against coffee script as it didn't seem to make the scraper files any shorter, but it 
did get rid of all the brackets and that made me uneasy.  

Because the scraper files are JavaScript you have a lot of power to do lots of tasks that you
would otherwise normally have to do in an ETL type process.


The [Js.java](src/main/java/com/wp/scrapie/Js.java)  Object is the communication bridge back 
to the Java world.  This is the object that fetches the web documents.  The Js object is put
into the JavaScript context as a variable you can access called emitter.

Two other objects are put into the scope.  

- [UrlIterator.js](src/main/UrlIterator.js), which you can implement to generate URLs.
- [EmitterWrapper.js](src/main/js/EmitterWrapper.js), which wraps the emitter object to give it some more useful JavaScripty syntax.   


Example Usage
-----

```Java
1   var urlIterator = new UrlIterator(function(pIndex){
2      if (pIndex < 2) {
3		  return "http://example.com/index.html?id=" + pIndex;
4	   } else {
5 		  return null;
6	   }
7  });
8  urlIterator.forEach(function(pContext){
9    pContext.emit("title", pContext.getJqText("title"));
10   pContext.flush();
11 });
```

Lines 1 - 7, create the UrlIterator object.  The function passed in to the constructor
is called until the function returns null.  The function is called with an index counter
that is incremented by 1 each time the function finishes.  The UrlIterator is not running,
it will only be called once 