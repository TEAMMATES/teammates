#Setting Up the Developer Environment
These instructions are for the Windows environment. Instructions for Mac OS is similar, with slight variations that you can figure out yourself.

##Prerequisites
Important: When a version is specified, please install that version instead of the latest version available.

1. Install GitHub for Windows/Mac (recommended), or at least, Git.
2. Clone the source code from `https://github.com/TEAMMATES/repo`
3. Install the latest JDK 7.
4. [Download Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/) (version: Kepler).
5. Change the text file encoding of your Eclipse workspace to UTF-8: <br> 
   Go to `Window → Preferences → General → Workspace`, change the 
   `Text file encoding` setting from `Default` to `Other: UTF-8`.
6. Install Google Plugin for Eclipse version 4.3. <br>
   Be careful to omit other plugins shown on the screen 
   (e.g., Google App Engine Tools for Android, GWT plugin).<br>
   Instructions are at https://developers.google.com/eclipse/docs/install-eclipse-4.3 <br>
   Note: Sometimes the update site for the GAE plug-in does not work. In which case, 
   follow the instructions at https://developers.google.com/eclipse/docs/install-from-zip.
7. Install Google App Engine SDK version 1.7.7.1 <br>
   Instructions are at https://developers.google.com/eclipse/docs/using_sdks.<br>
   Download link to the SDK is http://googleappengine.googlecode.com/files/appengine-java-sdk-1.7.7.1.zip.
8. Install the latest [TestNG Eclipse plugin](http://testng.org/doc/eclipse.html).
9. If you plan to use Firefox for testing TEAMMATES 
   (alternatively, you may use Chrome or IE, but FF is the most convenient of the three), 
   downgrade to Firefox 12.0 from [here](https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/)
   {The web driver we use does not work with the latest Firefox.} 
   or install multiple version of Firefox. To do this, grab Firefox 12.0 
   from the above link, and choose a custom setup during install, 
   in which you will be able to specify a different path for this version 
   (e.g. C:\Program Files\Mozilla Firefox 12) in a later step.
