#Dev Guide to Verify Each Steps

This document can help you to verify each step is done correctly while setting up a development environment.
It contains information about some of the steps but not all, which you can refer after completing eact step mentioned [here](https://github.com/TEAMMATES/teammates/blob/master/docs/settingUp.md).

The steps given for setting up a developer environment works with Linux or Mac or Windows only with these pointers :
- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.
- When a version is specified for any tool, install that version instead of the latest version available.

## Step 1: Install necessary tools and languages

After installing JDK 7 check the version of Java and the output will be something like this :

`java version "1.7.0_67"`  

## Step 2: Obtain your own repository copy

Check if the upstream is set properly by using this command `git remote -v` and the output will be something like this :

`upstream        https://github.com/TEAMMATES/teammates.git (fetch)`

`upstream        https://github.com/TEAMMATES/teammates.git (push)`

More information can be found at this [documentation](https://help.github.com/articles/fork-a-repo/).

## Step 3: Set up project-specific settings and dependencies

After the execution of the respective commands in each step the following folders will be created.

1. `./gradlew appengineDownloadSdk` will create the folders `.gradle` and `.git`  in the project root directory.

2. `./gradlew setup` will create the folder `.project` in the project root directory.

3. Do modify the config files :

   * If you are using JDK which is not specified in the PATH don't forget to add the path to the file `gradle.properties` and 
     do add your name or id default accounts in the file `src/test/resources/test.properties`

## Step 4: Set up Eclipse IDE

1. You can check if the plugin's were installed successfully : Go to Help → Eclipse Installation Details.
    Make sure that you have installed a specified version of the plugins not the latest version.
    
2. Configure the Eclipse

    JRE : After this the make sure when you go to `Window`→`Preferences`→`Java`→`Installed Jre` and click on the JDK and choose
     edit which looks something like this :
      ![devverification-guide-1.png](images/devverification-guide-1.png)

3. After running the `./gradlew resetEclipseDeps` the `.classpath` folder is added in the project root directory.

>
 If you encounter any problems during the setting up process, please refer to our [troubleshooting guide](https://github.com/TEAMMATES/teammates/blob/master/docs/troubleshooting-guide.md) 
 before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).
