![Emitter](src/main/images/sheepVeryVerySmall.png) UrlPatternIterator.js
=====


###Constructors
- [UrlPatternIterator(pPattern)](#1614101520)

###Methods
- [forEach(pDealWith)](#858050053) 


<a name="1614101520">UrlPatternIterator</a>(pPattern)
-----

- <b>pPattern</b>: defines the way this URL will be constructed.

Generate URLs based on a pattern.  
Supports numeric and alpha patterns.

###Numeric

Defines a numeric range and generates a URL for a sequence.  Values are inclusive.

    new UrlPatternIterator('http://example.com/p=[1,2]').forEach(function(pContext){
       print(pContext.getUrl());
    })
    
will produce

    http://example.com/p=1
    http://example.com/p=2
    
###Alpha

Defines an alpha range between two strings.  

    new UrlPatternIterator('http://example.com/q=[az,bb]').forEach(function(pContext){
       print(pContext.getUrl());
    })
    
will produce

    http://example.com/q=az
    http://example.com/q=ba
    http://example.com/q=bb

#### <a style="font-size:16px;" name="858050053">forEach</a><span style="font-size:16px;">(pDealWith)</span>
Calls back on the function pDealWith with each generated URL.

