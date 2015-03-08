/**
 * <code>
 * Generate URLs. Emit null to stop the iteration.
 * 
 * The passed in function will be used to get each URL.  Once this function emits null
 * the for each loop will exit.
 * 
 *     var urlIterator = new UrlIterator(function(pIndex){
 *         if (pIndex < 2) {
 *             return "http://localhost:####/index.html?id=" + pIndex;
 *         } else {
 *             return null;
 *         }
 *     });
 *     urlIterator.forEach(function(pContext){
 *         pContext.emit("title", pContext.getJqText("title").get(0));
 *         pContext.flush();
 *     });
 * </code>
 * @param pGenerate The function that will generate each URL in turn.
 */
var UrlIterator = function(pGenerate) {

	this.index = 0;
	this.url = null;
	this.generate = pGenerate;

	/**
	 * <code>
	 * Loop through each of the generated URLs loading each page in turn and passing the context load for 
	 * that page into the pDealWith function.
	 * return true from this method to stop the iteration
	 * </code>
	 * 
	 * @param pDealWith
	 *            the call back function that will be passed in the context of
	 *            the page loaded from the generated URL.
	 * @param prefixUrl
	 *            The prefix url that will be used for figuring out what the max index
	 *            reached was in the last run.
	 */
	this.forEach = function(pDealWith, prefixUrl) {
		var url = pGenerate(this.index);
		if (prefixUrl) {
		    this.index = emitter.findMaxIndex(prefixUrl);
		}
		while ((url = pGenerate(this.index)) != null && emitter.keepGoing()) {
			this.index++;
			emitter.load(url);
			var shouldStop = pDealWith(new EmitterWrapper(emitter));
			if (prefixUrl) {
			    emitter.saveMaxIndex(prefixUrl, this.index);
			}
			if (shouldStop) {
				return;
			}
		}
	}
}