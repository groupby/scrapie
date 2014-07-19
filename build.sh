version=`cat pom.xml | grep -1 "<artifactId>scrapie</artifactId>" | egrep -o "[0-9]+\.[0-9]+\.[0-9]+" | tr -d "\n"`
rm -rf target
rm -rf scrapie-* 
cp -f src/main/java/com/wp/scrapie/Emitter.java src/main/resources
mvn --quiet clean assembly:assembly -DskipTests
mkdir -p scrapie-$version
cp target/scrapie-*.jar scrapie-$version/scrapie.jar
cp src/main/bin/* scrapie-$version
cp src/test/js/google.js scrapie-$version/google.js
cp -r src/test/groceryGatewayFiles scrapie-$version/.google.js_data
mvn --quiet exec:java -Dexec.mainClass="com.wp.scrapie.GenerateReferenceDocs"
zip -qr scrapie-latest.zip scrapie-$version