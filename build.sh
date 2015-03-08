#!/bin/sh
echo "building"
version=`cat pom.xml | grep -1 "<artifactId>scrapie</artifactId>" | egrep -o "[0-9]+\.[0-9]+\.[0-9]+" | tr -d "\n"`
rm -rf target
cp -f src/main/java/com/wp/scrapie/Emitter.java src/main/resources
echo "building jar"
mvn --quiet clean assembly:assembly -DskipTests 2>&1 | grep -v "jdk" | grep -v "encoding"
mkdir -p target/scrapie-$version
cp target/scrapie-*.jar target/scrapie-$version/scrapie.jar
cp src/main/bin/* target/scrapie-$version
cp src/test/js/google.js target/scrapie-$version/google.js
if [ "$1" != "nodocs" ]; then
    echo "building docs"
    mvn --quiet exec:java -Dexec.mainClass="com.wp.scrapie.GenerateReferenceDocs" 2>&1 | grep -v "GenerateReferenceDocs" | grep -v "jdk"| grep -v "encoding"
fi
echo "zipping"
zip -qr target/scrapie-${version}.zip target/scrapie-$version
cp target/scrapie-${version}/scrapie.jar /cygdrive/e/crawls/scrapieScripts/
rm src/main/resources/Emitter.java
echo "deployed"