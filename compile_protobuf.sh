#!/bin/bash
protoc -I=protobuf/ --java_out=Protocol/src/main/java/ protobuf/*.proto
echo Protobuf files has been compiled.