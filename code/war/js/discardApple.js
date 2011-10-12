// Script to actively test for iPad, iPhone and iPod and block the app from loading on them

<!--
if((navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i)) || (navigator.userAgent.match(/iPad/i))) {
        window.location="../mobile.html"; // URL to redirect to.
}
//-->