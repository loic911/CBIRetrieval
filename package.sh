#!/bin/bash
NOW=$(date +"%Y%m%d")
rm -f CBIRetrieval*.zip
rm -f bin/*.jar
mvn package -Dmaven.test.skip=true
cp target/Java*with-dependencies.jar bin/
zip -r CBIRetrieval-$NOW.zip *


