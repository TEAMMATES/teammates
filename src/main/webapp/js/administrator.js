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

});
