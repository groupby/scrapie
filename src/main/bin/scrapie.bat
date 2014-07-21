@echo off
set CURRENT=%cd%
set DIR=%~dp0
set JAR=%DIR%/scrapie.jar
java -DworkingDir="%CURRENT%/" -jar "%JAR%" %*