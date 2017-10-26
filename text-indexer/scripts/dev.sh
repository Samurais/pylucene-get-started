#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/..
#mvn clean compile package
rm -rf src/test/resources/index/*
#mvn exec:java -Dexec.mainClass="lucene.text.FileIndexing" -Dexec.args="foo"
$baseDir/package.sh
$baseDir/run.sh $baseDir/../src/test/resources/text $baseDir/../src/test/resources/index