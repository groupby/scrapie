mvn clean assembly:assembly -DskipTests 
mkdir -p dist
cp target/scrapie-*.jar dist/scrapie.jar
cp src/main/bin/scrapie dist
mvn exec:java -Dexec.mainClass="com.wp.scrapie.GenerateReferenceDocs"