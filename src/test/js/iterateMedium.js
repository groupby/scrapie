var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://localhost:####/list?page=" + pIndex;
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
