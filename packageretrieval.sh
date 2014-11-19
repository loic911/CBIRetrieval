#!/bin/bash
# indique au système que l'argument qui suit est le programme utilisé pour exécuter ce fichier.
# En cas général les "#" servent à faire des commentaires comme ici
echo Build package...
cd /home/lrollus/Cytomine/TFE/TFEDIST/packaging/
cp ../dist/CBIRetrieval.jar main/
cp ../dist/lib/* lib/
jar -cvfm ../dist/CBIRetrievalFull.jar boot-manifest.mf .


