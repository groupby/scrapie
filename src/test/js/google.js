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