'use strict';

// Browser Compatibility and support
var MSIE = 'Microsoft Internet Explorer';
var MSIE_LOWEST_VERSION = 9;
var CHROME = 'Chrome';
var CHROME_LOWEST_VERSION = 15;
var FIREFOX = 'Firefox';
var FIREFOX_LOWEST_VERSION = 12;
var SAFARI = 'Safari';
var SAFARI_LOWEST_VERSION = 4;

/**
 * Function to check browser version and alert if browser version is lower than supported
 * Adapted from http://www.javascripter.net/faq/browsern.htm
 *
 */

function checkBrowserVersion() {
    var nAgt = navigator.userAgent;
    var browserName = navigator.appName;
    var fullVersion = parseFloat(navigator.appVersion);
    var majorVersion = parseInt(navigator.appVersion, 10);
    var verOffset;
    var supported = true;

    /* eslint-disable no-negated-condition */ // usage of .contains() equivalent requires !==
    // In MSIE, the true version is after "MSIE" in userAgent
    if ((verOffset = nAgt.indexOf('MSIE')) !== -1) {
        browserName = MSIE;
        fullVersion = nAgt.substring(verOffset + 5);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < MSIE_LOWEST_VERSION) {
            supported = false;
        }
    } else if ((verOffset = nAgt.indexOf('Chrome')) !== -1) {
        // In Chrome, the true version is after "Chrome"
        browserName = CHROME;
        fullVersion = nAgt.substring(verOffset + 7);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < CHROME_LOWEST_VERSION) {
            supported = false;
        }
    } else if ((verOffset = nAgt.indexOf('Safari')) !== -1) {
        // In Safari, the true version is after "Safari" or after "Version"
        browserName = SAFARI;
        fullVersion = nAgt.substring(verOffset + 7);
        if ((verOffset = nAgt.indexOf('Version')) !== -1) {
            fullVersion = nAgt.substring(verOffset + 8);
        }
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < SAFARI_LOWEST_VERSION) {
            supported = false;
        }
    } else if ((verOffset = nAgt.indexOf('Firefox')) !== -1) {
        // In Firefox, the true version is after "Firefox"
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
        var unsupportedBrowserErrorString =
            'You are currently using ' + browserName + ' v.' + majorVersion + '. '
            + 'This web browser is not officially supported by TEAMMATES. '
            + 'In case this web browser does not display the webpage correctly, '
            + 'you may wish to view it in the following supported browsers: <br>'
            + '<table>'
                + '<tr>'
                    + '<td width="50%"> - ' + MSIE + ' ' + MSIE_LOWEST_VERSION + '+</td>'
                    + '<td> - ' + CHROME + ' ' + CHROME_LOWEST_VERSION + '+</td>'
                + '</tr>'
                + '<tr>'
                    + '<td> - ' + FIREFOX + ' ' + FIREFOX_LOWEST_VERSION + '+</td>'
                    + '<td> - ' + SAFARI + ' ' + SAFARI_LOWEST_VERSION + '+</td>'
                + '</tr>'
            + '</table>';

        var message = $('#browserMessage');
        message.css('display', 'block');
        message.html(unsupportedBrowserErrorString);
    }

}
$('document').ready(checkBrowserVersion);
