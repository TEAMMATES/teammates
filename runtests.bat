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
set tempcp=%tempcp%;%1\lib\shared\appengine-local-runtime-shared.jar
set tempcp=%tempcp%;%1\lib\shared\-api.jar
set tempcp=%tempcp%;%1\lib\shared\jsp-api.jar
set tempcp=%tempcp%;%1\lib\shared\servlet-api.jar
set tempcp=%tempcp%;%1\lib\shared\jsp\repackaged-appengine-ant-1.7.1.jar
set tempcp=%tempcp%;%1\lib\shared\jsp\repackaged-appengine-ant-launcher-1.7.1.jar
set tempcp=%tempcp%;%1\lib\shared\jsp\repackaged-appengine-jasper-6.0.29.jar
set tempcp=%tempcp%;%1\lib\shared\jsp\repackaged-appengine-jasper-el-6.0.29.jar
set tempcp=%tempcp%;%1\lib\shared\jsp\repackaged-appengine-tomcat-juli-6.0.29.jar
set tempcp=%tempcp%;%1\lib\opt\user\appengine-api-labs\v1\appengine-api-labs.jar
set tempcp=%tempcp%;%1\lib\opt\user\appengine-endpoints\v1\appengine-endpoints.jar
set tempcp=%tempcp%;%1\lib\opt\user\appengine-endpoints\v1\appengine-endpoints-deps.jar
set tempcp=%tempcp%;%1\lib\opt\user\jsr107\v1\appengine-jsr107cache-1.9.4.jar
set tempcp=%tempcp%;%1\lib\opt\user\jsr107\v1\jsr107cache-1.1.jar
set tempcp=%tempcp%;%1\lib\opt\user\datanucleus\v1\datanucleus-appengine-1.0.10.final.jar
set tempcp=%tempcp%;%1\lib\opt\user\datanucleus\v1\datanucleus-core-1.1.5.jar
set tempcp=%tempcp%;%1\lib\opt\user\datanucleus\v1\datanucleus-jpa-1.1.5.jar
set tempcp=%tempcp%;%1\lib\opt\user\datanucleus\v1\geronimo-jpa_3.0_spec-1.1.1.jar
set tempcp=%tempcp%;%1\lib\opt\user\datanucleus\v1\geronimo-jta_1.1_spec-1.1.1.jar
set tempcp=%tempcp%;%1\lib\opt\user\datanucleus\v1\jdo2-api-2.3-eb.jar
set tempcp=%tempcp%;%1\lib\appengine-tools-api.jar
set tempcp=%tempcp%;%2\src\main\webapp\WEB-INF\classes
set tempcp=%tempcp%;%2\src\test\resources\lib\javamail\mail.jar
set tempcp=%tempcp%;%2\src\main\webapp\WEB-INF\lib\gson-2.2.2.jar
set tempcp=%tempcp%;%2\src\main\webapp\WEB-INF\lib\xercesImpl-2.9.1.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\appengine\appengine-remote-api.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\appengine\appengine-testing.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\appengine\appengine-api.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\appengine\appengine-api-stubs.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\appengine\appengine-api-labs.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\selenium\selenium-server-standalone-2.41.0.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\httpunit\httpunit.jar
set tempcp=%tempcp%;%2\src\test\resources\lib\testng\testng.jar
@echo on

rem ### run test suite once and retry failed ones five times
set vmparams=-Duser.timezone=UTC -Dfile.encoding=UTF8
java -cp "%tempcp%" %vmparams% org.testng.TestNG %3 %4 .\testng.xml
java -cp "%tempcp%" %vmparams% org.testng.TestNG .\test-output\testng-failed.xml
java -cp "%tempcp%" %vmparams% org.testng.TestNG .\test-output\testng-failed.xml
java -cp "%tempcp%" %vmparams% org.testng.TestNG .\test-output\testng-failed.xml
java -cp "%tempcp%" %vmparams% org.testng.TestNG .\test-output\testng-failed.xml
java -cp "%tempcp%" %vmparams% org.testng.TestNG .\test-output\testng-failed.xml

cd ..