# CBIRetrieval

## Documentation

https://github.com/loic911/CBIRetrieval/wiki

## Installation

    Download last release from https://github.com/loic911/CBIRetrieval/releases
    unzip CBIRetrieval-*.zip
    
## Quickstart

    # Start 1 server on port 1234
    bin/server.sh config/ConfigServer.prop 1234 &

    # Index image 1,2,3,4,5,6.jpg on this server. We only use one storage (storage "1")
    bin/indexer.sh localhost 1234 images/1.jpg sync 1 1
    bin/indexer.sh localhost 1234 images/2.jpg sync 1 2
    bin/indexer.sh localhost 1234 images/3.jpg sync 1 3
    bin/indexer.sh localhost 1234 images/4.jpg sync 1 4
    bin/indexer.sh localhost 1234 images/5.jpg sync 1 5

    # Search for similar images based on image 1.jpg (ask max 3 images)
    bin/client.sh config/ConfigClient.prop localhost:1234 images/1.jpg 3
    The result may be something like this:

    1 ====> 1.3519999999999987E-4 //first result is the same image, no surprise :-)
    5 ====> 4.633333333333334E-6
    2 ====> 3.168548387096774E-6

# How to run test

* Download redis
* make
* make install
* copy src/redis-server into $CBIRetrievalPATH/testdata/redis

