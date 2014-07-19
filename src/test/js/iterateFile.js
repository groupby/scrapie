var fileIterator = new FileIterator("idList.txt");
fileIterator.forEach(function(pContext){
   pContext.emit("title", pContext.getJqText("title"));
   pContext.flush();
});