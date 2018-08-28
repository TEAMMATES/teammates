// Browser Compatibility and support
const MSIE = 'Microsoft Internet Explorer';
const MSIE_LOWEST_VERSION = 9;
const CHROME = 'Chrome';
const CHROME_LOWEST_VERSION = 15;
const FIREFOX = 'Firefox';
const FIREFOX_LOWEST_VERSION = 12;
const SAFARI = 'Safari';
const SAFARI_LOWEST_VERSION = 4;

/**
 * Function to check browser version and alert if browser version is lower than supported
 * Adapted from http://www.javascripter.net/faq/browsern.htm
 *
 */

function checkBrowserVersion() {
    const nAgt = navigator.userAgent;
    let browserName = navigator.appName;
    let fullVersion = parseFloat(navigator.appVersion);
    let majorVersion = parseInt(navigator.appVersion, 10);
    let verOffset;
    let supported = true;

    /* eslint-disable no-negated-condition */ // usage of .contains() equivalent requires !==
    if (nAgt.indexOf('MSIE') !== -1) {
        // In MSIE, the true version is after "MSIE" in userAgent
        verOffset = nAgt.indexOf('MSIE');
        browserName = MSIE;
        fullVersion = nAgt.substring(verOffset + 5);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < MSIE_LOWEST_VERSION) {
            supported = false;
        }
    } else if (nAgt.indexOf('Chrome') !== -1) {
        // In Chrome, the true version is after "Chrome"
        verOffset = nAgt.indexOf('Chrome');
        browserName = CHROME;
        fullVersion = nAgt.substring(verOffset + 7);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < CHROME_LOWEST_VERSION) {
            supported = false;
        }
    } else if (nAgt.indexOf('Safari') !== -1) {
        // In Safari, the true version is after "Safari" or after "Version"
        verOffset = nAgt.indexOf('Safari');
        browserName = SAFARI;
        fullVersion = nAgt.substring(verOffset + 7);
        if (nAgt.indexOf('Version') !== -1) {
            verOffset = nAgt.indexOf('Version');
            fullVersion = nAgt.substring(verOffset + 8);
        }
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < SAFARI_LOWEST_VERSION) {
            supported = false;
        }
    } else if (nAgt.indexOf('Firefox') !== -1) {
        // In Firefox, the true version is after "Firefox"
        verOffset = nAgt.indexOf('Firefox');
        browserName = FIREFOX;
        fullVersion = nAgt.substring(verOffset + 8);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < FIREFOX_LOWEST_VERSION) {
            supported = false;
        }
    } else {
        // In most other browsers, "name/version" is at the end of userAgent
        browserName = 'Unsupported';
        fullVersion = 0;
        supported = false;
    }
    /* eslint-enable no-negated-condition */

    if (!supported) {
        const unsupportedBrowserErrorString =
            `You are currently using ${browserName} v.${majorVersion}. `
            + 'This web browser is not officially supported by TEAMMATES. '
            + 'In case this web browser does not display the webpage correctly, '
            + 'you may wish to view it in the following supported browsers: <br>'
            + '<table>'
                + '<tr>'
                    + `<td width="50%"> - ${MSIE} ${MSIE_LOWEST_VERSION}+</td>`
                    + `<td> - ${CHROME} ${CHROME_LOWEST_VERSION}+</td>`
                + '</tr>'
                + '<tr>'
                    + `<td> - ${FIREFOX} ${FIREFOX_LOWEST_VERSION}+</td>`
                    + `<td> - ${SAFARI} ${SAFARI_LOWEST_VERSION}+</td>`
                + '</tr>'
            + '</table>';

        const message = $('#browserMessage');
        message.css('display', 'block');
        message.html(unsupportedBrowserErrorString);
    }
}

export {
    checkBrowserVersion,
};
