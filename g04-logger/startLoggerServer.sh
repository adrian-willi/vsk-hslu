#!/bin/sh
# Author : Maurizio Hostettler

SERVERJAR="g04-loggerserver-1.0.0-SNAPSHOT.jar"
targetPath="logger-server/target"

dCount=0
for f in ./$targetPath/dependency/*.jar
do
	dCount=`expr $dCount + 1`
done

# There are 5 jars which need to be included
# Build them if they don't exist
if [ $dCount != 5 ] 
then
	echo dependencies not complete - building dependencies...
	mvn dependency:copy-dependencies -DincludeScope=compile
fi

# run maven install if the server jar does not exist
if [ ! -f $targetPath/$SERVERJAR ]
then 
	echo no server.jar found - building project
	mvn package -DskipTests
fi
java -cp "./logger-server/target/g04-loggerserver-1.0.0-SNAPSHOT.jar;./logger-server/target/dependency/*" ch.hslu.vsk.logger.server.LoggerServer