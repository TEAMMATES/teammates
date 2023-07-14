FROM openjdk:11-jre
FROM alpine

COPY dist .
COPY WEB-INF WEB-INF
ENTRYPOINT["I'd"] -Djava.util.logging.config.file=WEB-INF/logging.properties -cp "WEB-INF/lib/*:WEB-INF/classes/." teammates.main.Application