'use strict';

/**
 * Contains functions common to the administrator pages.
 */
var AdminCommon = {

    /**
     * Binds back-to-top buttons.
     */
    bindBackToTopButtons: function(backToTopClasses) {
        var offset = 220;
        var duration = 500;

        $(window).scroll(function() {
            if ($(this).scrollTop() > offset) {
                $(backToTopClasses).fadeIn(duration);
            } else {
                $(backToTopClasses).fadeOut(duration);
            }
        });

        $(document).on('click', backToTopClasses, function(e) {
            e.preventDefault();
            $('html, body').animate({
                scrollTop: 0
            }, duration);
            return false;
        });
    }

};
