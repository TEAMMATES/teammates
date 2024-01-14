FROM ubuntu:22.04
RUN apt-get update && apt-get install -y default-jdk unzip wget curl
RUN curl -sL https://deb.nodesource.com/setup_18.x -o nodesource_setup.sh && bash ./nodesource_setup.sh
RUN apt-get update && apt-get install -y nodejs
RUN wget -c https://services.gradle.org/distributions/gradle-7.5-all.zip -P /tmp && unzip -d /opt/gradle /tmp/gradle-7.5-all.zip && rm -rf /tmp/gradle-7.5-all.zip
RUN ln -s /opt/gradle/gradle-7.5/bin/gradle /usr/bin/gradle
RUN npm install -g @angular/cli@14
