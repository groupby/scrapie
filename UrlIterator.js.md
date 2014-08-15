![Emitter](src/main/images/sheepVeryVerySmall.png) UrlIterator.js
=====


###Constructors
- [UrlIterator(pGenerate)](#-445862417)

###Methods
- [forEach(pDealWith)](#858050053) 


<a name="-445862417">UrlIterator</a>(pGenerate)
-----

- <b>pGenerate</b>: The function that will generate each URL in turn.

Generate URLs. Emit null to stop the iteration.

The passed in function will be used to get each URL.  Once this function emits null
the for each loop will exit.

    var urlIterator = new UrlIterator(function(pIndex){
        if (pIndex < 2) {
            return "http://localhost:####/index.html?id=" + pIndex;
        } else {
            return null;
        }
    });
    urlIterator.forEach(function(pContext){
        pContext.emit("title", pContext.getJqText("title").get(0));
        pContext.flush();
    });

#### <a style="font-size:16px;" name="858050053">forEach</a><span style="font-size:16px;">(pDealWith)</span>
- <b>pDealWith</b>: the call back function that will be passed in the context of the page loaded from the generated URL.

Loop through each of the generated URLs loading each page in turn and passing the context load for 
that page into the pDealWith function.

