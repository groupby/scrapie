var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://localhost:####/list?page=" + pIndex;
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