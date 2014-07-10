var Emitter = function(pEmitter){
	this.emitter = pEmitter;

	this.breakIntoSections = function(pQuery, pDealWith){
		var sections = this.emitter.breakIntoSections(pQuery);
		for (var i = 0; i < sections.size(); i++){
			sectionEmitter = new Emitter(sections.get(i));
			pDealWith(sectionEmitter);
			sectionEmitter.emitter.flush();
		}
	};
	this.processUrlsJq = function(pQuery, pDealWith){
		var pages = this.emitter.processUrlsJq(pQuery);
		for (var i = 0; i < pages.size(); i++){
			pageEmitter = new Emitter(pages.get(i));
			pDealWith(pageEmitter);
			pageEmitter.emitter.flush();
		}
	};
	this.emit = function(pKey, pValue){
		this.emitter.emit(pKey, pValue);
	};
	this.emitForWorkingId = function(pKey, pValue){
		this.emitter.emitForWorkingId(pKey, pValue);
	};
	this.getJqText = function(pQuery){
		return this.emitter.getJqText(pQuery);
	};
	this.getJq = function(pQuery){
		return this.emitter.getJq(pQuery);
	};
	this.getXPath = function(pQuery){
		return this.emitter.getXPath(pQuery);
	};
	this.getXPathText = function(pQuery){
		return this.emitter.getXPathText(pQuery);
	};
	this.setWorkingId = function(pWorkingId) {
		this.emitter.setWorkingId(pWorkingId);
	};
	this.getDocument = function(){
		return this.emitter.getDocument();
	}
}
var UrlIterator = function(pGenerate) {
	
	this.index = 0;
	this.url = null;
	this.generate = pGenerate;
	
	this.forEach = function(pDealWith){
		var url = null;
		while( (url = pGenerate(this.index)) != null){
			this.index++;
			emitter.loadDom(url);
			pDealWith(new Emitter(emitter));
			emitter.flush();
		}
	}
	

}

function print(pString){
	emitter.print(pString);
}