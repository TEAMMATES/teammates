rem ##########################################
rem Usage: testrunner GAE_SDK_Location Project_Folder_location
rem ##########################################

mkdir testrunner
cd  testrunner

rem ### creat a file to hold xcopy exclusions
echo .java > exclude.txt
echo \lib\ >> exclude.txt

rem ### copy files from the original folder 
xcopy /E /Y  %2\src .\src\ /EXCLUDE:.\exclude.txt
xcopy /E /Y ..\target\test-classes\teammates .\teammates\
copy /Y .\src\test\resources\test.properties .
copy /Y .\src\test\testng.xml .

rem ### construct the classpath
@echo off
set tempcp=.
set tempcp=%tempcp%;%1\lib\shared\*
set tempcp=%tempcp%;%1\lib\shared\jsp\*
set tempcp=%tempcp%;%1\lib\opt\user\appengine-api-labs\v1\*
set tempcp=%tempcp%;%1\lib\opt\user\jsr107\v1\*
set tempcp=%tempcp%;%1\lib\opt\user\datanucleus\v1\*
set tempcp=%tempcp%;%1\lib\*
set tempcp=%tempcp%;%2\src\main\webapp\WEB-INF\classes
set tempcp=%tempcp%;%2\src\test\resources\lib\*
set tempcp=%tempcp%;%2\src\main\webapp\WEB-INF\lib\*
set tempcp=%tempcp%;%2\src\test\resources\lib\appengine\*
set tempcp=%tempcp%;%2\src\test\resources\lib\selenium\*
set tempcp=%tempcp%;%2\src\test\resources\lib\httpunit\*
set tempcp=%tempcp%;%2\src\test\resources\lib\testng\*
@echo on

rem ### run test suite once and retry failed ones five times
set vmparams=-Duser.timezone=UTC -Dfile.encoding=UTF8
java -cp "%tempcp%" %vmparams% org.testng.TestNG %3 %4 .\testng.xml
for /l %i in (1, 1, 5) do (
   java -cp "%tempcp%" %vmparams% org.testng.TestNG .\test-output\testng-failed.xml
)

cd ..