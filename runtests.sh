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
tempcp=$tempcp:$1/lib/shared/*
tempcp=$tempcp:$1/lib/shared/jsp/*
tempcp=$tempcp:$1/lib/opt/user/appengine-api-labs/v1/*
tempcp=$tempcp:$1/lib/opt/user/jsr107/v1/*
tempcp=$tempcp:$1/lib/opt/user/datanucleus/v1/*
tempcp=$tempcp:$1/lib/*
tempcp=$tempcp:$2/src/main/webapp/WEB-INF/classes
tempcp=$tempcp:$2/src/test/resources/lib/javamail/*
tempcp=$tempcp:$2/src/main/webapp/WEB-INF/lib/*
tempcp=$tempcp:$2/src/test/resources/lib/appengine/*
tempcp=$tempcp:$2/src/test/resources/lib/selenium/*
tempcp=$tempcp:$2/src/test/resources/lib/httpunit/*
tempcp=$tempcp:$2/src/test/resources/lib/testng/*
echo $tempcp

# run test suite once and retry failed ones five times
args="-Duser.timezone=UTC -Dfile.encoding=UTF8"
echo $args

java -cp $tempcp $args org.testng.TestNG $3 $4 ./testng.xml
for(( i=1; i <= 5; i++ ))
do
    java -cp $tempcp $args org.testng.TestNG ./test-output/testng-failed.xml
done

cd ..