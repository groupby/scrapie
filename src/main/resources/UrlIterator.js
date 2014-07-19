/**
 * Generate URLs. Emit null to stop the iteration.
 */
var UrlIterator = function(pGenerate) {

	this.index = 0;
	this.url = null;
	this.generate = pGenerate;

	this.forEach = function(pDealWith) {
		var url = null;
		while ((url = pGenerate(this.index)) != null && emitter.keepGoing()) {
			this.index++;
			emitter.load(url);
			pDealWith(new EmitterWrapper(emitter));
		}
	}
}