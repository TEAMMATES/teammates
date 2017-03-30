import { bindDefaultImageIfMissing } from './ui.es6';

/**
 * Checks if the current device is touch based device
 * Reference: https://github.com/Modernizr/Modernizr/blob/master/feature-detects/touchevents.js
 */
function isTouchDevice() {
    return ('ontouchstart' in window || window.DocumentTouch) && document instanceof window.DocumentTouch;
}

$(document).on('ajaxComplete ready', () => {
    $('.profile-pic-icon-hover, .profile-pic-icon-click, .teamMembersPhotoCell').children('img').each(function () {
        bindDefaultImageIfMissing(this);
    });

    /**
     * Initializing then disabling is better than simply
     * not initializing for mobile due to some tooltips-specific
     * code that throws errors.
    */
    const $tooltips = $('[data-toggle="tooltip"]');
    $tooltips.tooltip({
        html: true,
        container: 'body',
    });
    if (isTouchDevice()) {
        $tooltips.tooltip('disable');
    }

    /**
     * Underlines all span elements with tool-tips except for
     * the ones without a text value. This is to exclude elements
     * such as 'icons' from underlining.
    */
    $('span[data-toggle="tooltip"]').each(function () {
        const textValue = $(this).text().replace(/\s/g, '');
        if (textValue) {
            $(this).addClass('tool-tip-decorate');
        }
    });
});
