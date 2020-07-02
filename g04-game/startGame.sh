#!/bin/sh
# Author : Maurizio Hostettler

GAMEJAR="g04-game-1.0.0-SNAPSHOT.jar"
targetPath="target"

dCount=0
for f in ./$targetPath/dependency/*.jar
do
	dCount=`expr $dCount + 1`
done

# There are 5 jars which need to be included
# Build them if they don't exist
if [ $dCount != 6 ] 
then
	echo dependencies not complete - building dependencies...
	mvn clean
	mvn dependency:copy-dependencies -DincludeScope=compile
fi

# run maven install if the game jar does not exist
if [ ! -f $targetPath/$GAMEJAR ]
then 
	echo no game.jar found - building project
	mvn clean
	mvn package -DskipTests
fi

java -cp "./target/g04-game-1.0.0-SNAPSHOT.jar;./target/dependency/*" org.bitstorm.gameoflife.StandaloneGameOfLife