#!/bin/bash

set -ev
./gradlew createConfigs testClasses
./gradlew downloadStaticAnalysisTools
./gradlew lint --continue
npm install
npm run lint
