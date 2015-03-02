#!/bin/sh
version=`cat pom.xml | grep -1 "<artifactId>scrapie</artifactId>" | egrep -o "[0-9]+\.[0-9]+\.[0-9]+" | tr -d "\n"`
rm -rf target
cp -f src/main/java/com/wp/scrapie/Emitter.java src/main/resources
mvn --quiet clean assembly:assembly -DskipTests
mkdir -p target/scrapie-$version
cp target/scrapie-*.jar target/scrapie-$version/scrapie.jar
cp src/main/bin/* target/scrapie-$version
cp src/test/js/google.js target/scrapie-$version/google.js
mvn --quiet exec:java -Dexec.mainClass="com.wp.scrapie.GenerateReferenceDocs"
zip -qr target/scrapie-${version}.zip target/scrapie-$version
cp target/scrapie-${version}/scrapie.jar ../scrapieScripts/
