#!/bin/bash
# runtests.sh
#################################################################################
# If the file is not an executable, change its permission by: chmod +x runtests.sh
# Usage: ./runtests.sh GAE_SDK_Location Project_Folder_location
#################################################################################

mkdir testrunner
cd  testrunner

# copy files from the original folder 
rsync -av --exclude='*.java' --exclude='*/lib' $2/src/ ./src
cp -R ../target/test-classes/teammates ./teammates
cp ./src/test/resources/test.properties .
cp ./src/test/testng.xml .

# construct the classpath
tempcp=.
tempcp=$tempcp:$1/lib/shared/appengine-local-runtime-shared.jar
tempcp=$tempcp:$1/lib/shared/el-api.jar
tempcp=$tempcp:$1/lib/shared/jsp-api.jar
tempcp=$tempcp:$1/lib/shared/servlet-api.jar
tempcp=$tempcp:$1/lib/shared/jsp/repackaged-appengine-ant-1.7.1.jar
tempcp=$tempcp:$1/lib/shared/jsp/repackaged-appengine-ant-launcher-1.7.1.jar
tempcp=$tempcp:$1/lib/shared/jsp/repackaged-appengine-jasper-6.0.29.jar
tempcp=$tempcp:$1/lib/shared/jsp/repackaged-appengine-jasper-el-6.0.29.jar
tempcp=$tempcp:$1/lib/shared/jsp/repackaged-appengine-tomcat-juli-6.0.29.jar
tempcp=$tempcp:$1/lib/opt/user/appengine-api-labs/v1/appengine-api-labs.jar
tempcp=$tempcp:$1/lib/opt/user/appengine-endpoints/v1/appengine-endpoints.jar
tempcp=$tempcp:$1/lib/opt/user/appengine-endpoints/v1/appengine-endpoints-deps.jar
tempcp=$tempcp:$1/lib/opt/user/jsr107/v1/appengine-jsr107cache-1.9.4.jar
tempcp=$tempcp:$1/lib/opt/user/jsr107/v1/jsr107cache-1.1.jar
tempcp=$tempcp:$1/lib/opt/user/datanucleus/v1/datanucleus-appengine-1.0.10.final.jar
tempcp=$tempcp:$1/lib/opt/user/datanucleus/v1/datanucleus-core-1.1.5.jar
tempcp=$tempcp:$1/lib/opt/user/datanucleus/v1/datanucleus-jpa-1.1.5.jar
tempcp=$tempcp:$1/lib/opt/user/datanucleus/v1/geronimo-jpa_3.0_spec-1.1.1.jar
tempcp=$tempcp:$1/lib/opt/user/datanucleus/v1/geronimo-jta_1.1_spec-1.1.1.jar
tempcp=$tempcp:$1/lib/opt/user/datanucleus/v1/jdo2-api-2.3-eb.jar
tempcp=$tempcp:$1/lib/appengine-tools-api.jar
tempcp=$tempcp:$2/src/main/webapp/WEB-INF/classes
tempcp=$tempcp:$2/src/test/resources/lib/javamail/mail.jar
tempcp=$tempcp:$2/src/main/webapp/WEB-INF/lib/gson-2.2.2.jar
tempcp=$tempcp:$2/src/main/webapp/WEB-INF/lib/xercesImpl-2.9.1.jar
tempcp=$tempcp:$2/src/test/resources/lib/appengine/appengine-remote-api.jar
tempcp=$tempcp:$2/src/test/resources/lib/appengine/appengine-testing.jar
tempcp=$tempcp:$2/src/test/resources/lib/appengine/appengine-api.jar
tempcp=$tempcp:$2/src/test/resources/lib/appengine/appengine-api-stubs.jar
tempcp=$tempcp:$2/src/test/resources/lib/appengine/appengine-api-labs.jar
tempcp=$tempcp:$2/src/test/resources/lib/selenium/selenium-server-standalone-2.41.0.jar
tempcp=$tempcp:$2/src/test/resources/lib/httpunit/httpunit.jar
tempcp=$tempcp:$2/src/test/resources/lib/testng/testng.jar
echo $tempcp

# run test suite once and retry failed ones five times
args="-Duser.timezone=UTC -Dfile.encoding=UTF8"
echo $args

java -cp $tempcp $args org.testng.TestNG $3 $4 ./testng.xml
java -cp $tempcp $args org.testng.TestNG ./test-output/testng-failed.xml
java -cp $tempcp $args org.testng.TestNG ./test-output/testng-failed.xml
java -cp $tempcp $args org.testng.TestNG ./test-output/testng-failed.xml
java -cp $tempcp $args org.testng.TestNG ./test-output/testng-failed.xml
java -cp $tempcp $args org.testng.TestNG ./test-output/testng-failed.xml

cd ..