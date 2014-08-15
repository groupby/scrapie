/**
 * <code>
 * Load a file at iterate through each line.
 * Each line is assumed to be a URL.  The file is loaded relative to the JavaScript file that 
 * is calling the file iterator.
 * 
 *     new FileIterator("idList.txt").forEach(function(pContext){
 *         pContext.emit("title", pContext.getJqText("title"));
 *         pContext.flush();
 *     });
 *     
 * </code>
 * 
 * @param pFilePath
 *            the file to be loaded in. The file path is relative to the calling
 *            JavaScript file.
 */
var FileIterator = function(pFilePath) {

	this.lineIterator = emitter.getLineIterator(pFilePath);

	/**
	 * <code>
	 * Iterate though each line in the file, load the URL and pass that in to
	 * the pDealWith function as a context object.
	 * </code>
	 * 
	 * @param pDealWith
	 *            the method call to be called back after a document is loaded.
	 */
	this.forEach = function(pDealWith) {
		while (this.lineIterator.hasNext() && emitter.keepGoing()) {
			var url = this.lineIterator.nextLine();
			emitter.load(url);
			pDealWith(new EmitterWrapper(emitter));
		}
		this.lineIterator.close();
	}
}