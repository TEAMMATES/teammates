FROM gcr.io/google.com/cloudsdktool/cloud-sdk:alpine

RUN apk --update add openjdk21-jre
RUN gcloud components install beta cloud-datastore-emulator

ENTRYPOINT gcloud beta emulators datastore start --host-port 0.0.0.0:8484 --project placeholder
