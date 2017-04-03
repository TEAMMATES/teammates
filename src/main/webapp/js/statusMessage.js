'use strict';

$(document).ready(function() {
    var statusMessage = document.getElementById('statusMessagesToUser');
    var navbarHeight = 0;
    var extraPadding = 15;

    var navbar = document.getElementsByClassName('navbar')[0];

    if (navbar !== undefined) {
        navbarHeight = navbar.offsetHeight;
    }

    scrollToElement(statusMessage, {
        type: 'view',
        offset: (navbarHeight + extraPadding) * -1
    });
});
