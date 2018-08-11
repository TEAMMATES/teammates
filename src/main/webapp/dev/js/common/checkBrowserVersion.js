/* global UAParser:true */

/**
 * Function to check whether user's browser is in the
 * list of recommended browsers and issue a warning otherwise
 */
function checkBrowser() {
    const uaParser = new UAParser();
    const browser = uaParser.getBrowser().name;
    const recommendedBrowsers = ['Chrome', 'Firefox', 'Safari', 'Edge'];
    const isSupported = recommendedBrowsers.indexOf(browser) !== -1;

    if (!isSupported) {
        const discouragedBrowserErrorString =
            '<div class="alert alert-warning text-bold">'
                + '<span class="glyphicon glyphicon-info-sign padding-7px"></span>'
                + 'Please note that TEAMMATES works best in recent versions of Chrome, Firefox, Safari and Microsoft Edge'
            + '</div>';
        const message = $('#browserMessage');

        message.css('display', 'block');
        message.html(discouragedBrowserErrorString);
    }
}

export {
    checkBrowser,
};
