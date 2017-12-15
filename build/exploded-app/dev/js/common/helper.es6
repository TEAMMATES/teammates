/**
 * Tests whether the passed object is an actual date
 * with an accepted format
 *
 * Allowed formats : http://dygraphs.com/date-formats.html
 *
 * TEAMMATES currently follows the RFC2822 / IETF date syntax
 * e.g. 02 Apr 2012, 23:59
 *
 * @param date
 * @returns boolean
 */
function isDate(date) {
    return !isNaN(Date.parse(date));
}

/**
* Function to test if param is a numerical value
* @param num
* @returns boolean
*/
function isNumber(num) {
    return (typeof num === 'string' || typeof num === 'number') && !isNaN(num - 0) && num !== '';
}

/**
 * Checks if element is within browser's viewport.
 * @return true if it is within the viewport, false otherwise
 * @see http://stackoverflow.com/q/123999
 */
function isWithinView(element) {
    const baseElement = $(element)[0]; // unwrap jquery element
    const rect = baseElement.getBoundingClientRect();

    const $viewport = $(window);

    // makes the viewport size slightly larger to account for rounding errors
    const tolerance = 0.25;
    return (
        rect.top >= 0 - tolerance    // below the top of viewport
        && rect.left >= 0 - tolerance    // within the left of viewport
        && rect.right <= $viewport.width() + tolerance    // within the right of viewport
        && rect.bottom <= $viewport.height() + tolerance    // above the bottom of viewport
    );
}

export {
    isDate,
    isNumber,
    isWithinView,
};
