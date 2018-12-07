import {
    scrollToElement,
} from '../common/scrollTo';

$(document).ready(() => {
    const statusMessage = $('#statusMessagesToUser').get(0);
    let navbarHeight = 0;
    const extraPadding = 15;

    const navbar = $('.navbar')[0];

    if (navbar !== undefined) {
        navbarHeight = navbar.offsetHeight;
    }

    scrollToElement(statusMessage, {
        type: 'view',
        offset: (navbarHeight + extraPadding) * -1,
    });
});
