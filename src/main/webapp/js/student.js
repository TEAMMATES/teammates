/**
 * This JavaScript file is included in all student pages. Functions here
 * should be common to the student pages.
 */

$(document).ready(function() {
    $('[data-unreg].navLinks').click(function(event) {
        event.preventDefault();
        $clickedLink = $(event.target);

        var messageText = 'You have to register using a google account in order to access this page. Would '
                          + 'you like to proceed and register?';
        var okCallback = function() {
            window.location = $clickedLink.attr('href');
        };

        BootboxWrapper.showModalConfirmation('Register for TEAMMATES', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
    });
});
