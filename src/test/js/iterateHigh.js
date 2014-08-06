var urlIterator = new UrlIterator(function(pIndex) {
	if (pIndex < 2) {
		return "http://localhost:####/list?page=" + pIndex;
	} else {
		return null;
	}
});
urlIterator.forEach(function(pContext) {
	pContext.breakIntoSections(".item", function(pContext) {
		var workingId = pContext.getJq("a").attr("href").split("=")[1];
		pContext.setWorkingId(workingId);
		pContext.emitForWorkingId("listUrl", pContext.getUrl());
		processListItem(pContext);
		pContext.emitForWorkingId("id", workingId);
		pContext.processUrlsJq("a", function(pContext) {
			processDetailPage(pContext);
			pContext.flush();
		});
	});
});

function processListItem(pContext) {
	pContext.emitForWorkingId("title", pContext.getJqText("a"));
	pContext.emitForWorkingId("href", pContext.getXPathAttr("a", "href"));
}

function processDetailPage(pContext) {
	pContext.emitForWorkingId("price", pContext.getJqText("#price"));
	pContext.emitForWorkingId("detailUrl", pContext.getUrl());
}
