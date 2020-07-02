@echo off
rem Author : Maurizio Hostettler

set CLIENTJAR=g04-loggerclient-1.0.0-SNAPSHOT.jar
set targetPath=logger-client/target

set dCount=0
for /F %%x IN ('dir "%targetPath%/dependency/" /b /a:-d') DO (
	set /a dCount+=1
	)
rem There are 6 jars which need to be included
rem Build them if they don't exist
if %dCount% NEQ 6 (
	echo dependencies not complete - building dependencies...
	call mvn clean	
	call mvn package -DskipTests
	call mvn dependency:copy-dependencies -DincludeScope=compile
	)
	
rem run maven install if the client jar does not exist
if not exist %targetPath%/%CLIENTJAR% (
	echo no client.jar found - building project
	call mvn clean
	call mvn package -DskipTests
	call mvn dependency:copy-dependencies -DincludeScope=compile
	)
	
java -cp "./logger-client/target/%CLIENTJAR%;./logger-client/target/dependency/*" ch.hslu.vsk.logger.client.LogViewerClient
pause