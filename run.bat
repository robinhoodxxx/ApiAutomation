@echo off
REM Exit immediately on error
setlocal enabledelayedexpansion

echo Cleaning project...
call mvn clean

echo Packaging JAR with dependencies...
call mvn package

REM Define JAR file name
set JAR_FILE=target\ApiAutomation-0.0.1-jar-with-dependencies.jar

REM Check if the JAR file exists
if not exist "%JAR_FILE%" (
  echo JAR not found: %JAR_FILE%
  exit /b 1
)

REM Pass all arguments from command line
echo Running JAR with arguments: %*
java -jar "%JAR_FILE%" %*