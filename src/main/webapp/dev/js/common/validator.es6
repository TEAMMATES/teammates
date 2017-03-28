/* global FieldLength:false */
// import { FieldLength } from '../const.es6';

/**
 * Checks whether an e-mail is valid.
 * (Used in instructorCourseEdit.js)
 *
 * @param email
 * @returns {Boolean}
 */
function isEmailValid(email) {
    return email.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i) !== null;
}
/**
 * Check if the GoogleID is valid
 * GoogleID allow only alphanumeric, full stops, dashes, underscores or valid email
 *
 * @param rawGoogleId
 * @return {Boolean}
 */
function isValidGoogleId(rawGoogleId) {
    let isValidNonEmailGoogleId = false;
    const googleId = rawGoogleId.trim();

    // match() retrieve the matches when matching a string against a regular expression.
    const matches = googleId.match(/^([\w-]+(?:\.[\w-]+)*)/);

    isValidNonEmailGoogleId = matches !== null && matches[0] === googleId;

    let isValidEmailGoogleId = isEmailValid(googleId);

    if (googleId.toLowerCase().indexOf('@gmail.com') > -1) {
        isValidEmailGoogleId = false;
    }

    // email addresses are valid google IDs too
    return isValidNonEmailGoogleId || isValidEmailGoogleId;
}

/**
 * Checks whether a person's name is valid.
 * (Used in instructorCourseEdit.js)
 *
 * @param rawName
 * @returns {Boolean}
 */
function isNameValid(rawName) {
    const name = rawName.trim();

    if (name === '') {
        return false;
    }

    if (name.match(/[^/\\,.'\-()0-9a-zA-Z \t]/)) {
        // Returns true if a character NOT belonging to the following set
        // appears in the name: slash(/), backslash(\), fullstop(.), comma(,),
        // apostrophe('), hyphen(-), round brackets(()), alpha numeric
        // characters, space, tab
        return false;
    } else if (name.length > FieldLength.NAME_MAX_LENGTH) {
        return false;
    }
    return true;
}

/**
 * Checks whether an institution name is valid
 * Used in adminHome page (through administrator.js)
 * @param rawInstitution
 * @returns {Boolean}
 */
function isInstitutionValid(rawInstitution) {
    const institution = rawInstitution.trim();

    if (institution === '') {
        return false;
    }

    if (institution.match(/[^/\\,.'\-()0-9a-zA-Z \t]/)) {
        // Returns true if a character NOT belonging to the following set
        // appears in the name: slash(/), backslash(\), fullstop(.), comma(,),
        // apostrophe('), hyphen(-), round brackets(()), alpha numeric
        // characters, space, tab
        return false;
    } else if (institution.length > FieldLength.NAME_MAX_LENGTH) {
        return false;
    }
    return true;
}
/*
export default {
    isValidGoogleId,
    isEmailValid,
    isNameValid,
    isInstitutionValid,
};
*/
/*
exported
    isValidGoogleId,
    isEmailValid,
    isNameValid,
    isInstitutionValid
*/
