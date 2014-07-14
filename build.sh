mvn clean assembly:assembly -DskipTests 
rm -rf dist
mkdir dist
cp target/scrapie-*.jar dist/scrapie.jar
cp src/main/bin/scrapie dist