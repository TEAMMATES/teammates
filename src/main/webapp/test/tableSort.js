/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 45);
/******/ })
/************************************************************************/
/******/ ({

/***/ 0:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
 * Contains constants to be used across the application.
 */

// Frontend only constants
var Const = {
    ModalDialog: {
        UNREGISTERED_STUDENT: {
            header: 'Register for TEAMMATES',
            text: 'You have to register using a google account in order to access this page. ' + 'Would you like to proceed and register?'
        }
    },
    StatusMessages: {
        INSTRUCTOR_DETAILS_LENGTH_INVALID: 'Instructor Details must have 3 columns'
    }
};

// Shared constants between frontend and backend

/**
 * Subset of Bootstrap contextual colors for use in status messages and components of modals.
 * @enum {BootstrapContextualColors}
 */
var BootstrapContextualColors = {
    // Mirrored colors from StatusMessageColor
    SUCCESS: 'success',
    INFO: 'info',
    WARNING: 'warning',
    DANGER: 'danger',
    // Additional contextual colors that can be used in the components of modals
    PRIMARY: 'primary',
    isValidType: function isValidType(type) {
        return type === BootstrapContextualColors.SUCCESS || type === BootstrapContextualColors.INFO || type === BootstrapContextualColors.PRIMARY || type === BootstrapContextualColors.WARNING || type === BootstrapContextualColors.DANGER;
    }
};
BootstrapContextualColors.DEFAULT = BootstrapContextualColors.INFO;

// Mirrored subset of Const#ParamsNames
var ParamsNames = {
    SESSION_TOKEN: 'token',
    COPIED_FEEDBACK_SESSION_NAME: 'copiedfsname',

    COURSE_ID: 'courseid',
    COURSE_NAME: 'coursename',
    COURSE_TIME_ZONE: 'coursetimezone',

    FEEDBACK_SESSION_NAME: 'fsname',
    FEEDBACK_SESSION_STARTDATE: 'startdate',
    FEEDBACK_SESSION_STARTTIME: 'starttime',
    FEEDBACK_SESSION_VISIBLEDATE: 'visibledate',
    FEEDBACK_SESSION_VISIBLETIME: 'visibletime',
    FEEDBACK_SESSION_PUBLISHDATE: 'publishdate',
    FEEDBACK_SESSION_PUBLISHTIME: 'publishtime',
    FEEDBACK_SESSION_TIMEZONE: 'timezone',
    FEEDBACK_SESSION_SESSIONVISIBLEBUTTON: 'sessionVisibleFromButton',
    FEEDBACK_SESSION_RESULTSVISIBLEBUTTON: 'resultsVisibleFromButton',
    FEEDBACK_SESSION_ENABLE_EDIT: 'editsessiondetails',

    FEEDBACK_QUESTION_TEXT: 'questiontext',
    FEEDBACK_QUESTION_DESCRIPTION: 'questiondescription',
    FEEDBACK_QUESTION_TYPE: 'questiontype',
    FEEDBACK_QUESTION_NUMBEROFCHOICECREATED: 'noofchoicecreated',
    FEEDBACK_QUESTION_MCQCHOICE: 'mcqOption',
    FEEDBACK_QUESTION_MCQ_WEIGHT: 'mcqWeight',
    FEEDBACK_QUESTION_MSQCHOICE: 'msqOption',
    FEEDBACK_QUESTION_MSQ_WEIGHT: 'msqWeight',
    FEEDBACK_QUESTION_CONSTSUMOPTION: 'constSumOption',
    FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS: 'constSumToRecipients',
    FEEDBACK_QUESTION_CONSTSUMPOINTS: 'constSumPoints',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION: 'constSumPointsForEachOption',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT: 'constSumPointsForEachRecipient',
    FEEDBACK_QUESTION_CONSTSUMALLUNEVENDISTRIBUTION: 'All options',
    FEEDBACK_QUESTION_CONSTSUMSOMEUNEVENDISTRIBUTION: 'At least some options',
    FEEDBACK_QUESTION_CONSTSUMNOUNEVENDISTRIBUTION: 'None',
    FEEDBACK_QUESTION_RECIPIENTTYPE: 'recipienttype',
    FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE: 'numofrecipientstype',
    FEEDBACK_QUESTION_EDITTEXT: 'questionedittext',
    FEEDBACK_QUESTION_DISCARDCHANGES: 'questiondiscardchanges',
    FEEDBACK_QUESTION_EDITTYPE: 'questionedittype',
    FEEDBACK_QUESTION_SAVECHANGESTEXT: 'questionsavechangestext',
    FEEDBACK_QUESTION_SHOWRESPONSESTO: 'showresponsesto',
    FEEDBACK_QUESTION_SHOWGIVERTO: 'showgiverto',
    FEEDBACK_QUESTION_SHOWRECIPIENTTO: 'showrecipientto',
    FEEDBACK_QUESTION_NUMSCALE_MIN: 'numscalemin',
    FEEDBACK_QUESTION_NUMSCALE_MAX: 'numscalemax',
    FEEDBACK_QUESTION_NUMSCALE_STEP: 'numscalestep',
    FEEDBACK_QUESTION_RANKOPTION: 'rankOption',
    FEEDBACK_QUESTION_RANKTORECIPIENTS: 'rankToRecipients',
    FEEDBACK_QUESTION_NUMBER: 'questionnum',
    FEEDBACK_QUESTION_NUMBER_STATIC: 'questionnum-static',
    FEEDBACK_QUESTION_RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED: 'minOptionsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED: 'maxOptionsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_MIN_OPTIONS_TO_BE_RANKED: 'minOptionsToBeRanked',
    FEEDBACK_QUESTION_RANK_MAX_OPTIONS_TO_BE_RANKED: 'maxOptionsToBeRanked',
    FEEDBACK_QUESTION_RANK_IS_MIN_RECIPIENTS_TO_BE_RANKED_ENABLED: 'minRecipientsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_IS_MAX_RECIPIENTS_TO_BE_RANKED_ENABLED: 'maxRecipientsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_MIN_RECIPIENTS_TO_BE_RANKED: 'minRecipientsToBeRanked',
    FEEDBACK_QUESTION_RANK_MAX_RECIPIENTS_TO_BE_RANKED: 'maxRecipientsToBeRanked'
};

exports.Const = Const;
exports.ParamsNames = ParamsNames;
exports.BootstrapContextualColors = BootstrapContextualColors;

/***/ }),

/***/ 1:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
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
    return !Number.isNaN(Date.parse(date));
}

/**
* Function to test if param is a numerical value
* @param num
* @returns boolean
*/
function isNumber(num) {
    return (typeof num === 'string' || typeof num === 'number') && !Number.isNaN(num - 0) && num !== '';
}

/**
 * Checks if element is within browser's viewport.
 * @return true if it is within the viewport, false otherwise
 * @see http://stackoverflow.com/q/123999
 */
function isWithinView(element) {
    var baseElement = $(element)[0]; // unwrap jquery element
    var rect = baseElement.getBoundingClientRect();

    var $viewport = $(window);

    // makes the viewport size slightly larger to account for rounding errors
    var tolerance = 0.25;
    return rect.top >= 0 - tolerance // below the top of viewport
    && rect.left >= 0 - tolerance // within the left of viewport
    && rect.right <= $viewport.width() + tolerance // within the right of viewport
    && rect.bottom <= $viewport.height() + tolerance // above the bottom of viewport
    ;
}

/**
 * Extracts the suffix that follows the prefix from the id. For example, commentDelete-1-1-0-1 => 1-1-0-1.
 * @param {Object} options required options
 * @param {string} options.idPrefix the prefix of the id
 * @param {string} options.id the id to extract from
 * @return {string} the suffix that uniquely identifies an element among elements with the same prefix
 */
function extractIdSuffixFromId() {
    var _ref = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},
        idPrefix = _ref.idPrefix,
        id = _ref.id;

    return new RegExp(idPrefix + '-(.*)').exec(id)[1];
}

exports.isDate = isDate;
exports.isNumber = isNumber;
exports.isWithinView = isWithinView;
exports.extractIdSuffixFromId = extractIdSuffixFromId;

/***/ }),

/***/ 44:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _crypto = __webpack_require__(6);

var _sortBy = __webpack_require__(8);

/**
 * Polyfills the String.prototype.includes function finalized in ES6 for browsers that do not yet support the function.
 */
/* eslint-disable no-extend-native */ // necessary for polyfills
if (!String.prototype.includes) {
    String.prototype.includes = function (search, startParam) {
        var start = typeof startParam === 'number' ? startParam : 0;

        if (start + search.length > this.length) {
            return false;
        }
        return this.indexOf(search, start) !== -1;
    };
}

/**
 * Polyfills the Number.EPSILON property finalized in ES6 for browsers that do not yet support the property.
 */
if (!Number.EPSILON) {
    // Exponentiation operator (a ** b) is not supported in ES6
    // eslint-disable-next-line no-restricted-properties
    Number.EPSILON = Math.pow(2, -52);
}
/* eslint-enable no-extend-native */

/**
 * Checks if the current device is touch based device
 * Reference: https://github.com/Modernizr/Modernizr/blob/master/feature-detects/touchevents.js
 */
function isTouchDevice() {
    return 'ontouchstart' in window || window.DocumentTouch && document instanceof window.DocumentTouch;
}

$(document).on('click', '.toggle-sort', function (e) {
    var $button = $(e.currentTarget); // the button clicked on

    var comparatorStringOrNull = $button.data('toggle-sort-comparator');
    var extractorStringOrNull = $button.data('toggle-sort-extractor');

    (0, _sortBy.toggleSort)($button, comparatorStringOrNull, extractorStringOrNull);
});

$(document).on('ajaxComplete ready', function () {
    /**
     * Initializing then disabling is better than simply
     * not initializing for mobile due to some tooltips-specific
     * code that throws errors.
    */
    var $tooltips = $('[data-toggle="tooltip"]');
    $tooltips.tooltip({
        html: true,
        container: 'body'
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
        var textValue = $(this).text().replace(/\s/g, '');
        if (textValue) {
            $(this).addClass('tool-tip-decorate');
        }
    });

    /**
     * Updates the token in input fields with the latest one retrieved from the cookie.
     * The token becomes outdated once the session expires. The cookie might be updated
     * with the new token and session during page loads from other browser windows.
     * The latest value should be retrieved from the cookie before form submission.
     */
    $('form').submit(_crypto.updateCsrfTokenInInputFields);
});

/***/ }),

/***/ 45:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(44);


/***/ }),

/***/ 6:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.updateCsrfTokenInInputFields = exports.makeCsrfTokenParam = undefined;

var _const = __webpack_require__(0);

/**
 * Returns the value of a cookie given its name.
 * Returns null if the cookie is not set.
 */
function getCookie(cookieNameToFind) {
    var cookies = document.cookie.split('; ').map(function (s) {
        return s.split('=');
    });

    for (var i = 0; i < cookies.length; i += 1) {
        var cookieName = cookies[i][0];
        var cookieValue = cookies[i][1];

        // the cookie was found in the ith iteration
        if (cookieName === cookieNameToFind) {
            return cookieValue;
        }
    }

    // the cookie was not found
    return null;
}

function makeCsrfTokenParam() {
    return _const.ParamsNames.SESSION_TOKEN + '=' + getCookie(_const.ParamsNames.SESSION_TOKEN);
}

function updateCsrfTokenInInputFields() {
    var updatedToken = getCookie(_const.ParamsNames.SESSION_TOKEN);
    if (!updatedToken) {
        return;
    }
    $('input[name=' + _const.ParamsNames.SESSION_TOKEN + ']').val(updatedToken);
}

exports.makeCsrfTokenParam = makeCsrfTokenParam;
exports.updateCsrfTokenInInputFields = updateCsrfTokenInInputFields;

/***/ }),

/***/ 8:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.toggleSort = exports.getPointValue = exports.Comparators = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _helper = __webpack_require__(1);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/**
 * Returns false if the parameter is either one of null or undefined.
 * Returns true otherwise.
 *
 * @param varToCheck
 */
function isDefined(varToCheck) {
    return varToCheck !== null && varToCheck !== undefined;
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
    var s0 = s;
    var baseValue = 100;

    if (s0.indexOf('/') !== -1) {
        // magic expressions below as these cases will only be compared with
        // case E +(-)X% (0 <= X <= 100)
        if (s0.indexOf('S') !== -1) {
            return 2 * baseValue + 1; // Case N/S (feedback contribution not sure)
        }

        return 2 * baseValue + 2; // Case N/A
    }

    if (s0 === '0%') {
        // Case 0%
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
 * Sorting comparison functions.
 */

var Comparators = function () {
    function Comparators() {
        _classCallCheck(this, Comparators);
    }

    _createClass(Comparators, null, [{
        key: 'sortBase',

        /**
         * The base comparator (ascending)
         * @returns 1 if x comes after y, -1 if x comes before y, 0 if they are the same
         */
        value: function sortBase(x, y) {
            // Text sorting
            if (x < y) {
                return -1;
            }
            return x > y ? 1 : 0;
        }

        /**
         * Comparator for numbers (integer, double) (ascending)
         * @returns +ve if x > y, -ve if x < y and 0 otherwise
         */

    }, {
        key: 'sortNum',
        value: function sortNum(x, y) {
            return x - y;
        }

        /**
         * Comparator for date. Allows for the same format as isDate()
         * @returns 1 if Date x is after y, 0 if same and -1 if before
         */

    }, {
        key: 'sortDate',
        value: function sortDate(x, y) {
            var x0 = Date.parse(x);
            var y0 = Date.parse(y);
            if (x0 > y0) {
                return 1;
            }
            return x0 < y0 ? -1 : 0;
        }

        /**
         * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
         * possibly a tag that surrounds it.
         */

    }, {
        key: 'sortByPoints',
        value: function sortByPoints(a, b) {
            var a0 = getPointValue(a, true);
            var b0 = getPointValue(b, true);
            if ((0, _helper.isNumber)(a0) && (0, _helper.isNumber)(b0)) {
                return Comparators.sortNum(a0, b0);
            }
            return Comparators.sortBase(a0, b0);
        }

        /**
         * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
         * surrounds it.
         */

    }, {
        key: 'sortByDiff',
        value: function sortByDiff(a, b) {
            var a0 = getPointValue(a, false);
            var b0 = getPointValue(b, false);
            if ((0, _helper.isNumber)(a0) && (0, _helper.isNumber)(b0)) {
                return Comparators.sortNum(a0, b0);
            }
            return Comparators.sortBase(a0, b0);
        }
    }, {
        key: 'getDefaultComparator',
        value: function getDefaultComparator(columnType) {
            var defaultComparator = void 0;

            if (columnType === 1) {
                defaultComparator = Comparators.sortNum;
            } else if (columnType === 2) {
                defaultComparator = Comparators.sortDate;
            } else {
                defaultComparator = Comparators.sortBase;
            }

            return defaultComparator;
        }
    }]);

    return Comparators;
}();

/**
 * Functions that pull data out of a table cell.
 */


var Extractors = function () {
    function Extractors() {
        _classCallCheck(this, Extractors);
    }

    _createClass(Extractors, null, [{
        key: 'textExtractor',
        value: function textExtractor($tableCell) {
            return $tableCell.text();
        }
    }, {
        key: 'tooltipExtractor',
        value: function tooltipExtractor($tableCell) {
            return $tableCell.find('span').attr('data-original-title');
        }
    }, {
        key: 'dateStampExtractor',
        value: function dateStampExtractor($tableCell) {
            return $tableCell.data('dateStamp');
        }
    }, {
        key: 'getDefaultExtractor',
        value: function getDefaultExtractor() {
            return Extractors.textExtractor;
        }
    }]);

    return Extractors;
}();

var TableButtonHelpers = function () {
    function TableButtonHelpers() {
        _classCallCheck(this, TableButtonHelpers);
    }

    _createClass(TableButtonHelpers, null, [{
        key: 'getEnclosingTable',

        /**
         * Given a button, get the innermost table enclosing it.
         */
        value: function getEnclosingTable($button) {
            return $($button.parents('table')[0]);
        }

        /**
         * Given a button and an index idx,
         * find the button's column position in the table
         * where the columns are treated as idx-indexed.
         */

    }, {
        key: 'getColumnPositionOfButton',
        value: function getColumnPositionOfButton($button, idx) {
            return $button.parent().children().index($button) + idx;
        }

        /**
         * Given a table, clear all the sort states.
         */

    }, {
        key: 'clearAllSortStates',
        value: function clearAllSortStates($table) {
            $table.find('.icon-sort').attr('class', 'icon-sort unsorted'); // clear the icons
            $table.find('.button-sort-ascending').removeClass('button-sort-ascending').addClass('button-sort-none');
            $table.find('.button-sort-descending').removeClass('button-sort-descending').addClass('button-sort-none');
        }

        /**
         * Given a button in table, set its state to sorted ascending.
         * Clear all other button states.
         */

    }, {
        key: 'setButtonToSortedAscending',
        value: function setButtonToSortedAscending($button) {
            this.clearAllSortStates(this.getEnclosingTable($button));
            $button.addClass('button-sort-ascending');
            $button.find('.icon-sort').attr('class', 'icon-sort sorted-ascending'); // set the icon to ascending
        }

        /**
         * Given a button in table, set its state to sorted descending.
         * Clear all other button states.
         */

    }, {
        key: 'setButtonToSortedDescending',
        value: function setButtonToSortedDescending($button) {
            this.clearAllSortStates(this.getEnclosingTable($button));
            $button.addClass('button-sort-descending');
            $button.find('.icon-sort').attr('class', 'icon-sort sorted-descending'); // set the icon to descending
        }
    }]);

    return TableButtonHelpers;
}();

// http://stackoverflow.com/questions/7558182/sort-a-table-fast-by-its-first-column-with-javascript-or-jquery
/**
 * Sorts a table based on certain column and comparator
 *
 * @param $table
 *     A jQuery object representing the table.
 * @param colIdx
 *     The column index (1-based) as key for the sort
 * @param comparatorOrNull
 *     Function to compare two elements.
 *     May be null.
 * @param extractorOrNull
 *     Function to pull out data from a table cell for comparison.
 *     May be null.
 * @param shouldSortAscending
 *     If this is true, sort in ascending order.
 *     Otherwise, sort in descending order
 */


function sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending) {
    var columnType = 0;
    var store = [];
    var $rowList = $table.find('> tbody > tr');

    // Iterate through column's contents to decide which comparator to use
    for (var i = 0; i < $rowList.length; i += 1) {
        if ($rowList[i].cells[colIdx - 1] === undefined) {
            continue;
        }

        var extractor = isDefined(extractorOrNull) ? extractorOrNull : Extractors.getDefaultExtractor();

        // $.trim trims leading/trailing whitespaces
        // $rowList[i].cells[colIdx - 1] is where we get the table cell from
        var textToCompare = $.trim(extractor($($rowList[i].cells[colIdx - 1])));

        // Store rows together with the innerText to compare
        store.push([textToCompare, $rowList[i], i]);

        if ((columnType === 0 || columnType === 1) && (0, _helper.isNumber)(textToCompare)) {
            columnType = 1;
        } else if ((columnType === 0 || columnType === 2) && (0, _helper.isDate)(textToCompare)) {
            columnType = 2;
        } else {
            columnType = 3;
        }
    }

    var comparator = isDefined(comparatorOrNull) ? comparatorOrNull : Comparators.getDefaultComparator(columnType);

    store.sort(function (x, y) {
        var compareResult = shouldSortAscending ? comparator(x[0].toUpperCase(), y[0].toUpperCase()) : comparator(y[0].toUpperCase(), x[0].toUpperCase());
        if (compareResult === 0) {
            return x[2] - y[2];
        }
        return compareResult;
    });

    // Must rewrap because .get() does not return a jQuery wrapped DOM node
    // and hence does not have the .children() function
    var $tbody = $($table.get(0)).children('tbody');

    if ($tbody.size < 1) {
        $tbody = $table;
    }

    // Must push to target tbody else it will generate a new tbody for the table
    for (var j = 0; j < store.length; j += 1) {
        $tbody.get(0).appendChild(store[j][1]);
    }

    store = null;
}

/**
 * Sorts a table
 * @param sortButtonClicked
 *     The jQuery object representing the sort button that was clicked.
 * @param comparatorStringOrNull
 *     String representing the function to compare 2 elements.
 *     May be null.
 * @param extractorStringOrNull
 *     String representing the function to pull out data from a table cell for comparison.
 *     May be null.
 */
function toggleSort($button, comparatorStringOrNull, extractorStringOrNull) {
    var isSortedAscending = $button.hasClass('button-sort-ascending');

    var $table = TableButtonHelpers.getEnclosingTable($button);
    var colIdx = TableButtonHelpers.getColumnPositionOfButton($button, 1);
    var comparatorOrNull = isDefined(comparatorStringOrNull) ? Comparators[comparatorStringOrNull] : null;
    var extractorOrNull = isDefined(extractorStringOrNull) ? Extractors[extractorStringOrNull] : null;
    var shouldSortAscending = !isSortedAscending;

    sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending);

    // update the button and icon states
    if (shouldSortAscending) {
        TableButtonHelpers.setButtonToSortedAscending($button);
    } else {
        TableButtonHelpers.setButtonToSortedDescending($button);
    }
}

exports.Comparators = Comparators;
exports.getPointValue = getPointValue;
exports.toggleSort = toggleSort;

/***/ })

/******/ });