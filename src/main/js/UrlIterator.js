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
			pDealWith(new Emitter(emitter));
		}
	}
}

function print(pString) {
	emitter.print(pString);
}

function printDocument() {
	emitter.printDocument();
}

function addExcludeValue(pString) {
	emitter.addExcludeValue(pString);
}