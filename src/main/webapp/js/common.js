var COURSE_ID_MAX_LENGTH = 40;
var COURSE_NAME_MAX_LENGTH = 64;
var EVAL_NAME_MAX_LENGTH = 38;
var FEEDBACK_SESSION_NAME_MAX_LENGTH = 38;
var FEEDBACK_SESSION_INSTRUCTIONS_MAX_LENGTH = 500;

// Field names
var USER_ID = 'user';

// Used in instructorCourse.js
var COURSE_ID = 'courseid';
var COURSE_NAME = 'coursename';
var COURSE_INSTRUCTOR_NAME = 'instructorname';
var COURSE_INSTRUCTOR_EMAIL = 'instructoremail';
var COURSE_INSTRUCTOR_ID = 'instructorid';

// Used in instructorEval.js
var EVALUATION_START = 'start';
var EVALUATION_STARTTIME = 'starttime';
var EVALUATION_TIMEZONE = 'timezone';

// Used in instructorFeedback.js
// TODO: Check if we can move most of these into instructorFeedback.js
var FEEDBACK_SESSION_NAME = 'fsname'; // also used in feedbackResponseComments.js
var FEEDBACK_SESSION_STARTDATE = 'startdate';
var FEEDBACK_SESSION_STARTTIME = 'starttime';
var FEEDBACK_SESSION_TIMEZONE = 'timezone';
var FEEDBACK_SESSION_CHANGETYPE = 'feedbackchangetype';
var FEEDBACK_SESSION_VISIBLEDATE = 'visibledate';
var FEEDBACK_SESSION_VISIBLETIME = 'visibletime';
var FEEDBACK_SESSION_PUBLISHDATE = 'publishdate';
var FEEDBACK_SESSION_PUBLISHTIME = 'publishtime';
var FEEDBACK_SESSION_SESSIONVISIBLEBUTTON = 'sessionVisibleFromButton';
var FEEDBACK_SESSION_RESULTSVISIBLEBUTTON = 'resultsVisibleFromButton';

// Used in instructorFeedbackEdit.js
// TODO: Check if we can move most of these into instructorFeedbackEdit.js
var FEEDBACK_QUESTION_GIVERTYPE = 'givertype';
var FEEDBACK_QUESTION_RECIPIENTTYPE = 'recipienttype';
var FEEDBACK_QUESTION_NUMBEROFENTITIES = 'numofrecipients';
var FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE = 'numofrecipientstype';
var FEEDBACK_QUESTION_TYPE = 'questiontype';
var FEEDBACK_QUESTION_MCQCHOICE = 'mcqOption';
var FEEDBACK_QUESTION_MCQOTHEROPTION = "mcqOtherOption";
var FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG = "mcqOtherOptionFlag";
var FEEDBACK_QUESTION_MSQCHOICE = 'msqOption';
var FEEDBACK_QUESTION_MSQOTHEROPTION = "msqOtherOption";
var FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG = "msqOtherOptionFlag";
var FEEDBACK_QUESTION_CONSTSUMOPTION = 'constSumOption';
var FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE = 'constSumOptionTable';
var FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS = 'constSumToRecipients';
var FEEDBACK_QUESTION_CONSTSUMPOINTS = 'constSumPoints';
var FEEDBACK_QUESTION_NUMBEROFCHOICECREATED = 'noofchoicecreated';
var FEEDBACK_QUESTION_NUMSCALE_MIN = 'numscalemin';
var FEEDBACK_QUESTION_NUMSCALE_MAX = 'numscalemax';
var FEEDBACK_QUESTION_NUMSCALE_STEP = 'numscalestep';
var FEEDBACK_QUESTION_NUMBER = 'questionnum';
var FEEDBACK_QUESTION_TEXT = 'questiontext';
var FEEDBACK_QUESTION_EDITTEXT = 'questionedittext';
var FEEDBACK_QUESTION_CANCELEDIT = 'questioncanceledit';
var FEEDBACK_QUESTION_EDITTYPE = 'questionedittype';
var FEEDBACK_QUESTION_SAVECHANGESTEXT = 'questionsavechangestext';
var FEEDBACK_QUESTION_SHOWRESPONSESTO = 'showresponsesto';
var FEEDBACK_QUESTION_SHOWGIVERTO = 'showgiverto';
var FEEDBACK_QUESTION_SHOWRECIPIENTTO = 'showrecipientto';
var FEEDBACK_QUESTION_TYPENAME_TEXT = 'Essay question';
var FEEDBACK_QUESTION_TYPENAME_MCQ = 'Multiple-choice (single answer)';
var FEEDBACK_QUESTION_TYPENAME_MSQ = 'Multiple-choice (multiple answers)';
var FEEDBACK_QUESTION_TYPENAME_NUMSCALE = 'Numerical-scale question';
var FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION = 'Distribute points (among options) question';
var FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT = 'Distribute points (among recipients) question';
var FEEDBACK_QUESTION_TYPENAME_CONTRIB = 'Team contribution question';
var FEEDBACK_QUESTION_TYPENAME_RUBRIC = 'Rubric question';

// Used in feedbackResponseComments.js
var FEEDBACK_RESPONSE_ID = 'responseid';
var FEEDBACK_RESPONSE_COMMENT_ID = 'responsecommentid';
var FEEDBACK_RESPONSE_COMMENT_TEXT = 'responsecommenttext';

// Display messages
// Used for validating input
var DISPLAY_INPUT_FIELDS_EXTRA = 'There are too many fields.';
var DISPLAY_INPUT_FIELDS_MISSING = 'There are missing fields.';
var DISPLAY_GOOGLEID_INVALID = 'GoogleID should only consist of alphanumerics, fullstops, dashes or underscores.';
var DISPLAY_EMAIL_INVALID = 'The e-mail address is invalid.';
var DISPLAY_NAME_INVALID = 'Name should only consist of alphanumerics or hyphens, apostrophes, fullstops, commas, slashes, round brackets<br> and not more than 40 characters.';
var DISPLAY_STUDENT_TEAMNAME_INVALID = 'Team name should contain less than 60 characters.';

// Used in instructorCourse.js only
var DISPLAY_COURSE_LONG_ID = 'Course ID should not exceed ' +
    COURSE_ID_MAX_LENGTH + ' characters.';
var DISPLAY_COURSE_LONG_NAME = 'Course name should not exceed ' +
    COURSE_NAME_MAX_LENGTH + ' characters.';
var DISPLAY_COURSE_INVALID_ID = 'Please use only alphabets, numbers, dots, hyphens, underscores and dollar signs in course ID.';
var DISPLAY_COURSE_COURSE_ID_EMPTY = 'Course ID cannot be empty.';
var DISPLAY_COURSE_COURSE_NAME_EMPTY = 'Course name cannot be empty';

// Used in instructorCourseEdit.js
var DISPLAY_INSTRUCTOR_ID_EMPTY = 'Instructor ID cannot be empty.';
var DISPLAY_INSTRUCTOR_NAME_EMPTY = 'Instructor name cannot be empty.';
var DISPLAY_INSTRUCTOR_EMAIL_EMPTY = 'Instructor email cannot be empty.';
var DISPLAY_CANNOT_DELETE_LAST_INSTRUCTOR = 'There is only ONE instructor left in the course. You are not allowed to delete this instructor.';

// Used in instructorCourseEnroll.js only
var DISPLAY_ENROLLMENT_INPUT_EMPTY = 'Please input at least one student detail.';

// Used in instructorEval.js only
var DISPLAY_EVALUATION_NAMEINVALID = 'Please use only alphabets, numbers and whitespace in evaluation name.';
var DISPLAY_EVALUATION_NAME_LENGTHINVALID = 'Evaluation name should not exceed 38 characters.';
var DISPLAY_EVALUATION_SCHEDULEINVALID = 'The evaluation schedule (start/deadline) is not valid.<br>' +
    'The start time should be in the future, and the deadline should be after start time.';
var DISPLAY_FIELDS_EMPTY = 'Please fill in all the relevant fields.';
var DISPLAY_INVALID_INPUT = 'Unexpected error. Invalid Input';

// Used in instructorFeedback.js only
var FEEDBACK_SESSION_COPY_INVALID = 'There is no feedback session to be copied.';
var FEEDBACK_QUESTION_COPY_INVALID = 'There is no feedback question to be copied.';
var DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE = 'This feedback session name already existed in this course. Please use another name.';
var DISPLAY_FEEDBACK_SESSION_NAME_EMPTY = 'Feedback session name must not be empty.';
var DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID = 'Please enter the maximum number of recipients each respondants should give feedback to.';

var DISPLAY_FEEDBACK_QUESTION_TEXTINVALID = 'Please enter a valid question. The question text cannot be empty.';
var DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID = 'Please enter valid options. The min/max/step cannot be empty.';
var DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID = 'Please enter valid options. The interval is not divisible by the specified increment.';

var DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID = 'Feedback session visible date must not be empty';
var DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID = 'Feedback session publish date must not be empty';

// Max length for input
var TEAMNAME_MAX_LENGTH = 60;
var NAME_MAX_LENGTH = 40;
var INSTITUTION_MAX_LENGTH = 64;

$(document).on('ajaxComplete ready', function() {
    $('[data-toggle="tooltip"]').tooltip({html: true, container: 'body'});
});

/**
 * Sorts a table
 * @param divElement
 *     The sort button
 * @param colIdx
 *     The column index (1-based) as key for the sort
 * @param row
 *     Row to start sorting from.
 *     The column index (0-based) e.g. use 2 if <th> has 2 rows so that the headers are not sorted.
 */
function toggleSort(divElement, colIdx, comparator, row) {
    row = row || 1;
    
    var $selectedDivElement = $(divElement);
    
    if ($selectedDivElement.attr('class') === 'button-sort-none') {
        sortTable(divElement, colIdx, comparator, true, row);
        $selectedDivElement.parent().find('.button-sort-ascending').attr('class', 'button-sort-none');
        $selectedDivElement.parent().find('.button-sort-descending').attr('class', 'button-sort-none');
        $selectedDivElement.parent().find('.icon-sort').attr('class', 'icon-sort unsorted');
        $selectedDivElement.attr('class', 'button-sort-ascending');
        $selectedDivElement.find('.icon-sort').attr('class', 'icon-sort sorted-ascending');
    } else if ($selectedDivElement.attr('class') === 'button-sort-ascending') {
        sortTable(divElement, colIdx, comparator, false, row);
        $selectedDivElement.attr('class', 'button-sort-descending');
        $selectedDivElement.find('.icon-sort').attr('class', 'icon-sort sorted-descending');
    } else {
        sortTable(divElement, colIdx, comparator, true, row);
        $selectedDivElement.attr('class', 'button-sort-ascending');
        $selectedDivElement.find('.icon-sort').attr('class', 'icon-sort sorted-ascending');
    }
}

// http://stackoverflow.com/questions/7558182/sort-a-table-fast-by-its-first-column-with-javascript-or-jquery
/**
 * Sorts a table based on certain column and comparator
 *
 * @param oneOfTableCell
 *     One of the table cell
 * @param colIdx
 *     The column index (1-based) as key for the sort
 * @param ascending
 *     if this is true, it will be ascending order, else it will be descending order
 */
function sortTable(oneOfTableCell, colIdx, comparator, ascending, row) {
    // Get the table
    var $table = $(oneOfTableCell);
    
    if (!$table.is('table')) {
        $table = $table.parents('table');
    }
    
    var columnType = 0;
    var store = [];
    var $RowList = $('tr', $table);
    
    // Iterate through column's contents to decide which comparator to use
    for (var i = row; i < $RowList.length; i++) {
        if ($RowList[i].cells[colIdx - 1] == undefined || $RowList[i].cells[colIdx - 1] == null) {
            continue;
        }
        
        // $.trim trims leading/trailing whitespaces
        // jQuery(...).text() works like .innerText, but works in Firefox (.innerText does not)
        // $RowList[i].cells[colIdx - 1] is where we get the table cell from
        var innerText = $.trim(jQuery($RowList[i].cells[colIdx - 1]).text());
        
        // Store rows together with the innerText to compare
        store.push([innerText, $RowList[i], i]);
        
        if ((columnType === 0 || columnType === 1) && isNumber(innerText)) {
            columnType = 1;
        } else if ((columnType === 0 || columnType === 2) && isDate(innerText)) {
            columnType = 2;
        } else {
            columnType = 3;
        }
    }
    
    if (comparator == null) {
        if (columnType === 1) {
            comparator = sortNum;
        } else if (columnType === 2) {
            comparator = sortDate;
        } else {
            comparator = sortBase;
        }
    }
    
    store.sort(function(x, y) {
        if (ascending === true) {
            var compareResult = comparator(x[0].toUpperCase(), y[0].toUpperCase());
            if (compareResult === 0) {
                return x[2] - y[2];
            } else {
                return compareResult;
            }
        } else {
            var compareResult = comparator(y[0].toUpperCase(), x[0].toUpperCase());
            if (compareResult === 0) {
                return x[2] - y[2];
            } else {
                return compareResult;
            }
        }
    });
    
    // Must rewrap because .get() does not return a jQuery wrapped DOM node
    // and hence does not have the .children() function
    var $tbody = $($table.get(0)).children('tbody');

    if ($tbody.size < 1) {
        $tbody = $table;
    }
    
    // Must push to target tbody else it will generate a new tbody for the table
    for (var i = 0; i < store.length; i++) {
        $tbody.get(0).appendChild(store[i][1]);
    }
    
    store = null;
}

/**
 * The base comparator (ascending)
 *
 * @param x
 * @param y
 * @returns
 */
function sortBase(x, y) {
    // Text sorting
    return (x < y ? -1 : x > y ? 1 : 0);
}

/**
 * Comparator for numbers (integer, double) (ascending)
 *
 * @param x
 * @param y
 * @returns
 */
function sortNum(x, y) {
    return x - y;
}

/**
 * Comparator for date. Allows for the same format as isDate()
 *
 * @param x
 * @param y
 * @returns 1 if Date x is after y, 0 if same and -1 if before
 */
function sortDate(x, y) {
    x = Date.parse(x);
    y = Date.parse(y);
    var comparisonResult = (x > y) ? 1 : (x < y) ? -1 : 0;
    return comparisonResult;
}

/**
* Function that returns the pattern of DayMonthYearFormat (dd/mm/yyyy)
*
* @returns pattern string
*/
function getDayMonthYearFormat() {
    return /^\s*(\d{2})[\/\- ](\d{2})[\/\- ](\d{4}|\d{2})\s*$/;
}

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
 * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
 * possibly a tag that surrounds it.
 *
 * @param a
 * @param b
 */
function sortByPoint(a, b) {
    a = getPointValue(a, true);
    b = getPointValue(b, true);
    
    if (isNumber(a) && isNumber(b)) {
        return sortNum(a, b);
    } else {
        return sortBase(a, b);
    }
}

/**
 * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
 * surrounds it.
 *
 * @param a
 * @param b
 */
function sortByDiff(a, b) {
    a = getPointValue(a, false);
    b = getPointValue(b, false);

    if (isNumber(a) && isNumber(b)) {
        return sortNum(a, b);
    } else {
        return sortBase(a, b);
    }
}

/**
 * To get point value from a formatted string
 *
 * @param s
 *     A table cell (td tag) that contains the formatted string
 * @param ditchZero
 *     Whether 0% should be treated as lower than -90 or not
 * @returns
 */
function getPointValue(s, ditchZero) {
    if (s.lastIndexOf('<') !== -1) {
        s = s.substring(0, s.lastIndexOf('<'));
        s = s.substring(s.lastIndexOf('>') + 1);
    }
    
    if (s.indexOf('/') !== -1) {
        if (s.indexOf('S') !== -1) {
            return 999; // Case N/S
        }
        
        return 1000; // Case N/A
    }
    
    if (s === '0%') { // Case 0%
        if (ditchZero) {
            return 0;
        } else {
            return 100;
        }
    }
    
    s = s.replace('E', '');
    s = s.replace('%', '');
    
    if (s === '') {
        return 100; // Case E
    }
    
    return 100 + eval(s); // Other typical cases
}

/** -----------------------UI Related Helper Functions-----------------------* */

var DIV_TOPOFPAGE = 'topOfPage';

/**
 * Scrolls the screen to top
 */
function scrollToTop() {
    document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

/** Selector for status message div tag (to be used in jQuery) */
var DIV_STATUS_MESSAGE = '#statusMessage';

/**
 * Sets a status message. Change the background color to red if it's an error
 *
 * @param message
 * @param error
 */
function setStatusMessage(message, error) {
    if (message === '') {
        clearStatusMessage();
        return;
    }
    
    $(DIV_STATUS_MESSAGE).html(message);
    $(DIV_STATUS_MESSAGE).show();
    
    if (error === true) {
        $(DIV_STATUS_MESSAGE).attr('class', 'alert alert-danger');
    } else {
        $(DIV_STATUS_MESSAGE).attr('class', 'alert alert-warning');
    }

    var positionToScrollTo = $(DIV_STATUS_MESSAGE).offset().top - (window.innerHeight / 2);
    
    window.scrollTo(0, positionToScrollTo);
}

/**
 * Clears the status message div tag and hides it
 */
function clearStatusMessage() {
    $(DIV_STATUS_MESSAGE).html('');
    $(DIV_STATUS_MESSAGE).css('background', '');
    $(DIV_STATUS_MESSAGE).attr('style', 'display: none;');
}

/**
 * Sanitize GoogleID by trimming space and '@gmail.com'
 * Used in instructorCourse, instructorCourseEdit, adminHome
 *
 * @param googleId
 * @returns sanitizedGoolgeId
 */
function sanitizeGoogleId(googleId) {
    googleId = googleId.trim();
    var loc = googleId.toLowerCase().indexOf('@gmail.com');
    if (loc > -1) {
        googleId = googleId.substring(0, loc);
    }
    return googleId.trim();
}

/**
 * Check if the GoogleID is valid
 * GoogleID allow only alphanumeric, full stops, dashes, underscores or valid email
 * 
 * @param googleId
 * @return {Boolean}
 */
function isValidGoogleId(googleId) {
    var isValidNonEmailGoogleId = false;
    googleId = googleId.trim();
    
    // match() retrieve the matches when matching a string against a regular expression.
    var matches = googleId.match(/^([\w-]+(?:\.[\w-]+)*)/);
    
    isValidNonEmailGoogleId = (matches != null && matches[0] === googleId);
    
    var isValidEmailGoogleId = isEmailValid(googleId);
    
    if (googleId.toLowerCase().indexOf('@gmail.com') > -1) {
        isValidEmailGoogleId = false;
    }
    
    // email addresses are valid google IDs too
    return isValidNonEmailGoogleId || isValidEmailGoogleId;
}

/**
 * Checks whether an e-mail is valid.
 * (Used in instructorCourseEdit.js)
 *
 * @param email
 * @returns {Boolean}
 */
function isEmailValid(email) {
    return email.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i) != null;
}

/**
 * Checks whether a person's name is valid.
 * (Used in instructorCourseEdit.js)
 *
 * @param name
 * @returns {Boolean}
 */
function isNameValid(name) {
    name = name.trim();

    if (name === '') {
        return false;
    }
    
    if (name.match(/[^\/\\,.'\-\(\)0-9a-zA-Z \t]/)) {
        // Returns true if a character NOT belonging to the following set
        // appears in the name: slash(/), backslash(\), fullstop(.), comma(,),
        // apostrophe('), hyphen(-), round brackets(()), alpha numeric
        // characters, space, tab
        return false;
    } else if (name.length > NAME_MAX_LENGTH) {
        return false;
    } else {
        return true;
    }
}

/**
 * Checks whether an institution name is valid
 * Used in adminHome page (through administrator.js)
 * @param name
 * @returns {Boolean}
 */
function isInstitutionValid(institution) {
    institution = institution.trim();

    if (institution === '') {
        return false;
    }
    
    if (institution.match(/[^\/\\,.'\-\(\)0-9a-zA-Z \t]/)) {
        // Returns true if a character NOT belonging to the following set
        // appears in the name: slash(/), backslash(\), fullstop(.), comma(,),
        // apostrophe('), hyphen(-), round brackets(()), alpha numeric
        // characters, space, tab
        return false;
    } else if (institution.length > NAME_MAX_LENGTH) {
        return false;
    } else {
        return true;
    }
}

/**
 * Disallow non-numeric entries
 * [Source: http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery]
 */
function disallowNonNumericEntries(element, decimalPointAllowed, negativeAllowed) {
    element.on('keydown', function(event) {
        var key = event.which;
        // Allow: backspace, delete, tab, escape, and enter
        if (key === 46 || key === 8 || key === 9 || key === 27 || key === 13 ||
            // Allow: Ctrl+A
            (key === 65 && event.ctrlKey) ||
             // Allow: home, end, left, right
            (key >= 35 && key <= 39) ||
             // Allow dot if decimal point is allowed
            (decimalPointAllowed && key === 190) ||
             // Allow hyphen if negative is allowed
             // Code differs by browser (FF/Opera:109, IE/Chrome:189)
             // see http://www.javascripter.net/faq/keycodes.htm
            (negativeAllowed && (key === 189 || key === 109))) {
            
            // let it happen, don't do anything
            return;
        } else {
            // Ensure that it is a number and stop the keypress
            if (event.shiftKey || (key < 48 || key > 57) && (key < 96 || key > 105)) {
                event.preventDefault();
                return false;
            }
        }
    });
}

/**
 * Helper function to replace all occurrences of a sub-string in a string.
 */
function replaceAll(string, find, replace) {
    return string.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

function escapeRegExp(string) {
    return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, '\\$1');
}

/**
 * Sanitizes special characters such as ' and \ to \' and \\ respectively
 */
function sanitizeForJs(string) {
    string = replaceAll(string, '\\', '\\\\');
    string = replaceAll(string, '\'', '\\\'');
    return string;
}

/**
 * Polyfills the String.prototype.includes function finalized in ES6 for browsers that do not yet support
 * the function.
 */
if (!String.prototype.includes) {
    String.prototype.includes = function() {
        'use strict';
        return String.prototype.indexOf.apply(this, arguments) !== -1;
    }
}
