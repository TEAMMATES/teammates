/* globals bootbox:false
 */

const COURSE_ID_MAX_LENGTH = 40;
const COURSE_NAME_MAX_LENGTH = 64;
const EVAL_NAME_MAX_LENGTH = 38;
const FEEDBACK_SESSION_NAME_MAX_LENGTH = 38;
const FEEDBACK_SESSION_INSTRUCTIONS_MAX_LENGTH = 500;

// Field names
const USER_ID = 'user';

// Used in instructorCourse.js
const COURSE_ID = 'courseid';
const COURSE_NAME = 'coursename';
const COURSE_TIME_ZONE = 'coursetimezone';
const COURSE_INSTRUCTOR_NAME = 'instructorname';
const COURSE_INSTRUCTOR_EMAIL = 'instructoremail';
const COURSE_INSTRUCTOR_ID = 'instructorid';

// Used in instructorEval.js
const EVALUATION_START = 'start';
const EVALUATION_STARTTIME = 'starttime';
const EVALUATION_TIMEZONE = 'timezone';

// Used in instructorFeedback.js
// TODO: Check if we can move most of these into instructorFeedback.js
const FEEDBACK_SESSION_NAME = 'fsname'; // also used in feedbackResponseComments.js
const FEEDBACK_SESSION_STARTDATE = 'startdate';
const FEEDBACK_SESSION_STARTTIME = 'starttime';
const FEEDBACK_SESSION_TIMEZONE = 'timezone';
const FEEDBACK_SESSION_CHANGETYPE = 'feedbackchangetype';
const FEEDBACK_SESSION_VISIBLEDATE = 'visibledate';
const FEEDBACK_SESSION_VISIBLETIME = 'visibletime';
const FEEDBACK_SESSION_PUBLISHDATE = 'publishdate';
const FEEDBACK_SESSION_PUBLISHTIME = 'publishtime';
const FEEDBACK_SESSION_SESSIONVISIBLEBUTTON = 'sessionVisibleFromButton';
const FEEDBACK_SESSION_RESULTSVISIBLEBUTTON = 'resultsVisibleFromButton';

// Used in instructorFeedbackEdit.js
// TODO: Check if we can move most of these into instructorFeedbackEdit.js
const FEEDBACK_QUESTION_GIVERTYPE = 'givertype';
const FEEDBACK_QUESTION_RECIPIENTTYPE = 'recipienttype';
const FEEDBACK_QUESTION_NUMBEROFENTITIES = 'numofrecipients';
const FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE = 'numofrecipientstype';
const FEEDBACK_QUESTION_TYPE = 'questiontype';
const FEEDBACK_QUESTION_MCQCHOICE = 'mcqOption';
const FEEDBACK_QUESTION_MCQOTHEROPTION = 'mcqOtherOption';
const FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG = 'mcqOtherOptionFlag';
const FEEDBACK_QUESTION_MSQCHOICE = 'msqOption';
const FEEDBACK_QUESTION_MSQOTHEROPTION = 'msqOtherOption';
const FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG = 'msqOtherOptionFlag';
const FEEDBACK_QUESTION_CONSTSUMOPTION = 'constSumOption';
const FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE = 'constSumOptionTable';
const FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS = 'constSumToRecipients';
const FEEDBACK_QUESTION_CONSTSUMPOINTS = 'constSumPoints';
const FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION = 'constSumPointsForEachOption';
const FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT = 'constSumPointsForEachRecipient';
const FEEDBACK_QUESTION_NUMBEROFCHOICECREATED = 'noofchoicecreated';
const FEEDBACK_QUESTION_NUMSCALE_MIN = 'numscalemin';
const FEEDBACK_QUESTION_NUMSCALE_MAX = 'numscalemax';
const FEEDBACK_QUESTION_NUMSCALE_STEP = 'numscalestep';
const FEEDBACK_QUESTION_NUMBER = 'questionnum';
const FEEDBACK_QUESTION_TEXT = 'questiontext';
const FEEDBACK_QUESTION_DESCRIPTION = 'questiondescription';
const FEEDBACK_QUESTION_EDITTEXT = 'questionedittext';
const FEEDBACK_QUESTION_DISCARDCHANGES = 'questiondiscardchanges';
const FEEDBACK_QUESTION_EDITTYPE = 'questionedittype';
const FEEDBACK_QUESTION_SAVECHANGESTEXT = 'questionsavechangestext';
const FEEDBACK_QUESTION_SHOWRESPONSESTO = 'showresponsesto';
const FEEDBACK_QUESTION_SHOWGIVERTO = 'showgiverto';
const FEEDBACK_QUESTION_SHOWRECIPIENTTO = 'showrecipientto';
const FEEDBACK_QUESTION_TYPENAME_TEXT = 'Essay question';
const FEEDBACK_QUESTION_TYPENAME_MCQ = 'Multiple-choice (single answer)';
const FEEDBACK_QUESTION_TYPENAME_MSQ = 'Multiple-choice (multiple answers)';
const FEEDBACK_QUESTION_TYPENAME_NUMSCALE = 'Numerical-scale question';
const FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION = 'Distribute points (among options) question';
const FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT = 'Distribute points (among recipients) question';
const FEEDBACK_QUESTION_TYPENAME_CONTRIB = 'Team contribution question';
const FEEDBACK_QUESTION_TYPENAME_RUBRIC = 'Rubric question';
const FEEDBACK_QUESTION_TYPENAME_RANK_OPTION = 'Rank options question';
const FEEDBACK_QUESTION_TYPENAME_RANK_RECIPIENT = 'Rank recipients question';
const FEEDBACK_QUESTION_RANKOPTION = 'rankOption';
const FEEDBACK_QUESTION_RANKOPTIONTABLE = 'rankOptionTable';
const FEEDBACK_QUESTION_RANKTORECIPIENTS = 'rankToRecipients';

// Used in feedbackResponseComments.js
const FEEDBACK_RESPONSE_ID = 'responseid';
const FEEDBACK_RESPONSE_COMMENT_ID = 'responsecommentid';
const FEEDBACK_RESPONSE_COMMENT_TEXT = 'responsecommenttext';

// Status message type
const StatusType = {
    SUCCESS: 'success',
    INFO: 'info',
    WARNING: 'warning',
    DANGER: 'danger',
    PRIMARY: 'primary',
    isValidType(type) {
        return type === StatusType.SUCCESS || type === StatusType.INFO || type === StatusType.PRIMARY
               || type === StatusType.WARNING || type === StatusType.DANGER;
    },
};
StatusType.DEFAULT = StatusType.INFO;

// Display messages
// Used for validating input
const DISPLAY_INPUT_FIELDS_EXTRA = 'There are too many fields.';
const DISPLAY_INPUT_FIELDS_MISSING = 'There are missing fields.';
const DISPLAY_GOOGLEID_INVALID = 'GoogleID should only consist of alphanumerics, fullstops, dashes or underscores.';
const DISPLAY_EMAIL_INVALID = 'The e-mail address is invalid.';
const DISPLAY_NAME_INVALID = 'Name should only consist of alphanumerics or hyphens, apostrophes, fullstops, '
                           + 'commas, slashes, round brackets<br> and not more than 40 characters.';
const DISPLAY_STUDENT_TEAMNAME_INVALID = 'Team name should contain less than 60 characters.';

// Used in instructorCourse.js only
const DISPLAY_COURSE_LONG_ID = `Course ID should not exceed ${COURSE_ID_MAX_LENGTH} characters.`;
const DISPLAY_COURSE_LONG_NAME = `Course name should not exceed ${COURSE_NAME_MAX_LENGTH} characters.`;
const DISPLAY_COURSE_INVALID_ID = 'Please use only alphabets, numbers, dots, hyphens, underscores and dollar signs '
                                + 'in course ID. Spaces are not allowed for course ID.';
const DISPLAY_COURSE_INVALID_TIME_ZONE = 'Please select a valid course time zone from the provided options.';
const DISPLAY_COURSE_COURSE_ID_EMPTY = 'Course ID cannot be empty.';
const DISPLAY_COURSE_COURSE_NAME_EMPTY = 'Course name cannot be empty';

// Used in instructorCourseEdit.js
const DISPLAY_INSTRUCTOR_ID_EMPTY = 'Instructor ID cannot be empty.';
const DISPLAY_INSTRUCTOR_NAME_EMPTY = 'Instructor name cannot be empty.';
const DISPLAY_INSTRUCTOR_EMAIL_EMPTY = 'Instructor email cannot be empty.';
const DISPLAY_CANNOT_DELETE_LAST_INSTRUCTOR = 'There is only ONE instructor left in the course. '
                                            + 'You are not allowed to delete this instructor.';

// Used in instructorCourseEnroll.js only
const DISPLAY_ENROLLMENT_INPUT_EMPTY = 'Please input at least one student detail.';

const DISPLAY_FIELDS_EMPTY = 'Please fill in all the relevant fields.';

// Used in instructorFeedback.js only
const FEEDBACK_SESSION_COPY_INVALID = 'There is no feedback session to be copied.';
const FEEDBACK_QUESTION_COPY_INVALID = 'There are no questions to be copied.';
const DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE =
        'This feedback session name already existed in this course. Please use another name.';
const DISPLAY_FEEDBACK_SESSION_NAME_EMPTY = 'Feedback session name must not be empty.';
const DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID =
        'Please enter the maximum number of recipients each respondents should give feedback to.';

const DISPLAY_FEEDBACK_QUESTION_TEXTINVALID = 'Please enter a valid question. The question text cannot be empty.';
const DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID = 'Please enter valid options. The min/max/step cannot be empty.';
const DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID =
        'Please enter valid options. The interval is not divisible by the specified increment.';

const DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID = 'Feedback session visible date must not be empty';
const DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID = 'Feedback session publish date must not be empty';

// Max length for input
const TEAMNAME_MAX_LENGTH = 60;
const NAME_MAX_LENGTH = 40;
const INSTITUTION_MAX_LENGTH = 64;

/**
 * Wrapper for Bootbox.js (available at http://bootboxjs.com/)
 * "Bootbox.js is a small JavaScript library which allows you to create programmatic dialog boxes using
 *  Bootstrap modals"
 */
const BootboxWrapper = {
    DEFAULT_OK_TEXT: 'OK',
    DEFAULT_CANCEL_TEXT: 'Cancel',
    DEFAULT_YES_TEXT: 'Yes',
    DEFAULT_NO_TEXT: 'No',

    /**
     * Custom alert dialog to replace default alert() function
     * Required params: titleText and messageText
     * Optional params: okButtonText (defaults to "OK")
     *                  color (defaults to StatusType.DEFAULT)
     */
    showModalAlert(titleText, messageText, okButtonText, color) {
        bootbox.dialog({
            title: titleText,
            message: messageText,
            buttons: {
                okay: {
                    label: okButtonText || BootboxWrapper.DEFAULT_OK_TEXT,
                    className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
                },
            },
        })
        // applies bootstrap color to title background
        .find('.modal-header').addClass(`alert-${color}` || StatusType.DEFAULT);
    },

    /**
     * Custom confirmation dialog to replace default confirm() function
     * Required params: titleText, messageText and okCallback
     * Optional params: cancelCallBack (defaults to null)
     *                  okButtonText (defaults to "OK")
     *                  cancelButtonText (defaults to "Cancel")
     *                  color (defaults to StatusType.INFO)
     */
    showModalConfirmation(titleText, messageText, okCallback, cancelCallback,
                                    okButtonText, cancelButtonText, color) {
        bootbox.dialog({
            title: titleText,
            message: messageText,
            buttons: {
                cancel: {
                    label: cancelButtonText || BootboxWrapper.DEFAULT_CANCEL_TEXT,
                    className: 'modal-btn-cancel btn-default',
                    callback: cancelCallback || null,
                },
                ok: {
                    label: okButtonText || BootboxWrapper.DEFAULT_OK_TEXT,
                    className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
                    callback: okCallback,
                },
            },
        })
        // applies bootstrap color to title background
        .find('.modal-header').addClass(`alert-${color}` || StatusType.DEFAULT);
    },

    /**
     * Custom confirmation dialog to replace default confirm() function
     * Required params: titleText, messageText, yesButtonCallback and noButtonCallback
     * Optional params: cancelButtonCallBack (defaults to null)
     *                  yesButtonText (defaults to "Yes")
     *                  noButtonText (defaults to "No")
     *                  canelButtonText (defaults to "Cancel")
     *                  color (defaults to StatusType.INFO)
     */
    showModalConfirmationWithCancel(titleText, messageText, yesButtonCallback, noButtonCallback,
                                     cancelButtonCallback, yesButtonText, noButtonText, cancelButtonText, color) {
        bootbox.dialog({
            title: titleText,
            message: messageText,
            buttons: {
                yes: {
                    label: yesButtonText || BootboxWrapper.DEFAULT_YES_TEXT,
                    className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
                    callback: yesButtonCallback,
                },
                no: {
                    label: noButtonText || BootboxWrapper.DEFAULT_NO_TEXT,
                    className: `modal-btn-ok btn-${color}` || StatusType.DEFAULT,
                    callback: noButtonCallback,
                },
                cancel: {
                    label: cancelButtonText || BootboxWrapper.DEFAULT_CANCEL_TEXT,
                    className: 'modal-btn-cancel btn-default',
                    callback: cancelButtonCallback || null,
                },
            },
        })
        // applies bootstrap color to title background
        .find('.modal-header').addClass(`alert-${color}` || StatusType.DEFAULT);
    },
};

/**
* Encodes a string for displaying in a HTML document.
* Uses an in-memory element created with jQuery.
* @param the string to be encoded
*/
function encodeHtmlString(stringToEncode) {
    return $('<div>').text(stringToEncode).html();
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
    if (x < y) {
        return -1;
    }
    return x > y ? 1 : 0;
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
    const x0 = Date.parse(x);
    const y0 = Date.parse(y);
    if (x0 > y0) {
        return 1;
    }
    return x0 < y0 ? -1 : 0;
}

/**
* Function that returns the pattern of DayMonthYearFormat (dd/mm/yyyy)
*
* @returns pattern string
*/
function getDayMonthYearFormat() {
    return /^\s*(\d{2})[/\- ](\d{2})[/\- ](\d{4}|\d{2})\s*$/;
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
 * To get point value from a formatted string
 *
 * @param s
 *     A table cell (td tag) that contains the formatted string
 * @param ditchZero
 *     Whether 0% should be treated as lower than -90 or not
 * @returns
 */
function getPointValue(s, ditchZero) {
    let s0 = s;
    const baseValue = 100;

    if (s0.indexOf('/') !== -1) {
        // magic expressions below as these cases will only be compared with
        // case E +(-)X% (0 <= X <= 100)
        if (s0.indexOf('S') !== -1) {
            return 2 * baseValue + 1; // Case N/S (feedback contribution not sure)
        }

        return 2 * baseValue + 2; // Case N/A
    }

    if (s0 === '0%') { // Case 0%
        if (ditchZero) {
            return 0;
        }
        return baseValue;
    }

    s0 = s0.replace('E', '').replace('%', ''); // Case E +(-)X%

    if (s0 === '') {
        return baseValue; // Case E
    }

    return baseValue + parseFloat(s0); // Other typical cases
}

/**
 * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
 * possibly a tag that surrounds it.
 *
 * @param a
 * @param b
 */
function sortByPoint(a, b) {
    const a0 = getPointValue(a, true);
    const b0 = getPointValue(b, true);

    if (isNumber(a0) && isNumber(b0)) {
        return sortNum(a0, b0);
    }
    return sortBase(a0, b0);
}

/**
 * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
 * surrounds it.
 *
 * @param a
 * @param b
 */
function sortByDiff(a, b) {
    const a0 = getPointValue(a, false);
    const b0 = getPointValue(b, false);

    if (isNumber(a0) && isNumber(b0)) {
        return sortNum(a0, b0);
    }
    return sortBase(a0, b0);
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
function sortTable(oneOfTableCell, colIdx, comp, ascending, row) {
    // Get the table
    let $table = $(oneOfTableCell);

    if (!$table.is('table')) {
        $table = $table.parents('table');
    }

    let columnType = 0;
    let store = [];
    const $RowList = $('tr', $table);
    // For date comparisons in instructor home page we should use
    // the tool-tip value instead of display text since display text does not contain the year.
    const shouldConsiderToolTipYear = comp && comp.toString().includes('instructorHomeDateComparator');

    // Iterate through column's contents to decide which comparator to use
    let textToCompare;
    for (let i = row; i < $RowList.length; i += 1) {
        if ($RowList[i].cells[colIdx - 1] === undefined) {
            continue;
        }

        // $.trim trims leading/trailing whitespaces
        // jQuery(...).text() works like .innerText, but works in Firefox (.innerText does not)
        // $RowList[i].cells[colIdx - 1] is where we get the table cell from
        // If shouldConsiderToolTipYear is true, we consider the tooltip value instead of innerText
        textToCompare = shouldConsiderToolTipYear
                ? $.trim($($RowList[i].cells[colIdx - 1]).find('span').attr('data-original-title'))
                : $.trim($($RowList[i].cells[colIdx - 1]).text());

        // Store rows together with the innerText to compare
        store.push([textToCompare, $RowList[i], i]);

        if ((columnType === 0 || columnType === 1) && isNumber(textToCompare)) {
            columnType = 1;
        } else if ((columnType === 0 || columnType === 2) && isDate(textToCompare)) {
            columnType = 2;
        } else {
            columnType = 3;
        }
    }

    let comparator = comp;
    if (comparator === null || comparator === undefined) {
        if (columnType === 1) {
            comparator = sortNum;
        } else if (columnType === 2) {
            comparator = sortDate;
        } else {
            comparator = sortBase;
        }
    }

    store.sort((x, y) => {
        const compareResult = ascending ? comparator(x[0].toUpperCase(), y[0].toUpperCase())
                                      : comparator(y[0].toUpperCase(), x[0].toUpperCase());
        if (compareResult === 0) {
            return x[2] - y[2];
        }
        return compareResult;
    });

    // Must rewrap because .get() does not return a jQuery wrapped DOM node
    // and hence does not have the .children() function
    let $tbody = $($table.get(0)).children('tbody');

    if ($tbody.size < 1) {
        $tbody = $table;
    }

    // Must push to target tbody else it will generate a new tbody for the table
    for (let j = 0; j < store.length; j += 1) {
        $tbody.get(0).appendChild(store[j][1]);
    }

    store = null;
}

/**
 * Binds a default image if the image is missing.
 * @param element Image element.
 */
function bindDefaultImageIfMissing(element) {
    $(element).on('error', function () {
        if ($(this).attr('src') !== '') {
            $(this).attr('src', '/images/profile_picture_default.png');
        }
    });
}

/**
 * Checks if the current device is touch based device
 * Reference: https://github.com/Modernizr/Modernizr/blob/master/feature-detects/touchevents.js
 */
function isTouchDevice() {
    return 'ontouchstart' in window || window.DocumentTouch && document instanceof window.DocumentTouch;
}

/**
 * Sorts a table
 * @param divElement
 *     The sort button
 * @param comparator
 *     The function to compare 2 elements
 */
function toggleSort(divElement, comparator) {
    // The column index (1-based) as key for the sort
    const colIdx = $(divElement).parent().children().index($(divElement)) + 1;

    // Row to start sorting from (0-based), set to 1 since <th> occupies the first row
    const row = 1;

    const $selectedDivElement = $(divElement);

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

/** -----------------------UI Related Helper Functions-----------------------* */

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

    const navbar = document.getElementsByClassName('navbar')[0];
    const navbarHeight = navbar ? navbar.offsetHeight : 0;
    const footer = document.getElementById('footerComponent');
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
        offset = offset + element.offsetHeight - window.innerHeight;
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

/** Selector for status message div tag (to be used in jQuery) */
const DIV_STATUS_MESSAGE = '#statusMessagesToUser';

/**
 * Populates the status div with the message and the message status.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param status type
 * @return created status message div
 */
function populateStatusMessageDiv(message, status) {
    const $statusMessageDivToUser = $(DIV_STATUS_MESSAGE);
    const $statusMessageDivContent = $('<div></div>');

    $statusMessageDivContent.addClass('overflow-auto');
    $statusMessageDivContent.addClass('alert');
    // Default the status type to info if any invalid status is passed in
    $statusMessageDivContent.addClass(`alert-${StatusType.isValidType(status) ? status : StatusType.INFO}`);
    $statusMessageDivContent.addClass('statusMessage');
    $statusMessageDivContent.html(message);

    $statusMessageDivToUser.empty();
    $statusMessageDivToUser.append($statusMessageDivContent);
    return $statusMessageDivToUser;
}

/**
 * Sets a status message and the message status.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param status type
 */
function setStatusMessage(message, status) {
    if (message === '' || message === undefined || message === null) {
        return;
    }
    const $statusMessageDivToUser = populateStatusMessageDiv(message, status);
    $statusMessageDivToUser.show();
    scrollToElement($statusMessageDivToUser[0], { offset: window.innerHeight / 2 * -1 });
}

/**
 * Sets a status message and the message status to a given form.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param status type
 * @param form form which should own the status
 */
function setStatusMessageToForm(message, status, form) {
    if (message === '' || message === undefined || message === null) {
        return;
    }
    // Copy the statusMessage and prepend to form
    const $copyOfStatusMessagesToUser = populateStatusMessageDiv(message, status).clone().show();
    $(DIV_STATUS_MESSAGE).remove();
    $(form).prepend($copyOfStatusMessagesToUser);
    const opts = {
        offset: window.innerHeight / 8 * -1,
        duration: 1000,
    };
    scrollToElement($copyOfStatusMessagesToUser[0], opts);
}

/**
 * Appends the status messages panels into the current list of panels of status messages.
 * @param  messages the list of status message panels to be added (not just text)
 *
 */
function appendStatusMessage(messages) {
    const $statusMessagesToUser = $(DIV_STATUS_MESSAGE);

    $statusMessagesToUser.append($(messages));
    $statusMessagesToUser.show();
}

/**
 * Clears the status message div tag and hides it
 */
function clearStatusMessages() {
    const $statusMessagesToUser = $(DIV_STATUS_MESSAGE);

    $statusMessagesToUser.empty();
    $statusMessagesToUser.hide();
}

function addLoadingIndicator(button, loadingText) {
    button.html(loadingText);
    button.prop('disabled', true);
    button.append('<img src="/images/ajax-loader.gif">');
}

function removeLoadingIndicator(button, displayText) {
    button.empty();
    button.html(displayText);
    button.prop('disabled', false);
}

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
    } else if (name.length > NAME_MAX_LENGTH) {
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
    } else if (institution.length > NAME_MAX_LENGTH) {
        return false;
    }
    return true;
}

/**
 * Disallow non-numeric entries
 * [Source: http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery]
 */
function disallowNonNumericEntries(element, decimalPointAllowed, negativeAllowed) {
    element.on('keydown', (event) => {
        const key = event.which;
        // Allow: backspace, delete, tab, escape, and enter
        if (key === 46 || key === 8 || key === 9 || key === 27 || key === 13
            // Allow: Ctrl+A
            || key === 65 && event.ctrlKey
             // Allow: home, end, left, right
            || key >= 35 && key <= 39
             // Allow dot if decimal point is allowed
            || decimalPointAllowed && key === 190
             // Allow hyphen if negative is allowed
             // Code differs by browser (FF/Opera:109, IE/Chrome:189)
             // see http://www.javascripter.net/faq/keycodes.htm
            || negativeAllowed && (key === 189 || key === 109)) {
            // let it happen, don't do anything
        } else if (event.shiftKey || (key < 48 || key > 57) && (key < 96 || key > 105)) {
            // Ensure that it is a number and stop the keypress
            event.preventDefault();
        }
    });
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

/**
 * Highlights all words of searchKey (case insensitive), in a particular section
 * Format of the string  higlight plugin uses - ( ['string1','string2',...] )
 * @param searchKeyId - Id of searchKey input field
 * @param sectionToHighlight - sections to higlight separated by ',' (comma)
 *                             Example- '.panel-body, #panel-data, .sub-container'
 */
function highlightSearchResult(searchKeyId, sectionToHighlight) {
    const searchKey = $(searchKeyId).val().trim();
    // split search key string on symbols and spaces and add to searchKeyList
    let searchKeyList = [];
    if (searchKey.charAt(0) === '"' && searchKey.charAt(searchKey.length - 1) === '"') {
        searchKeyList.push(searchKey.replace(/"/g, '').trim());
    } else {
        $.each(searchKey.split(/[ "'.-]/), function () {
            searchKeyList.push($.trim(this));
        });
    }
    // remove empty elements from searchKeyList
    searchKeyList = searchKeyList.filter(n => n !== '');
    $(sectionToHighlight).highlight(searchKeyList);
}

/**
 * Polyfills the String.prototype.includes function finalized in ES6 for browsers that do not yet support the function.
 */
/* eslint-disable no-extend-native */ // necessary for polyfills
if (!String.prototype.includes) {
    String.prototype.includes = function (search, startParam) {
        const start = typeof startParam === 'number' ? startParam : 0;

        if (start + search.length > this.length) {
            return false;
        }
        return this.indexOf(search, start) !== -1;
    };
}
/* eslint-enable no-extend-native */

/**
 * Checks if the input value is a blank string
 *
 * @param str
 * @returns true if the input is a blank string, false otherwise
 */
function isBlank(str) {
    if (typeof str !== 'string' && !(str instanceof String)) {
        return false;
    }
    return str.trim() === '';
}

/**
 * Sets the chevron to point upwards.
 */
function setChevronToUp(chevronContainer) {
    chevronContainer.removeClass('glyphicon-chevron-down');
    chevronContainer.addClass('glyphicon-chevron-up');
}

/**
 * Sets the chevron to point downwards.
 */
function setChevronToDown(chevronContainer) {
    chevronContainer.removeClass('glyphicon-chevron-up');
    chevronContainer.addClass('glyphicon-chevron-down');
}

/**
 * Sets the chevron of a panel from up to down or from down to up depending on its current state.
 * clickedElement must be at least the parent of the chevron.
 */
function toggleChevron(clickedElement) {
    const $clickedElement = $(clickedElement);
    const isChevronDown = $clickedElement.find('.glyphicon-chevron-down').length > 0;
    const $chevronContainer = $clickedElement.find('.glyphicon');

    // clearQueue to clear the animation queue to prevent animation build up
    $chevronContainer.clearQueue();

    if (isChevronDown) {
        setChevronToUp($chevronContainer);
    } else {
        setChevronToDown($chevronContainer);
    }
}

/**
 * Shows panel's content and changes chevron to point up.
 */
function showSingleCollapse(e) {
    const heading = $(e).parent().children('.panel-heading');
    const glyphIcon = $(heading[0]).find('.glyphicon');
    setChevronToUp($(glyphIcon[0]));
    $(e).collapse('show');
    $(heading).find('a.btn').show();
}

/**
 * Hides panel's content and changes chevron to point down.
 */
function hideSingleCollapse(e) {
    const heading = $(e).parent().children('.panel-heading');
    const glyphIcon = $(heading[0]).find('.glyphicon');
    setChevronToDown($(glyphIcon[0]));
    $(e).collapse('hide');
    $(heading).find('a.btn').hide();
}

/**
 * Changes the state of the panel (collapsed/expanded).
 */
function toggleSingleCollapse(e) {
    if ($(e.target).is('a') || $(e.target).is('input')) {
        return;
    }
    const glyphIcon = $(this).find('.glyphicon');
    const className = $(glyphIcon[0]).attr('class');
    if (className.indexOf('glyphicon-chevron-up') === -1) {
        showSingleCollapse($(e.currentTarget).attr('data-target'));
    } else {
        hideSingleCollapse($(e.currentTarget).attr('data-target'));
    }
}


// Toggle the visibility of additional question information for the specified question.
function toggleAdditionalQuestionInfo(identifier) {
    const $questionButton = $(`#questionAdditionalInfoButton-${identifier}`);

    if ($questionButton.text() === $questionButton.attr('data-more')) {
        $questionButton.text($questionButton.attr('data-less'));
    } else {
        $questionButton.text($questionButton.attr('data-more'));
    }

    $(`#questionAdditionalInfo-${identifier}`).toggle();
}

$(document).on('ajaxComplete ready', () => {
    $('.profile-pic-icon-hover, .profile-pic-icon-click, .teamMembersPhotoCell').children('img').each(function () {
        bindDefaultImageIfMissing(this);
    });

    /**
     * Initializing then disabling is better than simply
     * not initializing for mobile due to some tooltips-specific
     * code that throws errors.
    */
    const $tooltips = $('[data-toggle="tooltip"]');
    $tooltips.tooltip({
        html: true,
        container: 'body',
    });
    if (isTouchDevice()) {
        $tooltips.tooltip('disable');
    }

    /**
     * Underlines all span elements with tool-tips except for
     * the ones without a text value. This is to exclude elements
     * such as 'icons' from underlining.
    */
    $('span[data-toggle="tooltip"]').each(function () {
        const textValue = $(this).text().replace(/\s/g, '');
        if (textValue) {
            $(this).addClass('tool-tip-decorate');
        }
    });
});
