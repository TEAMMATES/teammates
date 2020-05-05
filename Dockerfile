FROM java:8-alpine as type-builder

WORKDIR /teammates-types

COPY gradlew gradlew
COPY gradle.docker.properties gradle.properties
COPY gradle/wrapper gradle/wrapper
COPY build.gradle build.gradle
COPY src/main/java src/main/java
COPY src/web/environments src/web/environments

RUN sed -i 's/\r//g' gradlew
RUN chmod +x gradlew

RUN ./gradlew generateTypes

FROM node:10-alpine as front-end-builder

WORKDIR /teammates-web

COPY package.json package.json

RUN npm install

COPY src/main/webapp/WEB-INF src/main/webapp/WEB-INF
COPY src/web src/web
COPY angular.json angular.json
COPY tsconfig.json tsconfig.json
COPY ngsw-config.json ngsw-config.json
COPY --from=type-builder /teammates-types/src/web/types src/web/types
COPY --from=type-builder /teammates-types/src/web/environments src/web/environments

RUN npm run build

FROM google/cloud-sdk:latest

WORKDIR /teammates

COPY gradlew gradlew
COPY gradle.docker.properties gradle.properties
COPY gradle/wrapper gradle/wrapper
COPY build.gradle build.gradle

RUN sed -i 's/\r//g' gradlew
RUN chmod +x gradlew
RUN ./gradlew downloadDependencies

COPY src/main/java src/main/java
COPY src/main/resources src/main/resources
COPY --from=front-end-builder /teammates-web/src/main/webapp src/main/webapp
RUN ./gradlew createConfigs assemble

ENTRYPOINT ["./gradlew", "appengineRun", "-Pappengine_host=teammatescontainer"]
