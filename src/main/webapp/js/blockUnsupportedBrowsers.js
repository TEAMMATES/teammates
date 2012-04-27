// Script to actively test for iPad, iPhone and iPod and block the app from loading on them

<!--

// Android
var ua = navigator.userAgent.toLowerCase();
var isAndroid = ua.indexOf("android") > -1; // && ua.indexOf("mobile");
if(isAndroid) {
        window.location="../mobile.jsp"; // URL to redirect to.
}


// IE (7 and below)
var browser             = navigator.appName;
var ver                 = navigator.appVersion;
var thestart    = parseFloat(ver.indexOf("MSIE"))+1; // This finds the start of the MS version string.
var brow_ver    = parseFloat(ver.substring(thestart+4, thestart+7)); // This cuts out the bit of string we need.

if ((browser == "Microsoft Internet Explorer") && (brow_ver < 8)) {
        window.location="../oldIE.jsp"; // URL to redirect to.
}
//TODO: 1. these files being redirected to look like they can be plain HTML files. 2. Perhaps one file is enough? 
//TODO: shouldn't we check for supported browsers rather than unsupported browsers?
// Iphone
if((navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i)) || (navigator.userAgent.match(/iPad/i))) {
        window.location="../mobile.jsp"; // URL to redirect to.
}
//-->