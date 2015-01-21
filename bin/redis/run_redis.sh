#!/bin/bash
BASEDIR=$(dirname $0)
cd $BASEDIR/
echo "Running redis..."
redis-server redis.conf
