$(document).ready(function() {
    bindEventToChangeSideNavToFixedPositioning();

    // activates Bootstrap inbuilt scrollspy scroll-tracking functionality
    $('body').scrollspy({ target: '#sidenav' });

    bindEventToExpandSideNavSubMenu();

    // trigger scroll event on page load to ensure side nav is expanded if user refreshes page while already scrolled
    // halfway down the page
    $(window).trigger('scroll');
});

/**
 * Change the side nav from default positioning to fixed positioning when user scrolls down far enough
 * (for the "sticky" effect)
 */
function bindEventToChangeSideNavToFixedPositioning() {
    var scrollThreshold = getScrollThresholdToChangeToFixedPositioning();

    // record the original width of side nav before being changed to fixed positioning,
    // needed because the width of side nav changes when changed to fixed positioning
    var navWidth = $('#sidenav').width();

    $(window).on('scroll', function() {
        var isScrolledDown = this.scrollY > scrollThreshold;
        if (isScrolledDown) {
            $('#sidenav').addClass('fixed');
            $('#sidenav').css({ width: navWidth + 'px' });
        } else {
            $('#sidenav').removeClass('fixed');
        }
    });
}

/**
 * Get the point of scrolling at which to change the side nav from default positioning to fixed positioning
 */
function getScrollThresholdToChangeToFixedPositioning() {
    var MARGIN_FROM_TOP_WHEN_FIXED_AS_DEFINED_IN_CSS = 28;
    var amountOfScrollingToPutSideNavFlushedWithTop = $('#sidenav').offset().top;

    return amountOfScrollingToPutSideNavFlushedWithTop - MARGIN_FROM_TOP_WHEN_FIXED_AS_DEFINED_IN_CSS;
}

function bindEventToExpandSideNavSubMenu() {
    $('#sidenav > .nav > li').on('activate.bs.scrollspy', function(event) {
        $activatedLink = $(event.target);
        $('#sidenav > .nav > li > ul').hide();
        $activatedLink.find('ul').show();
    });
}

