@echo off
rem Author : Maurizio Hostettler

set SERVERJAR=g04-loggerserver-1.0.0-SNAPSHOT.jar
set targetPath=logger-server/target

set dCount=0
for /F %%x IN ('dir "%targetPath%/dependency/" /b /a:-d') DO (
	set /a dCount+=1
	)
rem There are 5 jars which need to be included
rem Build them if they don't exist
if %dCount% NEQ 5 (
	call mvn clean	
	call mvn package -DskipTests
	call mvn dependency:copy-dependencies -DincludeScope=compile
	)
	
rem run maven install if the server jar does not exist
if not exist %targetPath%/%SERVERJAR% (
	call mvn clean	
	call mvn package -DskipTests
	call mvn dependency:copy-dependencies -DincludeScope=compile
	)
	
java -cp "./logger-server/target/%SERVERJAR%;./logger-server/target/dependency/*" ch.hslu.vsk.logger.server.LoggerServer

pause