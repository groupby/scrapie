/**
 * <code>
 * Generate URLs based on a pattern.  
 * Supports numeric and alpha patterns.
 * 
 * ###Numeric
 * 
 * Defines a numeric range and generates a URL for a sequence.  Values are inclusive.
 * 
 *     new UrlPatternIterator('http://example.com/p=[1,2]').forEach(function(pContext){
 *        print(pContext.getUrl());
 *     })
 *     
 * will produce
 * 
 *     http://example.com/p=1
 *     http://example.com/p=2
 *     
 * ###Alpha
 * 
 * Defines an alpha range between two strings.  
 * 
 *     new UrlPatternIterator('http://example.com/q=[az,bb]').forEach(function(pContext){
 *        print(pContext.getUrl());
 *     })
 *     
 * will produce
 * 
 *     http://example.com/q=az
 *     http://example.com/q=ba
 *     http://example.com/q=bb
 * 
 * </code>
 * 
 * @param pPattern
 *            defines the way this URL will be constructed.
 */
var UrlPatternIterator = function(pPattern) {
	this.urlPatternIterator = emitter.getUrlPatternIterator(pPattern);

	/**
	 * <code>
	 * Calls back on the function pDealWith with each generated URL.
	 * </code>
	 */
	this.forEach = function(pDealWith) {
		while (this.urlPatternIterator.hasNext() && emitter.keepGoing()) {
			var url = this.urlPatternIterator.next();
			emitter.load(url);
			pDealWith(new EmitterWrapper(emitter));
		}
	}
}