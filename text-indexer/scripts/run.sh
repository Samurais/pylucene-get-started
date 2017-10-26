#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
JAR=target/text-indexer-1.0-SNAPSHOT-jar-with-dependencies.jar

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/..
if [ ! -f $JAR ]; then
    echo "first run $baseDir/package.sh to build jar."
    exit 1
fi
java -cp $JAR lucene.text.FileIndexing $1 $2