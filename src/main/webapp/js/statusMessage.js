$(document).ready(function() {
    var statusMessage = document.getElementById('statusMessage');
    var navbarHeight = 0;
    var extraPadding = 15;
    
    var navbar = document.getElementsByClassName('navbar')[0];
    
    if (navbar != null) {
        navbarHeight = navbar.offsetHeight;
    }    

    // scroll to element
    statusMessage.scrollIntoView(true);

    // now account for fixed header
    var scrolledY = window.scrollY;

    if (scrolledY) {
        window.scroll(0, scrolledY - (navbarHeight + extraPadding));
    }
});