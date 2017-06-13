/* global scrollToElement:false
 */

$(document).ready(() => {
    const statusMessage = document.getElementById('statusMessagesToUser');
    let navbarHeight = 0;
    const extraPadding = 15;

    const navbar = document.getElementsByClassName('navbar')[0];

    if (navbar !== undefined) {
        navbarHeight = navbar.offsetHeight;
    }

    scrollToElement(statusMessage, {
        type: 'view',
        offset: (navbarHeight + extraPadding) * -1,
    });
});
