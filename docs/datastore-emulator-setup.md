# Datastore emulator setup

## Why we need an emulator?

The Java data access API ([Objectify](https://github.com/objectify/objectify)) used for TEAMMATES will be upgraded from v5 to v6.

The latest version (v6+) uses `Cloud Datastore API` instead of `Google App Engine API` used in previous versions.

In order to locally simulate production Datastore environment during development and testing of relevant features, we will rely on this tool called Datastore Emulator.

## Steps to local setup
Refer to the official [installation guide](https://cloud.google.com/datastore/docs/tools/datastore-emulator).

## Troubleshoot
1. Sometimes the recommended emulator setup in [wiki](https://github.com/objectify/objectify/wiki/Setup#initialising-the-objectifyservice-to-work-with-emulator-applies-to-v6) might not work with `Exiting due to exception: java.io.IOException: Failed to bind`.

**Solution:**
In that case, try out some other localhost ports such as `gcloud beta emulators datastore start --host-port=localhost:8482`.
Also change `DatastoreOptions.setHost()` parameter in `src/main/java/teammates/storage/api/OfyHelper.java` accordingly.

2. Encounter `java.lang.IllegalStateException: Must use project ID as app ID if project ID is provided.` when trying to connect backend with emulator.

**Solution:**
Before running `./gradlew appengineRun` in the session, run the command `export DATASTORE_USE_PROJECT_ID_AS_APP_ID=true`.

## References
https://stackoverflow.com/questions/45659186/illegalstateexception-with-google-app-engine-local-datastore
