var urlIterator = new UrlPatternIterator("http://localhost:####/index.html?id=[0,2]");
urlIterator.forEach(function(pContext){
   pContext.emit("url", pContext.getUrl());
   pContext.flush();
});