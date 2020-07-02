@echo off
rem Author : Maurizio Hostettler

set GAMEJAR=g04-game-1.0.0-SNAPSHOT.jar
set targetPath=target

set dCount=0
for /F %%x IN ('dir "%targetPath%/dependency/" /b /a:-d') DO (
	set /a dCount+=1
	)
rem There are 6 jars which need to be included
rem Build them if they don't exist
if %dCount% NEQ 6 (
	echo dependencies not complete - building dependencies...
	call mvn clean
	call mvn dependency:copy-dependencies -DincludeScope=compile
	)
	
rem run maven install if the game jar does not exist
if not exist %targetPath%/%GAMEJAR% (
	echo no game.jar found - building project
	call mvn clean
	call mvn package -DskipTests
	)

java -cp "./target/g04-game-1.0.0-SNAPSHOT.jar;./target/dependency/*" org.bitstorm.gameoflife.StandaloneGameOfLife