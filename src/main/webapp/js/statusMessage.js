$(document).ready(function() {
    var statusMessage = document.getElementById('statusMessage');
    var navbarHeight = document.getElementsByClassName('navbar')[0].offsetHeight;
    var extraPadding = 15;

    // scroll to element
    statusMessage.scrollIntoView(true);

    // now account for fixed header
    var scrolledY = window.scrollY;

    if (scrolledY) {
        window.scroll(0, scrolledY - (parseInt(navbarHeight) + extraPadding));
    }
});