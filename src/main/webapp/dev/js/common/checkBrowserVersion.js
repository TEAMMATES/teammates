/* global UAParser:true */

/**
 * Function to check browser version and alert if browser version is lower than supported
 * Adapted from http://www.javascripter.net/faq/browsern.htm
 *
 */

function checkBrowserVersion() {
    const uaParser = new UAParser();
    const browser = uaParser.getBrowser().name;
    const supportedBrowsers = ['Chrome', 'Firefox', 'Safari', 'Edge'];
    const isSupported = supportedBrowsers.indexOf(browser) !== -1;

    if (!isSupported) {
        const unsupportedBrowserErrorString =
            '<div class="alert alert-warning text-bold">'
                + '<span class="glyphicon glyphicon-info-sign padding-7px"></span>'
                + 'Please note that TEAMMATES works best in recent versions of Chrome, Firefox, Safari and Microsoft Edge'
            + '</div>';
        const message = $('#browserMessage');

        message.css('display', 'block');
        message.html(unsupportedBrowserErrorString);
    }
}

export {
    checkBrowserVersion,
};
