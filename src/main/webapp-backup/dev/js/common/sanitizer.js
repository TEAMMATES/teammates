/**
* Encodes a string for displaying in a HTML document.
* Uses an in-memory element created with jQuery.
* @param the string to be encoded
*/
function encodeHtmlString(stringToEncode) {
    return $('<div>').text(stringToEncode).html();
}

function escapeRegExp(string) {
    return string.replace(/([.*+?^=!:${}()|[\]/\\])/g, '\\$1');
}

/**
 * Helper function to replace all occurrences of a sub-string in a string.
 */
function replaceAll(string, find, replace) {
    return string.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

/**
 * Sanitizes special characters such as ' and \ to \' and \\ respectively
 */
function sanitizeForJs(rawString) {
    let string = rawString;
    string = replaceAll(string, '\\', '\\\\');
    string = replaceAll(string, '\'', '\\\'');
    return string;
}

export {
    encodeHtmlString,
    sanitizeForJs,
};
