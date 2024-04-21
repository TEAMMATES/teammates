FROM amazoncorretto:11-alpine-jdk

COPY dist .
COPY WEB-INF WEB-INF

ENTRYPOINT java -ea -Djava.util.logging.config.file=WEB-INF/logging.properties -cp "WEB-INF/lib/*:WEB-INF/classes/." teammates.main.Application
