import {
    updateCsrfTokenInInputFields,
} from './crypto';

import {
    toggleSort,
} from './sortBy';

/**
 * Polyfills the String.prototype.includes function finalized in ES6 for browsers that do not yet support the function.
 */
/* eslint-disable no-extend-native */ // necessary for polyfills
if (!String.prototype.includes) {
    String.prototype.includes = function (search, startParam) {
        const start = typeof startParam === 'number' ? startParam : 0;

        if (start + search.length > this.length) {
            return false;
        }
        return this.indexOf(search, start) !== -1;
    };
}

/**
 * Polyfills the Number.EPSILON property finalized in ES6 for browsers that do not yet support the property.
 */
if (!Number.EPSILON) {
    Number.EPSILON = 2 ** -52;
}
/* eslint-enable no-extend-native */

/**
 * Checks if the current device is touch based device
 * Reference: https://github.com/Modernizr/Modernizr/blob/master/feature-detects/touchevents.js
 */
function isTouchDevice() {
    return ('ontouchstart' in window) || (window.DocumentTouch && document instanceof window.DocumentTouch);
}

$(document).on('click', '.toggle-sort', (e) => {
    const $button = $(e.currentTarget); // the button clicked on

    const comparatorStringOrNull = $button.data('toggle-sort-comparator');
    const extractorStringOrNull = $button.data('toggle-sort-extractor');

    toggleSort($button, comparatorStringOrNull, extractorStringOrNull);
});

$(document).on('ajaxComplete ready', () => {
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

    /**
     * Updates the token in input fields with the latest one retrieved from the cookie.
     * The token becomes outdated once the session expires. The cookie might be updated
     * with the new token and session during page loads from other browser windows.
     * The latest value should be retrieved from the cookie before form submission.
     */
    $('form').submit(updateCsrfTokenInInputFields);
});
