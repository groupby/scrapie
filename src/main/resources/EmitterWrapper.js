/**
 * <code>
 * Wrapper class for Js.java so that we can provide iterator type functionality.
 * </code>
 * @internal
 */
var EmitterWrapper = function(pEmitter) {
	this.emitter = pEmitter;

	this.breakIntoSections = function(pQuery, pDealWith) {
		var sections = this.emitter.breakIntoSections(pQuery);
		for ( var i = 0; i < sections.size() && emitter.keepGoing(); i++) {
			sectionEmitter = new EmitterWrapper(sections.get(i));
			pDealWith(sectionEmitter);
		}
	};
	this.processUrlsJq = function(pQuery, pDealWith) {
		var pages = this.emitter.processUrlsJq(pQuery);
		for ( var i = 0; i < pages.size() && emitter.keepGoing(); i++) {
			pageEmitter = new EmitterWrapper(pages.get(i));
			pDealWith(pageEmitter);
		}
	};
	this.getParent = function() {
		return new EmitterWrapper(this.emitter.getParent());
	};
	/**
	 * This section is replaced with generated code. Essentially all the public
	 * methods of Emitter.java that are not tagged with DontGenerate are
	 * generated here using source code inspection.
	 */
	// GENERATED
	// GENERATED
}