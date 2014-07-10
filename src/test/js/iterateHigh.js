var urlIterator = new UrlIterator(function(pIndex){
    if (pIndex < 2) {
		return "http://localhost:####/list?page=" + pIndex;
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext) {
    var sections = pContext.breakIntoSections(".item", function(pContext){
    	pContext.setWorkingId(workingId);
        processListItem(pContext);
     	var workingId = pContext.getJq("a").attr("href").split("=")[1];
     	pContext.emitForWorkingId("id", workingId);
        pContext.processUrlsJq("a", function(pContext){
            processDetailPage(pContext);
        });
    });    
});


function processListItem(pContext){
    pContext.emitForWorkingId("title", pContext.getJqText("a"));
}

function processDetailPage(pContext){
    pContext.emitForWorkingId("price", pContext.getJqText("#price"));
}
