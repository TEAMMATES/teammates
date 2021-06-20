FROM gradle:7.1.1-jdk11 AS type-builder

WORKDIR /teammates-types

COPY build.gradle build.gradle
RUN gradle downloadDependencies

COPY src/main/java src/main/java
RUN gradle generateTypes

FROM node:12-alpine as frontend-builder

WORKDIR /teammates-frontend

COPY package.json package.json
COPY package-lock.json package-lock.json
RUN npm ci

COPY src/web src/web
COPY --from=type-builder /teammates-types/src/web/types src/web/types
COPY angular.json angular.json
COPY tsconfig.json tsconfig.json
COPY tsconfig.app.json tsconfig.app.json
COPY ngsw-config.json ngsw-config.json
COPY browserslist browserslist
RUN node --max-old-space-size=4096 $(which npm) run build

FROM gradle:7.1.1-jdk11 AS main

WORKDIR /teammates-main

COPY build.gradle build.gradle
RUN gradle downloadDependencies

COPY src/main src/main
COPY --from=frontend-builder /teammates-frontend/src/web/dist src/web/dist
RUN gradle assemble

ENTRYPOINT ["gradle", "serverRun"]
