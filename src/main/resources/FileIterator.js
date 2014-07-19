/**
 * Load a file at iterate through each line.
 * Each line is assumed to be a URL.
 */
var FileIterator = function(pFilePath) {

	this.lineIterator = emitter.getLineIterator(pFilePath);

	this.forEach = function(pDealWith) {
		while (this.lineIterator.hasNext() && emitter.keepGoing()) {
			var url = this.lineIterator.nextLine();
			emitter.load(url);
			pDealWith(new EmitterWrapper(emitter));
		}
		this.lineIterator.close();
	}
}