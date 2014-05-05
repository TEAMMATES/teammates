//Browser Compatibility and support
var MICROSOFT_INTERNET_EXPLORER = "Microsoft Internet Explorer";
var MICROSOFT_INTERNET_EXPLORER_LOWEST_VERSION = 9;
var CHROME = "Chrome";
var CHROME_LOWEST_VERSION = 15;
var FIREFOX = "Firefox";
var FIREFOX_LOWEST_VERSION = 12;
var SAFARI = "Safari";
var SAFARI_LOWEST_VERSION = 4;

/**
 * Function to check browser version and alert if browser version is lower than supported
 * Adapted from http://www.javascripter.net/faq/browsern.htm
 * 
 */

function checkBrowserVersion(){
    var nAgt = navigator.userAgent;
    var browserName  = navigator.appName;
    var fullVersion  = parseFloat(navigator.appVersion); 
    var majorVersion = parseInt(navigator.appVersion,10);
    var verOffset;
    var supported = true;

    // In MSIE, the true version is after "MSIE" in userAgent
    if ((verOffset=nAgt.indexOf("MSIE"))!=-1) {
        browserName = MICROSOFT_INTERNET_EXPLORER;
        fullVersion = nAgt.substring(verOffset+5);
        majorVersion = parseInt(fullVersion,10);
        if (majorVersion < MICROSOFT_INTERNET_EXPLORER_LOWEST_VERSION){
            supported = false;
        }
    }
    // In Chrome, the true version is after "Chrome" 
    else if ((verOffset=nAgt.indexOf("Chrome"))!=-1) {
        browserName = CHROME;
        fullVersion = nAgt.substring(verOffset+7);
        majorVersion = parseInt(fullVersion,10);
        if (majorVersion < CHROME_LOWEST_VERSION){
            supported = false;
        }
    }
    // In Safari, the true version is after "Safari" or after "Version" 
    else if ((verOffset=nAgt.indexOf("Safari"))!=-1) {
        browserName = SAFARI;
        fullVersion = nAgt.substring(verOffset+7);
        if ((verOffset=nAgt.indexOf("Version"))!=-1){ 
            fullVersion = nAgt.substring(verOffset+8);
        }
        majorVersion = parseInt(fullVersion,10);
        if (majorVersion < SAFARI_LOWEST_VERSION){
            supported = false;
        }
    }
    // In Firefox, the true version is after "Firefox" 
    else if ((verOffset=nAgt.indexOf("Firefox"))!=-1) {
        browserName = FIREFOX;
        fullVersion = nAgt.substring(verOffset+8);
        majorVersion = parseInt(fullVersion,10);
        if (majorVersion < FIREFOX_LOWEST_VERSION){
            supported = false;
        }
    }
    // In most other browsers, "name/version" is at the end of userAgent 
    else {
        browserName = "Unsupported";
        fullVersion = 0;
        supported = false;
    }
    
    if (!supported){
        var message = document.getElementById("browserMessage");
        message.style.display = "block";
        message.innerHTML = "You are currently using " + browserName + " v." + majorVersion + ". This web browser is not officially supported by TEAMMATES. " + 
                            "In case this web browser does not display the webpage correctly, you may wish to view it in the following supported browsers: <br>" +
                            "<table><tr><td width=\"50%\"> - " + MICROSOFT_INTERNET_EXPLORER + " " + MICROSOFT_INTERNET_EXPLORER_LOWEST_VERSION + "+</td>" +
                            "<td> - " + CHROME + " " + CHROME_LOWEST_VERSION + "+</td></tr>" + 
                            "<tr><td> - " + FIREFOX + " " + FIREFOX_LOWEST_VERSION + "+</td> " +
                            "<td> - " + SAFARI + " " + SAFARI_LOWEST_VERSION + "+</td></tr></table>";
    }
    
 }
window.onload = checkBrowserVersion;