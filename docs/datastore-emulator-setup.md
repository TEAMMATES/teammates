# Datastore emulator setup

## Why we need an emulator?

The Java data access API ([Objectify](https://github.com/objectify/objectify)) used for TEAMMATES will be upgraded from v5 to v6.

The latest version (v6+) uses `Cloud Datastore API` instead of `Google App Engine API` used in previous versions.

In order to locally simulate production Datastore environment during development and testing of relevant features, we will rely on this tool called Datastore Emulator.

## Steps to local setup
**1. Installing the Datastore Emulator**    
Please refer to the official [installation guide](https://cloud.google.com/datastore/docs/tools/datastore-emulator).

**2. Running the Emulator**  
To run the emulator in `port 8484` , enter the command:
```
gcloud beta emulators datastore start --host-port=localhost:8484
```
If everything goes well, you should see something like this:
```
...
[datastore] Dev App Server is now running.
```
If you encounter any errors, refer to our [troubleshooting guide](#troubleshoot).


## Troubleshoot

**#1 Error:** 
The recommended emulator setup in [wiki](https://github.com/objectify/objectify/wiki/Setup#initialising-the-objectifyservice-to-work-with-emulator-applies-to-v6) might not work with `Exiting due to exception: java.io.IOException: Failed to bind`. 

**Reason:** 
This is a common encounter, and it is due to the emulator failing to shut down, leaving a dangling process in `port 8484`.

**Solution:**
The solution is to identify the process id and kill the process manually, before running the emulator again. 
Depending on your operating system, you may have access to different command line tools. 
On macOS for example, you can run the following command in the terminal:
 ```
lsof -i tcp:8484
 ```  
This allows you to find the process id of the dangling process running in `port 8484`. 
To kill the process, simply run: 
```
kill -9 <PID>
```
Note: `<PID>` is the process id you have identified. 

Finally, run `gcloud beta emulators datastore start --host-port=localhost:8484` to restart the emulator.

**#2 Error:** You encounter `java.lang.IllegalStateException: Must use project ID as app ID if project ID is provided` when trying to connect the backend with the emulator.

**Solution:**
Before running `./gradlew appengineRun` in the session, run the following command:
 ```
 export DATASTORE_USE_PROJECT_ID_AS_APP_ID=true
```

## FAQs

**Where is data stored?**  
The data is stored inside a `local_db.bin` file located in the default directory `~/.config/gcloud/emulators/datastore/`.

**How do I clear the content of `local_db.bin` file?**  
Stop the emulator and manually delete the file.


## References

https://stackoverflow.com/questions/45659186/illegalstateexception-with-google-app-engine-local-datastore
