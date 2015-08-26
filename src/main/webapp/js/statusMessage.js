$(document).ready(function() {
    var statusMessage = document.getElementById('statusMessage');
    var navbarHeight = '70';

    // scroll to element
    statusMessage.scrollIntoView(true);

    // now account for fixed header
    var scrolledY = window.scrollY;

    if (scrolledY) {
      window.scroll(0, scrolledY - navbarHeight);
    }
});