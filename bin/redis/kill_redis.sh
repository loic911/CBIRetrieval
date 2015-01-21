#!/bin/bash
BASEDIR=$(dirname $0)
cd $BASEDIR/
echo "Kill redis..."
redis-cli shutdown

