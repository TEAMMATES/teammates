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
 * Sanitize GoogleID by trimming space and '@gmail.com'
 * Used in instructorCourse, instructorCourseEdit, adminHome
 *
 * @param rawGoogleId
 * @returns sanitizedGoolgeId
 */
function sanitizeGoogleId(rawGoogleId) {
    let googleId = rawGoogleId.trim();
    const loc = googleId.toLowerCase().indexOf('@gmail.com');
    if (loc > -1) {
        googleId = googleId.substring(0, loc);
    }
    return googleId.trim();
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
/*
export default {
    sanitizeGoogleId,
    sanitizeForJs,
};
*/
/*
exported
    sanitizeGoogleId,
    sanitizeForJs
*/
