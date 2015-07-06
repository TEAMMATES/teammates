/**
 * This JavaScript file is included in all student pages. Functions here
 * should be common to the student pages.
 */

$(document).ready(function() {
    $('[data-unreg].navLinks').click(function() {
        return confirm('You have to register using a google account '
                       + 'in order to access this page. Would you like to proceed and register?');
    });
});
