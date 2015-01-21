#!/bin/bash
java -cp bin/JavaImageRetrieval.jar retrieval.client.main.RetrievalClientMain $1 $2 $3 $4 $5

     # Param0: Config client file
     # Param1: Servers (host:port) list (commat sep list: host1:port1,host2:port2,...)
     # Param2: Image path/url
     # Param3: Maximum similar pictures 
     # Param4: (Optional) Storages name (commat sep: test,mystorage,...)
