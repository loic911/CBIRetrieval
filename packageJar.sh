unzip -o 'dist/lib/*.jar' -d 'temp'
unzip -o 'dist/CBIRetrieval.jar' -d 'temp'
cd temp; jar cf ../dist/CBIRetrievalFull.jar */
cd ..; rm -r temp
