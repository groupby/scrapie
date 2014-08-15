![Emitter](src/main/images/sheepVeryVerySmall.png) FileIterator.js
=====


###Constructors
- [FileIterator(pFilePath)](#-1342096458)

###Methods
- [forEach(pDealWith)](#858050053) 


<a name="-1342096458">FileIterator</a>(pFilePath)
-----

- <b>pFilePath</b>: the file to be loaded in. The file path is relative to the calling JavaScript file.

Load a file at iterate through each line.
Each line is assumed to be a URL.  The file is loaded relative to the JavaScript file that 
is calling the file iterator.

    new FileIterator("idList.txt").forEach(function(pContext){
        pContext.emit("title", pContext.getJqText("title"));
        pContext.flush();
    });

#### <a style="font-size:16px;" name="858050053">forEach</a><span style="font-size:16px;">(pDealWith)</span>
- <b>pDealWith</b>: the method call to be called back after a document is loaded.

Iterate though each line in the file, load the URL and pass that in to
the pDealWith function as a context object.

