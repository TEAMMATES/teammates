/**
 * Contains functions common to the administrator pages.
 */
var AdminCommon = {
    
    /**
     * Binds back-to-top buttons.
     */
    bindBackToTopButtons: function() {
        var offset = 220;
        var duration = 500;
        var backToTopClasses = '.back-to-top-left, .back-to-top-right';
        
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
