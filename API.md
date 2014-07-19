![Scrapie](src/main/images/sheepVeryVerySmall.png) scrAPIe
=======

A Web Scraper.  
_Not the fatal, degenerative disease that affects the nervous systems of sheep and goats._

JavaScript
----

We decided against coffee script as it didn't seem to make the scraper files any shorter, but it 
did get rid of all the brackets and that made me uneasy.  

Because the scraper files are JavaScript you have a lot of power to do lots of tasks that you
would otherwise normally have to do in an ETL type process.


The [Emitter.java](src/main/java/com/wp/scrapie/Emitter.java)  Object is the communication bridge back 
to the Java world.  This is the object that fetches the web documents.  The Emitter object is put
into the JavaScript context as a variable you can access called emitter.  Whenever you're given a 
context object, you're getting a wrapped instance of emitter with access to all the parts of the Emitter class
that you need, plus some JavaScript wrappings that make closure possible.

Other JavaScript classes and functions are available.  

- [EmitterWrapper.js](src/main/js/EmitterWrapper.js), which wraps the Emitter object to give it some more useful JavaScripty syntax.
- [FileIterator.js](src/main/js/FileIterator.js), JavaScript backing to a FileLineIterator   
- [GlobalFunctions.js](src/main/js/GlobalFunctions.js), some useful methods like print()
- [UrlIterator.js](src/main/UrlIterator.js), which you can implement to generate URLs.


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

Lines 1 - 7, create the `UrlIterator` object.  The generator function passed in to the constructor
is used to generate each URL.  The generator function is called with an index counter
that is incremented by 1 each time the function is called. 

Line 8 starts the looping over the `URLIterator`.  Inside this function the generator function is 
called retrieving a URL which is then loaded into the context.    
The function passed in to the `forEach` is  passed the context which gives you access to the two 
method calls on lines 9 and 10.

Line 9 emits a key value pair.  The key is `"title"` and the value is the result of the call to
`pContext.getJqText` which calls a function on the emitter that retutrns a `List<String>` 
that match a JQuery like CSS path, in this case, for the HTML tag `<title>`. 
The emitter stores each of the emitted values in a map.
  
Line 10 tells the emitter that it should 
write the map to disk, XML or JSON, and then clear the map.  This way you can build out a record
between pages if you choose by not calling flush after each page.
