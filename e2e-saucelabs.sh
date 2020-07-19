#!/bin/sh

set -e

export TUNNEL_ID=$TRAVIS_JOB_NUMBER
export BUILD_ID=$TRAVIS_BUILD_NUMBER

export PATH=$HOME/google-cloud-sdk/bin:$PATH
cd $HOME
curl -fsS -o google-cloud-sdk.tar.gz https://dl.google.com/dl/cloudsdk/channels/rapid/google-cloud-sdk.tar.gz
tar -xzf google-cloud-sdk.tar.gz
./google-cloud-sdk/install.sh --usage-reporting false --path-update false --command-completion false
cd $TRAVIS_BUILD_DIR
gcloud -q components install app-engine-java
mv src/e2e/resources/test.saucelabs.properties src/e2e/resources/test.properties

rm -rf src/main/webapp
mkdir -p src/main/webapp
cp -r angular-build/. src/main/webapp
./gradlew createConfigs testClasses
./gradlew appengineStart
./gradlew e2eTests
