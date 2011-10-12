// Script to actively test for IE version and stop app load if version < 8

<!--
var browser             = navigator.appName;
var ver                 = navigator.appVersion;
var thestart    = parseFloat(ver.indexOf("MSIE"))+1; // This finds the start of the MS version string.
var brow_ver    = parseFloat(ver.substring(thestart+4, thestart+7)); // This cuts out the bit of string we need.

if ((browser == "Microsoft Internet Explorer") && (brow_ver < 8)) {
        window.location="../oldIE.html"; // URL to redirect to.
}
//-->