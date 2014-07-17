/**
 * We highly recommend you don't crawl google as they will block your 
 * IP and then you'll be sad.
 */
var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 1) {
		return "https://www.google.com";
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext){
   pContext.emit("title", pContext.getJqText("title"));
   pContext.flush();
});