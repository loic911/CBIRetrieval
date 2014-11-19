export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64/
export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH
#!/bin/bash
# current directory is packaging

ant clean
if [ -d "dist" ]; then
    rm -r dist
fi
mkdir dist
cp -r lib dist/ 

echo Build dist...
ant jar
# Build jar
echo Build full dist...
cp dist/CBIRetrieval.jar packaging/main/
cp -r dist/lib/* packaging/lib/
jar -cvfm dist/CBIRetrievalFull.jar packaging/boot-manifest.mf .
if [ -d "temp" ]; then
    rm -r temp
fi

mkdir temp
unzip -o 'dist/lib/*.jar' -d 'temp'
unzip -o 'dist/CBIRetrieval.jar' -d 'temp'
jar cf dist/CBIRetrievalFull.jar */

# Run test with coverage
ant test-coverage

ant cobertura-coverage-report

mvn sonar:sonar


# Mvn 
