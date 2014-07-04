Scrapie
=======

A Web Scraper.

Goals
-----

A scraper that will generate URLs to crawl and convert them into objects we want to keep.

- must not use XML configuration as using XML to parse HTML is an escaping nightmare.
- must understand the concept of multiple of the same objects being created from one big page.
- must be able to log in to password protected sites.
- must be able to understand the concpet of a listing page that goes to a detail page to generate the object or objects.
- must be able to resuse global items across pages.  Maybe back multiple pages.
- the syntax must be as small as possible.

Maybes
------

- A caching layer?

How To
------


###Low Complexity

One object created per page.

 - generate a list of URLs.
 - For each URL
  - Get out the bits of the page you care about and export them to some kind of record collecting thing.

###Medium Complexity

Multiple objects per page.

 - generate a list of urls
 - for each URL
   - create a context that chops the page up into bits.
   - for each page section
     - get out the bits of the section you care about and export them to a record collecting thing.

###High Complexity

