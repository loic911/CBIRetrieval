protoc --java_out=./ Value.proto


build jar:
-clear retrieval/
-put .java in retrieval/
-edit .java and add "package retrieval;"
javac -cp '/home/lrollus/git/CBIRetrieval/lib/protobuf-java-2.5.0.jar'  retrieval/Value.java
jar cf "ValueInfo.jar" retrieval/


