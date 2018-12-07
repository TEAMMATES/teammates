import {
    isWithinView,
} from './helper';

/**
 * Scrolls the screen to a certain position.
 * @param scrollPos Position to scroll the screen to.
 * @param duration Duration of animation in ms. Scrolling is instant if omitted.
 *                 'fast and 'slow' are 600 and 200 ms respectively,
 *                 400 ms will be used if any other string is supplied.
 */
function scrollToPosition(scrollPos, duration) {
    if (duration === undefined || duration === null) {
        $(window).scrollTop(scrollPos);
    } else {
        $('html, body').animate({ scrollTop: scrollPos }, duration);
    }
}

/**
 * Scrolls to an element.
 * Possible options are as follows:
 *
 * @param element - element to scroll to
 * @param options - associative array with optional values:
 *                  * type: ['top'|'view'], defaults to 'top';
 *                          'top' scrolls to the top of the element,
 *                          'view' scrolls the element into view
 *                  * offset: offset from element to scroll to in px,
 *                            defaults to navbar / footer offset for scrolling from above or below
 *                  * duration: duration of animation,
 *                              defaults to 0 for scrolling without animation
 */
function scrollToElement(element, opts) {
    const defaultOptions = {
        type: 'top',
        offset: 0,
        duration: 0,
    };

    const options = opts || {};
    const type = options.type || defaultOptions.type;
    let offset = options.offset || defaultOptions.offset;
    const duration = options.duration || defaultOptions.duration;

    const isViewType = type === 'view';
    if (isViewType && isWithinView(element)) {
        return;
    }

    const navbar = $('.navbar')[0];
    const navbarHeight = navbar ? navbar.offsetHeight : 0;
    const footer = $('#footerComponent')[0];
    const footerHeight = footer ? footer.offsetHeight : 0;
    const windowHeight = window.innerHeight - navbarHeight - footerHeight;

    const isElementTallerThanWindow = windowHeight < element.offsetHeight;
    const isFromAbove = window.scrollY < element.offsetTop;
    const isAlignedToTop = !isViewType || isElementTallerThanWindow || !isFromAbove;

    // default offset - from navbar / footer
    if (options.offset === undefined) {
        offset = isAlignedToTop ? navbarHeight * -1 : footerHeight * -1;
    }

    // adjust offset to bottom of element
    if (!isAlignedToTop) {
        offset *= -1;
        offset += element.offsetHeight - window.innerHeight;
    }

    const scrollPos = element.offsetTop + offset;

    scrollToPosition(scrollPos, duration);
}

/**
 * Scrolls the screen to top
 * @param duration Duration of animation in ms. Scrolling is instant if omitted.
 *                 'fast and 'slow' are 600 and 200 ms respectively,
 *                 400 ms will be used if any other string is supplied.
 */
function scrollToTop(duration) {
    scrollToPosition(0, duration);
}

export {
    scrollToElement,
    scrollToTop,
};
