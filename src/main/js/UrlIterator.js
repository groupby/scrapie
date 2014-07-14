var Emitter = function(pEmitter){
	this.emitter = pEmitter;

	this.breakIntoSections = function(pQuery, pDealWith){
		var sections = this.emitter.breakIntoSections(pQuery);
		for (var i = 0; i < sections.size() && emitter.keepGoing(); i++){
			sectionEmitter = new Emitter(sections.get(i));
			pDealWith(sectionEmitter);
		}
	};
	this.flush = function(){
		this.emitter.flush();
	};
	this.processUrlsJq = function(pQuery, pDealWith){
		var pages = this.emitter.processUrlsJq(pQuery);
		for (var i = 0; i < pages.size() && emitter.keepGoing(); i++){
			pageEmitter = new Emitter(pages.get(i));
			pDealWith(pageEmitter);
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
	this.getJqAttr = function(pQuery, pAttr){
		return this.emitter.getJqAttr(pQuery, pAttr);
	};
	this.getXPath = function(pQuery){
		return this.emitter.getXPath(pQuery);
	};
	this.getXPathText = function(pQuery){
		return this.emitter.getXPathText(pQuery);
	};
	this.getReText = function(pQuery){
		return this.emitter.getReText(pQuery);
	};
	this.setWorkingId = function(pWorkingId) {
		this.emitter.setWorkingId(pWorkingId);
	};
	this.getDocument = function(){
		return this.emitter.getDocument();
	}
	this.print = function(pStr){
		return this.emitter.print(pStr);
	}
	this.printDocument = function(){
		return this.emitter.printDocument();
	}
}
var UrlIterator = function(pGenerate) {
	
	this.index = 0;
	this.url = null;
	this.generate = pGenerate;
	
	this.forEach = function(pDealWith){
		var url = null;
		while( (url = pGenerate(this.index)) != null && emitter.keepGoing()){
			this.index++;
			emitter.load(url);
			pDealWith(new Emitter(emitter));
		}
	}
	

}

function print(pString){
	emitter.print(pString);
}

function printDocument(){
    emitter.printDocument();
}

function addExcludeValue(pString){
	emitter.addExcludeValue(pString);
}