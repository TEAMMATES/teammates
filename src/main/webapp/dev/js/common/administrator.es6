/**
 * Contains functions common to the administrator pages.
 */

/**
 * Binds back-to-top buttons.
 */
function bindBackToTopButtons(backToTopClasses) {
    const offset = 220;
    const duration = 500;

    $(window).scroll(function () {
        if ($(this).scrollTop() > offset) {
            $(backToTopClasses).fadeIn(duration);
        } else {
            $(backToTopClasses).fadeOut(duration);
        }
    });

    $(document).on('click', backToTopClasses, (e) => {
        e.preventDefault();
        $('html, body').animate({
            scrollTop: 0,
        }, duration);
        return false;
    });
}

export {
    bindBackToTopButtons,
};
