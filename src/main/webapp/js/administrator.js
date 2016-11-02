/*
 * This Javascript file is included in all administrator pages. Functions here
 * should be common to the administrator pages.
 */

$(document).ready(function() {
    var offset = 220;
    var duration = 500;
    $(window).scroll(function() {
        if ($(this).scrollTop() > offset) {
            $('.back-to-top-left').fadeIn(duration);
            $('.back-to-top-right').fadeIn(duration);
        } else {
            $('.back-to-top-left').fadeOut(duration);
            $('.back-to-top-right').fadeOut(duration);
        }
    });

    $('.back-to-top-left').click(function(event) {
        event.preventDefault();
        $('html, body').animate({
            scrollTop: 0
        }, duration);
        return false;
    });

    $('.back-to-top-right').click(function(event) {
        event.preventDefault();
        $('html, body').animate({
            scrollTop: 0
        }, duration);
        return false;
    });
    
    $('.admin-delete-account-link').on('click', function(event) {
        event.preventDefault();

        var $clickedLink = $(event.target);
        var googleId = $clickedLink.data('googleId');
        var existingCourses = document.getElementById('courses_' + googleId).innerHTML;

        var messageText = 'Are you sure you want to delete the account ' + googleId + '?'
                          + '<br><br>' + existingCourses
                          + '<br><br>This operation will delete ALL information about this account from the system.';

        var okCallback = function() {
            window.location = $clickedLink.attr('href');
        };

        BootboxWrapper.showModalConfirmation('Confirm deletion', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.DANGER);
    });
});
