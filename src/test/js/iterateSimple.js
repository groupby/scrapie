var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://localhost:####/index.html?id=" + pIndex;
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext){
   pContext.emit("title", pContext.getJqText("title"))
});